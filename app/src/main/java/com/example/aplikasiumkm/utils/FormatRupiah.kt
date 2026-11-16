package com.example.aplikasiumkm.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Pemformat angka ke string dengan format "Rp 15.000" (titik sebagai pemisah ribuan, tanpa desimal).
 */
fun formatRupiah(number: Number): String {
    val localeID = Locale.forLanguageTag("id-ID")
    val formatter = NumberFormat.getIntegerInstance(localeID)
    val formattedNumber = formatter.format(number.toLong()).replace(',', '.')
    return "Rp $formattedNumber"
}
