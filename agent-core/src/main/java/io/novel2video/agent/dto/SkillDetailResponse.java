package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDetailResponse {
    private String skillId;
    private String name;
    private String description;
    private String category;
    private String version;
    private List<String> tags;
    private String content;
    private Object metadata;
    private Long useCount;
    private Long successCount;
    private Double avgRating;
}
