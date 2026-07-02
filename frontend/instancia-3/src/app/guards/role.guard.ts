import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AnalystRole } from '../models/auth.model';

export const roleGuard =
  (roles: AnalystRole[]): CanActivateFn =>
  () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const user = authService.currentUser();

    if (user && roles.includes(user.role)) return true;
    return router.createUrlTree(['/dashboard']);
  };
