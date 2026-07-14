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
 * FAL.AI Provider
 *
 * 支持图片生成：FLUX, Stable Diffusion 等
 * API 文档: https://fal.ai/models
 */
@Slf4j
@Component
public class FalProvider implements AiProvider {

    private static final String API_BASE = "https://queue.fal.run";

    @Value("${ai.providers.fal.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "fal";
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public List<String> generateImage(String model, String prompt, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("FAL API key not configured");
        }

        // 映射模型名称到 FAL 的 endpoint
        String endpoint = mapModelToEndpoint(model);
        String url = API_BASE + "/" + endpoint;

        // 构建请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);

        // 添加可选参数
        if (options != null) {
            if (options.containsKey("negative_prompt")) {
                requestBody.put("negative_prompt", options.get("negative_prompt"));
            }
            if (options.containsKey("image_size")) {
                requestBody.put("image_size", options.get("image_size"));
            } else {
                requestBody.put("image_size", "landscape_16_9");
            }
            if (options.containsKey("num_images")) {
                requestBody.put("num_images", options.get("num_images"));
            } else {
                requestBody.put("num_images", 1);
            }
            if (options.containsKey("seed")) {
                requestBody.put("seed", options.get("seed"));
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Key " + apiKey);

            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.info("Calling FAL.AI API: {} for image generation", endpoint);

            // 提交任务
            ResponseEntity<String> submitResponse = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (submitResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to submit FAL task: " + submitResponse.getBody());
            }

            // 解析响应获取 request_id
            JsonNode submitJson = objectMapper.readTree(submitResponse.getBody());
            String requestId = submitJson.path("request_id").asText();

            // 轮询获取结果
            return pollForResult(endpoint, requestId, headers);

        } catch (Exception e) {
            log.error("Failed to generate image with FAL.AI", e);
            throw new RuntimeException("FAL.AI image generation failed: " + e.getMessage(), e);
        }
    }

    private List<String> pollForResult(String endpoint, String requestId, HttpHeaders headers) throws InterruptedException {
        String statusUrl = API_BASE + "/" + endpoint + "/requests/" + requestId + "/status";

        int maxAttempts = 60; // 最多等待 60 秒
        for (int i = 0; i < maxAttempts; i++) {
            Thread.sleep(1000);

            try {
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        statusUrl, HttpMethod.GET, entity, String.class);

                JsonNode json = objectMapper.readTree(response.getBody());
                String status = json.path("status").asText();

                log.debug("FAL task {} status: {}", requestId, status);

                if ("COMPLETED".equals(status)) {
                    List<String> urls = new ArrayList<>();
                    JsonNode images = json.path("images");
                    if (images.isArray()) {
                        for (JsonNode img : images) {
                            urls.add(img.path("url").asText());
                        }
                    }
                    return urls;
                } else if ("FAILED".equals(status)) {
                    throw new RuntimeException("FAL task failed: " + json.path("error").asText());
                }
            } catch (Exception e) {
                log.warn("Error polling FAL status: {}", e.getMessage());
            }
        }

        throw new RuntimeException("FAL task timeout after " + maxAttempts + " seconds");
    }

    private String mapModelToEndpoint(String model) {
        return switch (model.toLowerCase()) {
            case "flux-schnell", "fal-flux-schnell" -> "fal-ai/flux/schnell";
            case "flux-dev", "fal-flux-dev" -> "fal-ai/flux/dev";
            case "flux-pro", "fal-flux-pro" -> "fal-ai/flux/pro";
            case "sd3", "fal-sd3" -> "fal-ai/stable-diffusion-v35-large";
            default -> "fal-ai/flux/schnell"; // 默认用最快的
        };
    }
}
