import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DecisionRecordRequest, DecisionRecordResponse } from '../models/decision.model';

@Injectable({ providedIn: 'root' })
export class DecisionService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/decisions';

  create(request: DecisionRecordRequest): Observable<DecisionRecordResponse> {
    return this.http.post<DecisionRecordResponse>(this.base, request);
  }

  findById(id: number): Observable<DecisionRecordResponse> {
    return this.http.get<DecisionRecordResponse>(`${this.base}/${id}`);
  }

  findAllByManifestationId(manifestationId: number): Observable<DecisionRecordResponse[]> {
    return this.http.get<DecisionRecordResponse[]>(`${this.base}/manifestation/${manifestationId}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
