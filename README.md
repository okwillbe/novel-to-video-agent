# Novel to Video Agent

一个基于 **agentscope-java** 的智能小说转视频 Agent 系统。

## 特性

- **自主决策** - 用户只需描述需求，Agent 自动规划执行流程
- **Skills 库** - MySQL 存储 Skills，支持版本管理、权限控制、使用统计
- **ReAct 推理** - 基于 agentscope-java 的 ReActAgent 实现智能决策
- **多渠道接入** - 支持 Web、Telegram、微信等多渠道

## 技术栈

### 后端
- Java 21 + Spring Boot 3.2
- agentscope-java (ReActAgent + Skills 系统)
- MySQL 8.0 (Skills Repository)
- Redis Streams (任务队列)
- MinIO (文件存储)

### 前端
- Vue 3 + TypeScript
- Vite 6 + Pinia
- Element Plus + Tailwind CSS

## 快速开始

### 方式一：Docker Compose（推荐）

```bash
# 启动所有服务
docker compose up -d

# 访问应用
# API: http://localhost:18080
# MinIO Console: http://localhost:19001
```

### 方式二：本地开发

```bash
# 启动基础设施
docker compose up mysql redis minio -d

# 初始化数据库
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-schema.sql
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-preset-skills.sql
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-providers.sql

# 构建并运行后端
cd novel-to-video-agent
mvn clean package -DskipTests
java -jar agent-web/target/agent-web-*.jar

# 运行前端
cd frontend
npm install
npm run dev
```

## 项目结构

```
novel-to-video-agent/
├── docs/                   # 文档
│   ├── requirements.md     # 需求文档
│   ├── technical-solution.md  # 技术方案
│   └── four-projects-analysis.md  # 四项目分析
├── sql/                    # 数据库脚本
│   ├── init-schema.sql     # 表结构
│   ├── init-preset-skills.sql  # 预置 Skills
│   └── init-providers.sql  # AI 服务商配置
├── agent-core/             # 核心 Agent 引擎
│   ├── entity/             # 数据实体
│   ├── mapper/             # MyBatis Mapper
│   ├── service/            # 业务服务
│   ├── engine/             # OrchestratorAgent
│   ├── queue/              # Redis Streams 任务队列
│   └── config/             # 配置类
├── agent-api/              # REST API 模块
│   └── controller/         # API 控制器
├── agent-worker/           # Worker 模块（后台任务处理）
├── agent-web/              # Monolithic Web 模块
├── frontend/               # Vue 前端
│   ├── src/
│   │   ├── api/            # API 调用
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   └── router/         # 路由配置
└── docker-compose.yml      # Docker 配置
```

## API 接口

### 生成视频
```
POST /api/v1/generate
{
  "content": "小说文本...",
  "style": "武侠",
  "duration": 180
}

Response:
{
  "success": true,
  "data": {
    "taskId": "task_abc123",
    "status": "processing",
    "estimatedTime": 900
  }
}
```

### 查询进度
```
GET /api/v1/tasks/{taskId}

Response:
{
  "success": true,
  "data": {
    "taskId": "task_abc123",
    "status": "processing",
    "progress": 45,
    "currentStep": "image_generation",
    "steps": [...]
  }
}
```

### Skills 管理
```
GET /api/v1/skills          # 列出所有 Skills
GET /api/v1/skills/popular  # 热门 Skills
POST /api/v1/skills         # 创建新 Skill
```

## Skills 库

系统预置 11 个核心 Skills：

| Skill | 描述 |
|-------|------|
| novel-analysis | 小说分析，提取角色/场景/剧情 |
| character-extraction | 角色提取及视觉特征 |
| scene-extraction | 场景识别和环境描述 |
| script-generation | 剧本生成 |
| storyboard-design | 分镜设计 |
| prompt-optimization | 提示词优化 |
| image-generation | 图片生成 |
| video-synthesis | 视频合成 |
| voice-generation | 配音生成 |
| consistency-check | 一致性校验 |
| style-transfer | 风格迁移 |

## 参考项目

- [agentscope-java](https://java.agentscope.io/) - Agent 框架
- [ViMax](https://github.com/HKUDS/ViMax) - 视频生成能力参考
- [waoowaoo](https://github.com/saturndec/waoowaoo) - Web UI、计费、任务队列参考
- [nanobot](https://nanobot.wiki/) - 多渠道接入参考

## 开发计划

- [x] Phase 1: 基础架构
- [ ] Phase 2: 核心功能 - Agent 决策、Skills 执行
- [ ] Phase 3: 用户系统 - 认证、配额管理
- [ ] Phase 4: Web UI 完善
- [ ] Phase 5: 多渠道接入
- [ ] Phase 6: 测试上线

## License

MIT