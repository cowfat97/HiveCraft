"""
agenthub-data REST 客户端 — 直接调用 data 模块的 REST API
"""
from typing import Optional

import httpx

from src.config import config


class DataClient:
    """agenthub-data REST 客户端"""

    def __init__(self, base_url: str, timeout: int = 30):
        self.base_url = base_url.rstrip("/")
        self._client = httpx.AsyncClient(
            timeout=timeout,
            headers={"Content-Type": "application/json"},
        )

    async def close(self):
        await self._client.aclose()

    async def _get(self, path: str, **params) -> dict:
        params = {k: v for k, v in params.items() if v is not None}
        response = await self._client.get(f"{self.base_url}{path}", params=params)
        response.raise_for_status()
        wrapped = response.json()
        return wrapped.get("data", wrapped)

    async def _post(self, path: str, body: dict) -> dict:
        response = await self._client.post(f"{self.base_url}{path}", json=body)
        response.raise_for_status()
        wrapped = response.json()
        return wrapped.get("data", wrapped)

    # ── Agent ──

    async def list_agents(self) -> dict:
        return await self._get("/api/v1/agents")

    async def get_agent(self, agent_id: int) -> dict:
        return await self._get(f"/api/v1/agents/{agent_id}")

    async def search_agents(self, *, name: str = None, status: str = None,
                            page_num: int = 1, page_size: int = 10) -> dict:
        return await self._get("/api/v1/agents/query",
                               name=name, status=status,
                               pageNum=page_num, pageSize=page_size)

    # ── Card ──

    async def list_cards(self, *, keyword: str = None,
                         page_num: int = 1, page_size: int = 10) -> dict:
        return await self._get("/api/v1/cards/search" if keyword else "/api/v1/cards",
                               keyword=keyword, pageNum=page_num, pageSize=page_size)

    async def get_card(self, card_id: int) -> dict:
        return await self._get(f"/api/v1/cards/{card_id}")


# 全局实例
data_client: Optional[DataClient] = None


def init_data_client():
    global data_client
    data_client = DataClient(
        base_url=config.data_server_url,
        timeout=config.data_server_timeout,
    )
