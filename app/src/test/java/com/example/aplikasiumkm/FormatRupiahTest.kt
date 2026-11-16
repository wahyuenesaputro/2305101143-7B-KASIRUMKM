package com.example.aplikasiumkm

import com.example.aplikasiumkm.utils.formatRupiah
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatRupiahTest {

    @Test
    fun testFormatRupiah() {
        assertEquals("Rp 15.000", formatRupiah(15000))
        assertEquals("Rp 0", formatRupiah(0))
        assertEquals("Rp 16.500", formatRupiah(16500))
        assertEquals("Rp 1.500", formatRupiah(1500))
        assertEquals("Rp 28.000", formatRupiah(28000))
    }
}
