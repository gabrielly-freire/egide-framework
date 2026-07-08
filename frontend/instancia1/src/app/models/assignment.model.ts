export interface ResponsibleAssignmentRequest {
  manifestationId: number;
  responsibleId: number;
  assignedById?: number | null;
}

export interface ResponsibleAssignmentResponse {
  id: number;
  manifestationId: number;
  responsibleId: number;
  assignedById: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface ConflictCheckResponse {
  manifestationId: number;
  analystId: number;
  hasConflict: boolean;
}
