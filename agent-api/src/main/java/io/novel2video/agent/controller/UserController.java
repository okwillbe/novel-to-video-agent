package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.User;
import io.novel2video.agent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User API Controller
 *
 * 单用户模式：获取当前用户信息和配额
 */
@Tag(name = "User API", description = "User and quota management")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user info")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser() {
        User user = userService.getOrCreateDefaultUser();

        return ResponseEntity.ok(ApiResponse.success(
                new UserInfoResponse(
                        user.getUserId(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getRole(),
                        user.getQuotaVideoSeconds(),
                        user.getQuotaImageCount(),
                        user.getQuotaVoiceSeconds(),
                        user.getQuotaTextTokens()
                )
        ));
    }

    @Operation(summary = "Simple login (single user mode)")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> ApiResponse.success(
                        new LoginResponse(user.getUserId(), user.getNickname(), user.getRole(), "fake-token")
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(ApiResponse.error("邮箱或密码错误")));
    }
}
