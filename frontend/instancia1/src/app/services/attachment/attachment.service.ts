import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { AttachmentResponse } from '../../models/attachment.model';

@Injectable({ providedIn: 'root' })
export class AttachmentService {

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error('Erro na requisição de anexos:', error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  private url(manifestationId: number | string): string {
    return `/api/v1/manifestations/${manifestationId}/attachments`;
  }

  upload(manifestationId: number | string, file: File): Observable<AttachmentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<AttachmentResponse>(this.url(manifestationId), formData)
      .pipe(catchError(err => this.handleError(err)));
  }

  list(manifestationId: number | string): Observable<AttachmentResponse[]> {
    return this.http
      .get<AttachmentResponse[]>(this.url(manifestationId))
      .pipe(catchError(err => this.handleError(err)));
  }

  download(manifestationId: number | string, attachmentId: number | string): Observable<Blob> {
    return this.http
      .get(`${this.url(manifestationId)}/${attachmentId}/download`, { responseType: 'blob' })
      .pipe(catchError(err => this.handleError(err)));
  }
}
