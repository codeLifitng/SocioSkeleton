package com.SocioSkeleton.posts_service.controller;

import com.SocioSkeleton.posts_service.auth.UserContextHolder;
import com.SocioSkeleton.posts_service.dto.PostDto;
import com.SocioSkeleton.posts_service.model.PostCreateRequestModel;
import com.SocioSkeleton.posts_service.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestModel postDto) {
        Long userId = UserContextHolder.getCurrentUserId();
        PostDto createdPost = postsService.createPost(postDto, userId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        PostDto post = postsService.getPostById(postId);
        return post != null ? new ResponseEntity<>(post, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId) {
        List<PostDto> posts = postsService.getAllPostsOfUser(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
