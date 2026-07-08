import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { AccusationResponse } from '../../models/accusation.model';

@Injectable({ providedIn: 'root' })
export class AccusationService {

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error('Erro na requisição de acusações:', error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  private url(manifestationId: number | string): string {
    return `/api/v1/manifestations/${manifestationId}/accusations`;
  }

  create(manifestationId: number | string, accusedUserId: number): Observable<AccusationResponse> {
    return this.http
      .post<AccusationResponse>(this.url(manifestationId), { accusedUserId })
      .pipe(catchError(err => this.handleError(err)));
  }

  list(manifestationId: number | string): Observable<AccusationResponse[]> {
    return this.http
      .get<AccusationResponse[]>(this.url(manifestationId))
      .pipe(catchError(err => this.handleError(err)));
  }
}
