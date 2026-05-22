import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  errorMessage = signal<string>('');
  showErrorModal = signal<boolean>(false);

  showError(message: string) {
    this.errorMessage.set(message);
    this.showErrorModal.set(true);
  }

  closeError() {
    this.showErrorModal.set(false);
    this.errorMessage.set('');
  }
}
