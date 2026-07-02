export type ManifestationStatus = 'REGISTERED' | 'IN_REVIEW' | 'RESOLVED' | 'CLOSED';

export type ManifestationType =
  | 'RECLAMACAO'
  | 'SUGESTAO'
  | 'ELOGIO'
  | 'DENUNCIA'
  | 'SOLICITACAO';

export interface ManifestationRequest {
  title: string;
  description: string;
  type: ManifestationType;
}

export interface ManifestationResponse {
  id: number;
  protocolNumber: string;
  title: string;
  description: string;
  type: string;
  status: ManifestationStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const STATUS_LABELS: Record<ManifestationStatus, string> = {
  REGISTERED: 'Registrada',
  IN_REVIEW: 'Em Análise',
  RESOLVED: 'Resolvida',
  CLOSED: 'Encerrada',
};

export const TYPE_LABELS: Record<string, string> = {
  RECLAMACAO: 'Reclamação',
  SUGESTAO: 'Sugestão',
  ELOGIO: 'Elogio',
  DENUNCIA: 'Denúncia',
  SOLICITACAO: 'Solicitação',
};
