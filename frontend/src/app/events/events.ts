import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { IEvents, Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

@Component({
  selector: 'app-events',
  imports: [CommonModule, RouterModule],
  templateUrl: './events.html',
  styleUrls: ['./events.css'],
})
export class Events implements OnInit {
  private http = inject(HttpClient);

  eventArray = signal<Event[]>([]);
  pages = signal<number[]>([]);
  activePage = signal<number>(0);
  loading = signal<boolean>(false);

  // YENİ EKLENEN FİLTRE SIGNAL'İ
  activeCategory = signal<string | null>(null);

  // YENİ: KATEGORİLERİ GİZLE/GÖSTER DURUMU
  showAllCategories = signal<boolean>(false);

  // YENİ: "..." BUTONUNA TIKLANINCA ÇALIŞACAK METOT
  toggleCategories() {
    this.showAllCategories.update((show) => !show);
  }

  eventRoles = signal<{ [key: number]: string }>({});
  actionLoading: { [key: number]: boolean } = {};

  constructor() {
    console.log('Events component initialized');
  }

  ngOnInit() {
    this.loadEvents(0);
  }

  loadEvents(page: number) {
    this.activePage.set(page);
    this.loading.set(true);

    // YENİ EKLENEN DİNAMİK URL MANTIĞI
    const currentCategory = this.activeCategory();
    const endpoint = currentCategory
      ? `http://localhost:8080/event/category/${currentCategory}?page=${page}`
      : `http://localhost:8080/event/list?page=${page}`;

    this.http.get<IEvents>(endpoint, { withCredentials: true }).subscribe({
      next: (response) => {
        this.eventArray.set(response.content);
        this.pages.set(Array.from({ length: response.totalPages }, (_, i) => i));
        this.fetchRolesForEvents(response.content);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.loading.set(false);
      },
    });
  }

  // YENİ EKLENEN FİLTRELEME METODU
  filterByCategory(category: string | null) {
    this.activeCategory.set(category);
    this.loadEvents(0); // Filtre değiştiğinde 1. sayfaya dön
  }

  fetchRolesForEvents(events: Event[]) {
    events.forEach((event) => {
      this.updateRole(event.id, 'LOADING');
      this.http
        .get<{
          role: string;
        }>(`http://localhost:8080/event/user-role/${event.id}`, { withCredentials: true })
        .subscribe({
          next: (res) => this.updateRole(event.id, res.role),
          error: () => this.updateRole(event.id, 'GUEST'), // DÜZELTME: Eskiden LOADING'te kalıyordu, GUEST olmalı.
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
