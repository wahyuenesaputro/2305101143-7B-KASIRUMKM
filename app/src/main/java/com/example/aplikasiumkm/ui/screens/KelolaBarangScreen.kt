package com.example.aplikasiumkm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aplikasiumkm.models.Barang
import com.example.aplikasiumkm.ui.components.BilahNavigasiSekunder
import com.example.aplikasiumkm.ui.navigation.Screen
import com.example.aplikasiumkm.utils.BarcodeScannerDialog
import com.example.aplikasiumkm.utils.TemaKasir
import com.example.aplikasiumkm.utils.formatRupiah
import com.example.aplikasiumkm.viewmodel.KelolaBarangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaBarangScreen(
    viewModel: KelolaBarangViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val barangList by viewModel.barangList.collectAsState()
    val showFormDialog by viewModel.showFormDialog.collectAsState()
    val editingBarang by viewModel.editingBarang.collectAsState()
    val namaInput by viewModel.namaInput.collectAsState()
    val hargaInput by viewModel.hargaInput.collectAsState()
    val fotoPathInput by viewModel.fotoPathInput.collectAsState()
    val barcodeInput by viewModel.barcodeInput.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val barangToDelete by viewModel.barangToDelete.collectAsState()

    var showScanner by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Kelola Barang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TemaKasir.WarnaUtamaHijauTua,
                        titleContentColor = Color.White
                    )
                )
                BilahNavigasiSekunder(
                    currentScreen = Screen.KelolaBarang,
                    onNavigate = onNavigate
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = TemaKasir.WarnaAksesOranyeBata,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Tambah Barang") },
                text = { Text("Tambah Barang", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TemaKasir.LatarBelakangKrem)
        ) {
            if (barangList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada barang. Klik 'Tambah Barang' untuk menambahkan.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(barangList, key = { it.id }) { barang ->
                        ItemBarangRow(
                            barang = barang,
                            onEdit = { viewModel.openEditDialog(barang) },
                            onDelete = { viewModel.openDeleteDialog(barang) }
                        )
                    }
                }
            }
        }

        // Form Dialog (Tambah / Edit)
        if (showFormDialog) {
            FormBarangDialog(
                isEditMode = editingBarang != null,
                namaInput = namaInput,
                hargaInput = hargaInput,
                fotoPathInput = fotoPathInput,
                barcodeInput = barcodeInput,
                errorMessage = errorMessage,
                onNamaChange = viewModel::onNamaChange,
                onHargaChange = viewModel::onHargaChange,
                onFotoPathChange = viewModel::onFotoPathChange,
                onBarcodeChange = viewModel::onBarcodeChange,
                onScanClick = { showScanner = true },
                onDismiss = viewModel::closeFormDialog,
                onSave = viewModel::simpanBarang
            )
        }

        if (showScanner) {
            BarcodeScannerDialog(
                onBarcodeScanned = { scanned ->
                    showScanner = false
                    viewModel.onBarcodeChange(scanned)
                },
                onDismiss = { showScanner = false }
            )
        }

        // Delete Confirmation Dialog
        barangToDelete?.let { barang ->
            DialogKonfirmasiHapus(
                namaBarang = barang.nama,
                onDismiss = viewModel::closeDeleteDialog,
                onConfirm = viewModel::confirmDeleteBarang
            )
        }
    }
}

@Composable
fun ItemBarangRow(
    barang: Barang,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = TemaKasir.ShapeRadiusKecil,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar / Initial Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getInitialColor(barang.nama)),
                contentAlignment = Alignment.Center
            ) {
                if (!barang.fotoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = barang.fotoPath,
                        contentDescription = barang.nama,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = barang.nama.trim().take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Detail Barang
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = barang.nama,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TemaKasir.TeksUtamaHampirHitam,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatRupiah(barang.harga),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TemaKasir.WarnaUtamaHijauTua
                )
                if (!barang.barcode.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = barang.barcode,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Tombol Edit & Hapus dengan area sentuh min 56dp
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Barang",
                        tint = TemaKasir.WarnaUtamaHijauTua
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Barang",
                        tint = TemaKasir.MerahPeringatan
                    )
                }
            }
        }
    }
}

@Composable
fun FormBarangDialog(
    isEditMode: Boolean,
    namaInput: String,
    hargaInput: String,
    fotoPathInput: String,
    barcodeInput: String,
    errorMessage: String?,
    onNamaChange: (String) -> Unit,
    onHargaChange: (String) -> Unit,
    onFotoPathChange: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) "Ubah Barang" else "Tambah Barang Baru",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        color = TemaKasir.MerahPeringatan.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = TemaKasir.MerahPeringatan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = namaInput,
                    onValueChange = onNamaChange,
                    label = { Text("Nama Barang *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hargaInput,
                    onValueChange = onHargaChange,
                    label = { Text("Harga (Rp) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fotoPathInput,
                    onValueChange = onFotoPathChange,
                    label = { Text("Foto (Opsional - URL/Path)") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = onBarcodeChange,
                    label = { Text("Barcode (Opsional)") },
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = onScanClick) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan Barcode"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .heightIn(min = TemaKasir.MinTouchTarget),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TemaKasir.WarnaUtamaHijauTua,
                    contentColor = Color.White
                )
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .heightIn(min = TemaKasir.MinTouchTarget)
            ) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun DialogKonfirmasiHapus(
    namaBarang: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Konfirmasi Hapus",
                fontWeight = FontWeight.Bold,
                color = TemaKasir.MerahPeringatan,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Apakah Anda yakin ingin menghapus barang berikut?",
                    fontSize = 14.sp,
                    color = TemaKasir.TeksUtamaHampirHitam
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$namaBarang\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TemaKasir.MerahPeringatan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Peringatan: Data barang yang dihapus tidak dapat dikembalikan.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.heightIn(min = TemaKasir.MinTouchTarget),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TemaKasir.MerahPeringatan,
                    contentColor = Color.White
                )
            ) {
                Text("Hapus", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = TemaKasir.MinTouchTarget)
            ) {
                Text("Batal", color = Color.DarkGray)
            }
        }
    )
}
