-- =====================================================
-- Novel to Video Agent - Default User (Single User Mode)
-- Version: 1.0.0
-- =====================================================

USE `novel2video`;

-- 插入默认用户（单用户模式）
INSERT INTO `users` (`user_id`, `email`, `phone`, `password_hash`, `nickname`, `role`, `status`, `balance`, `quota_video_seconds`, `quota_image_count`, `quota_voice_seconds`, `quota_text_tokens`)
VALUES (
    'user_default',
    'admin@novel2video.local',
    NULL,
    'admin123',  -- 明文密码，单用户模式简化
    '管理员',
    'admin',
    1,
    0.0000,
    3600,     -- 1小时视频配额
    1000,     -- 1000张图片配额
    3600,     -- 1小时配音配额
    10000000  -- 1000万token配额
) ON DUPLICATE KEY UPDATE `nickname` = '管理员';
