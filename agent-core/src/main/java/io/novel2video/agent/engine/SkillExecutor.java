package io.novel2video.agent.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.novel2video.agent.ai.AiProvider;
import io.novel2video.agent.ai.AiProviderFactory;
import io.novel2video.agent.entity.Skill;
import io.novel2video.agent.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Skill 执行器
 *
 * 解析 Skill 的 Markdown + YAML frontmatter 格式，执行其中的逻辑
 * **默认使用国产 AI Provider**
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SkillExecutor {

    private final SkillService skillService;
    private final AiProviderFactory providerFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern FRONTMATTER_PATTERN = Pattern.compile(
            "^---\\s*\n([\\s\\S]*?)\n---\\s*\n([\\s\\S]*)$"
    );

    /**
     * 执行指定 Skill
     */
    public Object execute(String skillId, Map<String, Object> input) {
        String[] parts = skillId.split("_v");
        String name = parts[0];
        String version = parts.length > 1 ? "v" + parts[1] : "1.0.0";

        Skill skill = skillService.findBySkillIdAndVersion(name, version)
                .or(() -> skillService.findBySkillIdAndVersion(skillId, "1.0.0"))
                .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));

        return executeSkill(skill, input);
    }

    private Object executeSkill(Skill skill, Map<String, Object> input) {
        String content = skill.getSkillContent();

        Matcher matcher = FRONTMATTER_PATTERN.matcher(content);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid skill format: missing frontmatter");
        }

        String frontmatter = matcher.group(1);
        String template = matcher.group(2);

        Map<String, Object> metadata = parseFrontmatter(frontmatter);
        String prompt = renderTemplate(template, input);

        // 确定使用哪个 Provider（优先国产）
        AiProvider provider = selectProvider(metadata);
        String model = getString(metadata, "model", getDefaultModel(provider));

        log.info("Executing skill '{}' with provider '{}' model '{}'",
                skill.getSkillId(), provider.getName(), model);

        List<AiProvider.Message> messages = List.of(AiProvider.Message.user(prompt));

        String response = provider.chat(model, messages, Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 8192
        ));

        skillService.incrementUseCount(skill.getSkillId(), skill.getVersion(), true);

        return parseResponse(response);
    }

    /**
     * 选择 Provider（优先国产）
     */
    private AiProvider selectProvider(Map<String, Object> metadata) {
        String specifiedProvider = getString(metadata, "provider", null);

        if (specifiedProvider != null) {
            return providerFactory.getProvider(specifiedProvider)
                    .filter(AiProvider::isConfigured)
                    .orElseThrow(() -> new RuntimeException("Provider not configured: " + specifiedProvider));
        }

        // 自动选择：优先国产 LLM
        return providerFactory.getProviderByType("llm")
                .orElseThrow(() -> new RuntimeException("No LLM provider configured"));
    }

    /**
     * 获取默认模型
     */
    private String getDefaultModel(AiProvider provider) {
        return switch (provider.getName()) {
            case "deepseek" -> "deepseek-chat";
            case "moonshot" -> "moonshot-v1-8k";
            case "doubao" -> "doubao-pro-32k";
            case "qwen" -> "qwen-plus";
            case "google" -> "gemini-2.5-flash";
            case "openai" -> "gpt-4o-mini";
            default -> throw new RuntimeException("Unknown provider: " + provider.getName());
        };
    }

    private Map<String, Object> parseFrontmatter(String yaml) {
        Map<String, Object> result = new HashMap<>();
        for (String line : yaml.split("\n")) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                result.put(key, value);
            }
        }
        return result;
    }

    private String renderTemplate(String template, Map<String, Object> input) {
        String result = template;
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }

        Pattern conditionalPattern = Pattern.compile("\\{\\{#if\\s+(\\w+)\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}");
        Matcher matcher = conditionalPattern.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String content = matcher.group(2);
            Object value = input.get(varName);
            matcher.appendReplacement(sb, value != null && !"".equals(value.toString()) ? content : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Object parseResponse(String response) {
        String trimmed = response.trim();
        Pattern jsonBlock = Pattern.compile("```json\\s*\n([\\s\\S]*?)\n```");
        Matcher matcher = jsonBlock.matcher(trimmed);
        if (matcher.find()) {
            trimmed = matcher.group(1).trim();
        }

        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                return objectMapper.readValue(trimmed, Object.class);
            } catch (Exception ignored) {}
        }
        return trimmed;
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}