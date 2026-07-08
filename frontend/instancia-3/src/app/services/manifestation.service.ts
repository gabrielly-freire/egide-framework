import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ManifestationRequest,
  ManifestationResponse,
  Page,
} from '../models/manifestation.model';

@Injectable({ providedIn: 'root' })
export class ManifestationService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/manifestations';

  create(request: ManifestationRequest): Observable<ManifestationResponse> {
    return this.http.post<ManifestationResponse>(this.base, request);
  }

  findById(id: number): Observable<ManifestationResponse> {
    return this.http.get<ManifestationResponse>(`${this.base}/${id}`);
  }

  findByProtocol(protocol: string): Observable<ManifestationResponse> {
    return this.http.get<ManifestationResponse>(`${this.base}/protocol/${protocol}`);
  }

  list(page = 0, size = 10, sort = 'createdAt', direction = 'desc'): Observable<Page<ManifestationResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort)
      .set('direction', direction);
    return this.http.get<Page<ManifestationResponse>>(this.base, { params });
  }

  /** Manifestações designadas ao analista autenticado — único endpoint acessível a quem não é ADMIN. */
  listMine(page = 0, size = 10): Observable<Page<ManifestationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<ManifestationResponse>>(`${this.base}/mine`, { params });
  }

  update(id: number, request: Partial<ManifestationRequest>): Observable<ManifestationResponse> {
    return this.http.put<ManifestationResponse>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
