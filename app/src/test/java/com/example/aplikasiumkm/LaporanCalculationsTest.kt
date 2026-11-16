package com.example.aplikasiumkm

import com.example.aplikasiumkm.models.ItemTransaksi
import com.example.aplikasiumkm.models.Transaksi
import com.example.aplikasiumkm.models.TransaksiWithItems
import com.example.aplikasiumkm.viewmodel.BarangTerlarisItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaporanCalculationsTest {

    @Test
    fun testHitungLaporanHariIni() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val transaksi1 = TransaksiWithItems(
            transaksi = Transaksi(id = 1, tanggal = "$todayStr 10:00:00", totalHarga = 30000, nominalBayar = 50000, kembalian = 20000),
            items = listOf(
                ItemTransaksi(id = 1, transaksiId = 1, barangId = 101, namaBarang = "Beras 1kg", hargaBarang = 15000, jumlah = 2, subtotal = 30000)
            )
        )

        val transaksi2 = TransaksiWithItems(
            transaksi = Transaksi(id = 2, tanggal = "$todayStr 14:30:00", totalHarga = 25000, nominalBayar = 30000, kembalian = 5000),
            items = listOf(
                ItemTransaksi(id = 2, transaksiId = 2, barangId = 101, namaBarang = "Beras 1kg", hargaBarang = 15000, jumlah = 1, subtotal = 15000),
                ItemTransaksi(id = 3, transaksiId = 2, barangId = 102, namaBarang = "Minyak Goreng 1L", hargaBarang = 10000, jumlah = 1, subtotal = 10000)
            )
        )

        val transaksiLama = TransaksiWithItems(
            transaksi = Transaksi(id = 3, tanggal = "2020-01-01 09:00:00", totalHarga = 100000, nominalBayar = 100000, kembalian = 0),
            items = listOf(
                ItemTransaksi(id = 4, transaksiId = 3, barangId = 103, namaBarang = "Rokok Eceran", hargaBarang = 2500, jumlah = 40, subtotal = 100000)
            )
        )

        val listAll = listOf(transaksi1, transaksi2, transaksiLama)

        // Filter hari ini
        val transaksiHariIni = listAll.filter { it.transaksi.tanggal.startsWith(todayStr) }
        assertEquals(2, transaksiHariIni.size)

        val totalPendapatan = transaksiHariIni.sumOf { it.transaksi.totalHarga }
        assertEquals(55000L, totalPendapatan)

        val allItemsHariIni = transaksiHariIni.flatMap { it.items }
        val totalBarangTerjual = allItemsHariIni.sumOf { it.jumlah }
        assertEquals(4, totalBarangTerjual)

        // Barang terlaris calculation
        val mapTerlaris = mutableMapOf<String, Pair<Int, Long>>()
        allItemsHariIni.forEach { item ->
            val curr = mapTerlaris.getOrDefault(item.namaBarang, Pair(0, 0L))
            mapTerlaris[item.namaBarang] = Pair(curr.first + item.jumlah, curr.second + item.subtotal)
        }

        val listTerlaris = mapTerlaris.map { (nama, pair) ->
            BarangTerlarisItem(nama, pair.first, pair.second)
        }.sortedByDescending { it.totalJumlah }

        assertEquals(2, listTerlaris.size)
        assertEquals("Beras 1kg", listTerlaris[0].namaBarang)
        assertEquals(3, listTerlaris[0].totalJumlah)
        assertEquals(45000L, listTerlaris[0].totalOmset)
    }
}
