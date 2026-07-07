import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LegalImpedimentRequest, LegalImpedimentResponse } from '../models/legal-impediment.model';

@Injectable({ providedIn: 'root' })
export class LegalImpedimentService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/legal-impediments';

  register(request: LegalImpedimentRequest): Observable<LegalImpedimentResponse> {
    return this.http.post<LegalImpedimentResponse>(this.base, request);
  }

  findById(id: number): Observable<LegalImpedimentResponse> {
    return this.http.get<LegalImpedimentResponse>(`${this.base}/${id}`);
  }

  findByManifestationId(manifestationId: number): Observable<LegalImpedimentResponse[]> {
    return this.http.get<LegalImpedimentResponse[]>(`${this.base}/manifestation/${manifestationId}`);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
