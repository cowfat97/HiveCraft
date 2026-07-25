# AgentHub

Agent 发现平台。Agent 通过 CLI 注册，在平台上以卡片形式展示与发现。

## 架构

```
Agent 开发者 (A)                      平台                           使用者 (B)
     │                                │                                │
     │  CLI register                  │                                │
     │  ────────────────────────────→ │  注册 Agent + Card + Skill      │
     │                                │                                │
     │                                │  ←── 浏览/搜索 Agent ──────────│
     │                                │                                │
     │                                │  ←── 调用 Agent ──────────────│
     │  ←── 平台转发调用请求 ─────────│  POST /api/v1/agents/{id}/invoke
```

| 服务 | 端口 | 技术栈 | 职责 |
|------|------|--------|------|
| agenthub-data | 8080 | Spring Boot 2.7 + MyBatis + PostgreSQL | Agent/Card CRUD、社交互动、请求日志、调用转发 |
| agentHub-service | 8082 | FastAPI + httpx + OpenAI SDK | Agent 推荐引擎 |
| agentHub-cli | — | Python CLI | Agent 开发者工具：封装 Agent、注册到平台 |
| agenthub-web | 80/3001 | Vue 3 + Element Plus | 前端 SPA（Card 浏览、社交互动） |

## 项目结构

```
AgentHub/
├── agentHub-data/           # Java 后端 (DDD 多模块, :8080)
│   ├── agenthub-app/        # 启动入口
│   ├── agenthub-agent/      # Agent 管理 (22字段, 含11个A2A协议字段)
│   ├── agenthub-card/       # Card + Skill 管理 (DRAFT→PUBLISHED→ARCHIVED)
│   ├── agenthub-recommendation/  # 评论/点赞/评分/收藏
│   ├── agenthub-gateway/    # 请求日志 (RequestLogFilter)
│   ├── agenthub-common/     # 公共模块 (DTO/枚举/异常/雪花ID)
│   ├── agenthub-infrastructure/  # 持久化层 (PO/Mapper/XML)
│   └── agenthub-web/        # Vue 3 前端
├── agentHub-service/        # Python 推荐服务 (:8082)
├── agentHub-cli/            # Agent 开发者 CLI 工具
└── deploy/                  # Docker Compose
```

### agentHub-data 模块

| 模块 | 职责 |
|------|------|
| agenthub-app | 启动入口 |
| agenthub-agent | Agent CRUD、状态管理（ONLINE/OFFLINE/DISABLED） |
| agenthub-card | Card+Skill 管理（DRAFT→PUBLISHED→ARCHIVED） |
| agenthub-recommendation | 评论/点赞/评分/收藏 |
| agenthub-gateway | 请求日志（异步、脱敏） |
| agenthub-common | 共享 DTO/枚举/异常/雪花ID |
| agenthub-infrastructure | PO/Mapper/RepositoryImpl/init.sql |
| agenthub-web | Vue 3 前端（Card 浏览、社交互动） |

### 前端

Vue 3 单页应用。**仅提供浏览与互动功能**，Agent/Card 创建通过 CLI。

| 路径 | 说明 |
|------|------|
| `/` | 卡片堆叠滑动浏览（左滑跳过，右滑/点击查看详情） |
| `/card/:id` | Card 详情 + 评论 + 点赞 |

### CLI

```bash
agenthub init       # 生成 agent.yaml
agenthub serve      # 启动本地服务
agenthub register   # 注册 Agent + Card + Skill 到平台
```

## 数据库

9 张表：`agents`, `agent_cards`, `agent_skills`, `comments`, `likes`, `scores`, `favorites`, `favorite_folders`, `request_logs`

所有 ID 使用 Snowflake 算法（JavaScript 安全整数上限，后端序列化为字符串）。

## API

| 前缀 | 端点 | 说明 |
|------|------|------|
| `/api/v1/agents` | 13 | Agent CRUD + 状态 + 调用转发 |
| `/api/v1/cards` | 12 | Card CRUD + 发布/归档 + 点赞 |
| `/api/v1/cards/{id}/skills` | 4 | Skill CRUD |
| `/api/v1/comments` | 10 | 评论 + 审核 + 回复 |
| `/api/v1/likes` | 7 | 点赞/取消/批量检查 |
| `/api/v1/scores` | 4 | 评分 |
| `/api/v1/favorites` | 9 | 收藏夹 + 收藏项 |
| `/api/v1/gateway/logs` | 4 | 请求日志（admin） |

## 快速开始

```bash
# 后端
cd agentHub-data
mvn install -DskipTests && mvn spring-boot:run -pl agenthub-app

# 前端
cd agenthub-web
npm run dev        # → http://localhost:3000

# 推荐服务
cd agentHub-service
pip install -r requirements.txt
python -m src.main   # → http://localhost:8082
```

## License

MIT
