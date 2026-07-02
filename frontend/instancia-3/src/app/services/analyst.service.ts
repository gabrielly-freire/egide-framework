import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalystRequest, AnalystResponse } from '../models/analyst.model';

@Injectable({ providedIn: 'root' })
export class AnalystService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/analysts';

  list(): Observable<AnalystResponse[]> {
    return this.http.get<AnalystResponse[]>(this.base);
  }

  findById(id: number): Observable<AnalystResponse> {
    return this.http.get<AnalystResponse>(`${this.base}/${id}`);
  }

  create(request: AnalystRequest): Observable<AnalystResponse> {
    return this.http.post<AnalystResponse>(this.base, request);
  }

  update(id: number, request: AnalystRequest): Observable<AnalystResponse> {
    return this.http.put<AnalystResponse>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
