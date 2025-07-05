package com.joblog.mypage.dto;

import com.joblog.post.domain.Post;
import lombok.Getter;

@Getter
public class UserLikedPostResponse {
    private final Long postId;
    private final String title;
    private final String content;

    public UserLikedPostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}
