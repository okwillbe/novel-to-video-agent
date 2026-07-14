-- =====================================================
-- Novel to Video Agent - Database Schema
-- Version: 1.0.0
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS `novel2video` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `novel2video`;

-- =====================================================
-- 1. Skills System Tables
-- =====================================================

-- Skills Table (Core)
CREATE TABLE IF NOT EXISTS `skills` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT 'Unique skill identifier, e.g. novel-analysis_v1',
    `name` VARCHAR(64) NOT NULL COMMENT 'Skill name',
    `description` VARCHAR(512) NOT NULL COMMENT 'Skill description',
    `category` VARCHAR(32) NOT NULL COMMENT 'Category: analysis/generation/synthesis/postprocess',
    `skill_content` MEDIUMTEXT NOT NULL COMMENT 'Skill content (Markdown)',
    `version` VARCHAR(32) NOT NULL DEFAULT '1.0.0' COMMENT 'Version number',
    `author` VARCHAR(64) DEFAULT 'system' COMMENT 'Author',
    `tags` JSON DEFAULT NULL COMMENT 'Tags list',
    `metadata` JSON DEFAULT NULL COMMENT 'Extended metadata',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled 1-enabled 2-draft',
    `is_public` TINYINT NOT NULL DEFAULT 1 COMMENT 'Public: 0-private 1-public',
    `owner_id` VARCHAR(64) DEFAULT NULL COMMENT 'Owner ID (for private skills)',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT 'Tenant ID (multi-tenant)',
    `use_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Usage count',
    `success_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Success count',
    `avg_rating` DECIMAL(3,2) DEFAULT NULL COMMENT 'Average rating',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_id_version` (`skill_id`, `version`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_use_count` (`use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Skills Repository';

-- Skill Resources Table
CREATE TABLE IF NOT EXISTS `skill_resources` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT 'Associated skill ID',
    `version` VARCHAR(32) NOT NULL COMMENT 'Skill version',
    `resource_path` VARCHAR(256) NOT NULL COMMENT 'Resource path',
    `resource_type` VARCHAR(32) NOT NULL COMMENT 'Type: template/config/example',
    `content` MEDIUMTEXT COMMENT 'Resource content (text)',
    `storage_url` VARCHAR(512) DEFAULT NULL COMMENT 'External storage URL (large files)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_resource` (`skill_id`, `version`, `resource_path`),
    KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Skill Resources';

-- Skill Usage Logs Table
CREATE TABLE IF NOT EXISTS `skill_usage_logs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `log_id` VARCHAR(64) NOT NULL COMMENT 'Log unique ID',
    `skill_id` VARCHAR(128) NOT NULL COMMENT 'Skill ID',
    `version` VARCHAR(32) NOT NULL COMMENT 'Skill version',
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `session_id` VARCHAR(64) DEFAULT NULL COMMENT 'Session ID',
    `task_id` VARCHAR(64) DEFAULT NULL COMMENT 'Task ID',
    `input_summary` VARCHAR(512) DEFAULT NULL COMMENT 'Input summary',
    `output_summary` VARCHAR(512) DEFAULT NULL COMMENT 'Output summary',
    `execution_time_ms` INT UNSIGNED DEFAULT NULL COMMENT 'Execution time (ms)',
    `status` VARCHAR(16) NOT NULL COMMENT 'Status: success/failure/timeout',
    `error_message` TEXT DEFAULT NULL COMMENT 'Error message',
    `rating` TINYINT DEFAULT NULL COMMENT 'User rating 1-5',
    `feedback` TEXT DEFAULT NULL COMMENT 'User feedback',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_log_id` (`log_id`),
    KEY `idx_skill_id` (`skill_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Skill Usage Logs';

-- Skill Versions Table (Version History)
CREATE TABLE IF NOT EXISTS `skill_versions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT 'Skill ID',
    `version` VARCHAR(32) NOT NULL COMMENT 'Version number',
    `change_type` VARCHAR(16) NOT NULL COMMENT 'Change type: create/update/deprecate',
    `change_summary` VARCHAR(512) DEFAULT NULL COMMENT 'Change summary',
    `previous_version` VARCHAR(32) DEFAULT NULL COMMENT 'Previous version',
    `skill_content` MEDIUMTEXT NOT NULL COMMENT 'Skill content at this version',
    `changed_by` VARCHAR(64) DEFAULT NULL COMMENT 'Changed by',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_version` (`skill_id`, `version`),
    KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Skill Version History';

-- =====================================================
-- 2. User System Tables
-- =====================================================

-- Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User unique ID',
    `email` VARCHAR(128) DEFAULT NULL COMMENT 'Email',
    `phone` VARCHAR(32) DEFAULT NULL COMMENT 'Phone number',
    `password_hash` VARCHAR(256) DEFAULT NULL COMMENT 'Password hash',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT 'Nickname',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT 'Avatar URL',
    `role` VARCHAR(32) NOT NULL DEFAULT 'user' COMMENT 'Role: admin/user/vip',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled 1-active 2-frozen',
    `balance` DECIMAL(12,4) NOT NULL DEFAULT 0 COMMENT 'Account balance',
    `quota_video_seconds` INT UNSIGNED DEFAULT 0 COMMENT 'Video quota (seconds)',
    `quota_image_count` INT UNSIGNED DEFAULT 0 COMMENT 'Image quota (count)',
    `quota_voice_seconds` INT UNSIGNED DEFAULT 0 COMMENT 'Voice quota (seconds)',
    `quota_text_tokens` BIGINT UNSIGNED DEFAULT 0 COMMENT 'Text quota (tokens)',
    `settings` JSON DEFAULT NULL COMMENT 'User settings',
    `last_login_at` DATETIME DEFAULT NULL COMMENT 'Last login time',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Users';

-- OAuth Connections Table
CREATE TABLE IF NOT EXISTS `oauth_connections` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `provider` VARCHAR(32) NOT NULL COMMENT 'Provider: google/github/wechat/telegram',
    `provider_user_id` VARCHAR(128) NOT NULL COMMENT 'Provider user ID',
    `access_token` TEXT DEFAULT NULL COMMENT 'Access token',
    `refresh_token` TEXT DEFAULT NULL COMMENT 'Refresh token',
    `expires_at` DATETIME DEFAULT NULL COMMENT 'Token expiration',
    `profile_data` JSON DEFAULT NULL COMMENT 'Profile data from provider',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_user` (`provider`, `provider_user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth Connections';

-- =====================================================
-- 3. Task System Tables
-- =====================================================

-- Tasks Table
CREATE TABLE IF NOT EXISTS `tasks` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) NOT NULL COMMENT 'Task unique ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `session_id` VARCHAR(64) DEFAULT NULL COMMENT 'Session ID',
    `task_type` VARCHAR(32) NOT NULL COMMENT 'Type: novel_to_video/image_gen/voice_gen',
    `status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Status: pending/processing/completed/failed/cancelled',
    `priority` TINYINT NOT NULL DEFAULT 5 COMMENT 'Priority: 1-10, higher is more important',
    `input_data` JSON NOT NULL COMMENT 'Input data',
    `output_data` JSON DEFAULT NULL COMMENT 'Output data',
    `error_message` TEXT DEFAULT NULL COMMENT 'Error message',
    `progress` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Progress 0-100',
    `current_step` VARCHAR(64) DEFAULT NULL COMMENT 'Current step name',
    `total_steps` TINYINT UNSIGNED DEFAULT 0 COMMENT 'Total steps count',
    `completed_steps` TINYINT UNSIGNED DEFAULT 0 COMMENT 'Completed steps count',
    `retry_count` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Retry count',
    `max_retries` TINYINT UNSIGNED NOT NULL DEFAULT 3 COMMENT 'Max retries',
    `started_at` DATETIME DEFAULT NULL COMMENT 'Start time',
    `completed_at` DATETIME DEFAULT NULL COMMENT 'Completion time',
    `estimated_duration_seconds` INT UNSIGNED DEFAULT NULL COMMENT 'Estimated duration',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tasks';

-- Task Steps Table (Execution Pipeline)
CREATE TABLE IF NOT EXISTS `task_steps` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) NOT NULL COMMENT 'Task ID',
    `step_index` TINYINT UNSIGNED NOT NULL COMMENT 'Step index',
    `step_name` VARCHAR(64) NOT NULL COMMENT 'Step name',
    `skill_id` VARCHAR(128) DEFAULT NULL COMMENT 'Skill used',
    `status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT 'Status: pending/running/completed/failed/skipped',
    `input_data` JSON DEFAULT NULL COMMENT 'Step input',
    `output_data` JSON DEFAULT NULL COMMENT 'Step output',
    `error_message` TEXT DEFAULT NULL COMMENT 'Error message',
    `progress` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Progress 0-100',
    `started_at` DATETIME DEFAULT NULL COMMENT 'Start time',
    `completed_at` DATETIME DEFAULT NULL COMMENT 'Completion time',
    `execution_time_ms` INT UNSIGNED DEFAULT NULL COMMENT 'Execution time (ms)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Task Steps';

-- Task Artifacts Table (Generated Files)
CREATE TABLE IF NOT EXISTS `task_artifacts` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) NOT NULL COMMENT 'Task ID',
    `step_id` BIGINT UNSIGNED DEFAULT NULL COMMENT 'Step ID',
    `artifact_type` VARCHAR(32) NOT NULL COMMENT 'Type: image/video/audio/document/json',
    `name` VARCHAR(256) NOT NULL COMMENT 'Artifact name',
    `storage_path` VARCHAR(512) NOT NULL COMMENT 'Storage path',
    `storage_url` VARCHAR(1024) DEFAULT NULL COMMENT 'Public URL',
    `mime_type` VARCHAR(128) DEFAULT NULL COMMENT 'MIME type',
    `size_bytes` BIGINT UNSIGNED DEFAULT NULL COMMENT 'File size',
    `metadata` JSON DEFAULT NULL COMMENT 'Artifact metadata',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_artifact_type` (`artifact_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Task Artifacts';

-- =====================================================
-- 4. Billing System Tables
-- =====================================================

-- Billing Accounts Table
CREATE TABLE IF NOT EXISTS `billing_accounts` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `billing_mode` VARCHAR(16) NOT NULL DEFAULT 'OFF' COMMENT 'Mode: OFF/SHADOW/ENFORCE',
    `balance` DECIMAL(12,4) NOT NULL DEFAULT 0 COMMENT 'Account balance',
    `frozen_balance` DECIMAL(12,4) NOT NULL DEFAULT 0 COMMENT 'Frozen balance',
    `total_consumed` DECIMAL(12,4) NOT NULL DEFAULT 0 COMMENT 'Total consumed',
    `currency` VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-frozen 1-active',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Billing Accounts';

-- Billing Transactions Table
CREATE TABLE IF NOT EXISTS `billing_transactions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `transaction_id` VARCHAR(64) NOT NULL COMMENT 'Transaction unique ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `task_id` VARCHAR(64) DEFAULT NULL COMMENT 'Associated task',
    `transaction_type` VARCHAR(32) NOT NULL COMMENT 'Type: quote/freeze/execute/settle/refund/recharge',
    `resource_type` VARCHAR(32) NOT NULL COMMENT 'Resource: text/image/video/voice',
    `quantity` DECIMAL(12,4) NOT NULL COMMENT 'Quantity',
    `unit` VARCHAR(16) NOT NULL COMMENT 'Unit: token/second/count',
    `unit_price` DECIMAL(8,6) DEFAULT NULL COMMENT 'Unit price',
    `amount` DECIMAL(12,4) NOT NULL COMMENT 'Amount',
    `currency` VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
    `status` VARCHAR(16) NOT NULL COMMENT 'Status: pending/frozen/completed/refunded/cancelled',
    `description` VARCHAR(512) DEFAULT NULL COMMENT 'Description',
    `metadata` JSON DEFAULT NULL COMMENT 'Extended metadata',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_id` (`transaction_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Billing Transactions';

-- Pricing Rules Table
CREATE TABLE IF NOT EXISTS `pricing_rules` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `resource_type` VARCHAR(32) NOT NULL COMMENT 'Resource type',
    `provider` VARCHAR(64) DEFAULT NULL COMMENT 'Provider name',
    `model` VARCHAR(128) DEFAULT NULL COMMENT 'Model name',
    `unit_price` DECIMAL(8,6) NOT NULL COMMENT 'Unit price',
    `unit` VARCHAR(16) NOT NULL COMMENT 'Unit',
    `currency` VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
    `min_quantity` DECIMAL(12,4) DEFAULT 1 COMMENT 'Minimum quantity',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT 'Active flag',
    `valid_from` DATETIME DEFAULT NULL COMMENT 'Valid from',
    `valid_until` DATETIME DEFAULT NULL COMMENT 'Valid until',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_provider_model` (`provider`, `model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Pricing Rules';

-- =====================================================
-- 5. Project & Session Tables
-- =====================================================

-- Projects Table
CREATE TABLE IF NOT EXISTS `projects` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id` VARCHAR(64) NOT NULL COMMENT 'Project unique ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `name` VARCHAR(128) NOT NULL COMMENT 'Project name',
    `description` TEXT DEFAULT NULL COMMENT 'Project description',
    `project_type` VARCHAR(32) NOT NULL DEFAULT 'novel_to_video' COMMENT 'Project type',
    `status` VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT 'Status: draft/in_progress/completed/archived',
    `settings` JSON DEFAULT NULL COMMENT 'Project settings',
    `metadata` JSON DEFAULT NULL COMMENT 'Project metadata',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_id` (`project_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Projects';

-- Agent Sessions Table
CREATE TABLE IF NOT EXISTS `agent_sessions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `session_id` VARCHAR(64) NOT NULL COMMENT 'Session unique ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `project_id` VARCHAR(64) DEFAULT NULL COMMENT 'Project ID',
    `channel` VARCHAR(32) NOT NULL DEFAULT 'web' COMMENT 'Channel: web/telegram/wechat/discord',
    `status` VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT 'Status: active/paused/completed',
    `context` JSON DEFAULT NULL COMMENT 'Session context (memory)',
    `messages` JSON DEFAULT NULL COMMENT 'Message history (compressed)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent Sessions';

-- Agent Messages Table
CREATE TABLE IF NOT EXISTS `agent_messages` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `message_id` VARCHAR(64) NOT NULL COMMENT 'Message unique ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT 'Session ID',
    `role` VARCHAR(16) NOT NULL COMMENT 'Role: user/assistant/system/tool',
    `content` MEDIUMTEXT NOT NULL COMMENT 'Message content',
    `tool_calls` JSON DEFAULT NULL COMMENT 'Tool calls',
    `tool_call_id` VARCHAR(64) DEFAULT NULL COMMENT 'Tool call ID',
    `metadata` JSON DEFAULT NULL COMMENT 'Message metadata',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent Messages';

-- =====================================================
-- 6. AI Provider Configuration Tables
-- =====================================================

-- AI Providers Table
CREATE TABLE IF NOT EXISTS `ai_providers` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `provider_id` VARCHAR(64) NOT NULL COMMENT 'Provider unique ID',
    `name` VARCHAR(64) NOT NULL COMMENT 'Provider name',
    `provider_type` VARCHAR(32) NOT NULL COMMENT 'Type: llm/image/video/voice',
    `is_builtin` TINYINT NOT NULL DEFAULT 0 COMMENT 'Built-in provider',
    `config_schema` JSON DEFAULT NULL COMMENT 'Configuration JSON schema',
    `default_config` JSON DEFAULT NULL COMMENT 'Default configuration',
    `icon_url` VARCHAR(512) DEFAULT NULL COMMENT 'Provider icon',
    `website` VARCHAR(512) DEFAULT NULL COMMENT 'Provider website',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled 1-enabled',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_id` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Providers';

-- AI Models Table
CREATE TABLE IF NOT EXISTS `ai_models` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `model_id` VARCHAR(64) NOT NULL COMMENT 'Model unique ID',
    `provider_id` VARCHAR(64) NOT NULL COMMENT 'Provider ID',
    `name` VARCHAR(128) NOT NULL COMMENT 'Model name',
    `model_type` VARCHAR(32) NOT NULL COMMENT 'Type: chat/image/video/voice/embedding',
    `input_modalities` JSON DEFAULT NULL COMMENT 'Input modalities',
    `output_modalities` JSON DEFAULT NULL COMMENT 'Output modalities',
    `context_window` INT UNSIGNED DEFAULT NULL COMMENT 'Context window size',
    `max_output_tokens` INT UNSIGNED DEFAULT NULL COMMENT 'Max output tokens',
    `pricing_input` DECIMAL(8,6) DEFAULT NULL COMMENT 'Input price per token',
    `pricing_output` DECIMAL(8,6) DEFAULT NULL COMMENT 'Output price per token',
    `capabilities` JSON DEFAULT NULL COMMENT 'Model capabilities',
    `default_params` JSON DEFAULT NULL COMMENT 'Default parameters',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled 1-enabled',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_id` (`model_id`),
    KEY `idx_provider_id` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Models';

-- User Provider Configs Table (User's API Keys)
CREATE TABLE IF NOT EXISTS `user_provider_configs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(64) NOT NULL COMMENT 'User ID',
    `provider_id` VARCHAR(64) NOT NULL COMMENT 'Provider ID',
    `config_name` VARCHAR(64) DEFAULT NULL COMMENT 'Config name',
    `api_key_encrypted` TEXT COMMENT 'Encrypted API key',
    `api_base_url` VARCHAR(512) DEFAULT NULL COMMENT 'Custom base URL',
    `default_model` VARCHAR(64) DEFAULT NULL COMMENT 'Default model',
    `config` JSON DEFAULT NULL COMMENT 'Provider-specific config',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT 'Is default config',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled 1-enabled',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_provider` (`user_id`, `provider_id`, `config_name`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User Provider Configs';
