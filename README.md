# Bomb "Disposal Operator" (Minesweeper Pro)

Aplikasi Minesweeper taktis modern untuk Android dengan tema *Tactical Bomb Disposal Operator*, visual OLED yang nyaman di mata, kendali jumlah bom yang sangat fleksibel, dan build otomatis APK menggunakan **GitHub Actions**.

---

## 🚀 1. Cara Generate File APK Otomatis di GitHub

Repositori ini sudah dilengkapi alur kerja otomatis (**GitHub Actions**) di `.github/workflows/build-apk.yml`.

### Langkah-langkah:
1. **Push kode ke GitHub**:
   - Setiap kali Anda melakukan `git push` ke branch utama (`main` / `master`), GitHub Actions akan otomatis mengompilasi APK.
2. **Menjalankan Manual (Satu Klik Tanpa Push)**:
   - Buka tab **Actions** di repositori GitHub Anda.
   - Pilih alur kerja **"Build Android APK"** di sebelah kiri.
   - Klik tombol dropdown **"Run workflow"** lalu klik **"Run workflow"** hijau.
3. **Mengunduh File APK**:
   - Setelah proses selesai (ikon checklist hijau ✅), klik nama run tersebut.
   - Gulir ke bagian bawah pada tabel **Artifacts**.
   - Klik **`Bomb-Disposal-Minesweeper-APK`** untuk langsung mengunduh file `.apk` siap pasang ke HP Anda!

---

## 💣 2. Cara Menentukan Jumlah Bom Sesuai Keinginan

Anda dapat menentukan jumlah bom secara instan melalui 2 cara yang sangat mudah:

1. **Tap Langsung di Indikator "ORDNANCE" (HUD Atas)**:
   - Ketuk kotak jumlah ranjau merah di bagian atas layar bertuliskan **ORDNANCE • ATUR**.
   - Dialog interaktif **"TENTUKAN JUMLAH BOM"** akan langsung terbuka.
2. **Tombol "ATUR BOM" di Bilah Kontrol**:
   - Di baris kontrol bawah (sebelah pilihan tingkat kesulitan), ketuk chip **"ATUR BOM"**.
   - Anda dapat:
     - Menggunakan tombol stepper cepat: `[-10]`, `[-5]`, `[-1]`, `[+1]`, `[+5]`, `[+10]`.
     - Menggeser slider presisi dari 1 hingga kapasitas maksimal kotak.
     - Memilih preset kepadatan: **Kasual (10%)**, **Standar (15%)**, **Pro (20%)**, atau **Ekstrem (25%)**.
     - Melihat analisis tingkat bahaya ranjau secara real-time.
     - Tekan **"MULAI MISI DENGAN X BOM"** untuk langsung memulai!

---

## 🎨 3. Visual & Skema Warna yang Nyaman di Mata ("Eye-Pleasing")

- **Midnight OLED Dark Canvas**: Menggunakan palet `#0B0F19` dan `#131B2A` yang pekat dan adem di mata, mencegah kelelahan saat bermain lama.
- **Harmonious Tactical Accents**:
  - Angka 1: Sky Azure (`#38BDF8`)
  - Angka 2: Mint Emerald (`#34D399`)
  - Angka 3: Coral Crimson (`#F87171`)
  - Angka 4: Indigo Violet (`#818CF8`)
  - Angka 5: Warm Amber (`#FBBF24`)
  - Angka 6: Seafoam Teal (`#2DD4BF`)
  - Angka 7: Rose Flamingo (`#F472B6`)
  - Angka 8: Cool Titanium Slate (`#94A3B8`)
- **Desain Kotak Taktis**: Tekstur gradient halus dengan sudut membulat modern (`4dp`) dan highlight visual bevel yang tegas.

---

## ⚡ 4. Peningkatan Efisiensi UI & UX

- **One-Tap Mode Switch**: Beralih antara mode **REVEAL** (Gali) dan **FLAG** (Pasang Bendera) dalam 1 sentuhan.
- **Support Long-Press Quick Flag**: Tetap bisa tekan lama pada kotak untuk langsung memasang bendera tanpa perlu mengganti mode.
- **Quick Zoom & Pan**: Kontrol zoom `[-]`, `[Fit View]`, `[+]` memudahkan navigasi di layar kecil maupun besar.
- **Autosave Real-Time**: Status permainan otomatis tersimpan ke Room SQLite lokal, aman jika aplikasi ditutup tidak sengaja.
- **Statistik & Peringkat**: Papan skor lokal dan analitik performa operator lengkap (win rate, rekor waktu tercepat, streak kemenangan).
