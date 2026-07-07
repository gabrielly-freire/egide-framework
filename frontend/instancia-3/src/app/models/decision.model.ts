export type DecisionType = 'DECISION' | 'OPINION';

export interface DecisionRecordRequest {
  manifestationId: number;
  authorId: number;
  type: DecisionType;
  content: string;
}

export interface DecisionRecordResponse {
  id: number;
  manifestationId: number;
  authorId: number;
  type: DecisionType;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export const DECISION_TYPE_LABELS: Record<DecisionType, string> = {
  DECISION: 'Decisão',
  OPINION: 'Parecer',
};
