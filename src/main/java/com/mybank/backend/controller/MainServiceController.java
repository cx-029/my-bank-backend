package com.mybank.backend.controller;

import com.mybank.backend.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainServiceController {

    private final AnalysisService analysisService;

    public MainServiceController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * 主服务暴露的接口，调用微服务的交易摘要功能
     * @param accountId 账户 ID
     * @return 交易摘要数据
     */
    @GetMapping("/api/main/analysis/summary")
    public Map<String, Object> getTransactionSummary(@RequestParam Long accountId) {
        // 调用 Service 层
        return analysisService.getTransactionSummary(accountId);
    }
}