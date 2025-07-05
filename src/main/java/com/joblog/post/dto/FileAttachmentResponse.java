package com.joblog.post.dto;

import com.joblog.post.domain.FileAttachment;
import lombok.Getter;

@Getter
public class FileAttachmentResponse {

    private final String originalFilename; // 원래 파일명
    private final String fileUrl;          // 접근 가능한 URL
    private final boolean image;           // 이미지 여부

    public FileAttachmentResponse(FileAttachment file) {
        this.originalFilename = file.getOriginalFilename();
        this.fileUrl = "/uploads/" + file.getStoredFilename();
        this.image = file.getFileType() != null && file.getFileType().startsWith("image/");
    }
}
