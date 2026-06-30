import logging
from fastapi import APIRouter, Depends
from app.api.dependencies import verificar_api_key
from app.schemas.analysis_schema import AnalysisRequest, AnalysisResponse
from app.services.analysis_service import analysis_service

logger = logging.getLogger(__name__)
router = APIRouter()

@router.post("/analisar", response_model=AnalysisResponse, dependencies=[Depends(verificar_api_key)])
async def analisar_risco_categoria(payload: AnalysisRequest):
    logger.info("[/analisar] responsible_users recebidos: %s", payload.responsible_users)
    resposta = await analysis_service.analyze_report(payload)
    logger.info(
        "[/analisar] report_id=%s | category=%s | risk=%s | conflict=%s | conflicted=%s | manager_conflict=%s",
        resposta.report_id,
        resposta.category,
        resposta.risk_level,
        resposta.conflict_detected,
        resposta.conflicted_user_ids,
        resposta.manager_conflict,
    )
    return resposta