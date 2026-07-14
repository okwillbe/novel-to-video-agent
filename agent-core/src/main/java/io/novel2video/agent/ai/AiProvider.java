package io.novel2video.agent.ai;

import java.util.List;
import java.util.Map;

/**
 * AI Provider 接口
 *
 * 定义所有 AI 服务提供商的统一接口
 */
public interface AiProvider {

    /**
     * 获取提供商名称
     */
    String getName();

    /**
     * 获取提供商类型: llm, image, video, voice
     */
    String getType();

    /**
     * 检查是否已配置（API Key 等）
     */
    boolean isConfigured();

    // ===== LLM 相关 =====

    /**
     * 聊天补全
     */
    default String chat(String model, List<Message> messages, Map<String, Object> options) {
        throw new UnsupportedOperationException("This provider does not support chat");
    }

    /**
     * 流式聊天补全
     */
    default void chatStream(String model, List<Message> messages, Map<String, Object> options, StreamHandler handler) {
        throw new UnsupportedOperationException("This provider does not support streaming chat");
    }

    // ===== 图片生成相关 =====

    /**
     * 生成图片
     * @return 图片 URL 列表
     */
    default List<String> generateImage(String model, String prompt, Map<String, Object> options) {
        throw new UnsupportedOperationException("This provider does not support image generation");
    }

    // ===== 视频生成相关 =====

    /**
     * 生成视频
     * @return 任务 ID 或视频 URL
     */
    default String generateVideo(String model, String prompt, Map<String, Object> options) {
        throw new UnsupportedOperationException("This provider does not support video generation");
    }

    /**
     * 查询视频生成状态
     */
    default VideoStatus getVideoStatus(String taskId) {
        throw new UnsupportedOperationException("This provider does not support video generation");
    }

    // ===== 语音生成相关 =====

    /**
     * 文本转语音
     * @return 音频 URL
     */
    default String textToSpeech(String model, String text, String voiceId, Map<String, Object> options) {
        throw new UnsupportedOperationException("This provider does not support TTS");
    }

    // ===== 内部类型 =====

    record Message(String role, String content) {
        public static Message user(String content) {
            return new Message("user", content);
        }
        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
        public static Message system(String content) {
            return new Message("system", content);
        }
    }

    interface StreamHandler {
        void onToken(String token);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }

    record VideoStatus(String taskId, String status, int progress, String videoUrl, String errorMessage) {}
}
