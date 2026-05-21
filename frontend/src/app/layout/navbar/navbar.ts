import { HttpClient } from '@angular/common/http';
import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Router eklendi
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ngModel için eklendi

@Component({
  selector: 'app-navbar',
  imports: [RouterModule, CommonModule, FormsModule], // FormsModule eklendi
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router); // Router inject edildi

  globalName = 'Guest';
  showLogoutModal = false;
  showLogoutError = false;

  // YENİ: Arama metnini tutacak değişken
  searchQuery = '';

  constructor() {
    const name = localStorage.getItem('name');
    if (name) {
      this.globalName = name;
    }
  }

  // YENİ: Arama formunu tetikleyen metot
  onSearch(event: Event) {
    event.preventDefault(); // Sayfanın yenilenmesini engeller
    if (this.searchQuery.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery.trim() } });
    }
  }

  logout() {
    this.showLogoutModal = true;
    this.showLogoutError = false;
    this.cdr.detectChanges();
  }

  closeLogoutModal() {
    this.showLogoutModal = false;
    this.showLogoutError = false;
    this.cdr.detectChanges();
  }

  confirmLogout() {
    this.http.post('http://localhost:8080/user/logout', null, { withCredentials: true }).subscribe({
      next: (response) => {
        localStorage.clear();
        this.globalName = 'Guest';
        this.showLogoutModal = false;
        window.location.href = '/';
      },
      error: (error) => {
        this.showLogoutError = true;
        this.cdr.detectChanges();
      },
    });
  }
}
