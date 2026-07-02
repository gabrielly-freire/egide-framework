import logging
from fastapi import APIRouter, Depends
from app.api.dependencies import verificar_api_key
from app.schemas.report_schema import (
    ReportRequest,
    ReportAnonymizedResponse,
    ReportResponseSuggestionRequest,
    ReportResponseSuggestionResponse,
)
from app.services.anonymization_service import anonymization_service, response_suggestion_service

logger = logging.getLogger(__name__)
router = APIRouter()

@router.post("/anonimizar", response_model=ReportAnonymizedResponse, dependencies=[Depends(verificar_api_key)])
async def anonimizar_manifestacao(payload: ReportRequest):
    resposta = await anonymization_service.process_report(payload)
    logger.info(
        "[/anonimizar] report_id=%s | title=%s | description=%s",
        resposta.report_id,
        resposta.anonymized_title,
        resposta.anonymized_description,
    )
    return resposta


@router.post(
    "/sugerir-resposta",
    response_model=ReportResponseSuggestionResponse,
    dependencies=[Depends(verificar_api_key)],
)
async def sugerir_resposta(payload: ReportResponseSuggestionRequest):
    resposta = await response_suggestion_service.suggest_response(payload)
    logger.info("[/sugerir-resposta] report_id=%s", resposta.report_id)
    return resposta
