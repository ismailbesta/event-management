import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ErrorService } from '../services/error';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const errorService = inject(ErrorService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      // İSTİSNA (BYPASS) DURUMLARI:
      if (
        req.url.includes('/login') ||
        req.url.includes('/register') ||
        req.url.includes('/user/control') || // Session kontrolünü de yoksay
        err.status === 401 // Yetki hatalarını genel olarak yoksay
      ) {
        return throwError(() => err);
      }

      // Backend'den fırlatılan özel mesajı veya varsayılan mesajı al
      let errorMsg = 'Sunucu ile iletişimde bir hata oluştu.';

      if (err.error && err.error.message) {
        errorMsg = err.error.message;
      }

      // Hata servisini tetikle ve modalı aç!
      errorService.showError(errorMsg);

      // Hatayı component'in kendisine de fırlat (loading spinner'ları durdurmak vb. için)
      return throwError(() => err);
    }),
  );
};
