package com.joblog.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;
}
