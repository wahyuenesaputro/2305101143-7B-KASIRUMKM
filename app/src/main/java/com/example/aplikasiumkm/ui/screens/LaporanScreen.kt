package com.example.aplikasiumkm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasiumkm.models.TransaksiWithItems
import com.example.aplikasiumkm.ui.components.BilahNavigasiSekunder
import com.example.aplikasiumkm.ui.navigation.Screen
import com.example.aplikasiumkm.utils.BluetoothPrinterDialog
import com.example.aplikasiumkm.utils.TemaKasir
import com.example.aplikasiumkm.utils.formatRupiah
import com.example.aplikasiumkm.viewmodel.BarangTerlarisItem
import com.example.aplikasiumkm.viewmodel.LaporanHariIniState
import com.example.aplikasiumkm.viewmodel.LaporanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    viewModel: LaporanViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val transaksiList by viewModel.transaksiWithItemsList.collectAsState()
    val laporanHariIni by viewModel.laporanHariIniState.collectAsState()
    val selectedTransaksi by viewModel.selectedTransaksiDetail.collectAsState()
    val transaksiToDelete by viewModel.transaksiToDelete.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Laporan & Riwayat",
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
                    currentScreen = Screen.Laporan,
                    onNavigate = onNavigate
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TemaKasir.LatarBelakangKrem)
        ) {
            // Tab Switcher between Laporan Hari Ini and Riwayat Transaksi
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = TemaKasir.WarnaUtamaHijauTua,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = TemaKasir.WarnaUtamaHijauTua
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Laporan Hari Ini",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Riwayat Transaksi",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> LaporanHariIniSection(laporanHariIni = laporanHariIni)
                1 -> RiwayatTransaksiSection(
                    transaksiList = transaksiList,
                    onSelectDetail = viewModel::openDetail,
                    onDelete = viewModel::openDeleteDialog
                )
            }
        }

        // Rincian Transaksi Dialog
        selectedTransaksi?.let { transaksiWithItems ->
            DialogRincianTransaksi(
                transaksiWithItems = transaksiWithItems,
                onDismiss = viewModel::closeDetail
            )
        }

        // Delete Confirmation Dialog
        transaksiToDelete?.let { transaksiWithItems ->
            DialogKonfirmasiHapusTransaksi(
                transaksiId = transaksiWithItems.transaksi.id,
                onDismiss = viewModel::closeDeleteDialog,
                onConfirm = viewModel::confirmDeleteTransaksi
            )
        }
    }
}

