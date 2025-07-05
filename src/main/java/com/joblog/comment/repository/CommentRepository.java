package com.joblog.comment.repository;

import com.joblog.comment.domain.Comment;
import com.joblog.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom{
    Page<Comment> findByUser(User user, Pageable pageable);

}
