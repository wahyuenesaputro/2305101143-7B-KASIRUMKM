package com.example.aplikasiumkm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.aplikasiumkm.utils.TemaKasir

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = TemaKasir.UkuranTeksMinimum,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = TemaKasir.TeksUtamaHampirHitam
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = TemaKasir.UkuranTeksMinimum,
        lineHeight = 22.sp,
        color = TemaKasir.TeksUtamaHampirHitam
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = TemaKasir.UkuranTeksTotal,
        color = TemaKasir.TeksUtamaHampirHitam
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = TemaKasir.UkuranTeksKembalian,
        color = TemaKasir.WarnaUtamaHijauTua
    )
)
