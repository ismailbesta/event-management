import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { map, catchError, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const http = inject(HttpClient);
  const router = inject(Router);

  return http.get('http://localhost:8080/user/control', { withCredentials: true }).pipe(
    map(() => true),
    catchError(() => {
      localStorage.clear();
      router.navigate(['login']);
      return of(false);
    }),
  );
};
