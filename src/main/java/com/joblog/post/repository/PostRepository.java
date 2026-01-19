package com.joblog.post.repository;

import com.joblog.post.domain.Post;
import com.joblog.post.dto.PostSearchCondition;
import com.joblog.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Page<Post> findByUser(User user, Pageable pageable);

    @Query
    Page<Post> search(PostSearchCondition condition, Pageable pageable);
}
