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
}
