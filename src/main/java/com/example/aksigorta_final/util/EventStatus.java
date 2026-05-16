package com.example.aksigorta_final.util;

public enum EventStatus {
    PLANNED,        // Etkinlik planlandı, henüz yayına alınmadı veya tarihi bekleniyor
    PUBLISHED,      // Etkinlik yayında ve kullanıcılar katılım sağlayabilir
    ONGOING,        // Etkinlik şu anda gerçekleşiyor (saati geldi)
    COMPLETED,      // Etkinlik başarıyla tamamlandı
    CANCELED,       // Etkinlik iptal edildi
    ARCHIVED        // Süresi geçmiş veya yayından kaldırılmış etkinlikler
}
