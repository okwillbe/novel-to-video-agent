package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String email;
    private String nickname;
    private String role;
    private Integer quotaVideoSeconds;
    private Integer quotaImageCount;
    private Integer quotaVoiceSeconds;
    private Long quotaTextTokens;
}
