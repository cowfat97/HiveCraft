# QaSystem

综合问答系统，支持 MySQL 关键词检索 + Milvus 语义检索双路问答。

## 架构

```
离线：笔记 .md → 切分 → embedding → Milvus
在线：用户问题 → 两路召回（MySQL BM25 / Milvus 语义）→ 重排序 → LLM 生成
```

## 模块

| 目录 | 用途 |
|------|------|
| `main.py` | 入口，RAG 问答主流程 |
| `base/` | 配置（DashScope API）与日志 |
| `cli/` | Milvus 命令行工具 |
| `model/` | 模型定义 |
| `mysql_qa/` | MySQL + BM25 关键词检索，含 Redis 缓存 |
| `rag_qa/` | RAG 核心：文档处理、向量存储、查询分类、策略选择、教育文档加载器/分割器 |

## 技术栈

Python 3.11 / DashScope API / Milvus / MySQL / Redis / BGE-M3

## 运行

```bash
pip install -r rag_qa/requirements.txt
python main.py
```