@Composable
fun LaporanHariIniSection(
    laporanHariIni: LaporanHariIniState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card Total Pendapatan Hari Ini (32sp+ bold requirement)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TemaKasir.WarnaUtamaHijauTua),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "TOTAL PENDAPATAN HARI INI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatRupiah(laporanHariIni.totalPendapatan),
                        fontSize = 32.sp, // Requirement: 32sp+ tebal
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Row Stat Cards: Jumlah Transaksi & Barang Terjual
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Total Transaksi
                Card(
                    modifier = Modifier.weight(1f),
                    shape = TemaKasir.ShapeRadiusKecil,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    TemaKasir.WarnaAksesOranyeBata.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TemaKasir.WarnaAksesOranyeBata
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Total Transaksi",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${laporanHariIni.totalTransaksi} Transaksi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TemaKasir.TeksUtamaHampirHitam
                            )
                        }
                    }
                }

                // Card Total Barang Terjual
                Card(
                    modifier = Modifier.weight(1f),
                    shape = TemaKasir.ShapeRadiusKecil,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    TemaKasir.WarnaUtamaHijauTua.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = TemaKasir.WarnaUtamaHijauTua
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Barang Terjual",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${laporanHariIni.totalBarangTerjual} Unit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TemaKasir.TeksUtamaHampirHitam
                            )
                        }
                    }
                }
            }
        }

        // Section Daftar Barang Terlaris
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = TemaKasir.WarnaAksesOranyeBata
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daftar Barang Terlaris Hari Ini",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TemaKasir.TeksUtamaHampirHitam
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    if (laporanHariIni.daftarBarangTerlaris.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada transaksi hari ini.",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        laporanHariIni.daftarBarangTerlaris.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        color = if (index == 0) TemaKasir.WarnaAksesOranyeBata else Color(0xFF9CA3AF),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item.namaBarang,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TemaKasir.TeksUtamaHampirHitam,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${item.totalJumlah} unit terjual",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TemaKasir.WarnaUtamaHijauTua
                                    )
                                    Text(
                                        text = formatRupiah(item.totalOmset),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            if (index < laporanHariIni.daftarBarangTerlaris.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = Color(0xFFF3F4F6)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatTransaksiSection(
    transaksiList: List<TransaksiWithItems>,
    onSelectDetail: (TransaksiWithItems) -> Unit,
    onDelete: (TransaksiWithItems) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transaksiList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada riwayat transaksi.",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transaksiList, key = { it.transaksi.id }) { item ->
                ItemTransaksiRow(
                    transaksiWithItems = item,
                    onClick = { onSelectDetail(item) },
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}

@Composable
fun ItemTransaksiRow(
    transaksiWithItems: TransaksiWithItems,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transaksi = transaksiWithItems.transaksi
    val items = transaksiWithItems.items

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        TemaKasir.WarnaUtamaHijauTua.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = TemaKasir.WarnaUtamaHijauTua
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRX #${transaksi.id}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TemaKasir.TeksUtamaHampirHitam
                    )
                    Text(
                        text = formatRupiah(transaksi.totalHarga),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TemaKasir.WarnaUtamaHijauTua
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = transaksi.tanggal,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                val itemSummary = items.joinToString(", ") { "${it.namaBarang} (x${it.jumlah})" }
                Text(
                    text = itemSummary,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Transaksi",
                    tint = TemaKasir.MerahPeringatan
                )
            }
        }
    }
}

@Composable
fun DialogRincianTransaksi(
    transaksiWithItems: TransaksiWithItems,
    onDismiss: () -> Unit
) {
    val transaksi = transaksiWithItems.transaksi
    val items = transaksiWithItems.items
    var showBluetoothDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Rincian Transaksi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "TRX #${transaksi.id} - ${transaksi.tanggal}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // List Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.namaBarang,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${formatRupiah(item.hargaBarang)} x ${item.jumlah}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = formatRupiah(item.subtotal),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL TAGIHAN:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        formatRupiah(transaksi.totalHarga),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TemaKasir.WarnaUtamaHijauTua
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nominal Bayar:", fontSize = 13.sp, color = Color.DarkGray)
                    Text(formatRupiah(transaksi.nominalBayar), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kembalian:", fontSize = 13.sp, color = Color.DarkGray)
                    Text(formatRupiah(transaksi.kembalian), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
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
                        containerColor = TemaKasir.WarnaAksesOranyeBata,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cetak Struk", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = TemaKasir.MinTouchTarget),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TemaKasir.WarnaUtamaHijauTua,
                        contentColor = Color.White
                    )
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    )

    if (showBluetoothDialog) {
        BluetoothPrinterDialog(
            transaksiWithItems = transaksiWithItems,
            onDismiss = { showBluetoothDialog = false }
        )
    }
}

@Composable
fun DialogKonfirmasiHapusTransaksi(
    transaksiId: Long,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Konfirmasi Hapus Transaksi",
                fontWeight = FontWeight.Bold,
                color = TemaKasir.MerahPeringatan,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Apakah Anda yakin ingin menghapus transaksi TRX #$transaksiId?",
                    fontSize = 14.sp,
                    color = TemaKasir.TeksUtamaHampirHitam
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Peringatan: Data transaksi yang dihapus tidak dapat dikembalikan.",
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
