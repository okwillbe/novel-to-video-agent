# novel-to-video-agent — 可重复的一次通过测试手册（Machine-friendly）

目标
- 让 AI/CI 在“干净环境”里按步骤运行并第一次通过后端与前端测试。

必备工具与精确版本（强制）
- JDK 17 (Temurin/OpenJDK) — test: java -version
- Maven 3.8+ — test: mvn -v
- Node 18.x — test: node -v
- npm 9.x 或 pnpm — test: npm -v
- Docker 24+、docker-compose v2 — test: docker compose version
- nc (netcat) 用于端口等待

固定依赖镜像（推荐在 docker-compose.yml 中使用）
- mysql:8.0.36
- redis:7.2
- minio/minio:RELEASE.2024-xx-xx  （请 pin 一个发布版本）

仓库约定的文件（必须存在）
- docker-compose.yaml (在仓库根)
- sql/init-schema.sql
- sql/init-preset-skills.sql
- sql/init-providers.sql
- sql/init-default-user.sql
- 后端模块：agent-web
- 前端目录：frontend

环境变量 (.env.example)
- DB_HOST=127.0.0.1
- DB_PORT=13306          # 本地映射端口
- DB_NAME=novel2video
- DB_USER=root
- DB_PASSWORD=novel2video123
- REDIS_HOST=127.0.0.1
- REDIS_PORT=6379
- MINIO_ENDPOINT=http://127.0.0.1:9000
- MINIO_ACCESS_KEY=minioadmin
- MINIO_SECRET_KEY=minioadmin

一键脚本（位置）
- scripts/init-db.sh      # 初始化数据库
- scripts/run-tests.sh    # 启动依赖并运行所有测试
- logs/                   # 运行时日志输出目录
- report/last-failure/    # 失败时保存 diagnostc

启动依赖（本地/CI）
- docker compose -f docker-compose.yaml up mysql redis minio -d
- 在 CI 中，服务映射请使用 jobs.services

数据库初始化（脚本化 - scripts/init-db.sh）
- mysql -h 127.0.0.1 -P 13306 -u root -pnovel2video123 novel2video < sql/init-schema.sql
- 依次执行其他 init-*.sql 文件（脚本中实现重试与超时）

运行测试（脚本化 - scripts/run-tests.sh）
- 等待依赖就绪（nc / http health check，超时 120s）
- 执行 scripts/init-db.sh
- 后端测试:
    - cd agent-web
    - mvn -T1C -DskipITs=false test  # 或仓库默认的测试命令
    - 成功判定：退出码 0 且最后 50 行包含 "BUILD SUCCESS"
- 前端测试（若存在）:
    - cd frontend
    - npm ci
    - npm test
    - 成功判定：退出码 0 或输出包含 "All tests passed"

成功判定（agent/CI 用）
- 所有步骤退出码为 0
- 文件 logs/backend-test.log 和 logs/frontend-test.log 已生成且包含相应“success”关键字
- 脚本结束时打印 EXACT: TESTS PASSED: all modules

错误处理与报告
- 若失败：在 report/last-failure/ 写入 build.log、failing-tests.txt、docker-logs.txt、reproduction-commands.txt
- agent 必须抓取第一条堆栈、失败的 test 名称，并按 JSON 格式输出到控制台以便自动解析

注意事项
- 请在 docker-compose.yaml 中 pin 依赖镜像版本
- 在 CI 与本地一致的镜像/版本下验证一次

## 参考项目

- **waoowaoo**: D:\github\waoowaoo - 任务队列、计费、AI Provider 参考
- **agentscope-java**: Skills 系统架构参考

---

## 注意事项

1. **当前为单用户模式**，所有 API 不需要认证
2. **Skills 内容是 Markdown + YAML frontmatter 格式**
3. **任务队列用 Redis Streams**（替代 BullMQ）
4. **Worker 模块目前只是骨架**，需要实现具体执行逻辑