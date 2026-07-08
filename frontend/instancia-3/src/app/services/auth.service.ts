import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, switchMap, tap, throwError } from 'rxjs';
import { CurrentAnalyst, LoginRequest, LoginResponse } from '../models/auth.model';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly TOKEN_KEY = 'ouvidoria.token';
  private readonly tokenState = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));

  readonly currentUser = signal<CurrentAnalyst | null>(null);
  readonly isAuthenticated = computed(() => !!this.tokenState());

  login(credentials: LoginRequest): Observable<CurrentAnalyst> {
    return this.http.post<LoginResponse>('/api/v1/auth/login', credentials).pipe(
      tap(res => {
        this.tokenState.set(res.token);
        localStorage.setItem(this.TOKEN_KEY, res.token);
      }),
      switchMap(() => this.fetchCurrentUser()),
      catchError(err => {
        const msg = err?.error?.message ?? 'Credenciais inválidas.';
        return throwError(() => new Error(msg));
      }),
    );
  }

  fetchCurrentUser(): Observable<CurrentAnalyst> {
    return this.http
      .get<CurrentAnalyst>('/api/v1/auth/me')
      .pipe(tap(user => this.currentUser.set(user)));
  }

  restoreSession(): Observable<CurrentAnalyst | null> {
    if (!this.tokenState()) return of(null);
    if (this.currentUser()) return of(this.currentUser());
    return this.fetchCurrentUser().pipe(
      catchError(() => {
        this.clearSession();
        return of(null);
      }),
    );
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.tokenState();
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  private clearSession(): void {
    this.tokenState.set(null);
    this.currentUser.set(null);
    localStorage.removeItem(this.TOKEN_KEY);
  }
}
