import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>, 
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {
  const authService = inject(AuthService);

  // Skip interceptor for login and refresh token requests
  if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
    return next(req);
  }

  // Get the token
  const token = authService.getToken();
  
  if (token) {
    // Clone the request and add authorization header
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });

    return next(authReq).pipe(
      catchError((error) => {
        // If token is expired (401), try to refresh
        if (error.status === 401 && authService.getRefreshToken()) {
          return authService.refreshToken().pipe(
            switchMap((refreshResponse) => {
              // Retry the original request with new token
              const retryReq = req.clone({
                headers: req.headers.set('Authorization', `Bearer ${refreshResponse.accessToken}`)
              });
              return next(retryReq);
            }),
            catchError((refreshError) => {
              // Refresh failed, logout user
              authService.logout();
              return throwError(() => refreshError);
            })
          );
        }
        
        return throwError(() => error);
      })
    );
  }

  // No token, proceed without authorization
  return next(req);
};