# Novel to Video Agent - 需求文档

> 版本：1.0.0
> 日期：2026-06-12
> 状态：草稿

---

## 一、项目概述

### 1.1 项目背景

当前存在四个独立项目，各有优势但功能割裂：

| 项目 | 优势 | 劣势 |
|------|------|------|
| agentscope-java | 完整的 Agent 框架、ReAct 推理、Skills 系统 | 缺乏视频生成能力、无 Web UI |
| nanobot | 多渠道接入、轻量级 | 视频能力弱、无企业级功能 |
| ViMax | 专业视频生成、多智能体协作 | 无用户系统、无渠道接入 |
| waoowaoo | 完整 Web UI、用户系统、计费 | 流程固定、无自主决策 |

### 1.2 项目目标

构建一个**智能化的小说到视频 Agent 系统**：

1. **自主决策** — 用户只需描述需求，Agent 自动规划执行
2. **能力复用** — 成功案例保存为 Skills，自动学习改进
3. **多端接入** — 支持 Web、Telegram、微信等多渠道
4. **企业就绪** — 用户管理、计费、权限控制

### 1.3 核心价值

```
传统方式：
用户 → 学习提示词 → 选择模型 → 配置参数 → 手动调整 → 生成视频

融合后：
用户 → "把这段小说做成武侠风格视频" → Agent 自动完成 → 保存成功案例
```

---

## 二、系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         接入层                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Web UI      │  │ nanobot     │  │ nanobot channels        │  │
│  │ (waoowaoo)  │  │ WebUI       │  │ Telegram/微信/Discord   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Orchestrator Agent                           │
│                   (agentscope-java)                             │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐    │
│  │ 需求理解  │→ │ 任务规划  │→ │ Skills调用│→ │ 结果评估  │    │
│  └───────────┘  └───────────┘  └───────────┘  └───────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      Skills Registry                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    MySQL Skill Repository                  │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐         │  │
│  │  │小说分析 │ │角色提取 │ │分镜设计 │ │视频合成 │ ...     │  │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘         │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                       能力层 (Tools)                            │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐       │
│  │ 图片生成  │ │ 视频生成  │ │ 配音生成  │ │ 一致性校验│       │
│  │(waoowaoo) │ │ (ViMax)   │ │ (waoowaoo)│ │ (ViMax)   │       │
│  └───────────┘ └───────────┘ └───────────┘ └───────────┘       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                       数据层                                    │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐       │
│  │ MySQL     │ │ Redis     │ │ MinIO     │ │ 向量DB    │       │
│  │ Skills/用户│ │ 会话/队列 │ │ 文件存储  │ │ 案例检索  │       │
│  └───────────┘ └───────────┘ └───────────┘ └───────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 技术栈

| 层级 | 技术选型 |
|------|---------|
| **接入层** | Next.js 15 (waoowaoo) + nanobot channels |
| **决策层** | agentscope-java + ReActAgent |
| **能力层** | ViMax (Python) + waoowaoo Workers |
| **数据层** | MySQL 8.0 + Redis 7 + MinIO + Milvus/Qdrant |

---

## 三、MySQL Skills Repository 详细设计

### 3.1 为什么选择 MySQL 作为 Skills 源

| 需求 | MySQL 方案优势 |
|------|---------------|
| **多实例共享** | 集中式存储，多个 Agent 实例共享 Skills |
| **版本管理** | 支持版本字段，可实现 Skills 版本控制 |
| **权限控制** | 用户级、租户级 Skills 隔离 |
| **审计日志** | 记录 Skills 使用情况，支持统计分析 |
| **运维成熟** | MySQL 运维工具丰富，企业认可度高 |

### 3.2 数据库设计

#### 3.2.1 Skills 表

