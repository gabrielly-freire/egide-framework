from fastapi import APIRouter
from app.api.v1.endpoints import compliance, analysis

api_router = APIRouter()

api_router.include_router(
    compliance.router, 
    prefix="/compliance", 
    tags=["Compliance & Anonymization"]
)

api_router.include_router(
    analysis.router, 
    prefix="/analysis", 
    tags=["Risk & Categorization"]
)