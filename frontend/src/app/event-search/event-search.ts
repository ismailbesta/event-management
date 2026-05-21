import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { IEvents, Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

@Component({
  selector: 'app-event-search',
  imports: [CommonModule, RouterModule],
  templateUrl: './event-search.html',
  styleUrls: ['./event-search.css'],
})
export class EventSearch implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  searchQuery = signal<string>('');
  eventArray = signal<Event[]>([]);
  pages = signal<number[]>([]);
  activePage = signal<number>(0);
  loading = signal<boolean>(false);
  totalElements = signal<number>(0);

  eventRoles = signal<{ [key: number]: string }>({});
  actionLoading: { [key: number]: boolean } = {};

  ngOnInit() {
    // queryParams'ı subscribe ile dinliyoruz ki her arama yapıldığında sayfa kendini güncellesin
    this.route.queryParams.subscribe((params) => {
      const query = params['q'] || '';
      this.searchQuery.set(query);
      this.searchEvents(0);
    });
  }

  searchEvents(page: number = 0) {
    const query = this.searchQuery();
    if (!query) return;

    this.activePage.set(page);
    this.loading.set(true);

    const url = `http://localhost:8080/event/search?q=${query}&page=${page}&startDate=desc`;

    this.http.get<IEvents>(url, { withCredentials: true }).subscribe({
      next: (response) => {
        this.eventArray.set(response.content);
        this.pages.set(Array.from({ length: response.totalPages }, (_, i) => i));
        this.totalElements.set(response.totalElements);

        this.fetchRolesForEvents(response.content);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Arama hatası:', error);
        this.loading.set(false);
      },
    });
  }

  fetchRolesForEvents(events: Event[]) {
    events.forEach((event) => {
      this.updateRole(event.id, 'LOADING');
      this.http
        .get<{ role: string }>(`http://localhost:8080/event/user-role/${event.id}`, {
          withCredentials: true,
        })
        .subscribe({
          next: (res) => this.updateRole(event.id, res.role),
          error: () => this.updateRole(event.id, 'GUEST'),
        });
    });
  }

  private updateRole(eventId: number, role: string) {
    this.eventRoles.update((roles) => ({ ...roles, [eventId]: role }));
  }

  roleFor(eventId: number): string {
    return this.eventRoles()[eventId] || 'LOADING';
  }

  joinEvent(eventId: number) {
    this.actionLoading[eventId] = true;
    this.http
      .post(`http://localhost:8080/event/join/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => this.updateRole(eventId, 'PARTICIPANT'),
        error: (err) => console.error('Join event error:', err),
      });
  }

  leaveEvent(eventId: number) {
    this.actionLoading[eventId] = true;
    this.http
      .post(`http://localhost:8080/event/leave/${eventId}`, {}, { withCredentials: true })
      .pipe(finalize(() => (this.actionLoading[eventId] = false)))
      .subscribe({
        next: () => this.updateRole(eventId, 'GUEST'),
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
