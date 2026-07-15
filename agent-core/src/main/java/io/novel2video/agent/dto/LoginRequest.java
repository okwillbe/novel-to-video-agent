package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
