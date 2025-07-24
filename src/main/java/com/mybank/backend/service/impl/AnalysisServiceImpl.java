package com.mybank.backend.service.impl;

import com.mybank.backend.service.AnalysisService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final RestTemplate restTemplate;

    // 微服务的 URL
    private final String analysisServiceUrl = "http://localhost:8081";

    public AnalysisServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> getTransactionSummary(Long accountId) {
        // 构建微服务的完整 URL
        String url = analysisServiceUrl + "/api/analysis/summary?accountId=" + accountId;

        // 构建请求头，添加 Authorization Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ4enkiLCJyb2xlIjoiY3VzdG9tZXIiLCJpYXQiOjE3NTMzMzE3NDQsImV4cCI6MTc1MzQxODE0NH0.kZkhRnQ2vH5kXw8JsV2-l_jLFVRuB9pC7DMIi8jGJ3-_t0LVMCm-lH_mJ8yuc_Pq");

        // 创建 HttpEntity 并设置 Header
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // 使用 RestTemplate 发起 GET 请求，并携带 Header
        ResponseEntity<Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, requestEntity, Map.class);

        // 返回微服务的响应数据
        return response.getBody();
    }
}