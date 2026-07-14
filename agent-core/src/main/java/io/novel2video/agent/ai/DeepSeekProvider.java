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
 * DeepSeek Provider
 *
 * 支持 deepseek-chat, deepseek-reasoner 模型
 * 国产大模型，性价比高
 */
@Slf4j
@Component
public class DeepSeekProvider implements AiProvider {

    private static final String API_BASE = "https://api.deepseek.com";

    @Value("${ai.providers.deepseek.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "deepseek";
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
            throw new IllegalStateException("DeepSeek API key not configured");
        }

        String url = API_BASE + "/v1/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages.stream()
                .map(m -> Map.of("role", m.role(), "content", m.content()))
                .toList());

        if (options != null) {
            if (options.containsKey("temperature")) {
                requestBody.put("temperature", options.get("temperature"));
            }
            if (options.containsKey("max_tokens")) {
                requestBody.put("max_tokens", options.get("max_tokens"));
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.debug("Calling DeepSeek API: {}", model);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }

            throw new RuntimeException("Unexpected response from DeepSeek: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call DeepSeek API", e);
            throw new RuntimeException("DeepSeek API call failed: " + e.getMessage(), e);
        }
    }
}
