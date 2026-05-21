import { CanActivateFn } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';

export const notAuthGuard: CanActivateFn = (route, state) => {
  const http = inject(HttpClient);
  const router = inject(Router);

  return http.get('http://localhost:8080/user/control', { withCredentials: true }).pipe(
    map(() => {
      router.navigate(['events']);
      return false;
    }),
    catchError(() => of(true)),
  );
};
