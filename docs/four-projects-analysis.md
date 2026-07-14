# 四项目融合分析

> 目标：基于 agentscope-java 底座，融合 nanobot、ViMax、waoowaoo 三个项目的能力，构建一个智能化的小说到视频 Agent 系统。

---

## 一、项目概览

| 项目 | 语言 | 定位 | 核心能力 |
|------|------|------|----------|
| **agentscope-java** | Java 17+ | Agent 框架底座 | ReAct 推理、工具调用、记忆管理、多智能体协作 |
| **nanobot** | Python 3.11+ | 轻量级个人助手 | Skills 系统、多渠道接入、记忆 |
| **ViMax** | Python 3.12+ | 多智能体视频生成 | 剧本生成、分镜设计、一致性校验 |
| **waoowaoo** | TypeScript | Web 端视频制作工具 | Web UI、用户系统、计费、任务队列 |

---

## 二、各项目详细分析

### 2.1 agentscope-java — Agent 框架底座（含 Skills 系统）

#### Skills 系统详解

agentscope-java 提供了完整的 Skills 系统，支持动态加载和管理技能：

```
agentscope-core/src/main/java/io/agentscope/core/skill/
├── AgentSkill.java              # Skill 定义（Markdown + YAML frontmatter）
├── SkillRegistry.java           # Skill 注册表
├── SkillBox.java               # Skill 容器
├── SkillFilter.java            # Skill 过滤器
├── SkillHook.java              # Skill 钩子
├── DynamicSkillMiddleware.java # 动态 Skill 中间件
└── repository/
    ├── AgentSkillRepository.java      # Repository 接口
    ├── ClasspathSkillRepository.java  # 从 classpath 加载
    ├── FileSystemSkillRepository.java # 从文件系统加载
    └── ...
```

#### Skill 定义格式（Markdown + YAML）

```markdown
---
name: novel-analysis
description: 分析小说内容，提取角色、场景、剧情
version: 1.0.0
author: system
tags: [novel, analysis, nlp]
---

# 小说分析技能

## 功能
- 提取主要角色及其特征
- 识别故事场景和环境
- 分析剧情结构和关键事件

## 使用方法
当用户请求分析小说时，按照以下步骤执行：
1. 读取小说文本
2. 识别章节结构
3. 提取角色信息
4. 分析场景描写
5. 生成结构化报告
```

#### Skill 使用示例

```java
// 方式1：从文件系统加载 Skills
Path skillsDir = Paths.get("./skills");
FileSystemSkillRepository skillRepo = new FileSystemSkillRepository(skillsDir, false);

// 方式2：直接创建 Skill
AgentSkill skill = AgentSkill.builder()
    .name("storyboard-design")
    .description("根据剧本生成分镜方案")
    .skillContent("""
        # 分镜设计技能
        
        根据剧本内容，按照以下步骤设计分镜：
        1. 分析每个场景的关键动作
        2. 确定镜头类型（特写/中景/远景）
        3. 设计镜头运动
        4. 添加转场效果
        """)
    .addResource("templates/shot_types.json", shotTypesJson)
    .build();

// 注册到 Agent
ReActAgent agent = ReActAgent.builder()
    .name("NovelToVideoAssistant")
    .sysPrompt("你是一个小说到视频的智能助手...")
    .model(model)
    .toolkit(toolkit)
    .skillRepository(skillRepo)  // 加载 Skills 仓库
    .build();
```

#### Skills vs Tools 对比

| 特性 | Skills | Tools |
|------|--------|-------|
| **定义方式** | Markdown + YAML | Java 类 + @Tool 注解 |
| **用途** | 提示词模板、知识库 | 可执行代码 |
| **加载方式** | 文件系统、Classpath、Git、MySQL | 代码注册 |
| **动态性** | 运行时加载/卸载 | 编译时确定 |
| **适用场景** | 复杂提示词、工作流模板 | API 调用、文件操作 |

#### Skills 扩展仓库

