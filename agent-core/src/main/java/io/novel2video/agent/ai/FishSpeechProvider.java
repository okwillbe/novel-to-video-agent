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
 * Fish Speech Provider
 *
 * 国产 TTS 服务，支持高质量语音合成
 * API 文档: https://fish.audio/docs
 */
@Slf4j
@Component
public class FishSpeechProvider implements AiProvider {

    private static final String API_BASE = "https://api.fish.audio/v1";

    @Value("${ai.providers.fish-speech.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "fish-speech";
    }

    @Override
    public String getType() {
        return "voice";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String textToSpeech(String model, String text, String voiceId, Map<String, Object> options) {
        if (!isConfigured()) {
            throw new IllegalStateException("Fish Speech API key not configured");
        }

        String url = API_BASE + "/tts";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        if (voiceId != null) {
            requestBody.put("reference_id", voiceId); // 音色 ID
        }
        if (options != null) {
            if (options.containsKey("speed")) {
                requestBody.put("speed", options.get("speed"));
            }
            if (options.containsKey("format")) {
                requestBody.put("format", options.get("format")); // mp3, wav
            } else {
                requestBody.put("format", "mp3");
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String body = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            log.debug("Calling Fish Speech API for TTS, text length: {}", text.length());

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("url").asText(); // 返回音频 URL
            }

            throw new RuntimeException("Unexpected response from Fish Speech: " + response.getBody());

        } catch (Exception e) {
            log.error("Failed to call Fish Speech API", e);
            throw new RuntimeException("Fish Speech TTS failed: " + e.getMessage(), e);
        }
    }
}