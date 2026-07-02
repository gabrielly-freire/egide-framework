from app.schemas.report_schema import ReportRequest, ReportAnonymizedResponse
from app.ai_agents.graph import orquestrador
from app.schemas.report_schema import ReportResponseSuggestionRequest, ReportResponseSuggestionResponse
from pydantic import BaseModel
from langchain_groq import ChatGroq
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import HumanMessage
from app.core.config import settings
from app.ai_agents.prompts import RESPONSE_SUGGESTION_SYSTEM_PROMPT

class AnonymizationService:
    async def process_report(self, payload: ReportRequest) -> ReportAnonymizedResponse:

        estado_inicial = {
            "title_original": payload.title,
            "description_original": payload.description,
            "tentativas": 0,
            "aprovado": False
        }
        
        resultado = await orquestrador.ainvoke(estado_inicial)
        
        return ReportAnonymizedResponse(
            report_id=payload.report_id,
            anonymized_title=resultado["title_anonimizado"],
            anonymized_description=resultado["description_anonimizada"]
        )

anonymization_service = AnonymizationService()


class ResponseSuggestionOutput(BaseModel):
    suggested_response: str


class ResponseSuggestionService:
    def __init__(self):
        provider = (settings.LLM_PROVIDER or "groq").strip().lower()
        if provider == "google":
            if not settings.GOOGLE_API_KEY:
                raise RuntimeError("GOOGLE_API_KEY não configurada")
            self.llm = ChatGoogleGenerativeAI(
                model=settings.GOOGLE_MODEL,
                google_api_key=settings.GOOGLE_API_KEY,
                temperature=0.2
            )
        else:
            if not settings.GROQ_API_KEY:
                raise RuntimeError("GROQ_API_KEY não configurada")
            self.llm = ChatGroq(
                model=settings.GROQ_MODEL,
                groq_api_key=settings.GROQ_API_KEY,
                temperature=0.2
            )

    async def suggest_response(self, payload: ReportResponseSuggestionRequest) -> ReportResponseSuggestionResponse:
        protocol = payload.protocol_number or ""
        category = payload.category or ""
        risk = payload.risk or ""

        prompt = (
            f"{RESPONSE_SUGGESTION_SYSTEM_PROMPT}\n\n"
            f"PROTOCOLO: {protocol}\n"
            f"CATEGORIA: {category}\n"
            f"RISCO: {risk}\n\n"
            f"TÍTULO: {payload.title}\n"
            f"DESCRIÇÃO: {payload.description}\n"
        )

        llm_structured = self.llm.with_structured_output(ResponseSuggestionOutput)
        result = await llm_structured.ainvoke([HumanMessage(content=prompt)])

        return ReportResponseSuggestionResponse(
            report_id=payload.report_id,
            suggested_response=result.suggested_response
        )


response_suggestion_service = ResponseSuggestionService()
