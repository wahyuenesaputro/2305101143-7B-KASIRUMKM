package com.example.aplikasiumkm

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BarangValidationTest {

    private fun isValidBarangInput(nama: String, hargaStr: String): Boolean {
        val trimmedNama = nama.trim()
        val harga = hargaStr.trim().toLongOrNull() ?: 0L
        return trimmedNama.isNotEmpty() && harga > 0L
    }

    @Test
    fun testValidBarangInput() {
        assertTrue(isValidBarangInput("Kopi Sachet", "1500"))
        assertTrue(isValidBarangInput("Beras 5kg", "75000"))
    }

    @Test
    fun testInvalidBarangInput() {
        assertFalse(isValidBarangInput("", "1500"))
        assertFalse(isValidBarangInput("    ", "1500"))
        assertFalse(isValidBarangInput("Gula Pasir", "0"))
        assertFalse(isValidBarangInput("Gula Pasir", "-500"))
        assertFalse(isValidBarangInput("Gula Pasir", "abc"))
    }
}
