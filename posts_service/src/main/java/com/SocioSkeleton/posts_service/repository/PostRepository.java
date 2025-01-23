package com.SocioSkeleton.posts_service.repository;

import com.SocioSkeleton.posts_service.dto.PostDto;
import com.SocioSkeleton.posts_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
}
