export type ManifestationStatus = 'REGISTERED' | 'IN_REVIEW' | 'RESOLVED' | 'CLOSED';

export type ManifestationType =
  | 'SAUDE'
  | 'EDUCACAO'
  | 'INFRAESTRUTURA'
  | 'ASSISTENCIA_SOCIAL'
  | 'MEIO_AMBIENTE'
  | 'GESTAO';

export interface ManifestationRequest {
  title: string;
  description: string;
  type: ManifestationType;
  anonymous?: boolean;
  affectedRegion?: string;
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
  affectedRegion: string | null;
  appealCount: number;
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
  SAUDE: 'Saúde',
  EDUCACAO: 'Educação',
  INFRAESTRUTURA: 'Infraestrutura',
  ASSISTENCIA_SOCIAL: 'Assistência Social',
  MEIO_AMBIENTE: 'Meio Ambiente',
  GESTAO: 'Gestão',
};

export const MAX_APPEALS = 3;
