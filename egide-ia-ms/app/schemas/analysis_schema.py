from pydantic import BaseModel
from enum import Enum
from typing import List, Optional

class ReportCategory(str, Enum):
    DENUNCIATION = "DENUNCIATION"
    COMPLAINT = "COMPLAINT"
    COMPLIMENT = "COMPLIMENT"
    SUGGESTION = "SUGGESTION"
    REQUEST = "REQUEST"

class ReportRisk(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

class FileAttachment(BaseModel):
    filename: str
    mime_type: str 
    base64_data: str

class ResponsibleUser(BaseModel):
    id: str
    name: str
    email: str
    user_name: str
    role: str

class AnalysisRequest(BaseModel):
    report_id: int
    title: str
    description: str
    files: Optional[List[FileAttachment]] = []
    responsible_users: Optional[List[ResponsibleUser]] = []

class AnalysisResponse(BaseModel):
    report_id: int
    category: ReportCategory
    risk_level: ReportRisk
    conflict_detected: bool = False
    conflicted_user_ids: List[str] = []
    manager_conflict: bool = False