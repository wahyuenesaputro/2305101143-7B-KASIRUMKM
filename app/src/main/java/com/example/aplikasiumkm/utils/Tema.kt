package com.example.aplikasiumkm.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Konfigurasi Tema Kasir Sederhana UMKM
 */
object TemaKasir {
    // Warna
    val LatarBelakangKrem = Color(0xFFFAF7F0)
    val WarnaUtamaHijauTua = Color(0xFF1F6B3A)
    val TeksUtamaHampirHitam = Color(0xFF1A1A1A)
    val WarnaAksesOranyeBata = Color(0xFFC2410C)
    val MerahPeringatan = Color(0xFFDC2626)
    val Putih = Color(0xFFFFFFFF)

    // Layout & Radius
    val RadiusKecil: Dp = 8.dp
    val ShapeRadiusKecil = RoundedCornerShape(RadiusKecil)
    val MinTouchTarget: Dp = 56.dp

    // Tipografi Ukuran Teks
    val UkuranTeksMinimum: TextUnit = 16.sp
    val UkuranTeksTotal: TextUnit = 32.sp
    val UkuranTeksKembalian: TextUnit = 48.sp

    // Styles
    val StyleTeksUtama = TextStyle(
        fontSize = UkuranTeksMinimum,
        color = TeksUtamaHampirHitam
    )

    val StyleTeksTotal = TextStyle(
        fontSize = UkuranTeksTotal,
        fontWeight = FontWeight.Bold,
        color = TeksUtamaHampirHitam
    )

    val StyleTeksKembalian = TextStyle(
        fontSize = UkuranTeksKembalian,
        fontWeight = FontWeight.Bold,
        color = WarnaUtamaHijauTua
    )
}
