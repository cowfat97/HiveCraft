# Recommendation Service

AgentHub 子项目，Agent 推荐服务（端口 8082）。通过 REST API 获取 Agent 数据，结合 LLM 实现智能推荐。

## 快速开始

```bash
pip install -r requirements.txt
export LLM_API_KEY="sk-xxx"
export LLM_BASE_URL="https://dashscope.aliyuncs.com/compatible-mode/v1"
export LLM_MODEL="qwen-turbo"
python -m src.main
```

## API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/agents` | Agent 列表 |
| GET | `/api/v1/agents/search` | 搜索 Agent |
| GET | `/api/v1/agents/{id}` | Agent 详情 |
| GET | `/api/v1/cards` | Card 列表 |
| GET | `/api/v1/cards/{id}` | Card 详情 |
| GET | `/api/v1/health` | 健康检查 |
| POST | `/api/v1/recommend` | Agent 推荐（待实现） |

## License

MIT
