package com.joblog.post.dto;

import com.joblog.post.domain.FileAttachment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequest{
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
}