agentscope-java 支持多种 Skill 来源：

```xml
<!-- Git 仓库作为 Skill 源 -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-skill-git-repository</artifactId>
</dependency>

<!-- MySQL 作为 Skill 源 -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-skill-mysql-repository</artifactId>
</dependency>

<!-- Nacos 作为 Skill 源（分布式） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-nacos-skill</artifactId>
</dependency>
```

#### 基本信息

| 属性 | 详情 |
|------|------|
| **语言** | Java (JDK 17+) |
| **定位** | 生产级 AI Agent 编程框架 |
| **协议** | Apache 2.0 |
| **核心能力** | ReAct 推理、工具调用、记忆管理、多智能体协作 |

#### 项目结构

```
agentscope-java/
├── agentscope-core/              # 核心 Agent 引擎
│   ├── ReActAgent.java           # 推理-行动循环
│   ├── agent/
│   │   ├── Agent.java            # Agent 基类
│   │   ├── ObservableAgent.java  # 可观测 Agent
│   │   └── StreamableAgent.java # 流式 Agent
│   ├── memory/                   # 记忆系统
│   │   ├── LongTermMemory.java
│   │   └── AgentStateMemoryView.java
│   ├── event/                    # 事件流
│   │   ├── AgentStartEvent.java
│   │   ├── ToolCallStartEvent.java
│   │   └── ToolResultEndEvent.java
│   ├── tool/                     # 工具系统
│   │   └── ToolRegistry.java
│   └── hook/                     # 钩子系统
├── agentscope-extensions/        # 扩展模块
│   ├── agentscope-extensions-mcp/    # MCP 协议支持
│   ├── agentscope-extensions-a2a/    # A2A 协议支持
│   └── agentscope-extensions-scheduler/ # 调度器
├── agentscope-examples/          # 示例 Agent
│   ├── agentscope-claw/          # 小龙虾 Agent
│   ├── agentscope-codingagent/   # 编程 Agent
│   └── agentscope-dataagent/    # 数据 Agent
└── agentscope-harness/           # 运行时沙箱
```

#### 核心特性

1. **ReAct 范式**
   - 推理（Reasoning）：分析任务，规划下一步
   - 行动（Action）：调用工具执行操作
   - 循环迭代直到任务完成

2. **工具扩展**
   - MCP（Model Context Protocol）：集成任意 MCP 兼容服务
   - A2A（Agent-to-Agent）：分布式多智能体协作
   - 动态注册：运行时发现和调用工具

3. **生产就绪**
   - GraalVM 原生镜像：200ms 冷启动
   - OpenTelemetry：全链路可观测
   - 安全沙箱：隔离不可信代码

4. **控制机制**
   - 安全中断：暂停执行，保留上下文
   - 优雅取消：终止长时间任务
   - 人机协同：Hook 系统注入修正

#### 关键代码示例

```java
ReActAgent agent = ReActAgent.builder()
    .name("Assistant")
    .sysPrompt("You are a helpful AI assistant.")
    .model(DashScopeChatModel.builder()
        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
        .modelName("qwen-max")
        .build())
    .tools(tools)  // 动态注册工具
    .memory(memory) // 持久化记忆
    .build();

Msg response = agent.call(Msg.builder()
    .textContent("帮我分析这个小说")
    .build()).block();
```

---

### 2.2 nanobot — 轻量级个人 Agent

#### 基本信息

| 属性 | 详情 |
|------|------|
| **语言** | Python 3.11+ |
| **定位** | 超轻量个人 AI 助手 |
| **协议** | MIT |
| **核心能力** | Skills 系统、多渠道接入、记忆 |

#### 项目结构

