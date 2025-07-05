package com.joblog.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @JsonProperty("post_id")
    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;

    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    private String content;
}
