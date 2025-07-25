package com.mybank.backend.service.impl;

import com.mybank.backend.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public Map<String, Object> getRecommendations(Long accountId) {
        // 模拟推荐逻辑：根据用户账户ID返回推荐结果
        return Map.of(
                "accountId", accountId,
                "recommendations", List.of("ProductA", "ProductB", "ProductC"),
                "score", 85.5 // 示例评分
        );
    }
}