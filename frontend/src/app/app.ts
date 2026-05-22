import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
// ErrorService'in yolunu kendi projene göre düzenle (genelde böyledir)
import { ErrorService } from './core/services/error';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');

  // YENİ: Global hata servisimizi buraya dahil ediyoruz
  errorService = inject(ErrorService);
}
