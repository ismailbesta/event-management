import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { IEvents, Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

@Component({
  selector: 'app-joined-events',
  imports: [CommonModule, RouterModule],
  templateUrl: './joined-events.html',
  styleUrls: ['./joined-events.css'], // Kendi css dosyanı işaret ettiğinden emin ol
})
export class JoinedEvents implements OnInit {
  private http = inject(HttpClient);

  eventArray = signal<Event[]>([]);
  pages = signal<number[]>([]);
  activePage = signal<number>(0);
  loading = signal<boolean>(false);

  actionLoading: { [key: number]: boolean } = {};

  ngOnInit() {
    this.loadEvents(0);
  }

  loadEvents(page: number) {
    this.activePage.set(page);
    this.loading.set(true);

    this.http
      .get<IEvents>(`http://localhost:8080/event/joined-events?page=${page}`, {
        withCredentials: true,
      })
      .subscribe({
        next: (response) => {
          this.eventArray.set(response.content);
          this.pages.set(Array.from({ length: response.totalPages }, (_, i) => i));
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error loading joined events:', error);
          this.loading.set(false);
        },
      });
  }

  leaveEvent(eventId: number) {
    this.actionLoading[eventId] = true;
    this.http
      .post(`http://localhost:8080/event/leave/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => {
          // HARİKA UX: Başarıyla ayrıldıktan sonra o etkinliği listeden anında uçuruyoruz!
          this.eventArray.update((events) => events.filter((e) => e.id !== eventId));
        },
        error: (err) => console.error('Leave event error:', err),
      });
  }

  categoryLabel(category: string): string {
    return getEventCategoryLabel(category);
  }

  statusLabel(status: string): string {
    return getEventStatusLabel(status);
  }
}
