package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CreateAppearanceRequest {
    private String characterId;
    private Integer appearanceIndex;
    private String changeReason;
    private String artStyle;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private Integer selectedIndex;
}

// ==================== 场景 DTO ====================
