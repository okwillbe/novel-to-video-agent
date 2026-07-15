package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CreateSkillRequest {
    private String skillId;
    private String name;
    private String description;
    private String category;
    private String content;
    private String version;
    private String author;
    private List<String> tags;
}

// ---- User DTOs (单用户模式) ----