```
nanobot/
├── nanobot/
│   ├── agent/
│   │   ├── runner.py            # Agent 执行循环
│   │   ├── skills.py           # Skills 发现与管理
│   │   ├── memory.py           # 记忆系统
│   │   ├── hook.py             # 钩子系统
│   │   └── tools/              # 内置工具集
│   │       ├── filesystem.py   # 文件操作
│   │       ├── shell.py        # Shell 执行
│   │       ├── web.py          # Web 搜索/抓取
│   │       ├── mcp.py          # MCP 集成
│   │       └── image_generation.py
│   ├── channels/               # 渠道接入层
│   │   ├── telegram.py
│   │   ├── discord.py
│   │   ├── slack.py
│   │   ├── weixin.py           # 微信
│   │   ├── feishu.py           # 飞书
│   │   ├── dingtalk.py         # 钉钉
│   │   ├── whatsapp.py
│   │   ├── email.py
│   │   └── websocket.py        # WebUI 通道
│   ├── providers/              # LLM 提供者
│   │   ├── openai_compat_provider.py
│   │   ├── anthropic_provider.py
│   │   ├── deepseek_provider.py
│   │   └── image_generation.py
│   ├── api/                    # OpenAI 兼容 API
│   └── webui/                  # 内置 Web UI
├── bridge/                     # 渠道桥接
└── desktop/                    # 桌面客户端
```

#### 核心特性

1. **Skills 系统**
   - 自动发现：扫描目录发现 Skills
   - 模板渲染：Jinja2 模板生成提示词
   - 动态加载：运行时注册新 Skills

2. **渠道丰富**
   - 即时通讯：Telegram、Discord、Slack
   - 国内平台：微信、飞书、钉钉、QQ
   - 其他：Email、WebSocket

3. **轻量设计**
   - 核心代码精简（约 2000 行）
   - 无重型编排层
   - 易读易扩展

4. **记忆系统**
   - Dream 记忆：两阶段持久化
   - 会话管理：跨渠道会话
   - 向量存储：语义检索

#### 关键代码示例

```python
# Skills 定义示例
class NovelAnalysisSkill(Skill):
    name = "analyze_novel"
    description = "分析小说内容，提取角色、场景、剧情"
    
    async def execute(self, novel_text: str):
        # Skill 实现逻辑
        characters = await self.extract_characters(novel_text)
        scenes = await self.extract_scenes(novel_text)
        return {"characters": characters, "scenes": scenes}
```

---

### 2.3 ViMax — 多智能体视频生成

#### 基本信息

| 属性 | 详情 |
|------|------|
| **语言** | Python 3.12+ |
| **定位** | 小说/创意 → 视频生成系统 |
| **协议** | MIT |
| **核心能力** | 剧本生成、分镜设计、一致性校验 |

#### 项目结构

```
ViMax/
├── agents/                      # 专业智能体
│   ├── novel_compressor.py          # 小说压缩（RAG）
│   ├── character_extractor.py       # 角色提取
│   ├── scene_extractor.py           # 场景提取
│   ├── event_extractor.py           # 事件提取
│   ├── screenwriter.py              # 编剧（生成剧本）
│   ├── script_planner.py            # 剧本规划
│   ├── storyboard_artist.py          # 分镜师（生成分镜）
│   ├── camera_image_generator.py    # 图片生成
│   ├── best_image_selector.py       # 最佳图选择（MLLM）
│   └── reference_image_selector.py  # 参考图选择
├── pipelines/                   # 流程编排
│   ├── idea2video_pipeline.py       # 创意 → 视频
│   ├── script2video_pipeline.py    # 剧本 → 视频
│   └── novel2movie_pipeline.py     # 小说 → 电影（待实现）
├── tools/                       # 工具
│   ├── ImageGeneratorNanobananaGoogleAPI.py
│   ├── ImageGeneratorIdeogramAPI.py
│   └── VideoGeneratorVeoGoogleAPI.py
├── configs/                     # 配置
└── utils/                       # 工具函数
```

#### 核心特性

