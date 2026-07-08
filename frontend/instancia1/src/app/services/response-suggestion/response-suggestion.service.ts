import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

export interface ResponseSuggestion {
  suggestedResponse: string;
}

@Injectable({ providedIn: 'root' })
export class ResponseSuggestionService {

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error('Erro ao obter sugestão da IA:', error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  suggest(manifestationId: number | string): Observable<ResponseSuggestion> {
    return this.http
      .post<ResponseSuggestion>(`/api/v1/manifestations/${manifestationId}/response-suggestion`, {})
      .pipe(catchError(err => this.handleError(err)));
  }
}
