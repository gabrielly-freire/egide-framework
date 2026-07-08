import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../services/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const isLoginRequest = req.url.includes('/v1/auth/login');
  // Só o /me valida o token em si. Um 401 nele significa token inválido/expirado -> desloga.
  // 401 de outras rotas (ex.: endpoint inexistente) NÃO deve deslogar o usuário.
  const isSessionCheck = req.url.includes('/v1/auth/me');

  const requestWithAuth = token && !isLoginRequest
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(requestWithAuth).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && isSessionCheck) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};
