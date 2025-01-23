package com.SocioSkeleton.posts_service.service;

import com.SocioSkeleton.posts_service.auth.UserContextHolder;
import com.SocioSkeleton.posts_service.clients.ConnectionsClient;
import com.SocioSkeleton.posts_service.dto.PersonDto;
import com.SocioSkeleton.posts_service.dto.PostDto;
import com.SocioSkeleton.posts_service.entity.Post;
import com.SocioSkeleton.posts_service.event.PostCreatedEvent;
import com.SocioSkeleton.posts_service.exception.ResourceNotFoundException;
import com.SocioSkeleton.posts_service.model.PostCreateRequestModel;
import com.SocioSkeleton.posts_service.repository.PostRepository;
import com.SocioSkeleton.posts_service.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostsService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsClient connectionsClient;
    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    public PostDto createPost(PostCreateRequestModel postRequest, Long userId) {
        Post post = modelMapper.map(postRequest, Post.class);
        post.setUserId(userId);
        Post savedPost = postRepository.save(post);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                .postId(savedPost.getId())
                .creatorId(userId)
                .content(postRequest.getContent())
                .build();

        kafkaTemplate.send(Constants.POST_CREATED_TOPIC, postCreatedEvent);

        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Retrieving post with ID: {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post not found with id: " + postId)
        );
        Long userId = UserContextHolder.getCurrentUserId();
        List<PersonDto> firstConnection = connectionsClient.getFirstConnections();
        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        List<Post> post = postRepository.findByUserId(userId);

        return post
                .stream()
                .map(elem ->
                        modelMapper.map(elem, PostDto.class))
                .collect(Collectors.toList());

    }
}
