-- =====================================================
-- Novel to Video Agent - Asset Management Tables
-- Version: 1.0.0
-- =====================================================

USE `novel2video`;

-- =====================================================
-- 1. Global Asset Folders (组织文件夹)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_asset_folders` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `folder_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户',
    `name` VARCHAR(128) NOT NULL COMMENT '文件夹名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_folder_id` (`folder_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局资产文件夹';

-- =====================================================
-- 2. Global Characters (角色)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_characters` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `character_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户',
    `folder_id` VARCHAR(64) DEFAULT NULL COMMENT '归属文件夹',
    `name` VARCHAR(128) NOT NULL COMMENT '角色名',
    `aliases` JSON DEFAULT NULL COMMENT '别名数组',
    `profile_data` JSON DEFAULT NULL COMMENT '档案数据',
    `profile_confirmed` TINYINT NOT NULL DEFAULT 0 COMMENT '档案是否已确认',
    `voice_id` VARCHAR(64) DEFAULT NULL COMMENT '音色ID',
    `voice_type` VARCHAR(32) DEFAULT NULL COMMENT 'qwen-designed | custom',
    `custom_voice_url` VARCHAR(512) DEFAULT NULL COMMENT '上传的音频URL',
    `global_voice_id` VARCHAR(64) DEFAULT NULL COMMENT '绑定的全局音色ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_id` (`character_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局角色';

-- =====================================================
-- 3. Global Character Appearances (角色形象 - 子资产)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_character_appearances` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `appearance_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `character_id` VARCHAR(64) NOT NULL COMMENT '所属角色',
    `appearance_index` INT NOT NULL DEFAULT 0 COMMENT '形象序号',
    `change_reason` VARCHAR(128) DEFAULT NULL COMMENT '形象说明（如"便装"、"战甲"）',
    `art_style` VARCHAR(64) DEFAULT NULL COMMENT '艺术风格',
    `description` TEXT DEFAULT NULL COMMENT '形象描述',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '单张图URL',
    `image_urls` JSON DEFAULT NULL COMMENT '多图URL数组',
    `selected_index` INT DEFAULT 0 COMMENT '选中的图片索引',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_appearance_id` (`appearance_id`),
    KEY `idx_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色形象';

-- =====================================================
-- 4. Global Locations (场景)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_locations` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `location_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户',
    `folder_id` VARCHAR(64) DEFAULT NULL COMMENT '归属文件夹',
    `name` VARCHAR(128) NOT NULL COMMENT '场景名',
    `summary` TEXT DEFAULT NULL COMMENT '场景简介',
    `art_style` VARCHAR(64) DEFAULT NULL COMMENT '艺术风格',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_location_id` (`location_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局场景';

-- =====================================================
-- 5. Global Location Images (场景图片 - 子资产)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_location_images` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `image_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `location_id` VARCHAR(64) NOT NULL COMMENT '所属场景',
    `image_index` INT NOT NULL DEFAULT 0 COMMENT '图片序号',
    `description` TEXT DEFAULT NULL COMMENT '图片描述',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '图片URL',
    `is_selected` TINYINT NOT NULL DEFAULT 0 COMMENT '是否选中',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_image_id` (`image_id`),
    KEY `idx_location_id` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景图片';

-- =====================================================
-- 6. Global Voices (音色)
-- =====================================================

CREATE TABLE IF NOT EXISTS `global_voices` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `voice_id` VARCHAR(64) NOT NULL COMMENT 'UUID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户',
    `folder_id` VARCHAR(64) DEFAULT NULL COMMENT '归属文件夹',
    `name` VARCHAR(128) NOT NULL COMMENT '音色名称',
    `description` TEXT DEFAULT NULL COMMENT '音色描述',
    `qwen_voice_id` VARCHAR(64) DEFAULT NULL COMMENT 'qwen-tts生成的音色ID',
    `voice_type` VARCHAR(32) NOT NULL DEFAULT 'custom' COMMENT 'qwen-designed | custom',
    `custom_voice_url` VARCHAR(512) DEFAULT NULL COMMENT '上传的音频URL（预览）',
    `custom_voice_media_id` VARCHAR(64) DEFAULT NULL COMMENT '上传音频媒体ID',
    `voice_prompt` TEXT DEFAULT NULL COMMENT 'AI设计时的提示词',
    `gender` VARCHAR(16) DEFAULT NULL COMMENT 'male | female | neutral',
    `language` VARCHAR(16) NOT NULL DEFAULT 'zh' COMMENT '语言',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_voice_id` (`voice_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局音色';