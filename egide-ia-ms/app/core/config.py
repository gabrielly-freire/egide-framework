from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    PROJECT_NAME: str = "Égide.IA MS"
    API_V1_STR: str = "/api/v1"
    ENVIRONMENT: str = "development"
    LLM_PROVIDER: str = "groq"

    GROQ_API_KEY: Optional[str] = None
    GROQ_MODEL: str = "llama-3.1-8b-instant"

    GOOGLE_API_KEY: Optional[str] = None
    GOOGLE_MODEL: str = "gemini-2.5-flash"
    EGIDE_API_KEY: str = "chave_secreta_egide"

    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()
