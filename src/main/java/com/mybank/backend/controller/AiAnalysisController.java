package com.mybank.backend.controller;

import com.mybank.backend.service.AiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai/analysis")
public class AiAnalysisController {

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @PostMapping
    public Map<String, Object> analyze(@RequestBody Map<String, Double> scores) {
        // 调用服务层的分析方法
        String aiAnalysisResult = aiAnalysisService.analyze(scores);

        // 构造并返回响应
        Map<String, Object> response = new HashMap<>();
        response.put("aiAnalysis", aiAnalysisResult);
        return response;
    }
}