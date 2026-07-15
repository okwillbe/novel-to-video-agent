package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UpdateVoiceRequest {
    private String folderId;
    private String name;
    private String description;
    private String qwenVoiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String voicePrompt;
    private String gender;
    private String language;
}