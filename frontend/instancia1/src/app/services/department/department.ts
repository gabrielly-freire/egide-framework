import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { CreateDepartmentRequest, Department } from '../../models/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private readonly url = '/api/v1/departments';

  constructor(private readonly http: HttpClient) {}

  private handleError(error: any) {
    console.error(`Erro na requisição para ${this.url}:`, error);
    return throwError(() => error.error?.message || 'Erro interno no servidor');
  }

  listAll(): Observable<Department[]> {
    return this.http.get<Department[]>(this.url).pipe(catchError(err => this.handleError(err)));
  }

  create(request: CreateDepartmentRequest): Observable<Department> {
    return this.http.post<Department>(this.url, request).pipe(catchError(err => this.handleError(err)));
  }
}
