package com.joblog.post.repository;

import com.joblog.post.domain.Post;
import com.joblog.post.dto.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    Page<Post> search(PostSearchCondition condition, Pageable pageable);
    Optional<Post> searchOne(@Param("postId") Long postId);

//    @Query("select p from Post p join fetch p.author where p.id = :postId")
    Optional<Post> findPostForUpdate(@Param("postId") Long postId);

//    @Query("select distinct p from Post p join fetch p.author left join fetch p.comments where p.id = :postId")
    Optional<Post> findPostForDelete(@Param("postId") Long postId);

}
