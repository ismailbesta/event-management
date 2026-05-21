import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './register.html', // .component kısmını ekledik (senin klasör yapına uygun)
  styleUrls: ['./register.css'], // .component kısmını ekledik
})
export class Register {
  private http = inject(HttpClient);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  registerForm: FormGroup;
  errorMessage: string | null = null;
  showErrorModal = false;

  // Constructor içinde formu ve kurallarını (DTO'na tam uyumlu şekilde) inşa ediyoruz
  constructor(private formBuilder: FormBuilder) {
    this.registerForm = this.formBuilder.group({
      // Ad: Zorunlu, maks 100 karakter
      name: ['', [Validators.required, Validators.maxLength(100)]],

      // Soyad: Zorunlu, maks 100 karakter
      surname: ['', [Validators.required, Validators.maxLength(100)]],

      // Email: Zorunlu, geçerli email formatı, maks 200 karakter
      email: ['', [Validators.required, Validators.email, Validators.maxLength(200)]],

      // Şifre: Zorunlu, en az 6, maks 1000 karakter
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(1000)]],
    });
  }

  // Kayıt ol butonuna tıklandığında çalışacak metot
  onSubmit() {
    if (this.registerForm.valid) {
      this.errorMessage = null;
      this.showErrorModal = false;
      const registerData = this.registerForm.value;
      this.http
        .post('http://localhost:8080/user/register', registerData, { withCredentials: true })
        .subscribe({
          next: (response) => {
            this.router.navigate(['login'], { queryParams: { registered: '1' } });
          },
          error: (error) => {
            this.errorMessage = 'Kayıt hatası: ' + (error.error?.message || 'Bilinmeyen hata');
            this.showErrorModal = true;

            // Angular'a ekranı zorla güncellemesini emrediyoruz:
            this.cdr.detectChanges();
          },
        });
    } else {
      // Eğer form hatalıysa ve butona basıldıysa, hataları ekranda göstermesi için formu 'dokunulmuş' işaretle
      this.registerForm.markAllAsTouched();
    }
  }

  closeErrorModal() {
    this.showErrorModal = false;
  }
}
