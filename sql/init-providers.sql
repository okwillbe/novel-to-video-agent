-- =====================================================
-- Novel to Video Agent - AI Providers (国产优先)
-- =====================================================

USE `novel2video`;

-- 清空并重新插入 Provider
DELETE FROM `ai_providers`;

-- ===== 国产 LLM =====
INSERT INTO `ai_providers` (`provider_id`, `name`, `provider_type`, `is_builtin`, `website`, `status`) VALUES
('deepseek', 'DeepSeek', 'llm', 1, 'https://www.deepseek.com/', 1),
('moonshot', 'Moonshot (Kimi)', 'llm', 1, 'https://moonshot.cn/', 1),
('doubao', 'Doubao (豆包)', 'llm', 1, 'https://www.volcengine.com/', 1),
('qwen', 'Qwen (通义千问)', 'llm', 1, 'https://tongyi.aliyun.com/', 1),
('yi', 'Yi (零一万物)', 'llm', 1, 'https://www.lingyiwanwu.com/', 1);

-- ===== 国产图片/视频 =====
INSERT INTO `ai_providers` (`provider_id`, `name`, `provider_type`, `is_builtin`, `website`, `status`) VALUES
('kling', 'Kling (可灵)', 'image', 1, 'https://klingai.com/', 1),
('liblib', 'LibLib (哩布哩布)', 'image', 1, 'https://www.liblib.art/', 1),
('jimeng', 'Jimeng (即梦)', 'image', 1, 'https://jimeng.jianying.com/', 1);

-- ===== 国产语音 =====
INSERT INTO `ai_providers` (`provider_id`, `name`, `provider_type`, `is_builtin`, `website`, `status`) VALUES
('fish-speech', 'Fish Speech', 'voice', 1, 'https://fish.audio/', 1),
('gpt-sovits', 'GPT-SoVITS', 'voice', 1, 'https://github.com/RVC-Boss/GPT-SoVITS', 1);

-- ===== 国外服务商（备选）=====
INSERT INTO `ai_providers` (`provider_id`, `name`, `provider_type`, `is_builtin`, `website`, `status`) VALUES
('google', 'Google AI', 'llm', 1, 'https://ai.google.dev/', 1),
('openai', 'OpenAI', 'llm', 1, 'https://openai.com/', 1),
('anthropic', 'Anthropic', 'llm', 1, 'https://www.anthropic.com/', 1),
('fal', 'FAL.AI', 'image', 1, 'https://fal.ai/', 1),
('elevenlabs', 'ElevenLabs', 'voice', 1, 'https://elevenlabs.io/', 1);

-- ===== Models =====
DELETE FROM `ai_models`;

-- DeepSeek 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `pricing_input`, `pricing_output`, `status`) VALUES
('deepseek-chat', 'deepseek', 'DeepSeek Chat', 'chat', 64000, 0.00000014, 0.00000028, 1),
('deepseek-reasoner', 'deepseek', 'DeepSeek Reasoner', 'chat', 64000, 0.00000055, 0.00000219, 1);

-- Moonshot 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `status`) VALUES
('moonshot-v1-8k', 'moonshot', 'Moonshot V1 8K', 'chat', 8192, 1),
('moonshot-v1-32k', 'moonshot', 'Moonshot V1 32K', 'chat', 32768, 1),
('moonshot-v1-128k', 'moonshot', 'Moonshot V1 128K', 'chat', 131072, 1);

-- Doubao 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `status`) VALUES
('doubao-pro-32k', 'doubao', 'Doubao Pro 32K', 'chat', 32768, 1),
('doubao-pro-128k', 'doubao', 'Doubao Pro 128K', 'chat', 131072, 1),
('doubao-lite-4k', 'doubao', 'Doubao Lite 4K', 'chat', 4096, 1);

-- Qwen 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `status`) VALUES
('qwen-max', 'qwen', 'Qwen Max', 'chat', 32768, 1),
('qwen-plus', 'qwen', 'Qwen Plus', 'chat', 131072, 1),
('qwen-turbo', 'qwen', 'Qwen Turbo', 'chat', 131072, 1),
('qwen-vl-max', 'qwen', 'Qwen VL Max (视觉)', 'chat', 32768, 1);

-- Kling 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `status`) VALUES
('kling-image-v1', 'kling', 'Kling Image v1', 'image', 1),
('kling-video-v1', 'kling', 'Kling Video v1', 'video', 1),
('kling-video-pro', 'kling', 'Kling Video Pro', 'video', 1);

-- Fish Speech 模型
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `status`) VALUES
('fish-speech-v1', 'fish-speech', 'Fish Speech v1', 'voice', 1);

-- Google 模型（备选）
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `status`) VALUES
('gemini-2.5-flash', 'google', 'Gemini 2.5 Flash', 'chat', 1000000, 1);

-- OpenAI 模型（备选）
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `context_window`, `status`) VALUES
('gpt-4o-mini', 'openai', 'GPT-4o Mini', 'chat', 128000, 1);

-- FAL 模型（备选）
INSERT INTO `ai_models` (`model_id`, `provider_id`, `name`, `model_type`, `status`) VALUES
('fal-flux-schnell', 'fal', 'FLUX.1 Schnell', 'image', 1);