import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PdfService {

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error('Erro ao exportar PDF:', error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  export(manifestationId: number | string): Observable<Blob> {
    return this.http
      .get(`/api/v1/manifestations/${manifestationId}/pdf`, { responseType: 'blob' })
      .pipe(catchError(err => this.handleError(err)));
  }
}
