# Usta Kulüp Android

**Usta Kulüp** web uygulamasının native Android mobil versiyonu.

## Tech Stack

| Katman | Teknoloji |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Dil | Kotlin |
| DI | Hilt |
| Network | Retrofit 2 + OkHttp |
| Lokal Depolama | DataStore Preferences (JWT token) |
| Navigation | Navigation Compose |
| Architecture | MVVM (ViewModel + StateFlow) |

---

## Proje Yapısı

```
app/src/main/java/com/ustakulup/
├── data/
│   ├── model/
│   │   └── Models.kt          → Tüm veri modelleri (User, ServiceRequest, Offer...)
│   ├── network/
│   │   ├── ApiService.kt      → Retrofit endpoint'leri
│   │   └── AuthInterceptor.kt → JWT header ekleme
│   ├── repository/
│   │   └── UstaKulupRepository.kt → Tüm API çağrıları + Result sealed class
│   └── local/
│       └── TokenDataStore.kt  → JWT token + kullanıcı bilgisi saklama
├── di/
│   └── NetworkModule.kt       → Hilt DI (Retrofit, OkHttp, Repository)
├── ui/
│   ├── auth/
│   │   ├── AuthViewModel.kt
│   │   └── AuthScreens.kt     → Login, Register
│   ├── user/
│   │   ├── UserViewModel.kt
│   │   └── UserScreens.kt     → Dashboard, NewRequest, RequestDetail
│   ├── professional/
│   │   ├── ProfessionalViewModel.kt
│   │   └── ProfessionalScreens.kt → Apply, Dashboard, Offer
│   ├── admin/
│   │   ├── AdminViewModel.kt
│   │   └── AdminScreens.kt    → Professionals, Quotas, Requests
│   ├── navigation/
│   │   └── NavGraph.kt        → Tüm ekran rotaları ve navigasyon
│   ├── components/
│   │   └── Components.kt      → Ortak UI bileşenler
│   └── theme/
│       └── Theme.kt           → Renk paleti ve MaterialTheme
├── MainActivity.kt
└── UstaKulupApp.kt
```

---

## Kurulum

### 1. Projeyi Android Studio'da açın

```bash
# Android Studio → File → Open → usta-kulup-android klasörünü seçin
```

### 2. Backend URL'ini ayarlayın

`app/build.gradle` içinde:

```groovy
// Emülatör için:
buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:3000/api/\""

// Gerçek cihaz için (bilgisayarınızın IP'si):
buildConfigField "String", "BASE_URL", "\"http://192.168.1.x:3000/api/\""

// Production:
buildConfigField "String", "BASE_URL", "\"https://your-domain.com/api/\""
```

### 3. Backend'i çalıştırın

```bash
cd usta-kulup
npm run dev
```

### 4. Uygulamayı çalıştırın

Android Studio'da **Run ▶** butonuna basın.

---

## Ekranlar

### Auth
| Ekran | Açıklama |
|-------|----------|
| Login | E-posta/şifre ile giriş, rol bazlı yönlendirme |
| Register | Yeni kullanıcı kaydı |

### User (Müşteri)
| Ekran | Açıklama |
|-------|----------|
| Dashboard | Tüm talepler listesi, durum badge'leri |
| New Request | Kategori + ilçe seçerek talep oluşturma |
| Request Detail | Talep detayı, gelen teklifler, teklif seçme, değerlendirme |

### Professional (Usta)
| Ekran | Açıklama |
|-------|----------|
| Apply | Usta başvuru formu |
| Dashboard | Atanan talepler + aktif işler + istatistik |
| Offer | Talep detayı görme + fiyat teklifi gönderme |

### Admin
| Ekran | Açıklama |
|-------|----------|
| Professionals | Bekleyen/onaylı ustalar, onayla/reddet |
| Quotas | İlçe+kategori kota görüntüleme ve ayarlama |
| Requests | Tüm talepler, durum filtresi |

---

## Mimari

```
UI Layer (Compose Screens)
        ↕
ViewModel (StateFlow)
        ↕
Repository (suspend fun)
        ↕
ApiService (Retrofit)  ←→  Backend Next.js API
        ↕
AuthInterceptor (JWT)
        ↕
DataStore (Token)
```

### Hata Yönetimi

`Result<T>` sealed class kullanılır:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

---

## Otomatik Oturum

Uygulama açıldığında `DataStore`'daki token + kullanıcı bilgisi kontrol edilir.
Token varsa kullanıcı doğrudan kendi paneline yönlendirilir.

---

## Test Hesapları

Web uygulamasındaki seed verileriyle aynı:

| Rol | E-posta | Şifre |
|-----|---------|-------|
| Admin | admin@ustakulup.com | admin123 |
| User | user@example.com | user123 |
| Professional | mehmet@example.com | pro123 |

---

## Backend Cookie Notu

Next.js backend `httpOnly` cookie kullanıyor. Android tarafında login response'undaki
`Set-Cookie` header'ından token parse edilip DataStore'a kaydedilir.
Sonraki isteklerde hem `Authorization: Bearer <token>` hem de `Cookie: token=<token>`
header'ları gönderilir.

Eğer backend JWT doğrulaması farklı çalışıyorsa `lib/auth.ts` dosyasına bakarak
`AuthInterceptor.kt`'yi düzenleyin.
