import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { ResponsibleAssignmentResponse, ConflictCheckResponse } from '../../models/assignment.model';

@Injectable({ providedIn: 'root' })
export class DesignationService {
  private readonly url = '/api/v1/designations';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  autoAssign(manifestationId: number | string): Observable<ResponsibleAssignmentResponse> {
    return this.http
      .post<ResponsibleAssignmentResponse>(`${this.url}/${manifestationId}/auto`, {})
      .pipe(catchError(err => this.handleError(err)));
  }

  checkConflict(manifestationId: number | string, analystId: number | string): Observable<ConflictCheckResponse> {
    return this.http
      .get<ConflictCheckResponse>(`${this.url}/${manifestationId}/conflict/${analystId}`)
      .pipe(catchError(err => this.handleError(err)));
  }
}
