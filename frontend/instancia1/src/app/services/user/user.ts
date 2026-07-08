import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { User, UserResponse } from '../../models/usuario.model';

// GET /v1/users e POST /v1/users exigem ROLE_ADMIN e não são paginados no backend
// (não há endpoints de update/delete de usuário).
@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly url = '/api/v1/users';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  listAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.url).pipe(catchError(err => this.handleError(err)));
  }

  create(user: User): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.url, user).pipe(catchError(err => this.handleError(err)));
  }
}
