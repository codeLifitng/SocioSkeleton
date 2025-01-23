package com.SocioSkeleton.notfication_service.consumer;

import com.SocioSkeleton.connections_service.event.AcceptConnectionRequestEvent;
import com.SocioSkeleton.connections_service.event.SendConnectionRequestEvent;
import com.SocioSkeleton.notfication_service.service.SendNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionServiceConsumer {

    private final SendNotification sendNotification;

    @KafkaListener(topics="send-connection-request-topic")
    public void handleSendConnectionRequest(String event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SendConnectionRequestEvent sendConnectionRequestEvent = objectMapper.readValue(event, SendConnectionRequestEvent.class);
        String message = "You have received a connection request from user with id: %d"+sendConnectionRequestEvent.getSenderId();
        sendNotification.send(sendConnectionRequestEvent.getReceiverId(), message);
    }

    @KafkaListener(topics = "accept-connection-request-topic")
    public void handleAcceptConnectionRequest(String event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AcceptConnectionRequestEvent acceptConnectionRequestEvent = objectMapper.readValue(event, AcceptConnectionRequestEvent.class);
        String message = "Your connection request has been accepted by user with id: %d"+acceptConnectionRequestEvent.getReceiverId();
        sendNotification.send(acceptConnectionRequestEvent.getSenderId(), message);
    }
}
