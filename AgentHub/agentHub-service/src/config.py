"""
配置管理
"""
import os
from dataclasses import dataclass
from typing import Optional


@dataclass
class LLMConfig:
    """远程 LLM 配置"""
    api_key: Optional[str] = None
    base_url: Optional[str] = None
    model: str = "gpt-3.5-turbo"
    temperature: float = 0.1
    max_tokens: int = 100
    timeout: int = 30

    @classmethod
    def from_env(cls) -> "LLMConfig":
        return cls(
            api_key=os.getenv("LLM_API_KEY"),
            base_url=os.getenv("LLM_BASE_URL"),
            model=os.getenv("LLM_MODEL", "gpt-3.5-turbo"),
            temperature=float(os.getenv("LLM_TEMPERATURE", "0.1")),
            max_tokens=int(os.getenv("LLM_MAX_TOKENS", "100")),
            timeout=int(os.getenv("LLM_TIMEOUT", "30")),
        )


@dataclass
class Config:
    """服务配置"""
    host: str = "127.0.0.1"
    port: int = 8082
    debug: bool = False
    llm: Optional[LLMConfig] = None

    # agenthub-data REST API 地址
    data_server_url: str = "http://localhost:8080"
    data_server_timeout: int = 30

    @classmethod
    def from_env(cls) -> "Config":
        return cls(
            host=os.getenv("SERVICE_HOST", "127.0.0.1"),
            port=int(os.getenv("SERVICE_PORT", "8082")),
            debug=os.getenv("SERVICE_DEBUG", "false").lower() == "true",
            llm=LLMConfig.from_env(),
            data_server_url=os.getenv("DATA_SERVER_URL", "http://localhost:8080"),
            data_server_timeout=int(os.getenv("DATA_SERVER_TIMEOUT", "30")),
        )


config = Config.from_env()
