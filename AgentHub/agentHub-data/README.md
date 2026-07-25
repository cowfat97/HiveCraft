# AgentHub

<div align="center">

**Agent 注册与发现平台**

[![Java](https://img.shields.io/badge/Java-1.8-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue)](https://www.postgresql.org/)

</div>

---

## 简介

AgentHub 是一个 Agent 注册管理平台，参考 Google A2A 协议设计 Agent Card + Skill 模型。采用 DDD（领域驱动设计）架构，前后端分离。

## 功能特性

- **Agent 注册发现** — Agent 注册到平台，支持 A2A 协议字段，ONLINE/OFFLINE 状态管理
- **Agent Card** — Agent 公开展示卡片，DRAFT → PUBLISHED → ARCHIVED 状态机
- **Agent Skill** — 一个 Card 下挂多个 Skill，描述 Agent 具体能力
- **点赞评论** — 对 Card 进行点赞和评论互动，支持嵌套回复
- **收藏功能** — Card 可收藏，支持收藏夹分组管理
- **评分功能** — 对 Card 进行 1-5 分星级评分
- **请求日志** — 完整的 API 请求日志记录与追踪

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 开发语言 |
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis | 2.3.1 | ORM |
| PostgreSQL | 14+ | 数据库 |
| Vue | 3.x | 前端框架 |
| Element Plus | - | UI 组件库 |

## 项目结构

```
agenthub-ddd/
├── agenthub-app/             # 启动模块 (端口 8080)
├── agenthub-common/          # 公共模块
├── agenthub-infrastructure/  # 基础设施层
├── agenthub-agent/           # Agent 注册+发现
├── agenthub-card/            # Agent Card + Skill
├── agenthub-recommendation/  # 点赞/评论/评分/收藏
├── agenthub-gateway/         # 请求日志
└── agenthub-web/             # Vue 3 前端
```

## 快速开始

### 环境要求

- JDK 1.8+、Maven 3.6+、PostgreSQL 14+、Node.js 18+

### 启动

```bash
# macOS
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

# 数据库
psql -h localhost -U postgres -c "CREATE DATABASE agenthub;"
psql -h localhost -U postgres -d agenthub -f agenthub-infrastructure/src/main/resources/sql/init.sql

# 后端 (端口 8080)
mvn clean install -DskipTests
mvn spring-boot:run -pl agenthub-app

# 前端 (端口 3000)
cd agenthub-web
npm install && npm run dev
```

## API 概览

| 前缀 | 说明 |
|------|------|
| `/api/v1/agents` | Agent 注册/发现/状态管理 |
| `/api/v1/cards` | Card CRUD + publish/archive |
| `/api/v1/cards/{cardId}/skills` | Skill CRUD |
| `/api/v1/comments` | 评论（嵌套回复） |
| `/api/v1/likes` | 点赞 |
| `/api/v1/scores` | 评分 |
| `/api/v1/favorites` | 收藏 + 收藏夹 |

## 数据库表

| 表名 | 说明 |
|------|------|
| agents | Agent 注册信息（含 A2A 字段） |
| agent_cards | Agent Card |
| agent_skills | Agent Skill |
| comments | 评论 |
| likes | 点赞记录 |
| scores | 评分记录 |
| favorite_folders | 收藏夹 |
| favorites | 收藏项 |
| request_logs | API 请求日志 |

## License

[MIT License](LICENSE)
