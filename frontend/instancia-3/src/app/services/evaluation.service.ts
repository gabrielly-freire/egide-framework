import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServiceEvaluationRequest, ServiceEvaluationResponse } from '../models/evaluation.model';

@Injectable({ providedIn: 'root' })
export class EvaluationService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/evaluations';

  create(request: ServiceEvaluationRequest): Observable<ServiceEvaluationResponse> {
    return this.http.post<ServiceEvaluationResponse>(this.base, request);
  }

  findByManifestationId(manifestationId: number): Observable<ServiceEvaluationResponse> {
    return this.http.get<ServiceEvaluationResponse>(`${this.base}/manifestation/${manifestationId}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
