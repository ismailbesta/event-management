import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EVENT_CATEGORY_LABELS, Event } from '../../models/ievents';

interface EventUpdatePayload {
  id: number;
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
  selector: 'app-event-edit',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './event-edit.html',
  styleUrl: './event-edit.css',
})
export class EventEdit implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private formBuilder = inject(FormBuilder);

  eventForm: FormGroup;
  today = new Date().toISOString().slice(0, 10);
  categoryOptions = Object.entries(EVENT_CATEGORY_LABELS).map(([value, label]) => ({
    value,
    label,
  }));

  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  submitting = signal<boolean>(false);

  // YENİ: Sayfa ilk açıldığında veri çekilirken gösterilecek spinner
  pageLoading = signal<boolean>(true);
  eventId = signal<number | null>(null);

  constructor() {
    this.eventForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', [Validators.required, Validators.maxLength(1000)]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      startTime: ['', [Validators.required]],
      endTime: ['', [Validators.required]],
      location: ['', [Validators.required, Validators.maxLength(100)]],
      category: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const id = Number(params['id']);
      if (!id || Number.isNaN(id)) {
        this.router.navigate(['/events']);
        return;
      }

      this.eventId.set(id);
      this.fetchEventDetails(id);
    });
  }

  fetchEventDetails(id: number) {
    this.http.get<Event>(`http://localhost:8080/event/${id}`, { withCredentials: true }).subscribe({
      next: (event) => {
        // Gelen veriyi forma yerleştiriyoruz
        this.eventForm.patchValue({
          name: event.name,
          description: event.description,
          startDate: event.startDate,
          endDate: event.endDate,
          startTime: event.startTime,
          endTime: event.endTime,
          location: event.location,
          category: event.category,
        });
        this.pageLoading.set(false);
      },
      error: () => {
        this.router.navigate(['/events']);
      },
    });
  }

  control(name: string) {
    return this.eventForm.get(name);
  }

  onSubmit() {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    if (!this.validateDateRange()) {
      return;
    }

    // ID bilgisini payload'a ekliyoruz
    const payload: EventUpdatePayload = {
      ...this.eventForm.getRawValue(),
      id: this.eventId()!,
    };

    this.submitting.set(true);

    this.http
      .put('http://localhost:8080/event/update', payload, { withCredentials: true })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.successMessage.set('Etkinlik başarıyla güncellendi.');
        },
        error: (error) => {
          this.submitting.set(false);
          this.errorMessage.set(
            'Etkinlik güncellenemedi: ' + (error.error?.message || 'Bilinmeyen hata'),
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
