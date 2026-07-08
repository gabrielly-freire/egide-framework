import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/** Corpo esperado pelo Core em POST /v1/manifestations. */
export interface ManifestationRequest {
  title: string;
  description: string;
  type: string;
  anonymous: boolean;
}

/** Resposta do Core (ManifestationResponse). */
export interface Manifestation {
  id: number;
  protocolNumber: string;
  title: string;
  description: string;
  type: string;
  status: string;
  category: string | null;
  riskLevel: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Parte (analista ou acusado) com a unidade organizacional — registro genérico do Core. */
export interface Party {
  id: number;
  name: string;
  unit: string;
}

/** Resultado da checagem de conflito de interesse. */
export interface ConflictCheck {
  manifestationId: number;
  analystId: number;
  hasConflict: boolean;
}

/** Resumo gerencial do Core (ManifestationSummaryReport). */
export interface ManifestationSummary {
  totalManifestations: number;
  byStatus: Record<string, number>;
  byType: Record<string, number>;
  totalEvaluations: number;
  averageRating: number | null;
  totalDecisions: number;
  totalOpinions: number;
}

/**
 * Serviço da Instância 2 para as manifestações, falando direto com os endpoints do Core
 * (/api/v1/manifestations). Substitui o ReportService (herdado da inst. 1), que aponta para
 * endpoints de compliance inexistentes aqui.
 */
@Injectable({ providedIn: 'root' })
export class ManifestationService {
  private readonly url = '/api/v1/manifestations';

  constructor(private readonly http: HttpClient) {}

  create(body: ManifestationRequest): Observable<Manifestation> {
    return this.http.post<Manifestation>(this.url, body);
  }

  list(page = 0, size = 10): Observable<Manifestation[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<any>(this.url, { params })
      .pipe(map(res => (Array.isArray(res) ? res : res?.content ?? [])));
  }

  getById(id: number | string): Observable<Manifestation> {
    return this.http.get<Manifestation>(`${this.url}/${id}`);
  }

  summary(): Observable<ManifestationSummary> {
    return this.http.get<ManifestationSummary>('/api/v1/reports/summary');
  }

  // ── Workflow (ponto variável) ───────────────────────────────────────────
  advance(id: number | string): Observable<Manifestation> {
    return this.http.post<Manifestation>(`/api/v1/workflow/${id}/advance`, {});
  }

  appeal(id: number | string): Observable<Manifestation> {
    return this.http.post<Manifestation>(`/api/v1/workflow/${id}/appeal`, {});
  }

  // ── Conflito de interesse (ponto variável) ──────────────────────────────
  createParty(body: { name: string; unit: string }): Observable<Party> {
    return this.http.post<Party>('/api/v1/parties', body);
  }

  addAccusation(manifestationId: number, accusedPartyId: number): Observable<unknown> {
    return this.http.post(`/api/v1/manifestations/${manifestationId}/accusations`, { accusedPartyId });
  }

  checkConflict(manifestationId: number, analystId: number): Observable<ConflictCheck> {
    return this.http.get<ConflictCheck>(`/api/v1/designations/${manifestationId}/conflict/${analystId}`);
  }
}
