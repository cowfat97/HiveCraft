"""
API 路由定义
"""
from fastapi import APIRouter, HTTPException
import httpx

from src.api.schemas import ApiResponse, RecommendRequest
from src.data_client import data_client
from src.recommender import recommend_engine

api_router = APIRouter()


def _handle_network_error(e, target="data"):
    if isinstance(e, httpx.ConnectError):
        raise HTTPException(status_code=502, detail=f"{target} 服务不可达: {e}")
    if isinstance(e, httpx.TimeoutException):
        raise HTTPException(status_code=504, detail=f"{target} 服务超时")
    raise e


# ── Agent 代理 ──

@api_router.get("/agents")
async def list_agents():
    try:
        return ApiResponse(data=await data_client.list_agents())
    except Exception as e:
        _handle_network_error(e)


@api_router.get("/agents/search")
async def search_agents(name: str = None, status: str = None,
                        page_num: int = 1, page_size: int = 10):
    try:
        return ApiResponse(data=await data_client.search_agents(
            name=name, status=status,
            page_num=page_num, page_size=page_size,
        ))
    except Exception as e:
        _handle_network_error(e)


@api_router.get("/agents/{agent_id}")
async def get_agent(agent_id: int):
    try:
        return ApiResponse(data=await data_client.get_agent(agent_id))
    except Exception as e:
        _handle_network_error(e)


# ── Card 代理 ──

@api_router.get("/cards")
async def list_cards(keyword: str = None, page_num: int = 1, page_size: int = 10):
    try:
        return ApiResponse(data=await data_client.list_cards(
            keyword=keyword, page_num=page_num, page_size=page_size,
        ))
    except Exception as e:
        _handle_network_error(e)


@api_router.get("/cards/{card_id}")
async def get_card(card_id: int):
    try:
        return ApiResponse(data=await data_client.get_card(card_id))
    except Exception as e:
        _handle_network_error(e)


# ── 推荐 ──

@api_router.post("/recommend")
async def recommend(request: RecommendRequest):
    if recommend_engine is None:
        raise HTTPException(status_code=503, detail="推荐引擎未初始化，请配置 LLM_API_KEY")

    try:
        agents_data = await data_client.list_agents()
        agents = agents_data.get("agents", [])
    except Exception as e:
        _handle_network_error(e)

    try:
        results = await recommend_engine.recommend(request.query, agents)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"LLM 推荐失败: {e}")

    return ApiResponse(data={
        "recommendations": results,
        "total": len(results),
    })


# ── 健康 ──

@api_router.get("/health")
async def health():
    return {"status": "ok", "service": "agent-recommendation-service"}

