export interface AuditEntryRequest {
  manifestationId: number;
  actorId: number;
  action: string;
  description?: string;
}

export interface AuditEntryResponse {
  id: number;
  manifestationId: number;
  actorId: number;
  action: string;
  description: string | null;
  occurredAt: string;
}
