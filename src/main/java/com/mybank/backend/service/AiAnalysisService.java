package com.mybank.backend.service;

import java.util.Map;

public interface AiAnalysisService {

    /**
     * 分析用户的评分数据并生成个性化建议
     *
     * @param scores 用户评分数据
     * @return AI 分析的结果（个性化建议）
     */
    String analyze(Map<String, Double> scores);
}