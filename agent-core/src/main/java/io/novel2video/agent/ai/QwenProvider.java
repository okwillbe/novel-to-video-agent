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
 * Qwen Provider (阿里通义千问)
 *
 * 阿里云的大模型服务
 * API 文档: https://help.aliyun.com/zh/model-studio/developer-reference/api-details
 */
@Slf4j
@Component
public class QwenProvider implements AiProvider {

    private static final String API_BASE = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    @Value("${ai.providers.qwen.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "qwen";
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
            throw new IllegalStateException("Qwen API key not configured");
        }

        String url = API_BASE + "/chat/completions";

        String actualModel = model != null ? model : "qwen-plus";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", actualModel);
        requestBody.put("messages", messages.stream()
                .map(m -> Map.of("role", m.role(), "content", m.content()))
                .toList());

        if (options != null) {
            if (options.containsKey("temperature")) {
                requestBody.put("temperature", options.get("temperature"));
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.debug("Calling Qwen API: {}", actualModel);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }

            throw new RuntimeException("Unexpected response from Qwen: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call Qwen API", e);
            throw new RuntimeException("Qwen API call failed: " + e.getMessage(), e);
        }
    }
}