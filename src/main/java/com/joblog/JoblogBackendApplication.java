package com.joblog;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class JoblogBackendApplication {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public static void main(String[] args) {
        SpringApplication.run(JoblogBackendApplication.class, args);
    }

    @PostConstruct
    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
