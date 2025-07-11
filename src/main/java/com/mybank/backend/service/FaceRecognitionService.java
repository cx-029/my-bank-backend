package com.mybank.backend.service;

public interface FaceRecognitionService {
    /**
     * 人脸识别
     * @param base64Image base64图片
     * @return user_id 或 null
     */
    String recognize(String base64Image);
}