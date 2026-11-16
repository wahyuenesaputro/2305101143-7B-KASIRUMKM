package com.example.aplikasiumkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasiumkm.database.AppDatabase
import com.example.aplikasiumkm.models.Barang
import com.example.aplikasiumkm.models.ItemTransaksi
import com.example.aplikasiumkm.models.KeranjangItem
import com.example.aplikasiumkm.models.Transaksi
import com.example.aplikasiumkm.models.TransaksiWithItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KasirViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val barangDao = db.barangDao()
    private val transaksiDao = db.transaksiDao()

    val barangList: StateFlow<List<Barang>> = barangDao.getAllBarang()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _keranjangItems = MutableStateFlow<List<KeranjangItem>>(emptyList())
    val keranjangItems: StateFlow<List<KeranjangItem>> = _keranjangItems.asStateFlow()

    val totalHarga: StateFlow<Long> = _keranjangItems.map { items ->
        items.sumOf { it.subtotal }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    private val _nominalBayar = MutableStateFlow<Long>(0L)
    val nominalBayar: StateFlow<Long> = _nominalBayar.asStateFlow()

    private val _nominalBayarText = MutableStateFlow<String>("")
    val nominalBayarText: StateFlow<String> = _nominalBayarText.asStateFlow()

    private val _isProcessing = MutableStateFlow<Boolean>(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastSavedTransaksi = MutableStateFlow<TransaksiWithItems?>(null)
    val lastSavedTransaksi: StateFlow<TransaksiWithItems?> = _lastSavedTransaksi.asStateFlow()

    fun clearLastSavedTransaksi() {
        _lastSavedTransaksi.value = null
    }

    fun tambahItem(barang: Barang) {
        _keranjangItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.barang.id == barang.id }
            if (existingIndex >= 0) {
                currentList.toMutableList().apply {
                    val item = this[existingIndex]
                    this[existingIndex] = item.copy(jumlah = item.jumlah + 1)
                }
            } else {
                currentList + KeranjangItem(barang = barang, jumlah = 1)
            }
        }
    }

    fun kurangiItem(barang: Barang) {
        _keranjangItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.barang.id == barang.id }
            if (existingIndex >= 0) {
                val item = currentList[existingIndex]
                if (item.jumlah > 1) {
                    currentList.toMutableList().apply {
                        this[existingIndex] = item.copy(jumlah = item.jumlah - 1)
                    }
                } else {
                    currentList.filterNot { it.barang.id == barang.id }
                }
            } else {
                currentList
            }
        }
    }

    fun hapusItem(barang: Barang) {
        _keranjangItems.update { currentList ->
            currentList.filterNot { it.barang.id == barang.id }
        }
    }

    fun resetKeranjang() {
        _keranjangItems.value = emptyList()
        _nominalBayar.value = 0L
        _nominalBayarText.value = ""
    }

    fun setNominalBayar(nominal: Long) {
        _nominalBayar.value = nominal
        _nominalBayarText.value = if (nominal > 0) nominal.toString() else ""
    }

    fun setNominalBayarText(text: String) {
        val filtered = text.filter { it.isDigit() }
        _nominalBayarText.value = filtered
        _nominalBayar.value = filtered.toLongOrNull() ?: 0L
    }

    fun simpanTransaksi() {
        val items = _keranjangItems.value
        val total = items.sumOf { it.subtotal }
        val nominal = _nominalBayar.value
        if (items.isEmpty() || nominal < total || _isProcessing.value) return

        _isProcessing.value = true
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val tanggalSekarang = dateFormat.format(Date())
                val kembalian = nominal - total

                val transaksi = Transaksi(
                    tanggal = tanggalSekarang,
                    totalHarga = total,
                    nominalBayar = nominal,
                    kembalian = kembalian
                )

                val itemTransaksiList = items.map { keranjangItem ->
                    ItemTransaksi(
                        barangId = keranjangItem.barang.id,
                        namaBarang = keranjangItem.barang.nama,
                        hargaBarang = keranjangItem.barang.harga,
                        jumlah = keranjangItem.jumlah,
                        subtotal = keranjangItem.subtotal
                    )
                }

                val transaksiId = transaksiDao.insertFullTransaksi(transaksi, itemTransaksiList)
                val savedTransaksi = transaksi.copy(id = transaksiId)
                val savedItems = itemTransaksiList.map { it.copy(transaksiId = transaksiId) }

                resetKeranjang()
                _lastSavedTransaksi.value = TransaksiWithItems(transaksi = savedTransaksi, items = savedItems)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun scanAndAddBarang(barcode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val barang = barangDao.getBarangByBarcode(barcode)
            if (barang != null) {
                tambahItem(barang)
                onResult(true, "Berhasil menambahkan ${barang.nama}")
            } else {
                onResult(false, "Barang dengan barcode '$barcode' tidak ditemukan")
            }
        }
    }
}
