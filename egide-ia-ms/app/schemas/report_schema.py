from pydantic import BaseModel
from typing import Optional

class ReportRequest(BaseModel):
    report_id: int
    title: str
    description: str

class ReportAnonymizedResponse(BaseModel):
    report_id: int
    anonymized_title: str
    anonymized_description: str

class ReportResponseSuggestionRequest(BaseModel):
    report_id: int
    title: str
    description: str
    protocol_number: Optional[str] = None
    category: Optional[str] = None
    risk: Optional[str] = None

class ReportResponseSuggestionResponse(BaseModel):
    report_id: int
    suggested_response: str
