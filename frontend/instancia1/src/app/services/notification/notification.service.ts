import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';
import { NotificationResponse } from '../../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly url = '/api/v1/notifications';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  list(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.url).pipe(catchError(err => this.handleError(err)));
  }

  unreadCount(): Observable<number> {
    return this.list().pipe(map(list => list.filter(n => !n.read).length));
  }

  markAsRead(id: number | string): Observable<NotificationResponse> {
    return this.http
      .post<NotificationResponse>(`${this.url}/${id}/read`, {})
      .pipe(catchError(err => this.handleError(err)));
  }
}
