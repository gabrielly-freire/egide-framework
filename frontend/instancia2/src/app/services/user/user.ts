import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { BaseService } from '../base/base';
import { User } from '../../models/usuario.model';

/**
 * Serviço de usuários da Instância 2, apontando para o backend próprio da instância
 * (/api/v1/users). O backend expõe apenas listagem (GET) e criação (POST).
 */
@Injectable({
  providedIn: 'root',
})
export class UserService extends BaseService<User> {

  constructor(http: HttpClient) {
    super(http, '/api/v1/users');
  }

  listAll(): Observable<User[]> {
    return this.http.get<User[]>(this.url).pipe(
      catchError(err => this.handleError(err))
    );
  }
}
