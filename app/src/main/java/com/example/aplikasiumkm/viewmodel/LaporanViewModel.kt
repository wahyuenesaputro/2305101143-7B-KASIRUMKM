package com.example.aplikasiumkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasiumkm.database.AppDatabase
import com.example.aplikasiumkm.models.TransaksiWithItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BarangTerlarisItem(
    val namaBarang: String,
    val totalJumlah: Int,
    val totalOmset: Long
)

data class LaporanHariIniState(
    val totalPendapatan: Long = 0L,
    val totalTransaksi: Int = 0,
    val totalBarangTerjual: Int = 0,
    val daftarBarangTerlaris: List<BarangTerlarisItem> = emptyList()
)

class LaporanViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val transaksiDao = db.transaksiDao()

    val transaksiWithItemsList: StateFlow<List<TransaksiWithItems>> = transaksiDao.getAllTransaksiWithItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val laporanHariIniState: StateFlow<LaporanHariIniState> = transaksiWithItemsList.map { list ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val transaksiHariIni = list.filter { it.transaksi.tanggal.startsWith(todayStr) }

        val totalPendapatan = transaksiHariIni.sumOf { it.transaksi.totalHarga }
        val totalTransaksi = transaksiHariIni.size

        val allItemsHariIni = transaksiHariIni.flatMap { it.items }
        val totalBarangTerjual = allItemsHariIni.sumOf { it.jumlah }

        val barangTerlarisMap = mutableMapOf<String, Pair<Int, Long>>()
        allItemsHariIni.forEach { item ->
            val current = barangTerlarisMap.getOrDefault(item.namaBarang, Pair(0, 0L))
            barangTerlarisMap[item.namaBarang] = Pair(
                current.first + item.jumlah,
                current.second + item.subtotal
            )
        }

        val barangTerlarisList = barangTerlarisMap.map { (nama, pair) ->
            BarangTerlarisItem(
                namaBarang = nama,
                totalJumlah = pair.first,
                totalOmset = pair.second
            )
        }.sortedByDescending { it.totalJumlah }

        LaporanHariIniState(
            totalPendapatan = totalPendapatan,
            totalTransaksi = totalTransaksi,
            totalBarangTerjual = totalBarangTerjual,
            daftarBarangTerlaris = barangTerlarisList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LaporanHariIniState()
    )

    private val _selectedTransaksiDetail = MutableStateFlow<TransaksiWithItems?>(null)
    val selectedTransaksiDetail: StateFlow<TransaksiWithItems?> = _selectedTransaksiDetail.asStateFlow()

    private val _transaksiToDelete = MutableStateFlow<TransaksiWithItems?>(null)
    val transaksiToDelete: StateFlow<TransaksiWithItems?> = _transaksiToDelete.asStateFlow()

    fun openDetail(transaksi: TransaksiWithItems) {
        _selectedTransaksiDetail.value = transaksi
    }

    fun closeDetail() {
        _selectedTransaksiDetail.value = null
    }

    fun openDeleteDialog(transaksi: TransaksiWithItems) {
        _transaksiToDelete.value = transaksi
    }

    fun closeDeleteDialog() {
        _transaksiToDelete.value = null
    }

    fun confirmDeleteTransaksi() {
        val target = _transaksiToDelete.value ?: return
        viewModelScope.launch {
            transaksiDao.deleteTransaksi(target.transaksi)
            closeDeleteDialog()
            if (_selectedTransaksiDetail.value?.transaksi?.id == target.transaksi.id) {
                closeDetail()
            }
        }
    }
}
