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
 * OpenAI Provider
 *
 * 支持 GPT-4o, GPT-4o-mini 等模型
 */
@Slf4j
@Component
public class OpenAiProvider implements AiProvider {

    @Value("${ai.providers.openai.api-key:}")
    private String apiKey;

    @Value("${ai.providers.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "openai";
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
            throw new IllegalStateException("OpenAI API key not configured");
        }

        String url = baseUrl + "/v1/chat/completions";

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

            log.debug("Calling OpenAI API: {} with {} messages", model, messages.size());

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }

            throw new RuntimeException("Unexpected response from OpenAI: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call OpenAI API", e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage(), e);
        }
    }
}
