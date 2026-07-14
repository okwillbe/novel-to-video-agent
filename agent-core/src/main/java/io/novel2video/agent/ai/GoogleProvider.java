package io.novel2video.agent.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Google AI Provider
 *
 * 支持 Gemini 系列模型
 * API 文档: https://ai.google.dev/api
 */
@Slf4j
@Component
public class GoogleProvider implements AiProvider {

    private static final String API_BASE = "https://generativelanguage.googleapis.com/v1beta";

    @Value("${ai.providers.google.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "google";
    }

    @Override
    public String getType() {
        return "llm";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String chat(String model, List<Message> messages, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("Google API key not configured");
        }

        String url = API_BASE + "/models/" + model + ":generateContent?key=" + apiKey;

        // 构建 Gemini API 请求格式
        Map<String, Object> contents = new HashMap<>();
        List<Map<String, Object>> contentList = new ArrayList<>();

        for (Message msg : messages) {
            Map<String, Object> content = new HashMap<>();
            content.put("role", "user".equals(msg.role()) ? "user" : "model");
            content.put("parts", List.of(Map.of("text", msg.content())));
            contentList.add(content);
        }
        contents.put("contents", contentList);

        // 添加生成配置
        if (options != null && !options.isEmpty()) {
            Map<String, Object> generationConfig = new HashMap<>();
            if (options.containsKey("temperature")) {
                generationConfig.put("temperature", options.get("temperature"));
            }
            if (options.containsKey("maxOutputTokens")) {
                generationConfig.put("maxOutputTokens", options.get("maxOutputTokens"));
            }
            contents.put("generationConfig", generationConfig);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = objectMapper.writeValueAsString(contents);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            log.debug("Calling Google Gemini API: {} with {} messages", model, messages.size());

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                }
            }

            throw new RuntimeException("Unexpected response from Google API: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call Google Gemini API", e);
            throw new RuntimeException("Google API call failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void chatStream(String model, List<Message> messages, Map<String, Object> options, StreamHandler handler) {
        // 流式输出暂时用同步实现，后续可改进
        try {
            String response = chat(model, messages, options);
            handler.onComplete(response);
        } catch (Exception e) {
            handler.onError(e);
        }
    }
}
