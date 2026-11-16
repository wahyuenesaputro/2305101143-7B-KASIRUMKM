package com.example.aplikasiumkm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aplikasiumkm.models.Barang
import com.example.aplikasiumkm.models.KeranjangItem
import com.example.aplikasiumkm.ui.components.BilahNavigasiSekunder
import com.example.aplikasiumkm.ui.navigation.Screen
import com.example.aplikasiumkm.ui.theme.APLIKASIUMKMTheme
import com.example.aplikasiumkm.ui.theme.HijauTuaPrimary
import com.example.aplikasiumkm.ui.theme.OranyeBataAccent
import com.example.aplikasiumkm.utils.BarcodeScannerDialog
import com.example.aplikasiumkm.utils.TemaKasir
import com.example.aplikasiumkm.utils.formatRupiah
import com.example.aplikasiumkm.viewmodel.KasirViewModel

private val colorPalette = listOf(
    Color(0xFF1F6B3A), // Hijau Tua
    Color(0xFFC2410C), // Oranye Bata
    Color(0xFF1D4ED8), // Biru
    Color(0xFF7C3AED), // Ungu
    Color(0xFF059669), // Emerald
    Color(0xFFD97706), // Amber
    Color(0xFF0E7490), // Cyan
    Color(0xFFBE185D)  // Pink
)

