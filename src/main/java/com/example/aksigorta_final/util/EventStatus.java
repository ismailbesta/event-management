package com.example.aksigorta_final.util;

public enum EventStatus {
    PLANNED,        // Etkinlik planlandı, henüz yayına alınmadı veya tarihi bekleniyor
    PUBLISHED,      // Etkinlik yayında ve kullanıcılar katılım sağlayabilir [cite: 66]
    ONGOING,        // Etkinlik şu anda gerçekleşiyor (saati geldi)
    COMPLETED,      // Etkinlik başarıyla tamamlandı
    CANCELED,       // Etkinlik iptal edildi
    POSTPONED,      // Etkinlik ileri bir tarihe ertelendi
    ARCHIVED        // Süresi geçmiş veya yayından kaldırılmış etkinlikler [cite: 67, 68]
}
