import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core'; // signal import edildi
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { EVENT_CATEGORY_LABELS } from '../../models/ievents';

interface EventCreatePayload {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  startTime: string;
  endTime: string;
  location: string;
  category: string;
}

@Component({
  selector: 'app-event-create',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './event-create.html',
  styleUrl: './event-create.css',
})
export class EventCreate {
  private http = inject(HttpClient);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);

  eventForm: FormGroup;
  today = new Date().toISOString().slice(0, 10);
  categoryOptions = Object.entries(EVENT_CATEGORY_LABELS).map(([value, label]) => ({
    value,
    label,
  }));

  // MODERN ANGULAR: Değişkenleri Signal yapısına çevirdik
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  createdEventId = signal<number | null>(null);
  submitting = signal<boolean>(false);

  constructor() {
    this.eventForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', [Validators.required, Validators.maxLength(1000)]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      startTime: ['09:00', [Validators.required]],
      endTime: ['17:00', [Validators.required]],
      location: ['', [Validators.required, Validators.maxLength(100)]],
      category: ['GENERAL', [Validators.required]],
    });
  }

  control(name: string) {
    return this.eventForm.get(name);
  }

  onSubmit() {
    // Sinyalleri sıfırla
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.createdEventId.set(null);

    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    if (!this.validateDateRange()) {
      return;
    }

    const payload = this.eventForm.getRawValue() as EventCreatePayload;
    this.submitting.set(true);

    this.http
      .post('http://localhost:8080/event/create', payload, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.submitting.set(false);
          this.successMessage.set('Etkinlik oluşturuldu. İstersen şimdi görüntüleyebilirsin.');
          this.createdEventId.set(response?.id ?? null);
          this.eventForm.reset({ category: 'GENERAL' });
        },
        error: (error) => {
          this.submitting.set(false);
          this.errorMessage.set(
            'Etkinlik oluşturulamadı: ' + (error.error?.message || 'Bilinmeyen hata'),
          );
        },
      });
  }

  private validateDateRange() {
    const { startDate, endDate, startTime, endTime } = this.eventForm.getRawValue();

    if (startDate && endDate && endDate < startDate) {
      this.errorMessage.set('Bitiş tarihi başlangıç tarihinden önce olamaz.');
      return false;
    }

    if (startDate && endDate && startDate === endDate && startTime && endTime) {
      if (endTime < startTime) {
        this.errorMessage.set('Aynı gün için bitiş saati başlangıç saatinden önce olamaz.');
        return false;
      }
    }

    return true;
  }
}
