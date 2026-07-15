package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UpdateLocationRequest {
    private String folderId;
    private String name;
    private String summary;
    private String artStyle;
}

// ==================== 场景图片 DTO ====================
