package com.joblog.postlike.repository;

import com.joblog.post.domain.Post;
import com.joblog.postlike.domain.PostLike;
import com.joblog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryCustom {
    Optional<PostLike> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    void deleteByUserAndPost(User user, Post post);
}
