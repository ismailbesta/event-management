import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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

  ngOnInit() {
    this.loadEvents(0);
  }

  loadEvents(page: number) {
    this.activePage.set(page);
    this.loading.set(true);

    // Backend'deki arşiv listesini getiren endpoint (Kendi endpoint'ine göre güncelleyebilirsin)
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

  categoryLabel(category: string): string {
    return getEventCategoryLabel(category);
  }

  statusLabel(status: string): string {
    return getEventStatusLabel(status);
  }
}
