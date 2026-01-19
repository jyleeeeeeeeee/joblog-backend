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

        // 설정에서 받은 업로드 경로
        File dir = new File(uploadDir);

        // 상대경로라면 실행 디렉토리(user.dir) 기준으로 절대경로로 변환
        if (!dir.isAbsolute()) {
            dir = new File(dir.getAbsolutePath());
        }

        // 디렉토리가 없으면 생성 (기존 파일은 유지)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("업로드 디렉토리를 생성할 수 없습니다: " + dir.getAbsolutePath());
            }
        }

        // 저장 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID() + ext;

        // 최종 저장 위치
        File dest = new File(dir, storedFilename);

        // 실제 파일 저장
        multipartFile.transferTo(dest);

        return storedFilename;
    }

}
