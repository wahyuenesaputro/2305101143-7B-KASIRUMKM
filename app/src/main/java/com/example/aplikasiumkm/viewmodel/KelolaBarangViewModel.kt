package com.example.aplikasiumkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasiumkm.database.AppDatabase
import com.example.aplikasiumkm.models.Barang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KelolaBarangViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val barangDao = db.barangDao()

    val barangList: StateFlow<List<Barang>> = barangDao.getAllBarang()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _showFormDialog = MutableStateFlow(false)
    val showFormDialog: StateFlow<Boolean> = _showFormDialog.asStateFlow()

    private val _editingBarang = MutableStateFlow<Barang?>(null)
    val editingBarang: StateFlow<Barang?> = _editingBarang.asStateFlow()

    private val _namaInput = MutableStateFlow("")
    val namaInput: StateFlow<String> = _namaInput.asStateFlow()

    private val _hargaInput = MutableStateFlow("")
    val hargaInput: StateFlow<String> = _hargaInput.asStateFlow()

    private val _fotoPathInput = MutableStateFlow("")
    val fotoPathInput: StateFlow<String> = _fotoPathInput.asStateFlow()

    private val _barcodeInput = MutableStateFlow("")
    val barcodeInput: StateFlow<String> = _barcodeInput.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _barangToDelete = MutableStateFlow<Barang?>(null)
    val barangToDelete: StateFlow<Barang?> = _barangToDelete.asStateFlow()

    fun openAddDialog() {
        _editingBarang.value = null
        _namaInput.value = ""
        _hargaInput.value = ""
        _fotoPathInput.value = ""
        _barcodeInput.value = ""
        _errorMessage.value = null
        _showFormDialog.value = true
    }

    fun openEditDialog(barang: Barang) {
        _editingBarang.value = barang
        _namaInput.value = barang.nama
        _hargaInput.value = barang.harga.toString()
        _fotoPathInput.value = barang.fotoPath ?: ""
        _barcodeInput.value = barang.barcode ?: ""
        _errorMessage.value = null
        _showFormDialog.value = true
    }

    fun closeFormDialog() {
        _showFormDialog.value = false
        _editingBarang.value = null
        _errorMessage.value = null
    }

    fun onNamaChange(value: String) {
        _namaInput.value = value
        if (_errorMessage.value != null) _errorMessage.value = null
    }

    fun onHargaChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        _hargaInput.value = filtered
        if (_errorMessage.value != null) _errorMessage.value = null
    }

    fun onFotoPathChange(value: String) {
        _fotoPathInput.value = value
    }

    fun onBarcodeChange(value: String) {
        _barcodeInput.value = value
    }

    fun simpanBarang() {
        val nama = _namaInput.value.trim()
        val hargaString = _hargaInput.value.trim()
        val harga = hargaString.toLongOrNull() ?: 0L

        if (nama.isEmpty()) {
            _errorMessage.value = "Nama barang tidak boleh kosong."
            return
        }

        if (harga <= 0L) {
            _errorMessage.value = "Harga barang harus lebih dari 0."
            return
        }

        val fotoPath = _fotoPathInput.value.trim().ifEmpty { null }
        val barcode = _barcodeInput.value.trim().ifEmpty { null }

        viewModelScope.launch {
            val currentEditing = _editingBarang.value
            if (currentEditing == null) {
                val barangBaru = Barang(
                    nama = nama,
                    harga = harga,
                    fotoPath = fotoPath,
                    barcode = barcode
                )
                barangDao.insertBarang(barangBaru)
            } else {
                val barangUpdated = currentEditing.copy(
                    nama = nama,
                    harga = harga,
                    fotoPath = fotoPath,
                    barcode = barcode
                )
                barangDao.updateBarang(barangUpdated)
            }
            closeFormDialog()
        }
    }

    fun openDeleteDialog(barang: Barang) {
        _barangToDelete.value = barang
    }

    fun closeDeleteDialog() {
        _barangToDelete.value = null
    }

    fun confirmDeleteBarang() {
        val barang = _barangToDelete.value ?: return
        viewModelScope.launch {
            barangDao.deleteBarang(barang)
            closeDeleteDialog()
        }
    }
}
