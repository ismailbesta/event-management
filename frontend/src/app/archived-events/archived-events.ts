import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { IEvents, Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

@Component({
  selector: 'app-archived-events',
  imports: [CommonModule, RouterModule],
  templateUrl: './archived-events.html',
  styleUrls: ['./archived-events.css'],
})
export class ArchivedEvents implements OnInit {
  private http = inject(HttpClient);

  eventArray = signal<Event[]>([]);
  pages = signal<number[]>([]);
  activePage = signal<number>(0);
  loading = signal<boolean>(false);

  // Silme işlemi için gerekli
  actionLoading: { [key: number]: boolean } = {};
  showDeleteModal = signal<boolean>(false);
  selectedEventId = signal<number | null>(null);

  ngOnInit() {
    this.loadEvents(0);
  }

  loadEvents(page: number) {
    this.activePage.set(page);
    this.loading.set(true);

    this.http
      .get<IEvents>(`http://localhost:8080/event/archived-events?page=${page}`, {
        withCredentials: true,
      })
      .subscribe({
        next: (response) => {
          this.eventArray.set(response.content);
          this.pages.set(Array.from({ length: response.totalPages }, (_, i) => i));
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error loading archived events:', error);
          this.loading.set(false);
        },
      });
  }

  // --- SİLME MODALI METOTLARI ---
  openDeleteModal(eventId: number) {
    this.selectedEventId.set(eventId);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal() {
    this.showDeleteModal.set(false);
    this.selectedEventId.set(null);
  }

  confirmDelete() {
    const eventId = this.selectedEventId();
    if (eventId === null) return;

    this.actionLoading[eventId] = true;
    this.closeDeleteModal();

    this.http
      .delete(`http://localhost:8080/event/delete/${eventId}`, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => {
          this.eventArray.update((events) => events.filter((ev) => ev.id !== eventId));
        },
        error: (err) => console.error('Delete error:', err),
      });
  }

  categoryLabel(category: string): string {
    return getEventCategoryLabel(category);
  }

  statusLabel(status: string): string {
    return getEventStatusLabel(status);
  }
}
