package com.example.aplikasiumkm.utils

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasiumkm.models.TransaksiWithItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPrinterDialog(
    transaksiWithItems: TransaksiWithItems,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberPermissionState(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        null
    }

    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var isPrinting by remember { mutableStateOf(false) }
    var selectedDeviceName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (bluetoothConnectPermission != null && !bluetoothConnectPermission.status.isGranted) {
            bluetoothConnectPermission.launchPermissionRequest()
        } else {
            pairedDevices = BluetoothPrinterHelper.getPairedPrinters(context)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Pilih Printer Bluetooth", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (bluetoothConnectPermission != null && !bluetoothConnectPermission.status.isGranted) {
                    Text("Izin Bluetooth Connect diperlukan untuk mencetak struk.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { bluetoothConnectPermission.launchPermissionRequest() }) {
                        Text("Berikan Izin")
                    }
                } else if (pairedDevices.isEmpty()) {
                    Text("Tidak ada perangkat Bluetooth yang terpasang (paired). Silakan pasang printer thermal bluetooth terlebih dahulu di pengaturan Bluetooth perangkat.")
                } else {
                    Text("Pilih printer thermal untuk mencetak struk transaksi #${transaksiWithItems.transaksi.id}:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(pairedDevices) { device ->
                            val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                androidx.core.app.ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                "Perangkat (Izin diperlukan)"
                            } else {
                                device.name ?: "Unknown Device"
                            }
                            val deviceAddress = device.address

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isPrinting) {
                                        selectedDeviceName = deviceName
                                        isPrinting = true
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val result = BluetoothPrinterHelper.printReceipt(context, device, transaksiWithItems)
                                            withContext(Dispatchers.Main) {
                                                isPrinting = false
                                                if (result.isSuccess) {
                                                    Toast.makeText(context, "Struk berhasil dicetak!", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, "Gagal mencetak: ${result.exceptionOrNull()?.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    },
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = deviceName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = deviceAddress, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    if (isPrinting && selectedDeviceName == deviceName) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
