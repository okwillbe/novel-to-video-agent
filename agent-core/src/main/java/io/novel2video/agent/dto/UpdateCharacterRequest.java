package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UpdateCharacterRequest {
    private String folderId;
    private String name;
    private List<String> aliases;
    private Object profileData;
    private Boolean profileConfirmed;
    private String voiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String globalVoiceId;
}

// ==================== 角色形象 DTO ====================
