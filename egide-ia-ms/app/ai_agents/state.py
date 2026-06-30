from typing import TypedDict, List, Dict

class AnonymizationState(TypedDict):
    title_original: str
    description_original: str
    entidades: List[Dict[str, str]]
    title_anonimizado: str
    description_anonimizada: str
    feedback: str
    aprovado: bool
    tentativas: int