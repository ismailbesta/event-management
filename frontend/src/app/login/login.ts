import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);
  loginForm: FormGroup;
  showRegisterSuccess = false;
  errorMessage: string | null = null;
  showErrorModal = false;

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(200)]],
      password: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(1000)]],
    });

    this.route.queryParamMap.subscribe((params) => {
      this.showRegisterSuccess = params.get('registered') === '1';
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.errorMessage = null;
      this.showErrorModal = false;
      const loginData = this.loginForm.value;
      this.http
        .post('http://localhost:8080/user/login', loginData, { withCredentials: true })
        .subscribe({
          next: (response) => {
            const { id, name, surname, email } = response as any;
            localStorage.setItem('id', id);
            localStorage.setItem('name', name);
            localStorage.setItem('surname', surname);
            localStorage.setItem('email', email);
            this.router.navigate(['events']);
          },
          error: (error) => {
            this.errorMessage = 'Giriş hatası: ' + (error.error?.message || 'Bilinmeyen hata');
            this.showErrorModal = true;

            // Angular'a ekranı zorla güncellemesini emrediyoruz:
            this.cdr.detectChanges();
          },
        });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  closeErrorModal() {
    this.showErrorModal = false;
  }
}
