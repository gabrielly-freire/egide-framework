export interface CreateAccusationRequest {
  accusedUserId: number;
}

export interface AccusationResponse {
  id: number;
  manifestationId: number;
  accusedUserId: number;
}
