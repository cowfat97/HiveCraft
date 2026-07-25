"""
推荐引擎 — 基于 LLM 的 Agent 匹配
"""
import json
import re
from typing import List, Optional

from openai import AsyncOpenAI

from src.config import config


class RecommendEngine:
    """Agent 推荐引擎"""

    SYSTEM_PROMPT = """你是一个 Agent 推荐系统。根据用户需求，从可用 Agent 列表中选择最匹配的 Agent，按相关度降序排列。

## 规则
1. 仔细分析用户需求的关键词和意图
2. 匹配 Agent 的 description 和 capabilities 字段
3. 只返回确实相关的 Agent，不相关的不返回
4. score 为 0-1 之间的小数，表示匹配度
5. reason 用一句话解释匹配原因
6. matched_capabilities 列出匹配到的能力项

## 输出格式
{"recommendations": [{"agent_id": 1, "name": "...", "score": 0.95, "reason": "...", "matched_capabilities": ["..."]}]}

如果没有匹配的 Agent，返回 {"recommendations": []}"""

    def __init__(self):
        llm = config.llm
        self.client = AsyncOpenAI(
            api_key=llm.api_key,
            base_url=llm.base_url,
            timeout=llm.timeout,
        )
        self.model = llm.model
        self.temperature = 0.1
        self.max_tokens = 500

    async def recommend(self, query: str, agents: List[dict]) -> List[dict]:
        """根据用户查询推荐 Agent"""
        if not agents:
            return []

        user_prompt = self._build_user_prompt(query, agents)

        response = await self.client.chat.completions.create(
            model=self.model,
            messages=[
                {"role": "system", "content": self.SYSTEM_PROMPT},
                {"role": "user", "content": user_prompt},
            ],
            temperature=self.temperature,
            max_tokens=self.max_tokens,
        )

        content = response.choices[0].message.content
        return self._parse_response(content)

    def _build_user_prompt(self, query: str, agents: List[dict]) -> str:
        agent_lines = []
        for agent in agents:
            caps = agent.get("capabilities", "{}")
            agent_lines.append(
                f"- ID:{agent.get('id')}, "
                f"名称:{agent.get('name')}, "
                f"描述:{agent.get('description', '无')}, "
                f"能力:{caps}"
            )

        return f"""## 可用 Agent
{chr(10).join(agent_lines[:50])}

## 用户需求
{query}

## 输出（严格按 JSON 格式）"""

    def _parse_response(self, content: str) -> List[dict]:
        try:
            try:
                data = json.loads(content.strip())
            except json.JSONDecodeError:
                json_match = re.search(r"\{.*\}", content, re.DOTALL)
                if json_match:
                    data = json.loads(json_match.group())
                else:
                    return []

            recommendations = data.get("recommendations", [])

            result = []
            for r in recommendations:
                result.append({
                    "agent_id": int(r.get("agent_id", 0)),
                    "name": r.get("name", ""),
                    "score": min(1.0, max(0.0, float(r.get("score", 0)))),
                    "reason": r.get("reason", ""),
                    "matched_capabilities": r.get("matched_capabilities", []),
                })
            return result
        except Exception:
            return []


# 全局实例
recommend_engine: Optional[RecommendEngine] = None


def init_recommend_engine():
    global recommend_engine
    if config.llm.api_key:
        recommend_engine = RecommendEngine()
