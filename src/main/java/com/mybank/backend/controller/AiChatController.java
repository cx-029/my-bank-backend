package com.mybank.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Value("${aliyun.baichuan.api-key}")
    private String apiKey;

    // OpenAI兼容模式接口
    private static final String URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> req) {
        String question = req.get("question");
        String answer = callAliyunBaichuan(question);
        Map<String, Object> resp = new HashMap<>();
        resp.put("answer", answer);
        return resp;
    }

    private String callAliyunBaichuan(String question) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // OpenAI兼容模式body
        String payload = String.format(
                "{\"model\":\"qwen-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}", question);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);
            Map data = response.getBody();
            // 打印原始返回，方便排查
            System.out.println("Raw Response: " + data);

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
            // 如果有error字段，直接返回错误信息
            if (data != null && data.containsKey("error")) {
                return "错误: " + data.get("error").toString();
            }
            return "AI助手未能理解您的问题。";
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return "AI助手服务暂时不可用，请稍后再试。错误：" + e.getMessage();
        }
    }
}