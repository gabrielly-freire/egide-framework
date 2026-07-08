export type ManifestationStatus = 'REGISTERED' | 'IN_REVIEW' | 'RESOLVED' | 'CLOSED';

export interface ManifestationRequest {
  title: string;
  description: string;
  type: string;
  anonymous: boolean;
}

export interface ManifestationResponse {
  id: number;
  protocolNumber: string;
  title: string;
  description: string;
  type: string;
  status: ManifestationStatus;
  category: string | null;
  riskLevel: string | null;
  deadlineAt: string | null;
  createdAt: string;
  updatedAt: string;
}

// `type` é texto livre no backend (máx. 100 chars); esta lista é só uma sugestão de UI.
export const MANIFESTATION_TYPE_OPTIONS: ReadonlyArray<{ value: string; label: string }> = [
  { value: 'DENUNCIA', label: 'Denúncia' },
  { value: 'RECLAMACAO', label: 'Reclamação' },
  { value: 'ELOGIO', label: 'Elogio' },
  { value: 'SUGESTAO', label: 'Sugestão' },
  { value: 'SOLICITACAO', label: 'Solicitação' }
];

export function manifestationStatusLabel(status: ManifestationStatus | string | null | undefined): string {
  switch (status) {
    case 'REGISTERED': return 'Registrada';
    case 'IN_REVIEW': return 'Em investigação';
    case 'RESOLVED': return 'Decidida';
    case 'CLOSED': return 'Encerrada';
    default: return status || '-';
  }
}
