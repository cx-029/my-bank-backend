package com.mybank.backend.service.impl;

import com.mybank.backend.service.AiAnalysisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Value("${aliyun.baichuan.api-key}")
    private String apiKey;

    private static final String URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Override
    public String analyze(Map<String, Double> scores) {
        // 将 scores 转换为自然语言描述
        String question = buildQuestionWithScores(scores);

        // 调用阿里云百川服务
        return callAliyunBaichuan(question);
    }

    /**
     * 将 scores 转换为自然语言描述
     */
    private String buildQuestionWithScores(Map<String, Double> scores) {
        StringBuilder question = new StringBuilder("请扮演一位专业的智慧银行财富分析师，分析以下用户的财务状况并提供建议：\n\n");
        question.append("### 用户财务评分数据：\n");
        question.append(String.format("1. 流动性评分：%.2f，表示您的资产流动性。\n", scores.getOrDefault("liquidityScore", 0.0)));
        question.append(String.format("2. 收入趋势评分：%.2f，表示您的收入增长趋势。\n", scores.getOrDefault("incomeTrendScore", 0.0)));
        question.append(String.format("3. 现金流评分：%.2f，表示您的现金流稳定性。\n", scores.getOrDefault("cashFlowScore", 0.0)));
        question.append(String.format("4. 支出稳定性评分：%.2f，表示您的支出是否稳定。\n", scores.getOrDefault("expenseStabilityScore", 0.0)));
        question.append(String.format("5. 收入稳定性评分：%.2f，表示您的收入是否稳定。\n", scores.getOrDefault("incomeStabilityScore", 0.0)));
        question.append(String.format("6. 投资贡献评分：%.2f，表示投资对您财富的贡献。\n", scores.getOrDefault("investmentContributionScore", 0.0)));
        question.append(String.format("7. 支出增长趋势评分：%.2f，表示您的支出增长趋势。\n", scores.getOrDefault("expenseTrendScore", 0.0)));
        question.append("\n请根据以上评分数据和交易记录，分析用户的财务状况并提供个性化的理财建议。（仔细分析每一个数据，给出建议时注意排版美观，不要留太多空白）");
        return question.toString();
    }

    /**
     * 调用阿里云百川服务
     */
    private String callAliyunBaichuan(String question) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey); // 使用 API Key 作为 Bearer Token
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构造请求体
        String payload = String.format(
                "{\"model\":\"qwen-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}",
                escapeJsonString(question)); // 转义特殊字符

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            // 发送 POST 请求
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);

            // 获取响应体
            Map data = response.getBody();
            System.out.println("Raw Response: " + data); // 打印原始返回，方便调试

            // 解析响应数据
            if (data != null && data.containsKey("choices")) {
                Object choicesObj = data.get("choices");
                if (choicesObj instanceof java.util.List && !((java.util.List) choicesObj).isEmpty()) {
                    Map firstChoice = (Map) ((java.util.List) choicesObj).get(0);
                    Map message = (Map) firstChoice.get("message");
                    if (message != null) {
                        Object content = message.get("content");
                        if (content != null) {
                            return content.toString();
                        }
                    }
                }
            }

            // 如果返回中包含错误信息
            if (data != null && data.containsKey("error")) {
                return "错误: " + data.get("error").toString();
            }

            return "AI助手未能生成个性化建议，请稍后重试。";

        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return "AI助手服务暂时不可用，请稍后再试。错误：" + e.getMessage();
        }
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}