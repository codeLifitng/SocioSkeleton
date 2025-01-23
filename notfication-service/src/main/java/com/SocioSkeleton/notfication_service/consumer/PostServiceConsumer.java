package com.SocioSkeleton.notfication_service.consumer;

import com.SocioSkeleton.notfication_service.clients.ConnectionsClient;
import com.SocioSkeleton.notfication_service.dto.PersonDto;
import com.SocioSkeleton.notfication_service.entity.Notification;
import com.SocioSkeleton.notfication_service.repository.NotificationRepository;
import com.SocioSkeleton.notfication_service.service.SendNotification;
import com.SocioSkeleton.posts_service.event.PostCreatedEvent;
import com.SocioSkeleton.posts_service.event.PostLikedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.SocioSkeleton.notfication_service.util.Constants.POST_CREATED_TOPIC;
import static com.SocioSkeleton.notfication_service.util.Constants.POST_LIKED_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final SendNotification sendNotification;

    @KafkaListener(topics = POST_CREATED_TOPIC)
    public void handlePostCreated(String postCreatedEvent) throws JsonProcessingException {
        log.info("Sending notifications: handlePostCreated: {}", postCreatedEvent);
        ObjectMapper objectMapper = new ObjectMapper();
        PostCreatedEvent event = objectMapper.readValue(postCreatedEvent, PostCreatedEvent.class);
        List<PersonDto> connections = connectionsClient.getFirstConnections(event.getCreatorId());

        for(PersonDto connection: connections) {
            sendNotification.send(connection.getUserId(), "Your connection "+
                    event.getCreatorId() + " has a new post, Check it out");
        }
    }

    @KafkaListener(topics = POST_LIKED_TOPIC)
    public void handlePostLikes(String postLikedEvent) throws JsonProcessingException {
        log.info("Sending notifications: handlePostLiked: {}", postLikedEvent);
        ObjectMapper objectMapper = new ObjectMapper();
        PostLikedEvent event = objectMapper.readValue(postLikedEvent, PostLikedEvent.class);
        String message = String.format("Your post, %d has been liked by %d", event.getPostId(), event.getLikedByUserId());

        sendNotification.send(event.getCreatorId(), message);
    }
}
