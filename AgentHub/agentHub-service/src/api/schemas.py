"""
Pydantic 数据模型
"""
from typing import Optional, List
from pydantic import BaseModel, Field


class RecommendRequest(BaseModel):
    """推荐请求"""
    query: str = Field(..., min_length=1, description="用户需求描述")


class RecommendResult(BaseModel):
    """单条推荐结果"""
    agent_id: int
    name: str
    score: float = Field(..., ge=0, le=1)
    reason: str = ""
    matched_capabilities: List[str] = []


class ApiResponse(BaseModel):
    """统一响应格式"""
    code: int = 200
    message: str = "success"
    data: Optional[dict] = None

