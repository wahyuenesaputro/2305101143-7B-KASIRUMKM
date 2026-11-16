package com.example.aplikasiumkm

import com.example.aplikasiumkm.models.Barang
import com.example.aplikasiumkm.models.KeranjangItem
import org.junit.Assert.assertEquals
import org.junit.Test

class KeranjangTest {

    @Test
    fun testKeranjangSubtotal() {
        val barang1 = Barang(id = 1, nama = "Beras 1kg", harga = 14000)
        val item1 = KeranjangItem(barang = barang1, jumlah = 2)
        assertEquals(28000L, item1.subtotal)

        val barang2 = Barang(id = 2, nama = "Mie Instan Goreng", harga = 3000)
        val item2 = KeranjangItem(barang = barang2, jumlah = 5)
        assertEquals(15000L, item2.subtotal)

        val total = listOf(item1, item2).sumOf { it.subtotal }
        assertEquals(43000L, total)
    }

    @Test
    fun testKembalianHitung() {
        val totalHarga = 43000L
        val nominalBayar = 50000L
        val kembalian = maxOf(0L, nominalBayar - totalHarga)
        assertEquals(7000L, kembalian)
    }
}
