package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

// ---- Generate Request ----

@Data
class GenerateRequest {
    private String content;
    private String style;
    private Integer duration;
    private Map<String, Object> options;
    private String callback;
}

@Data
@AllArgsConstructor
class TaskCreatedResponse {
    private String taskId;
    private String status;
    private int estimatedTime;
}

// ---- Task Status ----

@Data
@AllArgsConstructor
class TaskStatusResponse {
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

@Data
@AllArgsConstructor
class TaskSummaryResponse {
    private String taskId;
    private String taskType;
    private String status;
    private int progress;
    private LocalDateTime createdAt;
}

// ---- Skill DTOs ----

@Data
@AllArgsConstructor
class SkillResponse {
    private String skillId;
    private String name;
    private String description;
    private String category;
    private String version;
    private List<String> tags;
    private Long useCount;
    private Double avgRating;
}

@Data
@AllArgsConstructor
class SkillDetailResponse {
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

@Data
class CreateSkillRequest {
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

@Data
@AllArgsConstructor
class UserInfoResponse {
    private String userId;
    private String email;
    private String nickname;
    private String role;
    private Integer quotaVideoSeconds;
    private Integer quotaImageCount;
    private Integer quotaVoiceSeconds;
    private Long quotaTextTokens;
}

@Data
class LoginRequest {
    private String email;
    private String password;
}

@Data
@AllArgsConstructor
class LoginResponse {
    private String userId;
    private String nickname;
    private String role;
    private String token;
}
