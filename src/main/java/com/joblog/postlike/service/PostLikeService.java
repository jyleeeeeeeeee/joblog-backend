package com.joblog.postlike.service;

import com.joblog.user.entity.User;

public interface PostLikeService {
    void like(User user, Long postId);
    void unlike(User user, Long postId);
    long countLikes(Long postId);
}
