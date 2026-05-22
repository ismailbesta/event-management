import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { IEvents, Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

@Component({
  selector: 'app-my-events',
  imports: [CommonModule, RouterModule],
  templateUrl: './my-events.html',
  styleUrls: ['./my-events.css'],
})
export class MyEvents implements OnInit {
  private http = inject(HttpClient);

  eventArray = signal<Event[]>([]);
  pages = signal<number[]>([]);
  activePage = signal<number>(0);
  loading = signal<boolean>(false);

  // Buton yükleniyor durumları
  actionLoading: { [key: number]: boolean } = {};

  // MODAL YÖNETİMİ İÇİN SİNYALLER
  showCancelModal = signal<boolean>(false);
  showArchiveModal = signal<boolean>(false);
  showDeleteModal = signal<boolean>(false);
  selectedEventId = signal<number | null>(null);

  ngOnInit() {
    this.loadEvents(0);
  }

  loadEvents(page: number) {
    this.activePage.set(page);
    this.loading.set(true);

    this.http
      .get<IEvents>(`http://localhost:8080/event/my-events?page=${page}`, { withCredentials: true })
      .subscribe({
        next: (response) => {
          this.eventArray.set(response.content);
          this.pages.set(Array.from({ length: response.totalPages }, (_, i) => i));
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error loading my events:', error);
          this.loading.set(false);
        },
      });
  }

  publishEvent(eventId: number) {
    this.actionLoading[eventId] = true;
    this.http
      .patch(`http://localhost:8080/event/publish/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => this.updateEventStatusLocally(eventId, 'PUBLISHED'),
        error: (err) => console.error('Publish error:', err),
      });
  }

  unpublishEvent(eventId: number) {
    this.actionLoading[eventId] = true;
    this.http
      .patch(`http://localhost:8080/event/unpublish/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => this.updateEventStatusLocally(eventId, 'UNPUBLISHED'),
        error: (err) => console.error('Unpublish error:', err),
      });
  }

  // --- İPTAL ETME MODALI ---
  openCancelModal(eventId: number) {
    this.selectedEventId.set(eventId);
    this.showCancelModal.set(true);
  }

  closeCancelModal() {
    this.showCancelModal.set(false);
    this.selectedEventId.set(null);
  }

  confirmCancel() {
    const eventId = this.selectedEventId();
    if (eventId === null) return;

    this.actionLoading[eventId] = true;
    this.closeCancelModal();

    this.http
      .patch(`http://localhost:8080/event/cancel/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => this.updateEventStatusLocally(eventId, 'CANCELED'),
        error: (err) => console.error('Cancel error:', err),
      });
  }

  // --- ARŞİVLEME MODALI ---
  openArchiveModal(eventId: number) {
    this.selectedEventId.set(eventId);
    this.showArchiveModal.set(true);
  }

  closeArchiveModal() {
    this.showArchiveModal.set(false);
    this.selectedEventId.set(null);
  }

  confirmArchive() {
    const eventId = this.selectedEventId();
    if (eventId === null) return;

    this.actionLoading[eventId] = true;
    this.closeArchiveModal();

    this.http
      .patch(`http://localhost:8080/event/archive/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => {
          this.eventArray.update((events) => events.filter((ev) => ev.id !== eventId));
        },
        error: (err) => console.error('Archive error:', err),
      });
  }

  // --- SİLME MODALI ---
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

  private updateEventStatusLocally(eventId: number, newStatus: string) {
    this.eventArray.update((events) =>
      events.map((ev) => (ev.id === eventId ? { ...ev, status: newStatus } : ev)),
    );
  }

  categoryLabel(category: string): string {
    return getEventCategoryLabel(category);
  }

  statusLabel(status: string): string {
    return getEventStatusLabel(status);
  }
}
