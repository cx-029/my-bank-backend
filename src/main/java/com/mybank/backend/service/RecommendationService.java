package com.mybank.backend.service;

import java.util.Map;

public interface RecommendationService {
    /**
     * 根据 accountId 获取推荐结果
     *
     * @param accountId 用户账户ID
     * @return 推荐结果（包含产品列表和评分等）
     */
    Map<String, Object> getRecommendations(Long accountId);
}