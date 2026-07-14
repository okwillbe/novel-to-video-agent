# Novel to Video Agent - 技术方案

> 版本：1.0.0
> 日期：2026-06-12
> 技术栈：Java 后端 + Vue 前端

---

## 一、技术栈确定

### 1.1 后端技术栈

| 组件 | 技术选型 | 说明 |
|------|----------|------|
| **核心框架** | Spring Boot 3.2+ | Java 17+ |
| **Agent 框架** | agentscope-java | ReAct 推理、Skills 系统 |
| **数据库** | MySQL 8.0 | Prisma → MyBatis-Plus |
| **缓存** | Redis 7 | BullMQ → Spring Data Redis |
| **消息队列** | Redis Streams | 替代 BullMQ |
| **对象存储** | MinIO | S3 兼容 |
| **认证** | Spring Security + JWT | 替代 NextAuth.js |
| **API 文档** | SpringDoc OpenAPI | Swagger UI |

### 1.2 前端技术栈

| 组件 | 技术选型 | 说明 |
|------|----------|------|
| **框架** | Vue 3 | Composition API |
| **构建工具** | Vite 5 | 快速构建 |
| **状态管理** | Pinia | 替代 TanStack Query |
| **UI 组件库** | Element Plus | 企业级组件 |
| **样式** | Tailwind CSS | 原子化 CSS |
| **HTTP 客户端** | Axios | API 调用 |
| **实时通信** | WebSocket| 进度推送 |

### 1.3 技术栈对比

| 层面 | waoowaoo (原) | 本项目 (新) |
|------|---------------|-------------|
| 前端框架 | Next.js 15 + React 19 | Vue 3 + Vite |
| 后端框架 | Next.js API Routes | Spring Boot |
| Agent 框架 | 无 (固定流程) | agentscope-java (ReAct) |
| 数据库 ORM | Prisma | MyBatis-Plus |
| 消息队列 | BullMQ | Redis Streams |
| 认证 | NextAuth.js | Spring Security + JWT |
| Skills 存储 | 无 | MySQL Repository |

---

## 二、项目结构

### 2.1 后端项目结构

