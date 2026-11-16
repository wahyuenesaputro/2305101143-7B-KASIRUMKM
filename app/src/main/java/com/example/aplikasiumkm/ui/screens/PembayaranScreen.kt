package com.example.aplikasiumkm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasiumkm.ui.theme.APLIKASIUMKMTheme
import com.example.aplikasiumkm.ui.theme.HijauTuaPrimary
import com.example.aplikasiumkm.ui.theme.MerahPeringatanError
import com.example.aplikasiumkm.ui.theme.OranyeBataAccent
import com.example.aplikasiumkm.utils.BluetoothPrinterDialog
import com.example.aplikasiumkm.utils.TemaKasir
import com.example.aplikasiumkm.utils.formatRupiah
import com.example.aplikasiumkm.viewmodel.KasirViewModel

@Composable
fun PembayaranScreen(
    viewModel: KasirViewModel,
    onNavigateBack: () -> Unit,
    onTransaksiSelesai: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalHarga by viewModel.totalHarga.collectAsState()
    val nominalBayar by viewModel.nominalBayar.collectAsState()
    val nominalBayarText by viewModel.nominalBayarText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val lastSavedTransaksi by viewModel.lastSavedTransaksi.collectAsState()

    var showBluetoothDialog by remember { mutableStateOf(false) }

    PembayaranContent(
        totalHarga = totalHarga,
        nominalBayar = nominalBayar,
        nominalBayarText = nominalBayarText,
        isProcessing = isProcessing,
        onNominalTextChange = { viewModel.setNominalBayarText(it) },
        onQuickNominalClick = { viewModel.setNominalBayar(it) },
        onUangPasClick = { viewModel.setNominalBayar(totalHarga) },
        onSimpanTransaksi = {
            viewModel.simpanTransaksi()
        },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )

    // Success & Print Dialog after transaction is saved
    if (lastSavedTransaksi != null) {
        val transaksi = lastSavedTransaksi!!
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Pembayaran Berhasil!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = HijauTuaPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Transaksi #${transaksi.transaksi.id} berhasil disimpan.")
                    Text("Total: ${formatRupiah(transaksi.transaksi.totalHarga)}")
                    Text("Bayar: ${formatRupiah(transaksi.transaksi.nominalBayar)}")
                    Text("Kembalian: ${formatRupiah(transaksi.transaksi.kembalian)}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cetak struk ke printer thermal Bluetooth?")
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showBluetoothDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TemaKasir.MinTouchTarget),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OranyeBataAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak Struk", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.clearLastSavedTransaksi()
                            onTransaksiSelesai()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TemaKasir.MinTouchTarget),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HijauTuaPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Selesai (Transaksi Baru)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    if (showBluetoothDialog && lastSavedTransaksi != null) {
        BluetoothPrinterDialog(
            transaksiWithItems = lastSavedTransaksi!!,
            onDismiss = {
                showBluetoothDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PembayaranContent(
    totalHarga: Long,
    nominalBayar: Long,
    nominalBayarText: String,
    isProcessing: Boolean,
    onNominalTextChange: (String) -> Unit,
    onQuickNominalClick: (Long) -> Unit,
    onUangPasClick: () -> Unit,
    onSimpanTransaksi: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val kembalian = maxOf(0L, nominalBayar - totalHarga)
    val isUangKurang = nominalBayar > 0 && nominalBayar < totalHarga
    val selisihKurang = totalHarga - nominalBayar
    val isFormValid = nominalBayar >= totalHarga && totalHarga > 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pembayaran",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HijauTuaPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Total Tagihan (32sp+)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = TemaKasir.ShapeRadiusKecil,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL TAGIHAN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(totalHarga),
                        fontSize = TemaKasir.UkuranTeksTotal, // 32.sp
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 2. Input Nominal Uang Tunai
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = TemaKasir.ShapeRadiusKecil,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Nominal Uang Tunai",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = nominalBayarText,
                        onValueChange = onNominalTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Masukkan jumlah uang") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (nominalBayarText.isNotEmpty()) {
                                IconButton(onClick = { onNominalTextChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Hapus input"
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = isUangKurang,
                        supportingText = {
                            if (isUangKurang) {
                                Text(
                                    text = "Uang kurang ${formatRupiah(selisihKurang)}",
                                    color = MerahPeringatanError,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pecahan Uang Cepat:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onUangPasClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OranyeBataAccent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.heightIn(min = 44.dp)
                        ) {
                            Text(
                                text = "Uang Pas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        val nominalList = listOf(
                            2000L to "2.000",
                            5000L to "5.000",
                            10000L to "10.000",
                            20000L to "20.000",
                            50000L to "50.000",
                            100000L to "100.000"
                        )

                        nominalList.forEach { (amount, label) ->
                            OutlinedButton(
                                onClick = { onQuickNominalClick(amount) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.heightIn(min = 44.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Tampilan KEMBALIAN
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = TemaKasir.ShapeRadiusKecil,
                color = if (isUangKurang) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KEMBALIAN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(kembalian),
                        fontSize = TemaKasir.UkuranTeksKembalian, // 48.sp
                        fontWeight = FontWeight.Bold,
                        color = if (isUangKurang) MerahPeringatanError else HijauTuaPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Tombol Konfirmasi "Simpan & Transaksi Baru"
            Button(
                onClick = onSimpanTransaksi,
                enabled = isFormValid && !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = TemaKasir.MinTouchTarget),
                shape = TemaKasir.ShapeRadiusKecil,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HijauTuaPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Simpan & Transaksi Baru",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
