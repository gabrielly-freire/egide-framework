export type ImpedimentReason = 'CITATION' | 'KINSHIP';

export interface LegalImpedimentRequest {
  manifestationId: number;
  analystId: number;
  reason: ImpedimentReason;
}

export interface LegalImpedimentResponse {
  id: number;
  manifestationId: number;
  analystId: number;
  reason: ImpedimentReason;
  createdAt: string;
}

export const REASON_LABELS: Record<ImpedimentReason, string> = {
  CITATION: 'Citação',
  KINSHIP: 'Parentesco',
};