```
novel-to-video-agent/
├── pom.xml
├── docs/                           # 文档
│   ├── four-projects-analysis.md
│   ├── requirements.md
│   └── technical-solution.md
│
├── agent-core/                     # 核心 Agent 模块
│   ├── src/main/java/
│   │   └── com/novel2video/
│   │       ├── agent/
│   │       │   ├── NovelToVideoAgent.java    # 主 Agent
│   │       │   ├── AgentConfig.java          # Agent 配置
│   │       │   └── AgentRunner.java         # Agent 运行器
│   │       ├── skill/
│   │       │   ├── SkillEntity.java         # Skill 实体
│   │       │   ├── SkillRepository.java     # Skill 仓库接口
│   │       │   ├── MysqlSkillRepository.java # MySQL 实现
│   │       │   └── builtin/                  # 预置 Skills
│   │       │       ├── NovelAnalysisSkill.java
│   │       │       ├── CharacterExtractionSkill.java
│   │       │       ├── StoryboardDesignSkill.java
│   │       │       └── ...
│   │       ├── tool/
│   │       │   ├── ImageGenerationTool.java
│   │       │   ├── VideoGenerationTool.java
│   │       │   ├── VoiceGenerationTool.java
│   │       │   └── ...
│   │       └── memory/
│   │           ├── WorkflowMemory.java
│   │           └── CaseRepository.java
│   └── src/test/java/
│
├── agent-api/                      # API 模块
│   ├── src/main/java/
│   │   └── com/novel2video/
│   │       ├── controller/
│   │       │   ├── GenerateController.java    # 生成 API
│   │       │   ├── SkillController.java       # Skills 管理
│   │       │   ├── TaskController.java        # 任务查询
│   │       │   └── UserController.java        # 用户管理
│   │       ├── service/
│   │       │   ├── GenerateService.java
│   │       │   ├── TaskService.java
│   │       │   ├── BillingService.java
│   │       │   └── StorageService.java
│   │       ├── dto/
│   │       └── config/
│   └── src/main/resources/
│       └── application.yml
│
├── agent-worker/                   # Worker 模块
│   ├── src/main/java/
│   │   └── com/novel2video/
│   │       ├── worker/
│   │       │   ├── ImageWorker.java
│   │       │   ├── VideoWorker.java
│   │       │   ├── VoiceWorker.java
│   │       │   └── TextWorker.java
│   │       ├── handler/
│   │       │   ├── NovelAnalysisHandler.java
│   │       │   ├── StoryToScriptHandler.java
│   │       │   └── ...
│   │       └── generator/
│   │           ├── ImageGenerator.java       # 图片生成器接口
│   │           ├── VideoGenerator.java       # 视频生成器接口
│   │           └── impl/
│   │               ├── FalImageGenerator.java
│   │               ├── GoogleImageGenerator.java
│   │               └── ...
│   └── src/test/java/
│
└── agent-web/                     # 前端模块 (Vue)
    ├── package.json
    ├── vite.config.ts
    ├── src/
    │   ├── main.ts
    │   ├── App.vue
    │   ├── api/                    # API 调用
    │   │   ├── generate.ts
    │   │   ├── skill.ts
    │   │   └── task.ts
    │   ├── stores/                 # Pinia 状态
    │   │   ├── useProjectStore.ts
    │   │   ├── useTaskStore.ts
    │   │   └── useSkillStore.ts
    │   ├── views/                  # 页面
    │   │   ├── HomeView.vue
    │   │   ├── WorkspaceView.vue
    │   │   ├── SkillManageView.vue
    │   │   └── SettingsView.vue
    │   ├── components/             # 组件
    │   │   ├── storyboard/
    │   │   ├── assets/
    │   │   ├── video/
    │   │   └── common/
    │   └── router/
    │       └── index.ts
    └── public/
```

### 2.2 模块依赖关系

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  agent-web  │────▶│  agent-api  │────▶│ agent-core  │
│   (Vue)     │     │ (Spring MVC)│     │ (Agent)    │
└─────────────┘     └─────────────┘     └─────────────┘
                          │                    │
                          ▼                    ▼
                    ┌─────────────┐     ┌─────────────┐
                    │ agent-worker │────│   MySQL     │
                    │ (队列处理)   │     │   Redis     │
                    └─────────────┘     │   MinIO     │
                                        └─────────────┘
```

---

## 三、参考其他项目的业务逻辑

### 3.1 参考 waoowaoo 的业务逻辑

#### 3.1.1 任务队列系统 (BullMQ → Redis Streams)

**waoowaoo 实现**：
```typescript
// BullMQ 队列
const imageQueue = new Queue('image', { connection: redis })
const videoQueue = new Queue('video', { connection: redis })
```

**Java 实现**：
```java
// Redis Streams 实现
@Service
public class TaskQueueService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String IMAGE_STREAM = "task:image";
    private static final String VIDEO_STREAM = "task:video";
    private static final String VOICE_STREAM = "task:voice";
    private static final String TEXT_STREAM = "task:text";
    
    public void enqueueImageTask(Task task) {
        Map<String, String> message = new HashMap<>();
        message.put("taskId", task.getId());
        message.put("userId", task.getUserId());
        message.put("payload", JsonUtil.toJson(task.getPayload()));
        
        redisTemplate.opsForStream().add(IMAGE_STREAM, message);
    }
}
```

#### 3.1.2 任务类型定义

参考 waoowaoo 的 `src/lib/task/types.ts`：

```java
public enum TaskType {
    // 图片生成
    IMAGE_PANEL("image", "IMAGE_PANEL"),
    IMAGE_CHARACTER("image", "IMAGE_CHARACTER"),
    IMAGE_LOCATION("image", "IMAGE_LOCATION"),
    
