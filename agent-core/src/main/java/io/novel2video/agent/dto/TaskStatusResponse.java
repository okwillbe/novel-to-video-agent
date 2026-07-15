package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusResponse {
    private String taskId;
    private String status;
    private int progress;
    private String currentStep;
    private List<StepInfo> steps;

    @Data
    @AllArgsConstructor
    public static class StepInfo {
        private String name;
        private String status;
        private int progress;
    }
}
