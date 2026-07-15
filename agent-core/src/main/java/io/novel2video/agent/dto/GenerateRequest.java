package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class GenerateRequest {
    private String content;
    private String style;
    private Integer duration;
    private Map<String, Object> options;
    private String callback;
}
