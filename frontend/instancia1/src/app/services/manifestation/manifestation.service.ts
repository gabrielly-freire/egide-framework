import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { PageResponse } from '../../models/page.model';
import { ManifestationRequest, ManifestationResponse } from '../../models/manifestation.model';

@Injectable({ providedIn: 'root' })
export class ManifestationService extends BaseService<ManifestationResponse, ManifestationRequest> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/manifestations');
  }

  listPaged(
    page = 0,
    size = 10,
    sort = 'createdAt',
    direction: 'asc' | 'desc' = 'desc'
  ): Observable<PageResponse<ManifestationResponse>> {
    return this.http
      .get<PageResponse<ManifestationResponse>>(this.url, { params: { page, size, sort, direction } })
      .pipe(catchError(err => this.handleError(err)));
  }

  getByProtocol(protocolNumber: string): Observable<ManifestationResponse> {
    return this.http
      .get<ManifestationResponse>(`${this.url}/protocol/${protocolNumber}`)
      .pipe(catchError(err => this.handleError(err)));
  }
}
