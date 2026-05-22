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

## 🚀 Backend Çalıştırma

Projenin backend tarafını yerel ortamınızda çalıştırmak için aşağıdaki adımları izleyin:

1. Terminalinizde `backend/` klasörüne gidin.
2. Projeyi tercih ettiğiniz bir Java IDE'sinde (IntelliJ IDEA, Eclipse, VS Code vb.) açın.
3. Maven (veya Wrapper) kullanarak gerekli bağımlılıkları indirin ve projeyi derleyin:
   ```bash
   mvn clean install
   ```
3. Projeyi başlatmak için ana sınıf olan com.works.backend.BackendApplication sınıfını çalıştırın.
4. Backend varsayılan olarak `http://localhost:8080` adresinde çalışır.

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
