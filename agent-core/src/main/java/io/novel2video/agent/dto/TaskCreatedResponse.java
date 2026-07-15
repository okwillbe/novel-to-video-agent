package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreatedResponse {
    private String taskId;
    private String status;
    private int estimatedTime;
}

// ---- Task Status ----
