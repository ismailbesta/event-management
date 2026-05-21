import { HttpClient } from '@angular/common/http';
import { CommonModule, DatePipe } from '@angular/common';
import { Component, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { Event, getEventCategoryLabel, getEventStatusLabel } from '../../models/ievents';

// YENİ: Model klasöründen import ediyoruz
import { Participant, ParticipantResponse } from '../../models/iparticipant';

@Component({
  selector: 'app-event-detail',
  imports: [CommonModule, DatePipe, RouterModule],
  templateUrl: './event-detail.html',
  styleUrls: ['./event-detail.css'],
})
export class EventDetail implements OnInit {
  eventItem = signal<Event | null>(null);
  categoryLabel = getEventCategoryLabel;
  statusLabel = getEventStatusLabel;
  userRole = signal<string | null>(null);

  isActionLoading = signal<boolean>(false);
  showCancelModal = signal<boolean>(false);

  // KATILIMCI MODALI İÇİN DEĞİŞKENLER
  showParticipantsModal = signal<boolean>(false);
  participantsLoading = signal<boolean>(false);
  participants = signal<Participant[]>([]); // Import edilen modeli kullanıyor
  participantPage = signal<number>(0);
  participantTotalPages = signal<number>(0);

  private eventId: number | null = null;

  // ... (Kodun geri kalanı tamamen aynı kalacak, hiçbir şeyi değiştirmene gerek yok)

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const id = Number(params['id']);

      if (!id || Number.isNaN(id)) {
        this.router.navigate(['/events']);
        return;
      }

      this.eventId = id;
      this.loadEvent(id);
      this.loadUserRole(id);
    });
  }

  isEventActive(status: string): boolean {
    return !['CANCELED', 'COMPLETED', 'ARCHIVED', 'TIMEOUT'].includes(status);
  }

  canJoinOrLeave(status: string): boolean {
    return ['PUBLISHED', 'ONGOING'].includes(status);
  }

  openCancelModal() {
    this.showCancelModal.set(true);
  }

  closeCancelModal() {
    this.showCancelModal.set(false);
  }

  confirmCancel() {
    if (!this.eventId) return;

    this.isActionLoading.set(true);
    this.closeCancelModal();

    this.http
      .patch(`http://localhost:8080/event/cancel/${this.eventId}`, null, {
        withCredentials: true,
      })
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: () => this.loadEvent(this.eventId!),
        error: (error) => console.error('Error cancelling event:', error),
      });
  }

  publishEvent() {
    if (!this.eventId) return;

    this.isActionLoading.set(true);
    this.http
      .patch(`http://localhost:8080/event/publish/${this.eventId}`, null, {
        withCredentials: true,
      })
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: () => this.loadEvent(this.eventId!),
        error: (error) => console.error('Error publishing event:', error),
      });
  }

  joinEvent() {
    if (!this.eventId || this.isActionLoading()) return;

    this.isActionLoading.set(true);
    this.http
      .post(`http://localhost:8080/event/join/${this.eventId}`, null, { withCredentials: true })
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: () => {
          this.userRole.set('PARTICIPANT');
          this.loadEvent(this.eventId!); // Katılımcı sayısını güncellemek için etkinliği tekrar çek
        },
        error: (error) => console.error('Error joining event:', error),
      });
  }

  leaveEvent() {
    if (!this.eventId || this.isActionLoading()) return;

    this.isActionLoading.set(true);
    this.http
      .post(`http://localhost:8080/event/leave/${this.eventId}`, null, { withCredentials: true })
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: () => {
          this.userRole.set('GUEST');
          this.loadEvent(this.eventId!); // Katılımcı sayısını güncellemek için etkinliği tekrar çek
        },
        error: (error) => console.error('Error leaving event:', error),
      });
  }

  // --- YENİ: KATILIMCI MODALI YÖNETİMİ ---
  openParticipantsModal() {
    if (!this.eventId) return;
    this.showParticipantsModal.set(true);
    this.loadParticipants(0); // İlk sayfayı çek
  }

  closeParticipantsModal() {
    this.showParticipantsModal.set(false);
  }

  loadParticipants(page: number) {
    if (!this.eventId) return;

    this.participantPage.set(page);
    this.participantsLoading.set(true);

    this.http
      .get<ParticipantResponse>(
        `http://localhost:8080/event/participants/${this.eventId}?page=${page}`,
        {
          withCredentials: true,
        },
      )
      .subscribe({
        next: (res) => {
          this.participants.set(res.content);
          this.participantTotalPages.set(res.totalPages);
          this.participantsLoading.set(false);
        },
        error: (err) => {
          console.error('Katılımcılar yüklenirken hata:', err);
          this.participantsLoading.set(false);
        },
      });
  }

  getParticipantPages(): number[] {
    return Array.from({ length: this.participantTotalPages() }, (_, i) => i);
  }

  private loadEvent(eventId: number) {
    this.http
      .get<Event>(`http://localhost:8080/event/${eventId}`, { withCredentials: true })
      .subscribe({
        next: (response) => this.eventItem.set(response),
        error: () => this.router.navigate(['/events']),
      });
  }

  private loadUserRole(eventId: number) {
    this.userRole.set(null);
    this.http
      .get<{ role: string }>(`http://localhost:8080/event/user-role/${eventId}`, {
        withCredentials: true,
      })
      .subscribe({
        next: (response) => this.userRole.set(response?.role ?? 'GUEST'),
        error: () => this.userRole.set('GUEST'),
      });
  }
}