fun getInitialColor(nama: String): Color {
    val hash = kotlin.math.abs(nama.hashCode())
    return colorPalette[hash % colorPalette.size]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirScreen(
    viewModel: KasirViewModel,
    onNavigate: (Screen) -> Unit,
    onNavigateToPembayaran: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val barangList by viewModel.barangList.collectAsState()
    val keranjangItems by viewModel.keranjangItems.collectAsState()
    val totalHarga by viewModel.totalHarga.collectAsState()
    var showScanner by remember { mutableStateOf(false) }

    KasirContent(
        barangList = barangList,
        keranjangItems = keranjangItems,
        totalHarga = totalHarga,
        onTambahItem = { viewModel.tambahItem(it) },
        onKurangiItem = { viewModel.kurangiItem(it) },
        onHapusItem = { viewModel.hapusItem(it) },
        onResetKeranjang = { viewModel.resetKeranjang() },
        onScanClick = { showScanner = true },
        onNavigate = onNavigate,
        onNavigateToPembayaran = onNavigateToPembayaran,
        modifier = modifier
    )

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeScanned = { scannedBarcode ->
                showScanner = false
                viewModel.scanAndAddBarang(scannedBarcode) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { showScanner = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirContent(
    barangList: List<Barang>,
    keranjangItems: List<KeranjangItem>,
    totalHarga: Long,
    onTambahItem: (Barang) -> Unit,
    onKurangiItem: (Barang) -> Unit,
    onHapusItem: (Barang) -> Unit,
    onResetKeranjang: () -> Unit,
    onScanClick: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateToPembayaran: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kasir UMKM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onScanClick) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan Barcode"
                            )
                        }
                        if (keranjangItems.isNotEmpty()) {
                            TextButton(onClick = onResetKeranjang) {
                                Text("Reset", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = HijauTuaPrimary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
                BilahNavigasiSekunder(
                    currentScreen = Screen.Kasir,
                    onNavigate = onNavigate
                )
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                // Wide Screen / Tablet / Landscape Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left 60%: Grid Barang
                    Box(modifier = Modifier.weight(1.2f)) {
                        GridBarang(
                            barangList = barangList,
                            keranjangItems = keranjangItems,
                            onItemClick = onTambahItem,
                            columnsCount = 3
                        )
                    }

                    // Right 40%: Keranjang + Total + Bayar
                    Surface(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Keranjang Belanja (${keranjangItems.sumOf { it.jumlah }})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Box(modifier = Modifier.weight(1f)) {
                                DaftarKeranjang(
                                    keranjangItems = keranjangItems,
                                    onTambah = onTambahItem,
                                    onKurangi = onKurangiItem,
                                    onHapus = onHapusItem
                                )
                            }

                            TotalDanBayarSection(
                                totalHarga = totalHarga,
                                isEnabled = keranjangItems.isNotEmpty(),
                                onBayarClick = onNavigateToPembayaran
                            )
                        }
                    }
                }
            } else {
                // Compact Screen / Phone Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Grid Barang atas (60% tinggi)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.55f)
                    ) {
                        GridBarang(
                            barangList = barangList,
                            keranjangItems = keranjangItems,
                            onItemClick = onTambahItem,
                            columnsCount = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Keranjang & Bayar bawah (40% tinggi)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.45f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Keranjang Belanja (${keranjangItems.sumOf { it.jumlah }})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Box(modifier = Modifier.weight(1f)) {
                                DaftarKeranjang(
                                    keranjangItems = keranjangItems,
                                    onTambah = onTambahItem,
                                    onKurangi = onKurangiItem,
                                    onHapus = onHapusItem
                                )
                            }

                            TotalDanBayarSection(
                                totalHarga = totalHarga,
                                isEnabled = keranjangItems.isNotEmpty(),
                                onBayarClick = onNavigateToPembayaran
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridBarang(
    barangList: List<Barang>,
    keranjangItems: List<KeranjangItem>,
    onItemClick: (Barang) -> Unit,
    columnsCount: Int,
    modifier: Modifier = Modifier
) {
    if (barangList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada barang tersedia.\nTambahkan barang di menu Kelola Barang.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(barangList, key = { it.id }) { barang ->
                val quantityInCart = keranjangItems.firstOrNull { it.barang.id == barang.id }?.jumlah ?: 0

                ItemBarangCard(
                    barang = barang,
                    quantityInCart = quantityInCart,
                    onClick = { onItemClick(barang) }
                )
            }
        }
    }
}

@Composable
fun ItemBarangCard(
    barang: Barang,
    quantityInCart: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = if (quantityInCart > 0) {
        HijauTuaPrimary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (quantityInCart > 0) {
        HijauTuaPrimary
    } else {
        Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = TemaKasir.ShapeRadiusKecil,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (quantityInCart > 0) androidx.compose.foundation.BorderStroke(1.5.dp, borderColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
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
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (quantityInCart > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .background(OranyeBataAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quantityInCart.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = barang.nama,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatRupiah(barang.harga),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = HijauTuaPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DaftarKeranjang(
    keranjangItems: List<KeranjangItem>,
    onTambah: (Barang) -> Unit,
    onKurangi: (Barang) -> Unit,
    onHapus: (Barang) -> Unit,
    modifier: Modifier = Modifier
) {
    if (keranjangItems.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Keranjang kosong.\nPilih barang dari katalog atau scan barcode.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(keranjangItems, key = { it.barang.id }) { item ->
                ItemKeranjangRow(
                    item = item,
                    onTambah = { onTambah(item.barang) },
                    onKurangi = { onKurangi(item.barang) },
                    onHapus = { onHapus(item.barang) }
                )
            }
        }
    }
}

@Composable
fun ItemKeranjangRow(
    item: KeranjangItem,
    onTambah: () -> Unit,
    onKurangi: () -> Unit,
    onHapus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.barang.nama,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatRupiah(item.subtotal),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HijauTuaPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    onClick = onKurangi,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
                }

                Text(
                    text = item.jumlah.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedIconButton(
                    onClick = onTambah,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onHapus,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun TotalDanBayarSection(
    totalHarga: Long,
    isEnabled: Boolean,
    onBayarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatRupiah(totalHarga),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HijauTuaPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBayarClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TemaKasir.MinTouchTarget),
            shape = TemaKasir.ShapeRadiusKecil,
            colors = ButtonDefaults.buttonColors(
                containerColor = OranyeBataAccent,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            )
        ) {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lanjut Pembayaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
