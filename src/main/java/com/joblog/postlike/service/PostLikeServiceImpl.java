package com.joblog.postlike.service;

import com.joblog.post.domain.Post;
import com.joblog.post.repository.PostRepository;
import com.joblog.postlike.domain.PostLike;
import com.joblog.postlike.repository.PostLikeRepository;
import com.joblog.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void like(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        boolean empty = postLikeRepository.findByUserAndPost(user, post).isEmpty();
        if (empty) {
            postLikeRepository.save(PostLike.builder()
                    .post(post)
                    .user(user)
                    .build());
        }
    }

    @Override
    @Transactional
    public void unlike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        postLikeRepository.deleteByUserAndPost(user, post);
    }

    @Override
    public long countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        return postLikeRepository.countByPost(post);
    }
}
