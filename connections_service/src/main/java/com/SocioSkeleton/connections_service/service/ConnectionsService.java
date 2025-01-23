package com.SocioSkeleton.connections_service.service;

import com.SocioSkeleton.connections_service.auth.UserContextHolder;
import com.SocioSkeleton.connections_service.entity.Person;
import com.SocioSkeleton.connections_service.event.AcceptConnectionRequestEvent;
import com.SocioSkeleton.connections_service.event.SendConnectionRequestEvent;
import com.SocioSkeleton.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonRepository personRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendRequestEventKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptConnectionRequesttKafkaTemplate;

    public List<Person> getFirstDegreeConnections() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Getting first degree connection for user with id: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public Boolean sendConnectionRequest(Long receiverId) {
        Long senderId = UserContextHolder.getCurrentUserId();

        log.info("Trying to send connection request, sender: {}, receiver: {}", senderId, receiverId);

        if(receiverId.equals(senderId)) {
            throw new RuntimeException("Sender and receiver are the same people!!");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if(alreadySentRequest) {
            throw new RuntimeException("Connection request already sent!");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if(alreadyConnected) {
            throw new RuntimeException("Already connected!!");
        }

        personRepository.addConnectionRequest(senderId, receiverId);
        log.info("Successfully sent the connection request!");
        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .build();

        sendRequestEventKafkaTemplate.send("send-connection-request-topic", sendConnectionRequestEvent);
        log.info("Successfully accepted the connection request, sender: {}, receiver: {}", senderId, receiverId);
        return true;
    }


    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverID = UserContextHolder.getCurrentUserId();

        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId, receiverID);
        if(!connectionRequestExists) {
            throw new RuntimeException("No connection request exists to accept");
        }

        personRepository.acceptConnectionRequest(senderId, receiverID);
        log.info("Connection accepted!!");

        AcceptConnectionRequestEvent acceptConnectionRequestEvent = AcceptConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverID)
                .build();

        acceptConnectionRequesttKafkaTemplate.send("accept-connection-request-topic", acceptConnectionRequestEvent);
        return true;
    }

    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiveId = UserContextHolder.getCurrentUserId();

        boolean connectionRequestExists = personRepository.connectionRequestExists(senderId, receiveId);
        if(!connectionRequestExists) {
            throw new RuntimeException("No connection request exists to delete");
        }

        personRepository.rejectConnectionRequest(senderId, receiveId);
        log.info("Connection rejected!!");
        return true;
    }
}
