import 'package:flutter/material.dart';

/// Konfigurasi Tema Aplikasi Kasir Sederhana UMKM
class TemaKasir {
  // Latar belakang: Krem (#FAF7F0)
  static const Color background = Color(0xFFFAF7F0);

  // Warna Utama: Hijau Tua (#1F6B3A)
  static const Color warnaUtama = Color(0xFF1F6B3A);

  // Teks Utama: Hampir Hitam (#1A1A1A)
  static const Color teksUtama = Color(0xFF1A1A1A);

  // Warna Akses/Tombol Bayar: Oranye Bata (#C2410C)
  static const Color warnaAkses = Color(0xFFC2410C);

  // Merah hanya untuk hapus/peringatan (#DC2626)
  static const Color merahPeringatan = Color(0xFFDC2626);

  // Radius kecil: 4-8px
  static const double radiusKecil = 8.0;

  // Minimum area sentuh tombol: 56dp
  static const double minTouchTarget = 56.0;

  // Ukuran teks minimum: 16sp, Total: 32sp+, Kembalian: 48sp+
  static const TextStyle teksMinimum = TextStyle(
    fontSize: 16.0,
    color: teksUtama,
  );

  static const TextStyle teksTotal = TextStyle(
    fontSize: 32.0,
    fontWeight: FontWeight.bold,
    color: teksUtama,
  );

  static const TextStyle teksKembalian = TextStyle(
    fontSize: 48.0,
    fontWeight: FontWeight.bold,
    color: warnaUtama,
  );

  static ThemeData get themeData => ThemeData(
        scaffoldBackgroundColor: background,
        primaryColor: warnaUtama,
        colorScheme: const ColorScheme.light(
          primary: warnaUtama,
          secondary: warnaAkses,
          surface: background,
          error: merahPeringatan,
          onPrimary: Colors.white,
          onSecondary: Colors.white,
          onSurface: teksUtama,
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: warnaAkses,
            foregroundColor: Colors.white,
            minimumSize: const Size.fromHeight(minTouchTarget),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(radiusKecil),
            ),
          ),
        ),
        outlinedButtonTheme: OutlinedButtonThemeData(
          style: OutlinedButton.styleFrom(
            minimumSize: const Size.fromHeight(minTouchTarget),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(radiusKecil),
            ),
          ),
        ),
        cardTheme: CardTheme(
          color: Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(radiusKecil),
          ),
        ),
      );
}
