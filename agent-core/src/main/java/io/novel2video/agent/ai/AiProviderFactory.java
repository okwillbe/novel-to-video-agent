package io.novel2video.agent.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * AI Provider 工厂
 *
 * 管理所有 AI 服务提供商实例
 * **优先国产服务商**
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiProviderFactory {

    private final List<AiProvider> providers;

    // 国产 Provider 优先级排序
    private static final List<String> PRIORITY_ORDER = List.of(
            // 国产 LLM
            "deepseek",
            "moonshot",
            "doubao",
            "qwen",
            "yi",
            // 国产图片/视频
            "kling",
            "liblib",
            "jimeng",
            // 国产语音
            "fish-speech",
            "gpt-sovits",
            // 国外服务商（备选）
            "google",
            "openai",
            "anthropic",
            "fal",
            "elevenlabs"
    );

    /**
     * 获取指定名称的 Provider
     */
    public Optional<AiProvider> getProvider(String name) {
        return providers.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * 获取指定类型的 Provider（优先国产，返回第一个已配置的）
     */
    public Optional<AiProvider> getProviderByType(String type) {
        return PRIORITY_ORDER.stream()
                .map(name -> getProvider(name).orElse(null))
                .filter(p -> p != null && p.getType().equalsIgnoreCase(type) && p.isConfigured())
                .findFirst();
    }

    /**
     * 获取所有已配置的 Provider（按优先级排序）
     */
    public List<AiProvider> getConfiguredProviders() {
        return PRIORITY_ORDER.stream()
                .map(name -> getProvider(name).orElse(null))
                .filter(p -> p != null && p.isConfigured())
                .toList();
    }

    /**
     * 获取指定类型所有已配置的 Provider
     */
    public List<AiProvider> getConfiguredProvidersByType(String type) {
        return PRIORITY_ORDER.stream()
                .map(name -> getProvider(name).orElse(null))
                .filter(p -> p != null && p.getType().equalsIgnoreCase(type) && p.isConfigured())
                .toList();
    }

    /**
     * 获取所有 Provider 信息（用于前端展示）
     */
    public List<ProviderInfo> getProviderInfos() {
        return PRIORITY_ORDER.stream()
                .map(name -> getProvider(name).orElse(null))
                .filter(Objects::nonNull)
                .map(p -> new ProviderInfo(p.getName(), p.getType(), p.isConfigured(), isDomestic(p.getName())))
                .toList();
    }

    /**
     * 判断是否国产服务商
     */
    private boolean isDomestic(String name) {
        return List.of("deepseek", "moonshot", "doubao", "qwen", "yi", "kling", "liblib", "jimeng", "fish-speech", "gpt-sovits")
                .contains(name.toLowerCase());
    }

    public record ProviderInfo(String name, String type, boolean configured, boolean domestic) {}
}