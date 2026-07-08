import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { ManifestationResponse } from '../../models/manifestation.model';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private readonly url = '/api/v1/workflow';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  advance(manifestationId: number | string): Observable<ManifestationResponse> {
    return this.http
      .post<ManifestationResponse>(`${this.url}/${manifestationId}/advance`, {})
      .pipe(catchError(err => this.handleError(err)));
  }
}
