package com.example.aplikasiumkm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplikasiumkm.ui.screens.KasirScreen
import com.example.aplikasiumkm.ui.screens.KelolaBarangScreen
import com.example.aplikasiumkm.ui.screens.LaporanScreen
import com.example.aplikasiumkm.ui.screens.PembayaranScreen
import com.example.aplikasiumkm.viewmodel.KasirViewModel
import com.example.aplikasiumkm.viewmodel.KelolaBarangViewModel
import com.example.aplikasiumkm.viewmodel.LaporanViewModel

sealed interface Screen {
    data object Kasir : Screen
    data object KelolaBarang : Screen
    data object Laporan : Screen
    data object Pembayaran : Screen
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    kasirViewModel: KasirViewModel = viewModel(),
    kelolaBarangViewModel: KelolaBarangViewModel = viewModel(),
    laporanViewModel: LaporanViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Kasir) }

    when (currentScreen) {
        Screen.Kasir -> {
            KasirScreen(
                viewModel = kasirViewModel,
                onNavigate = { newScreen -> currentScreen = newScreen },
                onNavigateToPembayaran = {
                    currentScreen = Screen.Pembayaran
                },
                modifier = modifier
            )
        }

        Screen.KelolaBarang -> {
            KelolaBarangScreen(
                viewModel = kelolaBarangViewModel,
                onNavigate = { newScreen -> currentScreen = newScreen },
                modifier = modifier
            )
        }

        Screen.Laporan -> {
            LaporanScreen(
                viewModel = laporanViewModel,
                onNavigate = { newScreen -> currentScreen = newScreen },
                modifier = modifier
            )
        }

        Screen.Pembayaran -> {
            PembayaranScreen(
                viewModel = kasirViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Kasir
                },
                onTransaksiSelesai = {
                    currentScreen = Screen.Kasir
                },
                modifier = modifier
            )
        }
    }
}
