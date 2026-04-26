import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si l'utilisateur est ADMIN, on laisse passer
  if (authService.isAdmin()) {
    return true;
  }

  // Sinon on redirige vers le dashboard (connecté mais pas admin)
  return router.createUrlTree(['/dashboard']);
};
