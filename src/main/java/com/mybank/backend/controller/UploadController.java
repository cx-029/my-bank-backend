package com.mybank.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${avatar.upload.dir:/data/avatar/}") // 可在 application.properties 配置
    private String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File dest = new File(uploadDir, filename);
        try {
            file.transferTo(dest);
            String url = "http://localhost:8080/avatar/" + filename;
            return ResponseEntity.ok(new AvatarResp(url));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("上传失败");
        }
    }

    // 响应结构
    static class AvatarResp {
        public String url;
        public AvatarResp(String url) { this.url = url; }
    }
}