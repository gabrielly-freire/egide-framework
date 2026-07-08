import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { PageResponse } from '../../models/page.model';
import { AuditEntryRequest, AuditEntryResponse } from '../../models/audit.model';

@Injectable({ providedIn: 'root' })
export class AuditService extends BaseService<AuditEntryResponse, AuditEntryRequest> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/audit');
  }

  listByManifestation(
    manifestationId: number | string,
    page = 0,
    size = 20
  ): Observable<PageResponse<AuditEntryResponse>> {
    return this.http
      .get<PageResponse<AuditEntryResponse>>(`${this.url}/manifestation/${manifestationId}`, { params: { page, size } })
      .pipe(catchError(err => this.handleError(err)));
  }
}
