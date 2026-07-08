import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditEntryRequest, AuditEntryResponse } from '../models/audit.model';
import { Page } from '../models/manifestation.model';

@Injectable({ providedIn: 'root' })
export class AuditService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/audit';

  create(request: AuditEntryRequest): Observable<AuditEntryResponse> {
    return this.http.post<AuditEntryResponse>(this.base, request);
  }

  findAllByManifestationId(
    manifestationId: number,
    page = 0,
    size = 20,
  ): Observable<Page<AuditEntryResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<AuditEntryResponse>>(`${this.base}/manifestation/${manifestationId}`, {
      params,
    });
  }
}
