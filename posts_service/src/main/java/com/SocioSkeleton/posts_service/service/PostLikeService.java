package com.SocioSkeleton.posts_service.service;

import com.SocioSkeleton.posts_service.auth.UserContextHolder;
import com.SocioSkeleton.posts_service.entity.Post;
import com.SocioSkeleton.posts_service.entity.PostLike;
import com.SocioSkeleton.posts_service.event.PostLikedEvent;
import com.SocioSkeleton.posts_service.exception.BadRequestException;
import com.SocioSkeleton.posts_service.exception.ResourceNotFoundException;
import com.SocioSkeleton.posts_service.repository.PostLikeRepository;
import com.SocioSkeleton.posts_service.repository.PostRepository;
import com.SocioSkeleton.posts_service.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Attempting to like the post with id: {} by user with id: {}", postId, userId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post not found with id: "+ postId)
        );
        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(alreadyLiked) throw new BadRequestException("Can not like the same post again :(");

        PostLike postLike = generateLikes(userId, postId);

        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                        .postId(postId)
                        .likedByUserId(userId)
                        .creatorId(post.getUserId())
                        .build();

        kafkaTemplate.send(Constants.POST_LIKE_TOPIC, postId, postLikedEvent);
        postLikeRepository.save(postLike);
        log.info("Post with id: {} liked successfully", postId);
    }

    private PostLike generateLikes(Long userId, Long postId) {
        PostLike likedPost = new PostLike();
        likedPost.setPostId(postId);
        likedPost.setUserId(userId);

        return likedPost;
    }

    public void unlikePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Attempting to unlike the post with id: {} by user with id: {}", postId, userId);
        boolean exists = postRepository.existsById(postId);
        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+ postId);

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(!alreadyLiked) throw new BadRequestException("Can not unlike the same post again :(");

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("Post with id: {} unliked successfully", postId);
    }
}
