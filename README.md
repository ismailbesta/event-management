# Aksigorta Final Projesi

## Proje Açıklaması

Bu proje, Spring Boot tabanlı bir backend ve Angular tabanlı bir frontend ile geliştirilmiş bir uygulamadır. Uygulamada etkinlik yönetimi, kullanıcı işlemleri ve REST API üzerinden veri alışverişi gibi temel özellikler yer alır.

## Kullanılan Teknolojiler

- Backend: Java 17, Spring Boot, Spring Web MVC, Spring Data JPA, Spring Validation
- Veritabanı: H2 Database
- API Dokümantasyonu: Springdoc OpenAPI / Swagger
- Yardımcı Kütüphaneler: Lombok, ModelMapper, jBCrypt
- Frontend: Angular 21, TypeScript,

## Kurulum Adımları

1. Projeyi bilgisayarınıza alın ve kök dizine geçin.
2. Backend ve frontend için gerekli bağımlılıkların yüklü olduğundan emin olun.
3. Backend için Java 17, frontend için angular cli kurulu olmalıdır.

## Backend Çalıştırma

1. `backend` klasörüne geçin.
2. Uygulamayı çalıştırın:

```bash
./mvnw spring-boot:run
```

3. Backend varsayılan olarak `http://localhost:8080` adresinde çalışır.

## Frontend Çalıştırma

1. `frontend` klasörüne geçin.
2. Bağımlılıkları yükleyin:

```bash
npm install
```

3. Uygulamayı başlatın:

```bash
npm start
```

4. Frontend varsayılan olarak `http://localhost:4200` adresinde çalışır.

## Swagger Bağlantısı

Backend çalıştıktan sonra Swagger arayüzüne şu adresten ulaşabilirsiniz:

```text
http://localhost:8080/swagger-ui/index.html
```

## Veritabanı Bilgileri

- Veritabanı türü: H2
- JDBC URL: `jdbc:h2:file:~/aksigorta_final_db;AUTO_SERVER=TRUE`
- Kullanıcı adı: `sa`
- Şifre: `sa`
- JPA ayarı: `spring.jpa.hibernate.ddl-auto=update`

## Not

H2 veritabanı dosyası kullanıcı ana dizininde `aksigorta_final_db` adıyla tutulur.