    // 视频生成
    VIDEO_PANEL("video", "VIDEO_PANEL"),
    LIP_SYNC("video", "LIP_SYNC"),
    
    // 语音合成
    VOICE_LINE("voice", "VOICE_LINE"),
    VOICE_DESIGN("voice", "VOICE_DESIGN"),
    
    // 文本分析
    ANALYZE_NOVEL("text", "ANALYZE_NOVEL"),
    STORY_TO_SCRIPT("text", "STORY_TO_SCRIPT"),
    SCRIPT_TO_STORYBOARD("text", "SCRIPT_TO_STORYBOARD");
    
    private final String queue;
    private final String type;
}
```

#### 3.1.3 AI 服务抽象层 (工厂模式)

参考 waoowaoo 的 `src/lib/generators/factory.ts`：

```java
// 生成器接口
public interface ImageGenerator {
    Mono<ImageResult> generate(ImageRequest request);
}

public interface VideoGenerator {
    Mono<VideoResult> generate(VideoRequest request);
}

public interface VoiceGenerator {
    Mono<VoiceResult> generate(VoiceRequest request);
}

// 工厂类
@Component
public class GeneratorFactory {
    
    public ImageGenerator createImageGenerator(String provider) {
        return switch (provider.toLowerCase()) {
            case "fal" -> falImageGenerator;
            case "google" -> googleImageGenerator;
            case "ark" -> arkImageGenerator;
            case "bailian" -> bailianImageGenerator;
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
    
    public VideoGenerator createVideoGenerator(String provider) {
        return switch (provider.toLowerCase()) {
            case "fal" -> falVideoGenerator;
            case "google" -> googleVideoGenerator;
            case "minimax" -> minimaxVideoGenerator;
            case "vidu" -> viduVideoGenerator;
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
```

#### 3.1.4 计费系统

参考 waoowaoo 的计费流程：

```java
@Service
public class BillingService {
    
    /**
     * 计费模式：OFF / SHADOW / ENFORCE
     */
    public enum BillingMode {
        OFF,      // 关闭计费
        SHADOW,   // 影子模式，记录但不扣费
        ENFORCE   // 强制扣费
    }
    
    /**
     * 同步计费包装器
     */
    public <T> Mono<T> withBilling(BillingParams params, Supplier<Mono<T>> execute) {
        if (params.getMode() == BillingMode.OFF) {
            return execute.get();
        }
        
        // 1. 计算报价
        BigDecimal quotedCost = calculateCost(params);
        
        if (params.getMode() == BillingMode.SHADOW) {
            return execute.get()
                .doOnSuccess(result -> recordShadowUsage(params, quotedCost));
        }
        
        // 2. 检查余额并冻结
        return freezeBalance(params.getUserId(), quotedCost)
            .flatMap(freezeId -> {
                try {
                    // 3. 执行业务逻辑
                    return execute.get()
                        .flatMap(result -> {
                            // 4. 计算实际用量并扣费
                            BigDecimal actualCost = calculateActualCost(params, result);
                            return confirmCharge(freezeId, actualCost)
                                .thenReturn(result);
                        });
                } catch (Exception e) {
                    // 5. 失败时回滚冻结
                    return rollbackFreeze(freezeId)
                        .then(Mono.error(e));
                }
            });
    }
}
```

### 3.2 参考 ViMax 的业务逻辑

#### 3.2.1 多智能体协作

ViMax 定义了多个专业智能体，可转换为 Skills：

```java
// Skills 定义（存储在 MySQL）
public class BuiltinSkills {
    
    public static final AgentSkill NOVEL_COMPRESSOR = AgentSkill.builder()
        .name("novel-compressor")
        .description("小说分块压缩，处理长文本")
        .skillContent("""
            # 小说压缩技能
            
            将长篇小说分块处理：
            1. 按章节或字数切分（每块约 15000 字符）
            2. 对每块提取摘要
            3. 合并为压缩版本
            
            输入：原始小说文本
            输出：压缩后的摘要文本
            """)
        .metadata(Map.of(
            "category", "analysis",
            "version", "1.0.0"
        ))
        .build();
    
    public static final AgentSkill CHARACTER_EXTRACTOR = AgentSkill.builder()
        .name("character-extractor")
        .description("从文本中提取角色信息")
        .skillContent("""
            # 角色提取技能
            
            分析文本，提取：
            - 角色名称和别名
            - 角色介绍（身份、关系）
            - 性格标签
            - 外观特征
            - 重要性层级 (S/A/B/C/D)
            
            输入：文本内容
            输出：角色档案列表
            """)
        .metadata(Map.of(
            "category", "analysis",
            "version", "1.0.0"
        ))
        .build();
    
    public static final AgentSkill STORYBOARD_ARTIST = AgentSkill.builder()
        .name("storyboard-artist")
        .description("根据剧本设计专业分镜方案")
        .skillContent("""
            # 分镜设计技能
            
            根据剧本设计分镜：
            1. 分析每个场景的关键动作
            2. 确定镜头类型（特写/中景/远景）
            3. 设计镜头运动
            4. 添加转场效果
            
            输入：剧本内容
            输出：分镜脚本
            """)
        .metadata(Map.of(
            "category", "generation",
            "version", "1.0.0"
        ))
        .build();
    
    public static final AgentSkill BEST_IMAGE_SELECTOR = AgentSkill.builder()
        .name("best-image-selector")
        .description("使用 MLLM 选择一致性最好的图片")
        .skillContent("""
            # 最佳图选择技能
            
            从多张候选图中选择最一致的：
            1. 并行生成 2-4 张图片
            2. 使用 MLLM 评估每张图
            3. 按一致性评分排序
            4. 选择得分最高的图片
            
            评估维度：
            - 角色一致性
            - 场景匹配度
            - 风格统一性
            """)
        .metadata(Map.of(
            "category", "postprocess",
            "version", "1.0.0"
        ))
        .build();
}
```

#### 3.2.2 一致性校验机制

参考 ViMax 的 BestImageSelector：

```java
@Service
public class ConsistencyCheckService {
    
    /**
     * 选择最佳图片
     */
    public Mono<String> selectBestImage(List<String> imageUrls, String referencePrompt) {
        // 构建 MLLM 评估 prompt
        String evaluatePrompt = """
            你是图片一致性评估专家。
            
            参考描述：%s
            
            请评估以下图片与参考描述的一致性，按 1-10 分评分：
            %s
            
            输出 JSON 格式：
            {
              "scores": [
                {"imageIndex": 0, "score": 8, "reason": "..."},
                {"imageIndex": 1, "score": 7, "reason": "..."}
              ],
              "bestIndex": 0
            }
            """.formatted(referencePrompt, formatImageList(imageUrls));
        
        return mllmService.evaluate(evaluatePrompt)
            .map(result -> parseBestIndex(result, imageUrls));
    }
}
```

#### 3.2.3 工作流引擎

参考 ViMax 的 Pipeline 设计：

```java
@Service
public class PipelineService {
    
    /**
     * 执行小说到视频的完整流程
     */
    public Mono<VideoResult> executeNovelToVideoPipeline(PipelineInput input) {
        PipelineGraph<NovelToVideoState> pipeline = PipelineGraph.<NovelToVideoState>builder()
            .addNode("novel_analysis", this::analyzeNovel)
            .addNode("character_extraction", this::extractCharacters)
            .addNode("script_generation", this::generateScript)
            .addNode("storyboard_design", this::designStoryboard)
            .addNode("image_generation", this::generateImages)
            .addNode("video_synthesis", this::synthesizeVideo)
            .addNode("voice_generation", this::generateVoice)
            .build();
        
        NovelToVideoState initialState = new NovelToVideoState();
        initialState.setInput(input);
        
        return pipeline.execute(initialState)
            .doOnNext(state -> saveSuccessCase(state))  // 保存成功案例
            .map(NovelToVideoState::getResult);
    }
    
    private Mono<NovelToVideoState> analyzeNovel(NovelToVideoState state) {
        return skillExecutor.execute("novel-analysis", state.getInput())
            .doOnNext(state::setAnalysisResult);
    }
}
```

### 3.3 参考 nanobot 的业务逻辑

#### 3.3.1 渠道接入

nanobot 支持多种渠道，可通过 A2A 协议复用：

```
用户 → Telegram/微信/Discord → nanobot → A2A → 本项目 Java Agent
```

或者独立实现渠道适配器：

```java
public interface ChannelAdapter {
    Mono<Void> sendMessage(String userId, String message);
    Mono<String> receiveMessage(String userId);
    Mono<Void> sendProgress(String userId, TaskProgress progress);
}

@Service
public class TelegramAdapter implements ChannelAdapter {
    // 实现 Telegram Bot API
}

@Service  
public class WechatAdapter implements ChannelAdapter {
    // 实现微信公众号 API
}
```

#### 3.3.2 Skills 发现机制

参考 nanobot 的 Skills 发现：

```java
@Service
public class SkillDiscoveryService {
    
    private final MysqlSkillRepository skillRepository;
    
    /**
     * 按分类发现 Skills
     */
    public Flux<AgentSkill> discoverByCategory(String category) {
        return Flux.fromIterable(skillRepository.findByMetadata("category", category));
    }
    
    /**
     * 按关键词搜索 Skills
     */
    public Flux<AgentSkill> searchSkills(String keyword) {
        return Flux.fromIterable(skillRepository.search(keyword));
    }
    
    /**
     * 智能推荐 Skills
     */
    public Flux<AgentSkill> recommendSkills(String userRequest) {
        // 1. 分析用户请求意图
        // 2. 匹配合适的 Skills
        // 3. 返回推荐列表
        return analyzeIntent(userRequest)
            .flatMapMany(intent -> Flux.fromIterable(
                skillRepository.findByTags(intent.getKeywords())
            ));
    }
}
```

---

## 四、核心业务流程

### 4.1 Agent 决策流程

```
用户请求
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    NovelToVideoAgent                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ 1. 理解需求                                                 │  │
│  │    - 解析用户意图                                           │  │
│  │    - 提取关键参数（风格、时长、角色等）                       │  │
│  └───────────────────────────────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ 2. 规划任务                                                 │  │
│  │    - 从 MySQL 加载可用 Skills                               │  │
│  │    - 选择合适的 Skills 组合                                  │  │
│  │    - 生成执行计划                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ 3. 执行任务                                                 │  │
│  │    - 调用 Skills（通过 Tool）                                │  │
│  │    - 处理图片/视频/语音生成                                  │  │
│  │    - 实时推送进度                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ 4. 评估结果                                                 │  │
│  │    - 一致性校验                                              │  │
│  │    - 质量评估                                                │  │
│  │    - 必要时重试                                              │  │
│  └───────────────────────────────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ 5. 记忆保存                                                 │  │
│  │    - 保存成功案例                                            │  │
│  │    - 更新用户偏好                                            │  │
│  │    - 优化 Skills 推荐                                        │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 生成视频完整流程

```java
@Service
public class GenerateService {
    
    private final NovelToVideoAgent agent;
    private final TaskService taskService;
    private final BillingService billingService;
    
    public Mono<GenerateResponse> generateVideo(GenerateRequest request) {
        // 1. 创建任务
        Task task = taskService.createTask(request);
        
        // 2. 计费预检查
        return billingService.checkBalance(request.getUserId(), request.getEstimatedCost())
            .flatMap(balance -> {
                if (!balance.isSufficient()) {
                    return Mono.just(GenerateResponse.insufficientBalance());
                }
                
                // 3. 启动 Agent
                return agent.call(request)
                    .map(result -> {
                        // 4. 返回任务 ID
                        return GenerateResponse.success(task.getId(), result);
                    });
            });
    }
}
```

---

## 五、API 接口设计

### 5.1 REST API

```java
@RestController
@RequestMapping("/api/v1")
public class GenerateController {
    
    @PostMapping("/generate")
    public Mono<ApiResponse<GenerateResponse>> generate(
            @RequestBody GenerateRequest request,
            @AuthenticationPrincipal User user) {
        return generateService.generateVideo(request.withUserId(user.getId()))
            .map(ApiResponse::success);
    }
    
    @GetMapping("/tasks/{taskId}")
    public Mono<ApiResponse<TaskStatus>> getTaskStatus(
            @PathVariable String taskId,
            @AuthenticationPrincipal User user) {
        return taskService.getTaskStatus(taskId, user.getId())
            .map(ApiResponse::success);
    }
    
    @GetMapping("/tasks/{taskId}/result")
    public Mono<ApiResponse<TaskResult>> getTaskResult(
            @PathVariable String taskId,
            @AuthenticationPrincipal User user) {
        return taskService.getTaskResult(taskId, user.getId())
            .map(ApiResponse::success);
    }
}

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {
    
    @GetMapping
    public Mono<ApiResponse<List<SkillDto>>> listSkills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return skillService.listSkills(category, keyword)
            .collectList()
            .map(ApiResponse::success);
    }
    
    @PostMapping
    public Mono<ApiResponse<SkillDto>> createSkill(
            @RequestBody CreateSkillRequest request,
            @AuthenticationPrincipal User user) {
        return skillService.createSkill(request, user.getId())
            .map(ApiResponse::success);
    }
    
    @PutMapping("/{skillId}")
    public Mono<ApiResponse<SkillDto>> updateSkill(
            @PathVariable String skillId,
            @RequestBody UpdateSkillRequest request,
            @AuthenticationPrincipal User user) {
        return skillService.updateSkill(skillId, request, user.getId())
            .map(ApiResponse::success);
    }
}
```

### 5.2 WebSocket 推送

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(taskProgressHandler(), "/ws/tasks")
            .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler taskProgressHandler() {
        return new TaskProgressWebSocketHandler();
    }
}

@Component
public class TaskProgressWebSocketHandler extends TextWebSocketHandler {
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        sessions.put(userId, session);
    }
    
    public void sendProgress(String userId, TaskProgress progress) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            String message = JsonUtil.toJson(progress);
            session.sendMessage(new TextMessage(message));
        }
    }
}
```

---

## 六、数据库设计

### 6.1 核心表结构

参考 waoowaoo 的 Prisma Schema，转换为 MySQL：

```sql
-- 用户表
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(128) UNIQUE,
    password VARCHAR(256),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 项目表
CREATE TABLE projects (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    mode VARCHAR(32) DEFAULT 'novel-promotion',
    status VARCHAR(32) DEFAULT 'draft',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 任务表
CREATE TABLE tasks (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    project_id VARCHAR(64),
    type VARCHAR(64) NOT NULL,
    target_type VARCHAR(64),
    target_id VARCHAR(64),
    status VARCHAR(32) DEFAULT 'queued',
    progress INT DEFAULT 0,
    payload JSON,
    result JSON,
    error_code VARCHAR(64),
    error_message TEXT,
    billing_info JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    completed_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_status (status)
);

-- Skills 表（见需求文档）
CREATE TABLE skills (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    skill_id VARCHAR(128) NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(512) NOT NULL,
    category VARCHAR(32) NOT NULL,
    skill_content MEDIUMTEXT NOT NULL,
    version VARCHAR(32) DEFAULT '1.0.0',
    status TINYINT DEFAULT 1,
    use_count BIGINT UNSIGNED DEFAULT 0,
    success_count BIGINT UNSIGNED DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_skill_id_version (skill_id, version),
    INDEX idx_category (category)
);

-- 成功案例表
CREATE TABLE success_cases (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    skill_ids JSON,
    user_request TEXT,
    execution_steps JSON,
    params JSON,
    result_summary TEXT,
    rating DECIMAL(3,2),
    embedding BLOB,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id)
);

-- 用户余额表
CREATE TABLE user_balances (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    balance DECIMAL(10,4) DEFAULT 0,
    frozen_amount DECIMAL(10,4) DEFAULT 0,
    total_spent DECIMAL(10,4) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 计费记录表
CREATE TABLE billing_records (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    task_id VARCHAR(64),
    action VARCHAR(64),
    api_type VARCHAR(32),
    model VARCHAR(128),
    quantity DECIMAL(10,4),
    unit VARCHAR(16),
    cost DECIMAL(10,4),
    status VARCHAR(32),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_created (user_id, created_at)
);
```

---

## 七、前端设计

### 7.1 页面结构

```
src/
├── views/
│   ├── HomeView.vue              # 首页
│   ├── LoginView.vue             # 登录
│   ├── RegisterView.vue          # 注册
│   ├── WorkspaceView.vue         # 工作区（核心页面）
│   │   ├── AssetsStage.vue      # 资产阶段
│   │   ├── ScriptStage.vue      # 剧本阶段
│   │   ├── StoryboardStage.vue  # 分镜阶段
│   │   ├── VoiceStage.vue       # 配音阶段
│   │   └── VideoStage.vue       # 视频阶段
│   ├── SkillManageView.vue       # Skills 管理
│   ├── SettingsView.vue          # 用户设置
│   └── HistoryView.vue           # 历史记录
```

### 7.2 状态管理

```typescript
// stores/useProjectStore.ts
import { defineStore } from 'pinia'

export const useProjectStore = defineStore('project', {
  state: () => ({
    project: null as Project | null,
    characters: [] as Character[],
    locations: [] as Location[],
    episodes: [] as Episode[],
    currentStage: 'assets' as Stage,
  }),
  
  actions: {
    async loadProject(projectId: string) {
      this.project = await api.getProject(projectId)
      this.characters = await api.getCharacters(projectId)
      this.locations = await api.getLocations(projectId)
      this.episodes = await api.getEpisodes(projectId)
    },
    
    async analyzeNovel(content: string) {
      const task = await api.analyzeNovel(this.project!.id, content)
      // 监听任务进度
      taskProgress.subscribe(task.id, (progress) => {
        if (progress.status === 'completed') {
          this.refreshCharacters()
        }
      })
    },
  },
})

// stores/useTaskStore.ts
export const useTaskStore = defineStore('task', {
  state: () => ({
    tasks: new Map<string, TaskStatus>(),
    ws: null as WebSocket | null,
  }),
  
  actions: {
    connectWebSocket() {
      this.ws = new WebSocket(`${WS_URL}/ws/tasks`)
      this.ws.onmessage = (event) => {
        const progress = JSON.parse(event.data)
        this.tasks.set(progress.taskId, progress)
      }
    },
    
    getTaskProgress(taskId: string): TaskStatus | undefined {
      return this.tasks.get(taskId)
    },
  },
})
```

### 7.3 组件示例

```vue
<!-- components/storyboard/StoryboardEditor.vue -->
<template>
  <div class="storyboard-editor">
    <div class="toolbar">
      <el-button @click="generateStoryboard">生成分镜</el-button>
      <el-button @click="regenerateSelected" :disabled="!selectedPanel">
        重新生成选中
      </el-button>
    </div>
    
    <div class="panels-grid">
      <PanelCard
        v-for="panel in panels"
        :key="panel.id"
        :panel="panel"
        :selected="selectedPanel?.id === panel.id"
        @select="selectPanel"
        @regenerate="regeneratePanel"
      />
    </div>
    
    <TaskProgress v-if="activeTask" :task="activeTask" />
  </div>
</template>

<script setup lang="ts">
import { useProjectStore } from '@/stores/useProjectStore'
import { useTaskStore } from '@/stores/useTaskStore'

const projectStore = useProjectStore()
const taskStore = useTaskStore()

const panels = computed(() => projectStore.currentEpisode?.panels || [])
const activeTask = ref<Task | null>(null)

async function generateStoryboard() {
  activeTask.value = await api.generateStoryboard(projectStore.project!.id)
}

function regeneratePanel(panelId: string) {
  // 重新生成单个分镜
}
</script>
```

---

## 八、部署架构

### 8.1 Docker Compose

```yaml
version: '3.8'

services:
  # 前端
  web:
    build: ./agent-web
    ports:
      - "80:80"
    depends_on:
      - api
    
  # API 服务
  api:
    build: ./agent-api
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:mysql://mysql:3306/novel2video
      REDIS_URL: redis://redis:6379
      MINIO_URL: http://minio:9000
    depends_on:
      - mysql
      - redis
      - minio
    
  # Worker 服务
  worker:
    build: ./agent-worker
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:mysql://mysql:3306/novel2video
      REDIS_URL: redis://redis:6379
    depends_on:
      - mysql
      - redis
    deploy:
      replicas: 3  # 多 Worker 实例
    
  # MySQL
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_DATABASE: novel2video
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    
  # Redis
  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    
  # MinIO
  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - minio_data:/data

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

### 8.2 生产部署建议

```
┌─────────────────────────────────────────────────────────────────┐
│                         Nginx (LB)                              │
└─────────────────────────────────────────────────────────────────┘
              │                        │
              ▼                        ▼
┌─────────────────────┐    ┌─────────────────────┐
│   API Server 1     │    │   API Server 2      │
│   (Spring Boot)     │    │   (Spring Boot)     │
└─────────────────────┘    └─────────────────────┘
              │                        │
              └────────────┬───────────┘
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Worker Cluster (K8s)                         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐           │
│  │ Image   │  │ Video   │  │ Voice   │  │ Text    │           │
│  │ Worker  │  │ Worker  │  │ Worker  │  │ Worker  │           │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘           │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Data Layer                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │ MySQL    │  │ Redis    │  │ MinIO    │  │ 向量DB   │       │
│  │ (主从)   │  │ (集群)   │  │ (集群)   │  │(Milvus) │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 九、开发计划

### Phase 1: 基础框架（2周）

- [x] 创建项目骨架
- [ ] 集成 agentscope-java
- [ ] 配置 MySQL + Redis + MinIO
- [ ] 实现 Skills Repository
- [ ] 基础 API 框架

### Phase 2: 核心 Agent（3周）

- [ ] 实现 NovelToVideoAgent
- [ ] 迁移 ViMax Skills
- [ ] 实现 Tool 调用层
- [ ] 集成 AI 服务商

### Phase 3: 任务系统（2周）

- [ ] 实现 Redis Streams 队列
- [ ] 实现 Worker 框架
- [ ] 并发控制
- [ ] 进度推送

### Phase 4: 计费系统（1周）

- [ ] 实现计费服务
- [ ] 余额管理
- [ ] 使用统计

### Phase 5: Vue 前端（3周）

- [ ] 页面结构
- [ ] 工作区组件
- [ ] Skills 管理界面
- [ ] 实时进度展示

### Phase 6: 测试上线（1周）

- [ ] 单元测试
- [ ] 集成测试
- [ ] 部署脚本
- [ ] 文档完善

---

## 十、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| agentscope-java 版本兼容 | 高 | 使用稳定版 1.0.12，锁定依赖 |
| AI 服务商 API 变更 | 中 | 抽象层隔离，快速适配 |
| Redis Streams 可靠性 | 中 | 消息持久化，失败重试 |
| 前端状态同步 | 中 | WebSocket 双向确认 |