1. **专业智能体分工**
   | Agent | 职责 |
   |-------|------|
   | NovelCompressor | 小说分块压缩 |
   | CharacterExtractor | 提取角色信息 |
   | SceneExtractor | 提取场景信息 |
   | Screenwriter | 生成剧本 |
   | StoryboardArtist | 生成分镜方案 |
   | BestImageSelector | 选择一致性最好的图 |

2. **一致性保证**
   - 参考图选择：智能选取首帧参考图
   - 最佳图选择：并行生成多张，MLLM 选择最优
   - 时间线追踪：跨场景角色/环境一致性

3. **长文本处理**
   - 分块处理：小说切分为小块
   - 并行压缩：异步压缩各块
   - RAG 检索：基于 Embedding 的语义搜索

4. **视频生成流程**
   ```
   小说输入 → 分块压缩 → 角色/场景提取 → 剧本生成 → 分镜设计 → 
   参考图选择 → 图片生成 → 最佳图选择 → 视频合成 → 输出
   ```

#### 关键代码示例

```python
# ViMax Pipeline 示例
class Idea2VideoPipeline(BasePipeline):
    async def __call__(self, idea: str, style: str):
        # Step 1: 生成剧本
        script = await self.screenwriter.generate(idea, style)
        
        # Step 2: 生成分镜
        storyboard = await self.storyboard_artist.generate(script)
        
        # Step 3: 并行生成图片
        images = await asyncio.gather(*[
            self.camera_image_generator.generate(shot)
            for shot in storyboard.shots
        ])
        
        # Step 4: 选择最佳图片
        best_images = await self.best_image_selector.select(images)
        
        # Step 5: 生成视频
        videos = await self.video_generator.generate(best_images)
        
        return videos
```

---

### 2.4 waoowaoo — Web 端视频制作工具

#### 基本信息

| 属性 | 详情 |
|------|------|
| **语言** | TypeScript (Next.js 15 + React 19) |
| **定位** | Web 端短剧/漫画视频制作工具 |
| **协议** | 开源版 + 商业版 |
| **核心能力** | Web UI、用户系统、计费、任务队列 |

#### 项目结构

```
waoowaoo/
├── src/
│   ├── app/                     # Next.js App Router
│   │   ├── [locale]/workspace/  # 工作台页面
│   │   └── api/                 # API 路由
│   │       ├── auth/            # 认证（NextAuth.js）
│   │       ├── projects/        # 项目管理
│   │       └── tasks/           # 任务提交
│   ├── components/              # React 组件
│   │   ├── storyboard/          # 分镜编辑器
│   │   ├── assets/              # 资产管理
│   │   └── video/               # 视频编辑
│   ├── lib/
│   │   ├── workers/            # BullMQ Worker
│   │   │   ├── handlers/       # 任务处理器
│   │   │   │   ├── script-to-storyboard.ts
│   │   │   │   ├── image-generation.ts
│   │   │   │   └── video-generation.ts
│   │   ├── billing/            # 计费系统
│   │   │   ├── service.ts      # 核心逻辑
│   │   │   ├── ledger.ts       # 账本
│   │   │   └── cost.ts         # 成本计算
│   │   ├── storage/            # 存储抽象
│   │   │   ├── minio.ts
│   │   │   └── cos.ts
│   │   ├── workflows/          # LangGraph 流程
│   │   │   └── script-to-storyboard/
│   │   │       └── graph.ts
│   │   └── run-runtime/       # 运行时
│   │       ├── langgraph-pipeline.ts
│   │       └── graph-executor.ts
│   └── types/
├── prisma/                     # 数据库 Schema
│   └── schema.prisma
├── docker-compose.yml          # Docker 编排
└── standards/                  # 标准化文档
    ├── capabilities/           # 能力目录
    └── pricing/               # 定价标准
```

#### 核心特性

1. **Web UI**
   - 分镜编辑器：拖拽式分镜编排
   - 角色管理：角色一致性设置
   - 视频预览：实时预览生成结果

2. **用户系统**
   - NextAuth.js 认证
   - 项目管理
   - 资产库（角色/场景/配音）

