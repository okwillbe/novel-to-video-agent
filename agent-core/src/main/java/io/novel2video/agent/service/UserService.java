package io.novel2video.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.novel2video.agent.entity.User;
import io.novel2video.agent.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * User Service
 *
 * Initial version: Single user mode
 * - Default user "admin" is created on startup
 * - All operations use this default user
 *
 * Future expansion:
 * - Multi-user registration/login
 * - OAuth integration
 * - Role-based access control
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public static final String DEFAULT_USER_ID = "user_default";
    public static final String DEFAULT_EMAIL = "admin";
    public static final String DEFAULT_PASSWORD = "admin"; // 明文，后续改加密

    private final UserMapper userMapper;

    /**
     * Get the default user (single user mode)
     * Creates if not exists
     */
    public User getOrCreateDefaultUser() {
        Optional<User> existing = findByUserId(DEFAULT_USER_ID);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Create default user
        User user = new User()
                .setUserId(DEFAULT_USER_ID)
                .setEmail(DEFAULT_EMAIL)
                .setNickname("管理员")
                .setRole("admin")
                .setStatus(1)
                .setBalance(BigDecimal.ZERO)
                .setQuotaVideoSeconds(3600)    // 1 hour
                .setQuotaImageCount(1000)      // 1000 images
                .setQuotaVoiceSeconds(3600)    // 1 hour
                .setQuotaTextTokens(10_000_000L); // 10M tokens

        userMapper.insert(user);
        log.info("Created default user: {}", user.getUserId());
        return user;
    }

    public Optional<User> findByUserId(String userId) {
        return Optional.ofNullable(userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUserId, userId)));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)));
    }

    /**
     * Simple login for single user mode
     * Returns default user if credentials match
     */
    public Optional<User> login(String email, String password) {
        // Single user mode: only accept default credentials
        if (DEFAULT_EMAIL.equalsIgnoreCase(email) && DEFAULT_PASSWORD.equals(password)) {
            return Optional.of(getOrCreateDefaultUser());
        }
        return Optional.empty();
    }

    /**
     * Update user quota after usage
     */
    public void consumeQuota(String userId, int videoSeconds, int imageCount, int voiceSeconds, long textTokens) {
        User user = findByUserId(userId).orElseThrow();

        if (videoSeconds > 0 && user.getQuotaVideoSeconds() != null) {
            user.setQuotaVideoSeconds(Math.max(0, user.getQuotaVideoSeconds() - videoSeconds));
        }
        if (imageCount > 0 && user.getQuotaImageCount() != null) {
            user.setQuotaImageCount(Math.max(0, user.getQuotaImageCount() - imageCount));
        }
        if (voiceSeconds > 0 && user.getQuotaVoiceSeconds() != null) {
            user.setQuotaVoiceSeconds(Math.max(0, user.getQuotaVoiceSeconds() - voiceSeconds));
        }
        if (textTokens > 0 && user.getQuotaTextTokens() != null) {
            user.setQuotaTextTokens(Math.max(0, user.getQuotaTextTokens() - textTokens));
        }

        userMapper.updateById(user);
    }

    /**
     * Check if user has enough quota
     */
    public boolean hasQuota(String userId, int videoSeconds, int imageCount, int voiceSeconds, long textTokens) {
        User user = findByUserId(userId).orElse(null);
        if (user == null) return false;

        if (videoSeconds > 0 && (user.getQuotaVideoSeconds() == null || user.getQuotaVideoSeconds() < videoSeconds)) {
            return false;
        }
        if (imageCount > 0 && (user.getQuotaImageCount() == null || user.getQuotaImageCount() < imageCount)) {
            return false;
        }
        return true;
    }
}
