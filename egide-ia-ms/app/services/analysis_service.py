import asyncio
import json
import logging
from pydantic import BaseModel

logger = logging.getLogger(__name__)
from typing import List
from langchain_groq import ChatGroq
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import HumanMessage
from app.core.config import settings
from app.schemas.analysis_schema import AnalysisRequest, AnalysisResponse, ReportCategory, ReportRisk
from app.ai_agents.prompts import CATEGORY_SYSTEM_PROMPT, RISK_SYSTEM_PROMPT, CONFLICT_SYSTEM_PROMPT

class CategoryOutput(BaseModel):
    category: ReportCategory

class RiskOutput(BaseModel):
    risk_level: ReportRisk

class ConflictOutput(BaseModel):
    conflicted_user_ids: List[str]

class AnalysisService:
    def __init__(self):
        self.provider = (settings.LLM_PROVIDER or "groq").strip().lower()
        if self.provider == "google":
            if not settings.GOOGLE_API_KEY:
                raise RuntimeError("GOOGLE_API_KEY não configurada")
            self.llm = ChatGoogleGenerativeAI(
                model=settings.GOOGLE_MODEL,
                google_api_key=settings.GOOGLE_API_KEY,
                temperature=0.1
            )
        else:
            if not settings.GROQ_API_KEY:
                raise RuntimeError("GROQ_API_KEY não configurada")
            self.llm = ChatGroq(
                model=settings.GROQ_MODEL,
                groq_api_key=settings.GROQ_API_KEY,
                temperature=0.1
            )
        
    def _montar_mensagem_multimodal(self, payload: AnalysisRequest, system_prompt: str) -> list:
        anexos = payload.files or []
        if self.provider == "google":
            conteudo = [
                {
                    "type": "text",
                    "text": f"{system_prompt}\n\nTÍTULO: {payload.title}\nDESCRIÇÃO: {payload.description}"
                }
            ]

            for f in anexos:
                conteudo.append({
                    "type": "image_url",
                    "image_url": {"url": f"data:{f.mime_type};base64,{f.base64_data}"}
                })

            return [HumanMessage(content=conteudo)]

        anexos_info = f"ANEXOS: {len(anexos)} arquivo(s) anexado(s)." if anexos else "ANEXOS: nenhum."
        prompt_texto = (
            f"{system_prompt}\n\n"
            f"TÍTULO: {payload.title}\n"
            f"DESCRIÇÃO: {payload.description}\n"
            f"{anexos_info}"
        )
        return [HumanMessage(content=prompt_texto)]

    async def _analisar_categoria(self, mensagens: list) -> CategoryOutput:
        llm_cat = self.llm.with_structured_output(CategoryOutput)
        return await llm_cat.ainvoke(mensagens)

    async def _analisar_risco(self, mensagens: list) -> RiskOutput:
        llm_risk = self.llm.with_structured_output(RiskOutput)
        return await llm_risk.ainvoke(mensagens)

    async def _detectar_conflito(self, payload: AnalysisRequest) -> ConflictOutput:
        if not payload.responsible_users:
            return ConflictOutput(conflicted_user_ids=[])

        responsaveis_json = json.dumps(
            [u.model_dump() for u in payload.responsible_users],
            ensure_ascii=False
        )

        prompt_texto = (
            f"{CONFLICT_SYSTEM_PROMPT}\n\n"
            f"TÍTULO DA DENÚNCIA: {payload.title}\n"
            f"DESCRIÇÃO DA DENÚNCIA: {payload.description}\n\n"
            f"RESPONSÁVEIS PELO CANAL:\n{responsaveis_json}"
        )

        logger.info("[conflito] prompt enviado:\n%s", prompt_texto)
        llm_conflict = self.llm.with_structured_output(ConflictOutput)
        return await llm_conflict.ainvoke([HumanMessage(content=prompt_texto)])

    async def analyze_report(self, payload: AnalysisRequest) -> AnalysisResponse:
        msg_categoria = self._montar_mensagem_multimodal(payload, CATEGORY_SYSTEM_PROMPT)
        msg_risco = self._montar_mensagem_multimodal(payload, RISK_SYSTEM_PROMPT)

        resultado_cat, resultado_risco, resultado_conflito = await asyncio.gather(
            self._analisar_categoria(msg_categoria),
            self._analisar_risco(msg_risco),
            self._detectar_conflito(payload)
        )

        ids_conflitados = resultado_conflito.conflicted_user_ids
        manager_conflict = any(
            u.role == "MANAGER"
            for u in (payload.responsible_users or [])
            if u.id in ids_conflitados
        )

        return AnalysisResponse(
            report_id=payload.report_id,
            category=resultado_cat.category,
            risk_level=resultado_risco.risk_level,
            conflict_detected=bool(ids_conflitados),
            conflicted_user_ids=ids_conflitados,
            manager_conflict=manager_conflict
        )

analysis_service = AnalysisService()
