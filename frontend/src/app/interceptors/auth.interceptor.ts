import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // On n'attache pas le token sur les appels d'auth (login/register)
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  // Un token expiré ou mal formé est supprimé avant même l'appel au backend.
  if (token && authService.isTokenExpired(token)) {
    authService.logout();
    void router.navigate(['/login']);
    return throwError(() => new HttpErrorResponse({
      status: 401,
      statusText: 'Session expirée',
      url: req.url
    }));
  }

  // On clone la requête et on ajoute le header Authorization: Bearer <token>
  const authReq = token ? req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  }) : req;

  return next(authReq).pipe(
    catchError((error: unknown) => {
      // Centralise la fin de session lorsque le backend refuse le JWT.
      if (error instanceof HttpErrorResponse && error.status === 401) {
        authService.logout();
        void router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
