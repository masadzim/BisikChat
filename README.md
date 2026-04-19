# 🔵 BisikChat v2.0 — Bluetooth Chat + Login Lokal

Chat P2P via Bluetooth. **Tanpa internet. Tanpa cloud. Tanpa akun online.**
Identitas pengguna disimpan 100% di perangkat sendiri (SharedPreferences).

---

## 🗺️ Alur Aplikasi

```
Buka App
   │
   ▼
SplashActivity (1.8 detik)
   │
   ├── Belum ada profil ──▶ RegisterActivity (buat profil + PIN)
   │                                │
   └── Sudah ada profil ──▶ LoginActivity (masukkan PIN)
                                     │
                              MainActivity (chat Bluetooth)
```

---

## 🔐 Sistem Login Lokal

- **Tidak ada server** — semua data di `SharedPreferences` device
- **Tidak ada internet** — zero network request
- **PIN lokal** — 4-6 digit, tersimpan di device
- **Identitas** — nama, avatar otomatis (inisial), warna unik, status
- **Logout** → hapus semua data profil dari device

---

## 📁 Struktur Project

```
BisikChat/
├── gradlew / gradlew.bat         ← Build scripts
├── build.gradle                  ← Root gradle
├── settings.gradle               ← Project settings
├── gradle.properties             ← JVM config
├── gradle/wrapper/
│   └── gradle-wrapper.properties ← Gradle version
├── preview-UI.html               ← Preview visual (buka di browser)
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/bisikChat/
        │   ├── SplashActivity.java    ← Entry point, routing login
        │   ├── RegisterActivity.java  ← Daftar profil lokal
        │   ├── LoginActivity.java     ← PIN numpad
        │   ├── MainActivity.java      ← Chat utama + identitas
        │   ├── UserSession.java       ← Manajemen data lokal
        │   ├── BluetoothService.java  ← Koneksi BT RFCOMM
        │   ├── ChatAdapter.java       ← RecyclerView adapter
        │   └── ChatMessage.java       ← Model pesan
        └── res/
            ├── layout/
            │   ├── activity_splash.xml
            │   ├── activity_register.xml  ← Form buat profil
            │   ├── activity_login.xml     ← Numpad PIN
            │   ├── activity_main.xml      ← Chat + header identitas
            │   ├── item_msg_mine.xml
            │   ├── item_msg_theirs.xml    ← Tampilkan nama pengirim
            │   └── item_msg_system.xml
            ├── drawable/
            │   ├── avatar_bg.xml
            │   ├── bubble_mine.xml
            │   ├── bubble_theirs.xml
            │   ├── input_bg.xml
            │   └── num_btn_bg.xml
            └── values/
                ├── styles.xml   ← NumBtn style untuk PIN pad
                └── strings.xml
```

---

## 🛠️ Cara Build APK

### Syarat:
- **Android Studio** (gratis) → https://developer.android.com/studio
- **JDK 8+** (biasanya sudah bundled di Android Studio)

### Langkah:
```bash
# 1. Ekstrak ZIP ini
# 2. Buka Android Studio → File → Open → pilih folder BisikChat
# 3. Tunggu Gradle sync (butuh internet sekali untuk download dependencies)
# 4. Build → Build Bundle(s) / APK(s) → Build APK(s)
# 5. APK ada di: app/build/outputs/apk/debug/app-debug.apk
# 6. Kirim APK ke HP Istri (via kabel/WA), install di KEDUA HP
```

---

## 📱 Cara Pakai

### Setup pertama (sekali saja):
1. **Pair** kedua HP via Settings → Bluetooth
2. Buka BisikChat di masing-masing HP
3. Masing-masing **buat profil** → isi nama, PIN, status

### Setiap kali pakai:
| HP Istri (menunggu)        | HP Kamu (menghubungkan)      |
|----------------------------|------------------------------|
| Buka BisikChat             | Buka BisikChat               |
| Masukkan PIN               | Masukkan PIN                 |
| Tap **"Dapat Ditemukan"**  | Tap **"Hubungkan"**          |
| Tunggu...                  | Pilih nama HP Istri          |
| Status 🟢 Terhubung ✓     | Status 🟢 Terhubung ✓       |

### Saat chat:
- Nama pengirim otomatis muncul di setiap pesan
- Long press avatar kiri atas → lihat profil / edit status / logout

---

## ✨ Fitur v2.0

- ✅ **Login lokal** — profil tersimpan di device, bukan cloud
- ✅ **PIN numpad** — keamanan sederhana tanpa akun online
- ✅ **Identitas di chat** — nama pengirim tampil di setiap pesan
- ✅ **Avatar warna** — inisial nama + warna unik otomatis
- ✅ **Status pengguna** — bisa diubah kapan saja
- ✅ **Logout** — hapus profil dari device
- ✅ **Bluetooth RFCOMM** — P2P langsung tanpa internet
- ✅ **Jangkauan ~10 meter**
- ✅ **Android 6.0+ (API 23+)**