3. **计费系统**
   - 三种模式：OFF / SHADOW / ENFORCE
   - 按类型计费：文本/图片/视频/语音
   - 冻结/结算/回滚机制

4. **任务队列**
   - BullMQ 任务队列
   - 并发控制
   - 任务进度推送

5. **存储抽象**
   - MinIO 本地存储
   - S3 兼容接口
   - 预签名 URL

#### 关键代码示例

```typescript
// waoowaoo LangGraph 流程
export async function runScriptToStoryboardGraph(input) {
  const initialState = { refs: {}, meta: {} };
  
  return await runPipelineGraph({
    runId: input.runId,
    projectId: input.projectId,
    userId: input.userId,
    state: initialState,
    nodes: [
      {
        key: 'script_to_storyboard_orchestrator',
        title: '分镜生成',
        maxAttempts: 2,
        timeoutMs: 1000 * 60 * 20,
        run: async (context) => {
          const result = await runOrchestrator(input);
          context.state.orchestratorResult = result;
          return { output: { clipCount: result.summary.clipCount } };
        },
      },
      {
        key: 'script_to_storyboard_validate',
        title: '结果校验',
        run: async (context) => {
          // 校验逻辑
          return { output: { validated: true } };
        },
      },
    ],
  });
}
```

---

## 三、能力对比矩阵

| 能力 | agentscope-java | nanobot | ViMax | waoowaoo |
|------|:---------------:|:-------:|:-----:|:--------:|
| **Agent 框架** | ✅ 核心能力 | ✅ 简化版 | ❌ 无 | ❌ 无 |
| **自主规划（ReAct）** | ✅ 完整实现 | ⚠️ 基础 | ❌ 固定流程 | ❌ 固定流程 |
| **工具扩展（MCP/A2A）** | ✅ 协议支持 | ✅ Skills | ❌ 硬编码 | ❌ 硬编码 |
| **多渠道接入** | ❌ 无 | ✅ 10+ 渠道 | ❌ 无 | ⚠️ Web Only |
| **视频生成能力** | ❌ 无 | ❌ 无 | ✅ 核心能力 | ✅ 核心能力 |
| **Web UI** | ❌ 无 | ✅ 内置 | ❌ 无 | ✅ 完整 |
| **用户系统** | ❌ 无 | ❌ 无 | ❌ 无 | ✅ 完整 |
| **计费系统** | ❌ 无 | ❌ 无 | ❌ 无 | ✅ 完整 |
| **任务队列** | ⚠️ 调度器 | ❌ 无 | ❌ 无 | ✅ BullMQ |
| **记忆系统** | ✅ LTM | ✅ Dream | ❌ 无 | ⚠️ 数据库 |
| **生产部署** | ✅ 企业级 | ✅ Docker | ⚠️ 实验性 | ✅ Docker |
| **多语言** | Java 生态 | Python 生态 | Python 生态 | Node 生态 |

---

## 四、融合架构设计

### 4.1 整体架构

