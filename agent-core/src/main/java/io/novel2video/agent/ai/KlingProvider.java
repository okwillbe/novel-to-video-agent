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
 * Kling Provider (可灵 AI - 快手)
 *
 * 国产视频/图片生成服务
 * API 文档: https://klingai.kuaishou.com/docs
 */
@Slf4j
@Component
public class KlingProvider implements AiProvider {

    private static final String API_BASE = "https://api.klingai.com/v1";

    @Value("${ai.providers.kling.access-key:}")
    private String accessKey;

    @Value("${ai.providers.kling.secret-key:}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "kling";
    }

    @Override
    public String getType() {
        return "image"; // 也支持 video
    }

    @Override
    public boolean isConfigured() {
        return accessKey != null && !accessKey.isEmpty() &&
               secretKey != null && !secretKey.isEmpty();
    }

    @Override
    public List<String> generateImage(String model, String prompt, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("Kling API keys not configured");
        }

        String url = API_BASE + "/images/generations";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model != null ? model : "kling-v1");
        requestBody.put("prompt", prompt);

        if (options != null) {
            if (options.containsKey("negative_prompt")) {
                requestBody.put("negative_prompt", options.get("negative_prompt"));
            }
            if (options.containsKey("image_ratio")) {
                requestBody.put("image_ratio", options.get("image_ratio")); // 16:9, 1:1, etc.
            } else {
                requestBody.put("image_ratio", "16:9");
            }
            if (options.containsKey("n")) {
                requestBody.put("n", options.get("n")); // 数量
            } else {
                requestBody.put("n", 1);
            }
        }

        try {
            HttpHeaders headers = buildHeaders();
            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.info("Calling Kling API for image generation");

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode images = root.path("data");

                List<String> urls = new ArrayList<>();
                if (images.isArray()) {
                    for (JsonNode img : images) {
                        urls.add(img.path("url").asText());
                    }
                }
                return urls;
            }

            throw new RuntimeException("Unexpected response from Kling: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to generate image with Kling", e);
            throw new RuntimeException("Kling image generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateVideo(String model, String prompt, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("Kling API keys not configured");
        }

        String url = API_BASE + "/videos/generations";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model != null ? model : "kling-v1");
        requestBody.put("prompt", prompt);

        if (options != null) {
            if (options.containsKey("negative_prompt")) {
                requestBody.put("negative_prompt", options.get("negative_prompt"));
            }
            if (options.containsKey("duration")) {
                requestBody.put("duration", options.get("duration")); // 5 or 10 seconds
            } else {
                requestBody.put("duration", 5);
            }
            if (options.containsKey("aspect_ratio")) {
                requestBody.put("aspect_ratio", options.get("aspect_ratio"));
            }
        }

        try {
            HttpHeaders headers = buildHeaders();
            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.info("Calling Kling API for video generation");

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("task_id").asText();
            }

            throw new RuntimeException("Unexpected response from Kling: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to generate video with Kling", e);
            throw new RuntimeException("Kling video generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public VideoStatus getVideoStatus(String taskId) {
        String url = API_BASE + "/videos/generations/" + taskId;

        try {
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String status = root.path("task_status").asText();
                int progress = root.path("task_status_percent").asInt(0);
                String videoUrl = root.path("task_result").path("videos").get(0).path("url").asText("");
                String error = root.path("task_status_msg").asText("");

                return new VideoStatus(taskId, status, progress, videoUrl, error);
            }

            return new VideoStatus(taskId, "unknown", 0, null, "Failed to get status");

        } catch (Exception e) {
            log.error("Failed to get Kling video status", e);
            return new VideoStatus(taskId, "error", 0, null, e.getMessage());
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessKey + ":" + secretKey);
        return headers;
    }
}