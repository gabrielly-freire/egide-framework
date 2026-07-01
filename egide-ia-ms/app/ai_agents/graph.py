from langgraph.graph import StateGraph, END
from langchain_groq import ChatGroq
from langchain_google_genai import ChatGoogleGenerativeAI
from pydantic import BaseModel, Field
from typing import List

from app.core.config import settings
from app.ai_agents.state import AnonymizationState
from app.ai_agents.prompts import EXTRACTOR_SYSTEM_PROMPT, REDACTOR_SYSTEM_PROMPT, REVIEWER_SYSTEM_PROMPT

def get_llm():
    provider = (settings.LLM_PROVIDER or "groq").strip().lower()
    if provider == "google":
        if not settings.GOOGLE_API_KEY:
            raise RuntimeError("GOOGLE_API_KEY não configurada")
        return ChatGoogleGenerativeAI(
            model=settings.GOOGLE_MODEL,
            google_api_key=settings.GOOGLE_API_KEY,
            temperature=0.1
        )

    if not settings.GROQ_API_KEY:
        raise RuntimeError("GROQ_API_KEY não configurada")
    return ChatGroq(
        model=settings.GROQ_MODEL,
        groq_api_key=settings.GROQ_API_KEY,
        temperature=0.1
    )


llm = get_llm()

class Entidade(BaseModel):
    tipo: str = Field(description="Tipo do dado (NOME, CPF, CARGO, LOCAL, etc)")
    valor: str = Field(description="O texto exato encontrado no relato")

class SaidaExtrator(BaseModel):
    entidades: List[Entidade]

class SaidaRedator(BaseModel):
    title_anonimizado: str
    description_anonimizada: str

class SaidaRevisor(BaseModel):
    aprovado: bool
    feedback: str

def node_extrator(state: AnonymizationState):
    llm_estruturada = llm.with_structured_output(SaidaExtrator)
    
    prompt = f"""{EXTRACTOR_SYSTEM_PROMPT}
    
    TÍTULO: {state['title_original']}
    DESCRIÇÃO: {state['description_original']}
    
    FEEDBACK ANTERIOR DO REVISOR (se houver): {state.get('feedback', 'Nenhum')}
    """
    
    resultado = llm_estruturada.invoke(prompt)
    entidades_dict = [{"tipo": e.tipo, "valor": e.valor} for e in resultado.entidades]
    
    return {"entidades": entidades_dict}

def node_redator(state: AnonymizationState):
    llm_estruturada = llm.with_structured_output(SaidaRedator)
    
    prompt = f"""{REDACTOR_SYSTEM_PROMPT}
    
    TÍTULO ORIGINAL: {state['title_original']}
    DESCRIÇÃO ORIGINAL: {state['description_original']}
    
    ENTIDADES A OCULTAR: {state['entidades']}
    """
    
    resultado = llm_estruturada.invoke(prompt)
    
    return {
        "title_anonimizado": resultado.title_anonimizado,
        "description_anonimizada": resultado.description_anonimizada
    }

def node_revisor(state: AnonymizationState):
    llm_estruturada = llm.with_structured_output(SaidaRevisor)
    
    prompt = f"""{REVIEWER_SYSTEM_PROMPT}
    
    TEXTO ORIGINAL COMPLETO:
    Título: {state['title_original']}
    Descrição: {state['description_original']}
    
    TEXTO ANONIMIZADO GERADO PARA AVALIAÇÃO:
    Título: {state['title_anonimizado']}
    Descrição: {state['description_anonimizada']}
    """
    
    resultado = llm_estruturada.invoke(prompt)    
    tentativas_atuais = state.get('tentativas', 0) + 1
    
    return {
        "aprovado": resultado.aprovado,
        "feedback": resultado.feedback,
        "tentativas": tentativas_atuais
    }

def roteador(state: AnonymizationState):
    if state["aprovado"] == True:
        return END
    
    if state["tentativas"] >= 3:
        return END 
    
    return "extrator"

workflow = StateGraph(AnonymizationState)

workflow.add_node("extrator", node_extrator)
workflow.add_node("redator", node_redator)
workflow.add_node("revisor", node_revisor)

workflow.set_entry_point("extrator")
workflow.add_edge("extrator", "redator")
workflow.add_edge("redator", "revisor")
workflow.add_conditional_edges("revisor", roteador)

orquestrador = workflow.compile()
