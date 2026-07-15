package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryResponse {
    private String taskId;
    private String taskType;
    private String status;
    private int progress;
    private LocalDateTime createdAt;
}

// ---- Skill DTOs ----
