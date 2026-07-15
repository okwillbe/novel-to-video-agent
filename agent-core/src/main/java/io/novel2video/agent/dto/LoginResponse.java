package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String nickname;
    private String role;
    private String token;
}
