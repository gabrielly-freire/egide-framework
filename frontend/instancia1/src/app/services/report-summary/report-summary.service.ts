import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { ManifestationSummaryReport } from '../../models/report-summary.model';

@Injectable({ providedIn: 'root' })
export class ReportSummaryService {
  private readonly url = '/api/v1/reports/summary';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  summary(from?: string, to?: string): Observable<ManifestationSummaryReport> {
    const params: Record<string, string> = {};
    if (from) params['from'] = from;
    if (to) params['to'] = to;
    return this.http
      .get<ManifestationSummaryReport>(this.url, { params })
      .pipe(catchError(err => this.handleError(err)));
  }
}
