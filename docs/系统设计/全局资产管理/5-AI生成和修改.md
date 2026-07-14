# AI 生成和修改设计文档

## 1. 接口总览

| 接口 | 类型 | 输入 | 输出 | 功能 |
|------|------|------|------|------|
| generate-image | 图片生成 | 描述词 + 风格 | 图片 URL | 根据描述词生成图片 |
| modify-image | 图片修改 | 图片 + 修改指令 | 图片 URL | 局部编辑/重绘图片 |
| ai-design-character | 文本生成 | 用户指令 | 角色描述词 | AI 生成角色描述 |
| ai-design-location | 文本生成 | 用户指令 | 场景描述词 | AI 生成场景描述 |
| ai-modify-character | 文本修改 | 当前描述 + 指令 | 新描述词 | AI 修改角色描述 |
| ai-modify-location | 文本修改 | 当前描述 + 指令 | 新描述词 | AI 修改场景描述 |

---

## 2. 架构设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                        前端调用                                      │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Controller Layer                                │
│  ────────────────────────────────────────────────────────────────   │
│  /api/v1/assets/*                                                    │
│  • 参数校验 (@Valid)                                                 │
│  • 权限验证                                                          │
│  • 提交任务                                                          │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│              Task Queue (Redis Streams + Spring Integration)          │
│  ────────────────────────────────────────────────────────────────   │
│  • 任务队列管理                                                       │
│  • 去重控制 (Redis Set)                                              │
│  • 任务状态追踪                                                       │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Worker Handler Layer                            │
│  ────────────────────────────────────────────────────────────────   │
│  • handleAssetHubImageTask()        - 图片生成                       │
│  • handleAssetHubModifyTask()       - 图片修改                       │
│  • handleAssetHubAIDesignTask()     - AI 设计描述词                  │
│  • handleAssetHubAIModifyTask()     - AI 修改描述词                  │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      AI Service Layer                                │
│  ────────────────────────────────────────────────────────────────   │
│  • 图片模型 (Fal / Kling)                                           │
│  • 分析模型 (Google Gemini / DeepSeek)                               │
│  • 流式响应 (WebSocket / SSE)                                        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Storage Layer (MinIO)                           │
│  • 图片存储                                                          │
│  • 黑边标签处理                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. 接口详细设计

### 3.1 generate-image（图片生成）

**REST API:**

```
POST /api/v1/assets/generate-image
```

**Request DTO:**

```java
@Data
public class GenerateImageRequest {
    
    @NotBlank
    private String type;  // "character" | "location"
    
    @NotBlank
    private String id;    // 资产ID
    
    private Integer appearanceIndex;  // 角色形象序号（仅角色）
    
    private String artStyle;  // 艺术风格（可选）
    
    private Integer count = 4;  // 生成数量，默认4
}
```

**Response DTO:**

```java
@Data
@AllArgsConstructor
public class GenerateImageResponse {
    private String taskId;
    private String status;  // "queued" | "processing" | "completed" | "failed"
}
```

**处理流程:**

```java
@Service
@RequiredArgsConstructor
public class ImageGenerateService {

    private final CharacterService characterService;
    private final LocationService locationService;
    private final TaskService taskService;
    private final AIProviderFactory aiProviderFactory;
    private final MinioService minioService;

    @Transactional
    public String generateImage(String userId, GenerateImageRequest request) {
        // 1. 读取资产信息
        AssetInfo asset = getAssetInfo(userId, request);
        
        // 2. 构建提示词
        String prompt = buildPrompt(asset, request.getArtStyle());
        
        // 3. 获取模型配置
        String modelId = getModelForType(request.getType(), userId);
        
        // 4. 创建任务
        Task task = taskService.createTask(Task.builder()
            .taskType(TaskType.ASSET_HUB_IMAGE)
            .targetType(getTargetType(request.getType()))
            .targetId(request.getId())
            .userId(userId)
            .inputData(Map.of(
                "prompt", prompt,
                "count", request.getCount(),
                "aspectRatio", getAspectRatio(request.getType()),
                "modelId", modelId
            ))
            .build());
        
        // 5. 提交到 Redis Streams
        taskService.submitToQueue(task);
        
        return task.getTaskId();
    }

    /**
     * 获取资产信息
     */
    private AssetInfo getAssetInfo(String userId, GenerateImageRequest request) {
        if ("character".equals(request.getType())) {
            GlobalCharacter character = characterService.getCharacterWithAppearances(request.getId());
            GlobalCharacterAppearance appearance = character.getAppearances()
                .get(request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0);
            return new AssetInfo(
                character.getName(),
                appearance.getDescriptions() != null ? appearance.getDescriptions() : List.of(appearance.getDescription()),
                appearance.getArtStyle()
            );
        } else {
            GlobalLocation location = locationService.getLocation(request.getId());
            return new AssetInfo(
                location.getName(),
                List.of(location.getSummary()),
                location.getArtStyle()
            );
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(AssetInfo asset, String artStyle) {
        StringBuilder prompt = new StringBuilder();
        
        // 用户描述词
        String description = String.join("，", asset.getDescriptions());
        prompt.append(description);
        
        // 系统后缀
        if ("character".equals(asset.getType())) {
            prompt.append("，").append(CHARACTER_PROMPT_SUFFIX);
        } else {
            prompt.append("，").append(LOCATION_PROMPT_SUFFIX);
        }
        
        // 艺术风格
        if (artStyle != null && !artStyle.isEmpty()) {
            prompt.append("，").append(artStyle);
        } else if (asset.getArtStyle() != null) {
            prompt.append("，").append(asset.getArtStyle());
        }
        
        return prompt.toString();
    }

    private static final String CHARACTER_PROMPT_SUFFIX = 
        "角色设定图，全身像，正面视角，清晰的面部特征，三视图，高质量，详细细节";
    
    private static final String LOCATION_PROMPT_SUFFIX = 
        "场景设定图，全景视角，环境细节丰富，高质量，详细细节";

    private String getAspectRatio(String type) {
        return "character".equals(type) ? "3:2" : "1:1";
    }

    private String getModelForType(String type, String userId) {
        // 从用户配置获取模型
        UserProviderConfig config = userConfigService.getProviderConfig(userId, "image");
        return "character".equals(type) 
            ? config.getDefaultCharacterModel() 
            : config.getDefaultLocationModel();
    }
}
```

**任务类型:**

```java
public enum TaskType {
    NOVEL_TO_VIDEO,
    ASSET_HUB_IMAGE,       // 图片生成
    ASSET_HUB_MODIFY,      // 图片修改
    ASSET_HUB_AI_DESIGN,   // AI 设计描述词
    ASSET_HUB_AI_MODIFY    // AI 修改描述词
}

public enum TargetType {
    GLOBAL_CHARACTER,
    GLOBAL_LOCATION,
    GLOBAL_CHARACTER_APPEARANCE,
    GLOBAL_LOCATION_IMAGE
}
```

---

### 3.2 modify-image（图片修改）

**REST API:**

```
POST /api/v1/assets/modify-image
```

**Request DTO:**

```java
@Data
public class ModifyImageRequest {
    
    @NotBlank
    private String type;  // "character" | "location"
    
    @NotBlank
    private String id;    // 资产ID
    
    private Integer appearanceIndex;  // 角色形象序号
    
    private Integer imageIndex;  // 图片索引
    
    @NotBlank
    private String modifyPrompt;  // 修改指令
    
    private List<String> extraImageUrls;  // 参考图（可选）
}
```

**处理流程:**

```java
@Service
@RequiredArgsConstructor
public class ImageModifyService {

    private final CharacterService characterService;
    private final LocationService locationService;
    private final TaskService taskService;
    private final MinioService minioService;
    private final AIProviderFactory aiProviderFactory;

    public String modifyImage(String userId, ModifyImageRequest request) {
        // 1. 获取目标图片
        String currentImageUrl = getCurrentImageUrl(userId, request);
        
        // 2. 准备参考图
        List<String> referenceImages = prepareReferenceImages(currentImageUrl, request.getExtraImageUrls());
        
        // 3. 构建修改提示词
        String prompt = buildModifyPrompt(request.getType(), request.getModifyPrompt());
        
        // 4. 获取编辑模型
        String editModelId = getEditModelId(userId);
        
        // 5. 创建任务
        Task task = taskService.createTask(Task.builder()
            .taskType(TaskType.ASSET_HUB_MODIFY)
            .targetType(getModifyTargetType(request.getType()))
            .targetId(buildTargetId(request))
            .userId(userId)
            .inputData(Map.of(
                "prompt", prompt,
                "referenceImages", referenceImages,
                "modelId", editModelId
            ))
            .build());
        
        // 6. 提交到队列
        taskService.submitToQueue(task);
        
        return task.getTaskId();
    }

    /**
     * 获取当前图片 URL
     */
    private String getCurrentImageUrl(String userId, ModifyImageRequest request) {
        if ("character".equals(request.getType())) {
            GlobalCharacter character = characterService.getCharacterWithAppearances(request.getId());
            GlobalCharacterAppearance appearance = character.getAppearatures()
                .get(request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0);
            return appearance.getImageUrls().get(request.getImageIndex() != null ? request.getImageIndex() : 0);
        } else {
            GlobalLocation location = locationService.getLocation(request.getId());
            GlobalLocationImage image = location.getImages().get(request.getImageIndex() != null ? request.getImageIndex() : 0);
            return image.getImageUrl();
        }
    }

    /**
     * 准备参考图
     */
    private List<String> prepareReferenceImages(String currentUrl, List<String> extraUrls) {
        List<String> references = new ArrayList<>();
        
        // 当前图片（去除黑边标签）
        references.add(minioService.stripLabelBar(currentUrl));
        
        // 额外参考图
        if (extraUrls != null && !extraUrls.isEmpty()) {
            references.addAll(extraUrls);
        }
        
        return references;
    }

    /**
     * 构建修改提示词
     */
    private String buildModifyPrompt(String type, String modifyPrompt) {
        if ("character".equals(type)) {
            return "请根据以下指令修改图片，保持人物核心特征一致：\n" + modifyPrompt;
        } else {
            return "请根据以下指令修改场景图片，保持整体风格一致：\n" + modifyPrompt;
        }
    }

    private String buildTargetId(ModifyImageRequest request) {
        return String.format("%s:%d:%d", 
            request.getId(),
            request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0,
            request.getImageIndex() != null ? request.getImageIndex() : 0);
    }
}
```

---

### 3.3 ai-design-character（AI 设计角色描述）

**REST API:**

```
POST /api/v1/assets/ai-design-character
```

**Request DTO:**

```java
@Data
public class AIDesignCharacterRequest {
    
    @NotBlank
    private String userInstruction;  // 用户设计指令
}
```

**Response DTO:**

```java
@Data
@AllArgsConstructor
public class AIDesignCharacterResponse {
    private String prompt;  // 生成的角色描述词
}
```

**Service 实现:**

```java
@Service
@RequiredArgsConstructor
public class AIDesignService {

    private final AIProviderFactory aiProviderFactory;
    private final PromptTemplateService promptTemplateService;
    private final TaskService taskService;

    /**
     * AI 设计角色描述
     */
    public AIDesignCharacterResponse designCharacter(String userId, AIDesignCharacterRequest request) {
        // 1. 构建提示词
        String prompt = promptTemplateService.buildPrompt(PromptTemplate.builder()
            .promptId("NP_CHARACTER_CREATE")
            .locale("zh")
            .variables(Map.of("user_input", request.getUserInstruction()))
            .build());
        
        // 2. 获取分析模型
        AiProvider provider = aiProviderFactory.getProvider("analysis");
        
        // 3. 调用 LLM
        AiCompletion completion = provider.complete(AiCompletionRequest.builder()
            .model(provider.getDefaultModel())
            .messages(List.of(new AiMessage("user", prompt)))
            .temperature(0.7)
            .build());
        
        // 4. 解析响应
        String responseText = completion.getText();
        Map<String, Object> parsed = parseJsonResponse(responseText);
        
        return new AIDesignCharacterResponse((String) parsed.get("prompt"));
    }

    /**
     * AI 设计场景描述
     */
    public AIDesignLocationResponse designLocation(String userId, AIDesignLocationRequest request) {
        String prompt = promptTemplateService.buildPrompt(PromptTemplate.builder()
            .promptId("NP_LOCATION_CREATE")
            .locale("zh")
            .variables(Map.of("user_input", request.getUserInstruction()))
            .build());
        
        AiProvider provider = aiProviderFactory.getProvider("analysis");
        AiCompletion completion = provider.complete(AiCompletionRequest.builder()
            .model(provider.getDefaultModel())
            .messages(List.of(new AiMessage("user", prompt)))
            .temperature(0.7)
            .build());
        
        Map<String, Object> parsed = parseJsonResponse(completion.getText());
        return new AIDesignLocationResponse((String) parsed.get("prompt"));
    }

    /**
     * AI 修改角色描述
     */
    public AIModifyCharacterResponse modifyCharacter(String userId, AIModifyCharacterRequest request) {
        // 移除系统后缀
        String cleanDescription = removeCharacterPromptSuffix(request.getCurrentDescription());
        
        // 构建修改提示词
        String prompt = promptTemplateService.buildPrompt(PromptTemplate.builder()
            .promptId("NP_CHARACTER_MODIFY")
            .locale("zh")
            .variables(Map.of(
                "character_input", cleanDescription,
                "user_input", request.getModifyInstruction()
            ))
            .build());
        
        AiProvider provider = aiProviderFactory.getProvider("analysis");
        AiCompletion completion = provider.complete(AiCompletionRequest.builder()
            .model(provider.getDefaultModel())
            .messages(List.of(new AiMessage("user", prompt)))
            .build());
        
        String modifiedDescription = parseJsonPrompt(completion.getText());
        return new AIModifyCharacterResponse(modifiedDescription);
    }

    private String removeCharacterPromptSuffix(String description) {
        return description.replace(CHARACTER_PROMPT_SUFFIX, "").trim();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonResponse(String text) {
        try {
            return new ObjectMapper().readValue(text, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String parseJsonPrompt(String text) {
        Map<String, Object> parsed = parseJsonResponse(text);
        return (String) parsed.get("prompt");
    }
}
```

**提示词模板:**

```java
@Service
@RequiredArgsConstructor
public class PromptTemplateService {

    private final MessageSource messageSource;

    public String buildPrompt(PromptTemplate template) {
        String templateText = messageSource.getMessage(
            template.getPromptId(), 
            null, 
            Locale.forLanguageTag(template.getLocale())
        );
        
        // 替换变量
        String result = templateText;
        for (Map.Entry<String, String> entry : template.getVariables().entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return result;
    }
}
```

**提示词配置 (messages_zh.properties):**

```properties
# 角色创建
NP_CHARACTER_CREATE=请根据以下用户指令，生成角色外观描述词：\n\n用户指令：{{user_input}}\n\n请返回 JSON 格式：\n{\n  "prompt": "角色描述词..."\n}

# 场景创建
NP_LOCATION_CREATE=请根据以下用户指令，生成场景外观描述词：\n\n用户指令：{{user_input}}\n\n请返回 JSON 格式：\n{\n  "prompt": "场景描述词..."\n}

# 角色修改
NP_CHARACTER_MODIFY=请根据以下指令修改角色描述：\n\n当前描述：{{character_input}}\n\n修改指令：{{user_input}}\n\n请返回 JSON 格式：\n{\n  "prompt": "修改后的角色描述词..."\n}

# 场景修改
NP_LOCATION_MODIFY=请根据以下指令修改场景描述：\n\n当前描述：{{location_input}}\n\n修改指令：{{user_input}}\n\n请返回 JSON 格式：\n{\n  "prompt": "修改后的场景描述词..."\n}
```

---

## 4. Worker 处理逻辑

### 4.1 图片生成 Worker

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageGenerateWorker {

    private final TaskService taskService;
    private final CharacterService characterService;
    private final LocationService locationService;
    private final AIProviderFactory aiProviderFactory;
    private final MinioService minioService;
    private final SSEService sseService;

    @Scheduled(fixedDelay = 1000)
    public void processImageTasks() {
        List<Task> tasks = taskService.pollTasks(TaskType.ASSET_HUB_IMAGE, 10);
        
        for (Task task : tasks) {
            try {
                handleImageTask(task);
            } catch (Exception e) {
                log.error("Failed to process image task: {}", task.getTaskId(), e);
                taskService.failTask(task.getTaskId(), e.getMessage());
            }
        }
    }

    private void handleImageTask(Task task) {
        String userId = task.getUserId();
        Map<String, Object> input = task.getInputData();
        
        String type = (String) input.get("type");
        String targetId = task.getTargetId();
        int count = (int) input.getOrDefault("count", 4);
        String prompt = (String) input.get("prompt");
        String aspectRatio = (String) input.get("aspectRatio");
        String modelId = (String) input.get("modelId");
        
        // 获取 AI Provider
        AiProvider provider = aiProviderFactory.getProvider(modelId);
        
        List<String> generatedUrls = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // 更新进度
            int progress = (i + 1) * 100 / count;
            taskService.updateProgress(task.getTaskId(), progress);
            sseService.pushTaskProgress(userId, task.getTaskId(), progress, "processing");
            
            // 生成图片
            AiImageResponse response = provider.generateImage(AiImageRequest.builder()
                .prompt(prompt)
                .aspectRatio(aspectRatio)
                .build());
            
            // 添加黑边标签
            byte[] labeledImage = minioService.addLabelBar(response.getImageData(), type);
            
            // 上传到 MinIO
            String url = minioService.upload(labeledImage, 
                "assets/" + userId + "/" + type + "/" + targetId + "/" + UUID.randomUUID() + ".png");
            
            generatedUrls.add(url);
        }
        
        // 更新数据库
        updateAssetImageUrls(type, targetId, generatedUrls);
        
        // 完成任务
        taskService.completeTask(task.getTaskId());
        sseService.pushImageGenerated(userId, targetId, generatedUrls);
    }

    private void updateAssetImageUrls(String type, String targetId, List<String> urls) {
        if ("character".equals(type)) {
            GlobalCharacterAppearance appearance = characterService.getDefaultAppearance(targetId);
            appearance.setImageUrls(urls);
            appearance.setSelectedIndex(0);
            characterService.updateAppearance(appearance);
        } else {
            GlobalLocation location = locationService.getLocation(targetId);
            location.setImageUrl(urls.get(0));
            locationService.updateLocation(location);
        }
    }
}
```

### 4.2 图片修改 Worker

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageModifyWorker {

    private final TaskService taskService;
    private final AIProviderFactory aiProviderFactory;
    private final MinioService minioService;
    private final SSEService sseService;

    @Scheduled(fixedDelay = 1000)
    public void processModifyTasks() {
        List<Task> tasks = taskService.pollTasks(TaskType.ASSET_HUB_MODIFY, 10);
        
        for (Task task : tasks) {
            try {
                handleModifyTask(task);
            } catch (Exception e) {
                log.error("Failed to process modify task: {}", task.getTaskId(), e);
                taskService.failTask(task.getTaskId(), e.getMessage());
            }
        }
    }

    private void handleModifyTask(Task task) {
        String userId = task.getUserId();
        Map<String, Object> input = task.getInputData();
        
        String prompt = (String) input.get("prompt");
        @SuppressWarnings("unchecked")
        List<String> referenceImages = (List<String>) input.get("referenceImages");
        String modelId = (String) input.get("modelId");
        
        // 获取编辑模型
        AiProvider provider = aiProviderFactory.getProvider(modelId);
        
        // 调用图片编辑
        AiEditResponse response = provider.editImage(AiEditRequest.builder()
            .prompt(prompt)
            .referenceImages(referenceImages)
            .build());
        
        // 添加新黑边标签
        byte[] labeledImage = minioService.addLabelBar(response.getImageData(), "character");
        
        // 上传到 MinIO
        String newUrl = minioService.upload(labeledImage,
            "assets/" + userId + "/modified/" + UUID.randomUUID() + ".png");
        
        // 保存撤销数据
        saveUndoData(task.getTargetId(), newUrl);
        
        // 完成任务
        taskService.completeTask(task.getTaskId());
        sseService.pushTaskProgress(userId, task.getTaskId(), 100, "completed");
    }

    private void saveUndoData(String targetId, String newUrl) {
        String[] parts = targetId.split(":");
        String characterId = parts[0];
        int appearanceIndex = Integer.parseInt(parts[1]);
        int imageIndex = Integer.parseInt(parts[2]);
        
        GlobalCharacterAppearance appearance = characterService.getAppearanceByIndex(characterId, appearanceIndex);
        
        // 保存当前状态
        appearance.setPreviousImageUrls(appearance.getImageUrls());
        appearance.setPreviousDescription(appearance.getDescription());
        
        // 更新新图片
        List<String> urls = new ArrayList<>(appearance.getImageUrls());
        urls.set(imageIndex, newUrl);
        appearance.setImageUrls(urls);
        
        characterService.updateAppearance(appearance);
    }
}
```

---

## 5. 任务队列设计

### 5.1 Redis Streams 实现

```java
@Service
@RequiredArgsConstructor
public class TaskQueueService {

    private final StringRedisTemplate redisTemplate;
    private final TaskService taskService;
    
    private static final String STREAM_KEY = "task:queue";
    private static final String DEDUPE_SET_KEY = "task:dedupe";

    /**
     * 提交任务到队列
     */
    public void submitTask(Task task) {
        // 1. 生成去重键
        String dedupeKey = generateDedupeKey(task);
        
        // 2. 检查去重
        Boolean isNew = redisTemplate.opsForSet().add(DEDUPE_SET_KEY, dedupeKey) > 0;
        if (!isNew) {
            log.warn("Duplicate task rejected: {}", dedupeKey);
            throw new DuplicateTaskException("Task already in progress");
        }
        
        // 3. 设置去重键过期时间
        redisTemplate.expire(DEDUPE_SET_KEY, Duration.ofHours(1));
        
        // 4. 添加到 Redis Stream
        Map<String, String> message = new HashMap<>();
        message.put("taskId", task.getTaskId());
        message.put("type", task.getTaskType().name());
        message.put("dedupeKey", dedupeKey);
        
        redisTemplate.opsForStream().add(STREAM_KEY, message);
        
        log.info("Task submitted: {}", task.getTaskId());
    }

    /**
     * 轮询任务
     */
    public List<Task> pollTasks(TaskType type, int count) {
        // 从数据库获取待处理任务
        return taskService.findPendingTasks(type, count);
    }

    /**
     * 任务完成，清理去重键
     */
    public void completeTask(String taskId, String dedupeKey) {
        taskService.completeTask(taskId);
        redisTemplate.opsForSet().remove(DEDUPE_SET_KEY, dedupeKey);
    }

    private String generateDedupeKey(Task task) {
        switch (task.getTaskType()) {
            case ASSET_HUB_IMAGE:
                return String.format("asset_hub_image:%s:%s:%d",
                    task.getTargetType(), task.getTargetId(), 
                    task.getInputData().get("count"));
            case ASSET_HUB_MODIFY:
                return String.format("asset_hub_modify:%s", task.getTargetId());
            case ASSET_HUB_AI_DESIGN:
                return String.format("asset_hub_ai_design:%s:%s",
                    task.getUserId(), DigestUtils.sha1Hex(
                        String.valueOf(task.getInputData().get("userInstruction"))));
            default:
                return task.getTaskId();
        }
    }
}
```

### 5.2 任务恢复机制

```java
@Service
@RequiredArgsConstructor
public class TaskRecoveryService {

    private final TaskService taskService;
    private final ExternalApiService externalApiService;

    /**
     * 恢复中断的任务
     */
    @Scheduled(fixedDelay = 60000)
    public void recoverInterruptedTasks() {
        // 查找处理中的任务（可能因服务重启中断）
        List<Task> processingTasks = taskService.findProcessingTasks(Duration.ofMinutes(5));
        
        for (Task task : processingTasks) {
            if (task.getExternalId() != null) {
                // 有外部ID，恢复轮询
                recoverExternalTask(task);
            } else {
                // 没有外部ID，重新处理
                retryTask(task);
            }
        }
    }

    private void recoverExternalTask(Task task) {
        String externalId = task.getExternalId();
        ExternalTaskResult result = externalApiService.pollResult(externalId);
        
        if (result != null) {
            // 外部任务完成，处理结果
            handleExternalResult(task, result);
        } else {
            // 继续等待
            log.info("Waiting for external task: {}", externalId);
        }
    }

    private void retryTask(Task task) {
        // 重置任务状态为待处理
        taskService.resetTask(task.getTaskId());
        log.info("Reset task for retry: {}", task.getTaskId());
    }
}
```

---

## 6. MinIO 存储处理

### 6.1 黑边标签处理

```java
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 上传图片
     */
    public String upload(byte[] data, String path) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .stream(new ByteArrayInputStream(data), data.length, -1)
                .contentType("image/png")
                .build());
            
            return getPublicUrl(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO", e);
        }
    }

    /**
     * 添加黑边标签
     */
    public byte[] addLabelBar(byte[] imageData, String type) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            int width = image.getWidth();
            int height = image.getHeight();
            
            // 标签高度（约占图片高度 5%）
            int barHeight = Math.max(20, height / 20);
            
            // 创建带黑边的新图片
            BufferedImage labeledImage = new BufferedImage(width, height + barHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = labeledImage.createGraphics();
            
            // 绘制原图
            g.drawImage(image, 0, 0, null);
            
            // 绘制黑边标签
            g.setColor(Color.BLACK);
            g.fillRect(0, height, width, barHeight);
            
            // 添加文字
            g.setColor(Color.WHITE);
            g.setFont(new Font("Microsoft YaHei", Font.PLAIN, barHeight - 4));
            String label = "character".equals(type) ? "角色设定" : "场景设定";
            g.drawString(label, 10, height + barHeight - 5);
            
            g.dispose();
            
            // 转换为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(labeledImage, "png", out);
            return out.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to add label bar", e);
        }
    }

    /**
     * 去除黑边标签
     */
    public String stripLabelBar(String imageUrl) {
        try {
            // 下载图片
            String objectName = extractObjectName(imageUrl);
            InputStream in = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
            
            BufferedImage image = ImageIO.read(in);
            
            // 检测并裁剪黑边
            int cropHeight = detectLabelBarHeight(image);
            if (cropHeight > 0) {
                BufferedImage cropped = image.getSubimage(0, 0, image.getWidth(), image.getHeight() - cropHeight);
                
                // 重新上传
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(cropped, "png", out);
                
                String newPath = objectName.replace(".png", "_stripped.png");
                upload(out.toByteArray(), newPath);
                return getPublicUrl(newPath);
            }
            
            return imageUrl;
            
        } catch (Exception e) {
            log.warn("Failed to strip label bar, returning original: {}", imageUrl);
            return imageUrl;
        }
    }

    private int detectLabelBarHeight(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        
        // 从底部向上检测黑色区域
        for (int y = height - 1; y >= height - 50; y--) {
            int rgb = image.getRGB(width / 2, y);
            if (rgb != Color.BLACK.getRGB()) {
                return height - y - 1;
            }
        }
        return 0;
    }
}
```

---

## 7. Controller 实现

```java
@Tag(name = "Asset AI API", description = "资产 AI 生成和修改")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetAIController {

    private final ImageGenerateService imageGenerateService;
    private final ImageModifyService imageModifyService;
    private final AIDesignService aiDesignService;
    private final UserService userService;

    private String getDefaultUserId() {
        return userService.getOrCreateDefaultUser().getUserId();
    }

    // ==================== 图片生成 ====================

    @Operation(summary = "生成图片")
    @PostMapping("/generate-image")
    public ResponseEntity<ApiResponse<GenerateImageResponse>> generateImage(
            @RequestBody @Valid GenerateImageRequest request) {
        String userId = getDefaultUserId();
        String taskId = imageGenerateService.generateImage(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
            new GenerateImageResponse(taskId, "queued")));
    }

    // ==================== 图片修改 ====================

    @Operation(summary = "修改图片")
    @PostMapping("/modify-image")
    public ResponseEntity<ApiResponse<ModifyImageResponse>> modifyImage(
            @RequestBody @Valid ModifyImageRequest request) {
        String userId = getDefaultUserId();
        String taskId = imageModifyService.modifyImage(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
            new ModifyImageResponse(taskId, "queued")));
    }

    // ==================== AI 设计 ====================

    @Operation(summary = "AI 设计角色描述")
    @PostMapping("/ai-design-character")
    public ResponseEntity<ApiResponse<AIDesignCharacterResponse>> designCharacter(
            @RequestBody @Valid AIDesignCharacterRequest request) {
        String userId = getDefaultUserId();
        AIDesignCharacterResponse response = aiDesignService.designCharacter(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 设计场景描述")
    @PostMapping("/ai-design-location")
    public ResponseEntity<ApiResponse<AIDesignLocationResponse>> designLocation(
            @RequestBody @Valid AIDesignLocationRequest request) {
        String userId = getDefaultUserId();
        AIDesignLocationResponse response = aiDesignService.designLocation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== AI 修改 ====================

    @Operation(summary = "AI 修改角色描述")
    @PostMapping("/ai-modify-character")
    public ResponseEntity<ApiResponse<AIModifyCharacterResponse>> modifyCharacter(
            @RequestBody @Valid AIModifyCharacterRequest request) {
        String userId = getDefaultUserId();
        AIModifyCharacterResponse response = aiDesignService.modifyCharacter(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 修改场景描述")
    @PostMapping("/ai-modify-location")
    public ResponseEntity<ApiResponse<AIModifyLocationResponse>> modifyLocation(
            @RequestBody @Valid AIModifyLocationRequest request) {
        String userId = getDefaultUserId();
        AIModifyLocationResponse response = aiDesignService.modifyLocation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

---

## 8. 模型配置

```java
@Data
@Entity
@TableName("user_model_configs")
public class UserModelConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userId;
    
    /**
     * 角色图片模型 ID
     */
    private String characterModel;
    
    /**
     * 场景图片模型 ID
     */
    private String locationModel;
    
    /**
     * 图片编辑模型 ID
     */
    private String editModel;
    
    /**
     * 分析模型 ID（LLM）
     */
    private String analysisModel;
}
```

| 操作类型 | 模型配置字段 | 说明 |
|----------|-------------|------|
| 角色图片生成 | characterModel | 角色专用图片模型 |
| 场景图片生成 | locationModel | 场景专用图片模型 |
| 图片编辑 | editModel | 局部重绘模型 |
| 描述词生成/修改 | analysisModel | LLM 分析模型 |

---

## 9. 流程图

### 9.1 图片生成流程

```
用户点击"生成图片"
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  POST /api/v1/assets/generate-image                         │
│  { type, id, count }                                        │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  submitTask()                                               │
│  • 校验参数                                                  │
│  • 生成 dedupeKey                                           │
│  • 提交到 Redis Streams                                      │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  Worker: handleImageTask()                                  │
│  ─────────────────────────────────────────────────────────── │
│  1. 读取资产 + 描述词                                         │
│  2. 获取艺术风格                                              │
│  3. for i < count:                                           │
│     ├── prompt = buildPrompt(descriptions[i])               │
│     ├── 调用图片模型                           │
│     ├── 添加黑边标签                                          │
│     └── 上传到 MinIO                                         │
│  4. 更新数据库 imageUrls                                      │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
WebSocket/SSE 推送进度 → 前端更新 UI
```

### 9.2 图片修改流程

```
用户选择图片 + 输入修改指令
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  POST /api/v1/assets/modify-image                           │
│  { type, id, imageIndex, modifyPrompt, extraImageUrls }     │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  Worker: handleModifyTask()                                 │
│  ─────────────────────────────────────────────────────────── │
│  1. 获取当前图片 URL                                          │
│  2. stripLabelBar() 去除黑边                                 │
│  3. 合并参考图：[当前图片, ...extraImageUrls]                  │
│  4. 调用编辑模型                            │
│  5. 添加新黑边标签                                            │
│  6. 上传到 MinIO                                             │
│  7. 保存撤销数据到 previous* 字段                             │
│  8. [可选] 同步更新描述词                                     │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
返回新图片 URL
```

### 9.3 AI 描述词生成流程

```
用户输入设计指令（如"一个古代女侠"）
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  POST /api/v1/assets/ai-design-character                    │
│  { userInstruction }                                        │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  Service: designCharacter()                                 │
│  ─────────────────────────────────────────────────────────── │
│  1. 构建提示词（i18n 模板）                                   │
│  2. 调用 analysisModel (Gemini/DeepSeek)                     │
│  3. 解析 JSON 响应                                           │
│  4. 返回 { prompt: "..." }                                   │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
前端显示描述词，用户可编辑确认
```

---

## 10. 前端调用示例

### 10.1 图片生成

```typescript
// 提交生成任务
const result = await api.post('/api/v1/assets/generate-image', {
  type: 'character',
  id: characterId,
  count: 4,
  artStyle: '古风'
})

// WebSocket 订阅进度
const socket = new WebSocket('/ws')
socket.onmessage = (event) => {
  const data = JSON.parse(event.data)
  if (data.type === 'IMAGE_GENERATED') {
    // 更新角色形象的 imageUrls
    updateCharacterAppearance(data.characterId, data.imageUrls)
  }
}
```

### 10.2 图片修改

```typescript
// 提交修改
await api.post('/api/v1/assets/modify-image', {
  type: 'character',
  id: characterId,
  appearanceIndex: 0,
  imageIndex: 2,
  modifyPrompt: '把衣服改成蓝色',
  extraImageUrls: []
})
```

### 10.3 AI 设计

```typescript
// 触发 AI 设计
const result = await api.post('/api/v1/assets/ai-design-character', {
  userInstruction: '一个穿红衣服的古代女侠'
})

// 获取描述词
const { prompt } = result.data

// 用户可编辑后保存
setDescription(prompt)
```

---

## 11. 总结

| 接口 | 核心原理 | 关键步骤 |
|------|----------|----------|
| generate-image | 提示词 + 图片模型 | 描述词 → 系统后缀 → 风格 → AI生成 → 黑边标签 → 存储 |
| modify-image | 参考图 + 编辑模型 | 当前图 → 参考图合并 → AI编辑 → 标签 → 存储 → 撤销支持 |
| ai-design-* | LLM + i18n模板 | 用户指令 → LLM提示词 → 结构化输出 |
| ai-modify-* | LLM + i18n模板 | 当前描述 + 指令 → LLM修改 → 新描述 |

**设计亮点：**

- 图片生成与描述词生成分离，用户可先设计描述词再生成图片
- 图片修改支持撤销，保存 previous* 字段
- 任务队列去重（Redis Set），避免重复提交
- 服务重启后可恢复轮询（检查 externalId），不丢失进度
- i18n 提示词模板，支持多语言