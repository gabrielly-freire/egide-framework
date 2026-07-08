import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ManifestationResponse } from '../models/manifestation.model';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/workflow';

  advance(manifestationId: number): Observable<ManifestationResponse> {
    return this.http.post<ManifestationResponse>(`${this.base}/${manifestationId}/advance`, {});
  }

  appeal(manifestationId: number): Observable<ManifestationResponse> {
    return this.http.post<ManifestationResponse>(`${this.base}/${manifestationId}/appeal`, {});
  }
}
