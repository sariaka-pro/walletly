import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // On n'attache pas le token sur les appels d'auth (login/register)
  if (!token || req.url.includes('/auth/')) {
    return next(req);
  }

  // On clone la requête et on ajoute le header Authorization: Bearer <token>
  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });

  return next(authReq);
};
