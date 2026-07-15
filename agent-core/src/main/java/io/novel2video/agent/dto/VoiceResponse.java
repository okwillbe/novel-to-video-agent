package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public static VoiceResponse from(io.novel2video.agent.entity.GlobalVoice voice) {
        if (voice == null) return null;
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