```sql
CREATE TABLE `skills` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT '技能唯一标识，如 novel-analysis_v1',
    `name` VARCHAR(64) NOT NULL COMMENT '技能名称',
    `description` VARCHAR(512) NOT NULL COMMENT '技能描述',
    `category` VARCHAR(32) NOT NULL COMMENT '分类：analysis/generation/synthesis/postprocess',
    `skill_content` MEDIUMTEXT NOT NULL COMMENT '技能内容（Markdown）',
    `version` VARCHAR(32) NOT NULL DEFAULT '1.0.0' COMMENT '版本号',
    `author` VARCHAR(64) DEFAULT 'system' COMMENT '作者',
    `tags` JSON DEFAULT NULL COMMENT '标签列表',
    `metadata` JSON DEFAULT NULL COMMENT '扩展元数据',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用 2-草稿',
    `is_public` TINYINT NOT NULL DEFAULT 1 COMMENT '是否公开：0-私有 1-公开',
    `owner_id` VARCHAR(64) DEFAULT NULL COMMENT '所有者ID（私有技能）',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID（多租户隔离）',
    `use_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用次数',
    `success_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '成功次数',
    `avg_rating` DECIMAL(3,2) DEFAULT NULL COMMENT '平均评分',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_id_version` (`skill_id`, `version`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_use_count` (`use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能库';
```

#### 3.2.2 Skill Resources 表

```sql
CREATE TABLE `skill_resources` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT '关联的技能ID',
    `version` VARCHAR(32) NOT NULL COMMENT '技能版本',
    `resource_path` VARCHAR(256) NOT NULL COMMENT '资源路径',
    `resource_type` VARCHAR(32) NOT NULL COMMENT '资源类型：template/config/example',
    `content` MEDIUMTEXT COMMENT '资源内容（文本）',
    `storage_url` VARCHAR(512) DEFAULT NULL COMMENT '外部存储URL（大文件）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_resource` (`skill_id`, `version`, `resource_path`),
    KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能资源';
```

#### 3.2.3 Skill Usage Logs 表

```sql
CREATE TABLE `skill_usage_logs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `log_id` VARCHAR(64) NOT NULL COMMENT '日志唯一ID',
    `skill_id` VARCHAR(128) NOT NULL COMMENT '技能ID',
    `version` VARCHAR(32) NOT NULL COMMENT '技能版本',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `session_id` VARCHAR(64) DEFAULT NULL COMMENT '会话ID',
    `project_id` VARCHAR(64) DEFAULT NULL COMMENT '项目ID',
    `input_summary` VARCHAR(512) DEFAULT NULL COMMENT '输入摘要',
    `output_summary` VARCHAR(512) DEFAULT NULL COMMENT '输出摘要',
    `execution_time_ms` INT UNSIGNED DEFAULT NULL COMMENT '执行耗时(ms)',
    `status` VARCHAR(16) NOT NULL COMMENT '状态：success/failure/timeout',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `rating` TINYINT DEFAULT NULL COMMENT '用户评分 1-5',
    `feedback` TEXT DEFAULT NULL COMMENT '用户反馈',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_log_id` (`log_id`),
    KEY `idx_skill_id` (`skill_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能使用日志';
```

#### 3.2.4 Skill Versions 表（版本历史）

```sql
CREATE TABLE `skill_versions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `skill_id` VARCHAR(128) NOT NULL COMMENT '技能ID',
    `version` VARCHAR(32) NOT NULL COMMENT '版本号',
    `change_type` VARCHAR(16) NOT NULL COMMENT '变更类型：create/update/deprecate',
    `change_summary` VARCHAR(512) DEFAULT NULL COMMENT '变更摘要',
    `previous_version` VARCHAR(32) DEFAULT NULL COMMENT '前一版本',
    `skill_content` MEDIUMTEXT NOT NULL COMMENT '该版本的技能内容',
    `changed_by` VARCHAR(64) DEFAULT NULL COMMENT '变更人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_version` (`skill_id`, `version`),
    KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能版本历史';
```

### 3.3 Java 实现

#### 3.3.1 依赖配置

```xml
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-skill-mysql-repository</artifactId>
    <version>1.0.12</version>
</dependency>
```

#### 3.3.2 Repository 配置

```java
@Configuration
public class SkillRepositoryConfig {
    
    @Bean
    public MysqlSkillRepository skillRepository(DataSource dataSource) {
        return MysqlSkillRepository.builder()
            .dataSource(dataSource)
            .tableName("skills")
            .resourceTableName("skill_resources")
            .cacheEnabled(true)
            .cacheTtl(Duration.ofMinutes(10))
            .build();
    }
}
```

#### 3.3.3 Skill CRUD 操作

```java
@Service
public class SkillService {
    
    private final MysqlSkillRepository skillRepository;
    
    /**
     * 创建新技能
     */
    public Mono<AgentSkill> createSkill(SkillCreateRequest request) {
        AgentSkill skill = AgentSkill.builder()
            .name(request.getName())
            .description(request.getDescription())
            .skillContent(request.getContent())
            .metadata(Map.of(
                "category", request.getCategory(),
                "version", request.getVersion(),
                "author", request.getAuthor(),
                "tags", request.getTags()
            ))
            .build();
        
        return Mono.fromCallable(() -> {
            skillRepository.save(skill);
            return skill;
        });
    }
    
    /**
     * 按分类查询技能
     */
    public Flux<AgentSkill> findByCategory(String category) {
        return Flux.fromIterable(
            skillRepository.findByMetadata("category", category)
        );
    }
    
    /**
     * 搜索技能（名称/描述/标签）
     */
    public Flux<AgentSkill> searchSkills(String keyword) {
        return Flux.fromIterable(
            skillRepository.search(keyword)
        );
    }
    
    /**
     * 获取热门技能
     */
    public Flux<AgentSkill> getPopularSkills(int limit) {
        return Flux.fromIterable(
            skillRepository.findTopByOrderByUseCountDesc(limit)
        );
    }
    
    /**
     * 记录使用日志
     */
    public Mono<Void> logUsage(SkillUsageLog log) {
        return Mono.fromRunnable(() -> {
            skillRepository.logUsage(log);
        });
    }
}
```

### 3.4 预置技能清单

系统启动时自动初始化以下核心技能：

| skill_id | 名称 | 分类 | 描述 |
|----------|------|------|------|
| `novel-analysis_v1` | 小说分析 | analysis | 分析小说结构、提取角色/场景/剧情 |
| `character-extraction_v1` | 角色提取 | analysis | 从文本中提取角色信息及特征 |
| `scene-extraction_v1` | 场景提取 | analysis | 识别和提取故事场景 |
| `script-generation_v1` | 剧本生成 | generation | 将小说/创意转化为剧本格式 |
| `storyboard-design_v1` | 分镜设计 | generation | 设计镜头方案和分镜脚本 |
| `prompt-optimization_v1` | 提示词优化 | generation | 优化图片/视频生成提示词 |
| `image-generation_v1` | 图片生成 | synthesis | 生成角色/场景图片 |
| `video-synthesis_v1` | 视频合成 | synthesis | 将分镜合成为视频 |
| `voice-generation_v1` | 配音生成 | synthesis | 为角色生成配音 |
| `consistency-check_v1` | 一致性校验 | postprocess | 校验角色/场景一致性 |
| `style-transfer_v1` | 风格迁移 | postprocess | 统一视觉风格 |

---

## 四、核心功能需求

### 4.1 Agent 决策系统

#### 4.1.1 需求描述

用户无需了解技术细节，只需用自然语言描述需求，Agent 自动规划并执行。

#### 4.1.2 功能要求

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 需求理解 | 解析用户意图，提取关键参数（风格、时长、角色数等） | P0 |
| 任务分解 | 将复杂需求分解为可执行的子任务序列 | P0 |
| Skills 选择 | 根据任务类型自动选择合适的 Skills | P0 |
| 动态调整 | 执行过程中根据结果动态调整策略 | P1 |
| 进度反馈 | 实时向用户推送执行进度 | P1 |
| 错误恢复 | 遇到错误时自动尝试替代方案 | P1 |

#### 4.1.3 示例场景

```
用户输入：
"把这段小说做成3分钟的武侠风格视频，主角要用我上传的照片"

Agent 执行流程：
1. [理解] 风格=武侠，时长=3分钟，角色一致性=用户照片
2. [规划] 
   - 小说分析 → 角色提取 → 场景提取
   - 剧本生成 → 分镜设计
   - 角色图片生成（绑定用户照片）
   - 视频合成 → 配音生成
3. [执行] 调用相关 Skills
4. [反馈] 实时展示进度
5. [评估] 检查结果质量
6. [记忆] 保存成功案例
```

### 4.2 Skills 管理系统

#### 4.2.1 需求描述

支持 Skills 的创建、更新、版本管理、权限控制和统计分析。

#### 4.2.2 功能要求

| 功能 | 描述 | 优先级 |
|------|------|--------|
| Skill 创建 | 通过 Web UI 或 API 创建新 Skill | P0 |
| Skill 编辑 | 在线编辑 Markdown 内容 | P0 |
| 版本管理 | 支持多版本并存，可回滚 | P1 |
| 权限控制 | 公开/私有/租户级隔离 | P1 |
| 使用统计 | 统计使用次数、成功率、评分 | P2 |
| 智能推荐 | 根据场景推荐合适的 Skills | P2 |

#### 4.2.3 Skill 管理界面

```
┌─────────────────────────────────────────────────────────────┐
│  Skills 管理中心                                             │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │ 全部    │ │ 分析    │ │ 生成    │ │ 合成    │ ...       │
│  │ (128)   │ │ (32)    │ │ (48)    │ │ (28)    │           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │
├─────────────────────────────────────────────────────────────┤
│  🔍 搜索技能...                                              │
├─────────────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 📄 novel-analysis v1.2.0         ⭐ 4.8  📊 1.2k使用  │  │
│  │ 分析小说内容，提取角色、场景、剧情                      │  │
│  │ 标签：小说 分析 NLP                                    │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 🎬 storyboard-design v2.0.0      ⭐ 4.6  📊 856使用   │  │
│  │ 根据剧本设计专业分镜方案                                │  │
│  │ 标签：分镜 镜头 视觉                                   │  │
│  └───────────────────────────────────────────────────────┘  │
│  ...                                                        │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 用户系统

#### 4.3.1 需求描述

完整的用户管理、权限控制、配额管理。

#### 4.3.2 功能要求

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 用户注册/登录 | 支持邮箱、手机、OAuth | P0 |
| 角色权限 | 管理员/普通用户/VIP | P1 |
| 配额管理 | 视频时长/生成次数限制 | P1 |
| 使用历史 | 查看历史项目和生成记录 | P1 |
| 偏好设置 | 保存用户偏好和常用配置 | P2 |

### 4.4 计费系统

#### 4.4.1 需求描述

支持按使用量计费，与 waoowaoo 计费系统对接。

#### 4.4.2 计费模式

| 模式 | 描述 | 适用场景 |
|------|------|---------|
| OFF | 不计费 | 开源版/内测 |
| SHADOW | 记录但不扣费 | 统计分析 |
| ENFORCE | 实际扣费 | 商业版 |

#### 4.4.3 计费维度

| 类型 | 单位 | 说明 |
|------|------|------|
| 文本 | token | LLM 调用 |
| 图片 | 张 | 图片生成 |
| 视频 | 秒 | 视频生成 |
| 配音 | 秒 | 语音合成 |

### 4.5 多渠道接入

#### 4.5.1 需求描述

复用 nanobot 的渠道能力，支持多平台接入。

#### 4.5.2 支持渠道

| 渠道 | 优先级 | 说明 |
|------|--------|------|
| Web UI | P0 | 主入口 |
| Telegram | P1 | 国际用户 |
| 微信 | P1 | 国内用户 |
| Discord | P2 | 社区用户 |
| 飞书 | P2 | 企业用户 |
| 钉钉 | P2 | 企业用户 |

---

## 五、API 接口设计

### 5.1 核心 API

#### 5.1.1 生成视频

```
POST /api/v1/generate

Request:
{
    "content": "小说文本或创意描述",
    "style": "武侠",
    "duration": 180,
    "options": {
        "resolution": "1080p",
        "voiceStyle": "mature",
        "characterImages": ["url1", "url2"]
    },
    "callback": "https://..."  // 可选，异步回调
}

Response:
{
    "taskId": "task_123456",
    "status": "processing",
    "estimatedTime": 300
}
```

#### 5.1.2 查询进度

```
GET /api/v1/tasks/{taskId}

Response:
{
    "taskId": "task_123456",
    "status": "processing",
    "progress": 45,
    "currentStep": "image_generation",
    "steps": [
        {"name": "novel_analysis", "status": "completed", "progress": 100},
        {"name": "script_generation", "status": "completed", "progress": 100},
        {"name": "storyboard_design", "status": "completed", "progress": 100},
        {"name": "image_generation", "status": "processing", "progress": 30},
        {"name": "video_synthesis", "status": "pending", "progress": 0},
        {"name": "voice_generation", "status": "pending", "progress": 0}
    ]
}
```

#### 5.1.3 Skills 管理

```
# 创建 Skill
POST /api/v1/skills

# 查询 Skill
GET /api/v1/skills/{skillId}

# 更新 Skill
PUT /api/v1/skills/{skillId}

# 删除 Skill
DELETE /api/v1/skills/{skillId}

# 搜索 Skills
GET /api/v1/skills?category=analysis&keyword=小说

# 获取热门 Skills
GET /api/v1/skills/popular?limit=10
```

### 5.2 WebSocket 推送

```
# 连接
ws://host/api/v1/ws?token=xxx

# 进度推送
{
    "type": "progress",
    "taskId": "task_123456",
    "step": "image_generation",
    "progress": 45,
    "message": "正在生成第 5/12 张图片"
}

# 完成推送
{
    "type": "completed",
    "taskId": "task_123456",
    "result": {
        "videoUrl": "https://...",
        "duration": 180
    }
}
```

---

## 六、非功能需求

### 6.1 性能要求

| 指标 | 要求 | 说明 |
|------|------|------|
| API 响应时间 | < 200ms | 同步接口 |
| 视频生成时间 | < 5min/分钟视频 | 不含排队 |
| 并发用户数 | 1000+ | 单实例 |
| Skills 查询 | < 50ms | 含缓存 |

### 6.2 可用性要求

| 指标 | 要求 |
|------|------|
| 系统可用性 | 99.9% |
| 数据持久性 | 99.999% |
| 故障恢复时间 | < 5min |

### 6.3 安全要求

| 要求 | 描述 |
|------|------|
| 认证授权 | JWT + OAuth2.0 |
| 数据加密 | 传输层 TLS，存储层 AES |
| SQL 注入防护 | 参数化查询 |
| XSS 防护 | 输入过滤 + 输出编码 |
| 访问控制 | RBAC 模型 |

---

## 七、实施计划

### Phase 1: 基础架构（2周）

- [ ] 创建 Java 项目骨架
- [ ] 集成 agentscope-java 核心
- [ ] 配置 MySQL Skills Repository
- [ ] 实现基础 Skills CRUD
- [ ] 单元测试

### Phase 2: 核心功能（3周）

- [ ] 实现 ReActAgent 决策逻辑
- [ ] 迁移 ViMax 核心能力为 Skills
- [ ] 实现 Tool 调用层
- [ ] 集成图片/视频生成服务
- [ ] 集成测试

### Phase 3: 用户系统（2周）

- [ ] 用户认证授权
- [ ] 项目管理
- [ ] 使用历史
- [ ] 配额管理

### Phase 4: Web UI（2周）

- [ ] 复用 waoowaoo 前端
- [ ] Skills 管理界面
- [ ] 进度展示优化
- [ ] 用户体验优化

### Phase 5: 多渠道接入（2周）

- [ ] 集成 nanobot 渠道层
- [ ] Telegram Bot
- [ ] 微信公众号
- [ ] 消息路由

### Phase 6: 测试上线（1周）

- [ ] 压力测试
- [ ] 安全测试
- [ ] 文档完善
- [ ] 部署上线

---

## 八、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| LLM 调用成本高 | 高 | 优化提示词、使用缓存、复用成功案例 |
| 视频生成慢 | 中 | 异步队列、进度反馈、并行处理 |
| 一致性差 | 高 | ViMax 一致性校验、多轮优化 |
| 用户不懂使用 | 中 | 引导教程、智能推荐、简化操作 |

---

## 九、附录

### 9.1 参考文档

- [agentscope-java 官方文档](https://java.agentscope.io/)
- [nanobot 文档](https://nanobot.wiki/)
- [ViMax GitHub](https://github.com/HKUDS/ViMax)
- [waoowaoo GitHub](https://github.com/saturndec/waoowaoo)

### 9.2 术语表

| 术语 | 定义 |
|------|------|
| Skill | 可复用的提示词模板或工作流定义 |
| Tool | 可执行的代码单元，用于调用外部服务 |
| ReAct | Reasoning + Acting，推理-行动循环 |
| MCP | Model Context Protocol，模型上下文协议 |
| A2A | Agent-to-Agent，智能体间通信协议 |