```
┌────────────────────────────────────────────────────────────────────┐
│                          用户接入层                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │ waoowaoo     │  │ nanobot      │  │ nanobot channels          │  │
│  │ Web UI       │  │ WebUI        │  │ Telegram/Discord/微信...   │  │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
                              ↓ HTTP / WebSocket
┌────────────────────────────────────────────────────────────────────┐
│                     Orchestrator Agent                              │
│                   (agentscope-java 核心)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │ 需求理解    │→ │ 任务规划    │→ │ 动态调度    │                  │
│  │ (LLM)       │  │ (ReAct)     │  │ (Skills)    │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                              ↓                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    Hook System                               │   │
│  │  人机协同 / 安全中断 / 优雅取消 / 进度推送                    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────┘
                              ↓ Tool Calling
┌────────────────────────────────────────────────────────────────────┐
│                       Skills Registry                                │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │
│  │ 小说分析     │ │ 角色提取     │ │ 场景提取     │ │ 剧本生成   │ │
│  │ (ViMax)      │ │ (ViMax)      │ │ (ViMax)      │ │ (ViMax)    │ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │
│  │ 分镜设计     │ │ 图片生成     │ │ 视频合成     │ │ 配音生成   │ │
│  │ (ViMax)      │ │ (waoowaoo)   │ │ (waoowaoo)   │ │ (waoowaoo) │ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │
│  │ 一致性校验   │ │ 参考图选择   │ │ 最佳图选择   │ │ 风格迁移   │ │
│  │ (ViMax)      │ │ (ViMax)      │ │ (ViMax)      │ │ (waoowaoo) │ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │
└────────────────────────────────────────────────────────────────────┘
                              ↓ 记忆持久化
┌────────────────────────────────────────────────────────────────────┐
│                      Workflow Memory                                │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 成功案例库：成功的提示词、参数组合、模型选择                  │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 用户偏好：风格偏好、常用设置、历史记录                        │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 模板库：可复用的提示词模板、工作流模板                        │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
```

### 4.2 分层职责

| 层级 | 来源 | 职责 | 技术 |
|------|------|------|------|
| **接入层** | waoowaoo + nanobot | 用户交互、渠道接入 | Next.js / WebSocket |
| **决策层** | agentscope-java | 需求理解、任务规划、动态调度 | ReAct / Tools |
| **能力层** | ViMax + waoowaoo | 具体的生成能力 | Skills / MCP |
| **记忆层** | agentscope-java | 案例记忆、用户偏好 | LTM / DB |

### 4.3 数据流

```
用户请求
    ↓
接入层（Web/渠道）接收
    ↓
决策层（ReActAgent）理解需求
    ↓
┌─────────────────────────────────────┐
│ 自主规划：                           │
│ 1. 分析用户需求                      │
│ 2. 拆解为子任务                      │
│ 3. 选择合适的 Skills                 │
│ 4. 动态调整执行顺序                  │
│ 5. 遇到问题时尝试其他方案            │
│ 6. 完成后保存成功案例                │
└─────────────────────────────────────┘
    ↓
调用 Skills（ViMax + waoowaoo 能力）
    ↓
生成结果 + 记忆持久化
    ↓
返回用户
```

---

## 五、实施路径

### Phase 1: 底座搭建（基于 agentscope-java）

**目标：** 构建基础的 Orchestrator Agent

**任务：**
1. 创建 Maven 项目，引入 agentscope-core
2. 实现 NovelToVideoAgent（继承 ReActAgent）
3. 设计 System Prompt（理解用户需求）
4. 实现基础的事件流和进度推送

**产出：**
```java
public class NovelToVideoAgent extends ReActAgent {
    public NovelToVideoAgent(Model model) {
        super(builder()
            .name("NovelToVideoAssistant")
            .sysPrompt(NOVEL_TO_VIDEO_SYSTEM_PROMPT)
            .model(model)
            .tools(registerSkills())
            .memory(new WorkflowMemory())
            .build());
    }
    
    private List<Tool> registerSkills() {
        return List.of(
            new NovelAnalysisSkill(),
            new CharacterExtractionSkill(),
            new StoryboardDesignSkill(),
            new ImageGenerationSkill(),
            new VideoSynthesisSkill()
        );
    }
}
```

### Phase 2: Skills 迁移（ViMax → Java）

**目标：** 将 ViMax 的核心能力封装为 Skills

**任务：**
1. 迁移 NovelCompressor（小说压缩）
2. 迁移 CharacterExtractor（角色提取）
3. 迁移 SceneExtractor（场景提取）
4. 迁移 Screenwriter（剧本生成）
5. 迁移 StoryboardArtist（分镜设计）
6. 迁移 BestImageSelector（最佳图选择）

**策略：**
- 核心逻辑用 Java 重写（性能考虑）
- 或通过 MCP 调用 Python 服务（快速集成）

