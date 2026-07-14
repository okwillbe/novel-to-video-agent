package io.novel2video.agent.dto;

import io.novel2video.agent.entity.GlobalVoice;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音色响应 DTO
 */
@Data
public class VoiceResponse {

    private String voiceId;
    private String folderId;
    private String name;
    private String description;
    private String qwenVoiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String voicePrompt;
    private String gender;
    private String language;
    private LocalDateTime createdAt;

    public static VoiceResponse from(GlobalVoice voice) {
        VoiceResponse response = new VoiceResponse();
        response.setVoiceId(voice.getVoiceId());
        response.setFolderId(voice.getFolderId());
        response.setName(voice.getName());
        response.setDescription(voice.getDescription());
        response.setQwenVoiceId(voice.getQwenVoiceId());
        response.setVoiceType(voice.getVoiceType());
        response.setCustomVoiceUrl(voice.getCustomVoiceUrl());
        response.setVoicePrompt(voice.getVoicePrompt());
        response.setGender(voice.getGender());
        response.setLanguage(voice.getLanguage());
        response.setCreatedAt(voice.getCreatedAt());
        return response;
    }
}
