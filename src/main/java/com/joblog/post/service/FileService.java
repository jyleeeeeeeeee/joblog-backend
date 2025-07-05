package com.joblog.post.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFilename = UUID.randomUUID() + ext;

        File dest = new File(dir, storedFilename);

        // 실제 파일을 지정된 위치에 저장
        multipartFile.transferTo(dest);

        return storedFilename;
    }
}
