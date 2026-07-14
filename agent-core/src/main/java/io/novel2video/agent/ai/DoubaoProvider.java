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
 * Doubao Provider (字节豆包)
 *
 * 字节跳动的大模型服务
 * API 文档: https://www.volcengine.com/docs/82379
 */
@Slf4j
@Component
public class DoubaoProvider implements AiProvider {

    @Value("${ai.providers.doubao.api-key:}")
    private String apiKey;

    @Value("${ai.providers.doubao.endpoint-id:}")
    private String endpointId;

    @Value("${ai.providers.doubao.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "doubao";
    }

    @Override
    public String getType() {
        return "llm";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty() && endpointId != null && !endpointId.isEmpty();
    }

    @Override
    public String chat(String model, List<Message> messages, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("Doubao API key or endpoint not configured");
        }

        String url = baseUrl + "/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", endpointId); // 豆包用 endpointId 作为 model
        requestBody.put("messages", messages.stream()
                .map(m -> Map.of("role", m.role(), "content", m.content()))
                .toList());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.debug("Calling Doubao API: endpoint={}", endpointId);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }

            throw new RuntimeException("Unexpected response from Doubao: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call Doubao API", e);
            throw new RuntimeException("Doubao API call failed: " + e.getMessage(), e);
        }
    }
}