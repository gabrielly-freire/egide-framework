export interface ServiceEvaluationRequest {
  manifestationId: number;
  rating: number;
  comment?: string | null;
}

export interface ServiceEvaluationResponse {
  id: number;
  manifestationId: number;
  rating: number;
  comment: string | null;
  createdAt: string;
  updatedAt: string;
}
