package com.example.aksigorta_final.util;

public enum UserStatus {
    ACTIVE,     // Kullanıcı sisteme giriş yapabilir ve tüm işlemleri gerçekleştirebilir
    PASSIVE,    // Kullanıcı hesabı dondurmuş olabilir, verileri saklanır ama işlem yapamaz
    BLOCKED    // Kullanıcı kural ihlali nedeniyle sistemden uzaklaştırılmıştır
}
