package com.mybank.backend.controller;

import com.mybank.backend.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> recommendProducts(@RequestBody Map<String, Object> request) {
        // 将 accountId 转换为 Long 类型
        Number accountIdNumber = (Number) request.get("accountId");
        Long accountId = accountIdNumber.longValue();

        // 调用服务层获取推荐结果
        Map<String, Object> response = recommendationService.getRecommendations(accountId);
        return ResponseEntity.ok(response);
    }
}