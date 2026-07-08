export interface ServiceEvaluationRequest {
  manifestationId: number;
  rating: number;
  comment?: string;
}

export interface ServiceEvaluationResponse {
  id: number;
  manifestationId: number;
  rating: number;
  comment: string | null;
  createdAt: string;
  updatedAt: string;
}
