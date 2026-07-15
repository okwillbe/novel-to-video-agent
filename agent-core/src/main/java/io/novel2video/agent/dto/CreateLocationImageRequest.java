package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CreateLocationImageRequest {
    private Integer imageIndex;
    private String description;
    private String imageUrl;
    private Boolean isSelected;
}

// ==================== 音色 DTO ====================
