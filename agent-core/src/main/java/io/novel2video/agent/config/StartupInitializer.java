package io.novel2video.agent.config;

import io.novel2video.agent.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动初始化
 *
 * 单用户模式：启动时创建默认用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        log.info("=== Initializing Novel to Video Agent ===");

        // 创建默认用户
        var user = userService.getOrCreateDefaultUser();
        log.info("Default user ready: {} ({})", user.getNickname(), user.getEmail());
        log.info("Quota: video={}s, image={}, voice={}s, tokens={}",
                user.getQuotaVideoSeconds(),
                user.getQuotaImageCount(),
                user.getQuotaVoiceSeconds(),
                user.getQuotaTextTokens());

        log.info("=== Initialization Complete ===");
    }
}
