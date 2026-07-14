# CLAUDE.md - Novel to Video Agent

> 此文件记录项目开发思路和 Claude Code 的协作指南

## 项目概述

基于 agentscope-java 的智能小说转视频 Agent 系统。

**核心理念**：渐进式开发，先实现核心 Agent 能力，再扩展多用户等功能。

---

## 开发策略

### 单用户模式（当前阶段）

- **思路**：先跑通核心功能，用户系统暂时简化
- **实现**：用户表只有一条默认记录 `user_default`
- **默认账号**：admin@novel2video.local / admin123
- **后续扩展**：启用 JWT 认证、OAuth、多用户注册

### 为什么先单用户？

1. **聚焦核心**：Agent 的 ReAct 推理、Skills 执行是核心难点
2. **快速验证**：减少用户系统的复杂度，快速验证业务逻辑
3. **预留扩展**：数据库表结构已设计完整，后续加功能成本低

---

## 技术架构

```
用户请求 → OrchestratorAgent → MySQL Skills库 → 执行步骤 → 返回结果
    ↓
  Redis Streams (任务队列)
    ↓
  Worker (调用外部AI API)
```

### 核心组件

| 组件 | 作用 | 状态 |
|------|------|------|
| OrchestratorAgent | ReAct 决策引擎 | ✅ 骨架完成 |
| SkillService | MySQL Skills CRUD | ✅ 完成 |
| TaskService | 任务生命周期管理 | ✅ 完成 |
| TaskQueueService | Redis Streams 队列 | ✅ 骨架完成 |
| UserService | 用户管理（单用户） | ✅ 完成 |
| AI Provider | 调用外部AI服务 | ⏳ 待实现 |

---

## 分阶段实现

### Phase 1: 基础功能（当前）

**目标**：实现一个完整的最小可用流程

- [x] Maven 项目骨架
- [x] 数据库表结构和初始化
- [x] Entity/Mapper/Service 基础代码
- [x] REST API 控制器
- [x] Vue 前端骨架
- [ ] **Worker 执行逻辑** - 真正执行 Skills
- [ ] **AI Provider 集成** - 调用 Google Gemini / FAL 等
- [ ] **进度推送** - WebSocket 或 SSE
- [ ] **MinIO 文件存储** - 上传下载文件

### Phase 2: 核心能力

- [ ] 章节分析（小说文本处理）
- [ ] 角色提取和一致性图片生成
- [ ] 分镜脚本生成
- [ ] 视频合成（调用 Kling 等）

### Phase 3: 用户系统扩展

- [ ] JWT 认证
- [ ] 多用户注册/登录
- [ ] 配额管理完善
- [ ] 计费系统（SHADOW 模式）

---

## 代码规范

### Java

- 使用 Lombok 简化代码
- Service 层负责业务逻辑，Controller 只做参数转换
- 配置类放在 `config` 包
- 异常使用 RuntimeException（后续可改进）

### Vue

- 使用 `<script setup>` 语法
- API 调用集中在 `api/index.ts`
- 使用 Element Plus 组件
- Tailwind CSS 使用 `tw-` 前缀

---

## 环境变量

```bash
# 数据库
DB_HOST=mysql
DB_PORT=3306
DB_NAME=novel2video
DB_USER=root
DB_PASSWORD=novel2video123

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# MinIO
MINIO_ENDPOINT=http://minio:9000
MINIO_BUCKET=novel2video
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# AI Provider Keys（设置页面配置）
GOOGLE_API_KEY=xxx
FAL_API_KEY=xxx
```

---

## 启动命令

```bash
# Docker 启动基础设施
docker compose up mysql redis minio -d

# 初始化数据库
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-schema.sql
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-preset-skills.sql
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-providers.sql
mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-default-user.sql

# 启动后端
mvn spring-boot:run -pl agent-web

# 启动前端
cd frontend && npm run dev
```

---

## 参考项目

- **waoowaoo**: D:\github\waoowaoo - 任务队列、计费、AI Provider 参考
- **agentscope-java**: Skills 系统架构参考

---

## 注意事项

1. **当前为单用户模式**，所有 API 不需要认证
2. **Skills 内容是 Markdown + YAML frontmatter 格式**
3. **任务队列用 Redis Streams**（替代 BullMQ）
4. **Worker 模块目前只是骨架**，需要实现具体执行逻辑