### Phase 3: 能力整合（waoowaoo）

**目标：** 整合 waoowaoo 的 Web 能力

**任务：**
1. 保留 waoowaoo Web UI 作为前端
2. 后端 API 改造，调用 agentscope-java
3. 整合任务队列（BullMQ → Java Worker）
4. 整合计费系统

**接口设计：**
```typescript
// waoowaoo API 改造
export async function POST(request: NextRequest) {
    const { novel, style, userId } = await request.json();
    
    // 调用 Java Agent 服务
    const response = await fetch('http://agent-server:8080/api/generate', {
        method: 'POST',
        body: JSON.stringify({
            userId,
            request: `将这段小说生成视频：${novel}，风格：${style}`,
            context: { style }
        })
    });
    
    return NextResponse.json(await response.json());
}
```

### Phase 4: 记忆系统

**目标：** 实现成功案例的保存和复用

**任务：**
1. 设计案例数据结构
2. 实现语义检索（向量数据库）
3. 实现提示词模板复用
4. 实现用户偏好学习

**数据结构：**
```java
public class SuccessCase {
    private String caseId;
    private String userRequest;      // 用户原始需求
    private List<String> steps;      // 执行步骤
    private Map<String, Object> params;  // 参数组合
    private String resultSummary;    // 结果摘要
    private float rating;            // 用户评分
    private Instant createdAt;
}
```

### Phase 5: 渠道接入（nanobot）

**目标：** 复用 nanobot 的多渠道能力

**任务：**
1. nanobot 作为 Gateway，通过 A2A 协议调用后端
2. 或将 nanobot 的渠道模块移植为 Java 实现

**架构：**
```
用户 → Telegram/微信/Discord → nanobot → A2A → agentscope-java
```

---

## 六、技术挑战与解决方案

### 6.1 跨语言调用

| 挑战 | 解决方案 |
|------|---------|
| Python → Java 迁移 | 方案A：核心逻辑用 Java 重写<br>方案B：通过 MCP 协议调用 Python 服务 |
| TypeScript → Java | REST API / gRPC 通信 |

### 6.2 状态管理

| 挑战 | 解决方案 |
|------|---------|
| 长任务状态 | agentscope-java 的 PlanNotebook + 持久化 |
| 跨渠道会话 | 统一 Session ID + Redis 存储 |

### 6.3 性能优化

| 挑战 | 解决方案 |
|------|---------|
| 图片生成慢 | 并行生成 + 最佳选择策略（ViMax 已有） |
| 视频合成慢 | 异步队列 + 进度推送 |

### 6.4 成本控制

| 挑战 | 解决方案 |
|------|---------|
| Agent 决策成本 | 合理设计 System Prompt，减少无效推理 |
| 生成成本 | 复用成功案例，避免重复生成 |

---

## 七、总结

### 核心思路

| 项目 | 角色 | 贡献 |
|------|------|------|
| **agentscope-java** | 大脑 | ReAct 自主决策、工具调度、记忆管理 |
| **ViMax** | 双手 | 视频生成的专业技能 |
| **waoowaoo** | 脸面 | 用户交互、项目管理、计费 |
| **nanobot** | 嘴巴 | 多渠道接入、即时通讯 |

### 预期效果

**传统方式：**
```
用户需要懂：提示词、模型选择、分镜设计、参数调整...
用户体验差，门槛高，难以复用
```

**融合后：**
```
用户只需说：把这段小说做成视频
Agent 自主：理解需求 → 规划步骤 → 调用 Skills → 动态调整 → 记忆复用
用户零门槛，成功案例自动保存，下次自动复用
```

---

## 附录：项目仓库

| 项目 | 地址 |
|------|------|
| agentscope-java | https://github.com/agentscope-ai/agentscope-java |
| nanobot | https://github.com/HKUDS/nanobot |
| ViMax | https://github.com/HKUDS/ViMax |
| waoowaoo | https://github.com/saturndec/waoowaoo |
