package com.example.aksigorta_final.util;

public enum EventStatus {
    UNPUBLISHED,     // Etkinlik yayinda degil, kullanicilar katilim saglayamaz
    PUBLISHED,       // Etkinlik yayinda ve kullanicilar katilim saglayabilir
    ONGOING,         // Etkinlik su anda gerceklesiyor (saati geldi)
    COMPLETED,       // Etkinlik basariyla tamamlandi
    CANCELED,        // Etkinlik iptal edildi
    ARCHIVED         // Suresi gecmis veya yayindan kaldirilmis etkinlikler
}
