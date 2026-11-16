package com.example.aplikasiumkm.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.aplikasiumkm.models.TransaksiWithItems
import java.io.IOException
import java.util.UUID

object BluetoothPrinterHelper {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun getPairedPrinters(context: Context): List<BluetoothDevice> {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val bluetoothAdapter = bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter() ?: return emptyList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return emptyList()
            }
        }

        return bluetoothAdapter.bondedDevices?.toList() ?: emptyList()
    }

    fun printReceipt(context: Context, device: BluetoothDevice, transaksiWithItems: TransaksiWithItems): Result<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(SecurityException("Izin BLUETOOTH_CONNECT belum diberikan."))
            }
        }

        return try {
            val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()
            val outputStream = socket.outputStream

            val sb = StringBuilder()
            // ESC/POS Init
            val init = byteArrayOf(0x1B, 0x40)
            val alignCenter = byteArrayOf(0x1B, 0x61, 0x01)
            val alignLeft = byteArrayOf(0x1B, 0x61, 0x00)
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            outputStream.write(init)

            // Header
            outputStream.write(alignCenter)
            outputStream.write(boldOn)
            outputStream.write("WARUNG / KASIR UMKM\n".toByteArray(Charsets.UTF_8))
            outputStream.write(boldOff)
            outputStream.write("--------------------------------\n".toByteArray(Charsets.UTF_8))

            // Transaction Info
            outputStream.write(alignLeft)
            sb.append("No. Transaksi : #${transaksiWithItems.transaksi.id}\n")
            sb.append("Tanggal       : ${transaksiWithItems.transaksi.tanggal}\n")
            outputStream.write(sb.toString().toByteArray(Charsets.UTF_8))
            outputStream.write("--------------------------------\n".toByteArray(Charsets.UTF_8))

            // Items
            for (item in transaksiWithItems.items) {
                val itemSb = StringBuilder()
                itemSb.append("${item.namaBarang}\n")
                val subtotalStr = formatRupiah(item.subtotal)
                val detailStr = "  ${item.jumlah} x ${formatRupiah(item.hargaBarang)} = $subtotalStr\n"
                itemSb.append(detailStr)
                outputStream.write(itemSb.toString().toByteArray(Charsets.UTF_8))
            }
            outputStream.write("--------------------------------\n".toByteArray(Charsets.UTF_8))

            // Totals
            val totalSb = StringBuilder()
            totalSb.append(String.format("%-16s %16s\n", "Total:", formatRupiah(transaksiWithItems.transaksi.totalHarga)))
            totalSb.append(String.format("%-16s %16s\n", "Bayar:", formatRupiah(transaksiWithItems.transaksi.nominalBayar)))
            totalSb.append(String.format("%-16s %16s\n", "Kembalian:", formatRupiah(transaksiWithItems.transaksi.kembalian)))
            outputStream.write(totalSb.toString().toByteArray(Charsets.UTF_8))
            outputStream.write("--------------------------------\n".toByteArray(Charsets.UTF_8))

            // Footer
            outputStream.write(alignCenter)
            outputStream.write("Terima kasih atas kunjungan Anda!\n".toByteArray(Charsets.UTF_8))
            outputStream.write("Barang yang sudah dibeli\ntidak dapat ditukar/dikembalikan\n\n\n".toByteArray(Charsets.UTF_8))

            // Feed & Cut
            val cut = byteArrayOf(0x0A, 0x0A, 0x1D, 0x56, 0x41, 0x03)
            outputStream.write(cut)

            outputStream.flush()
            outputStream.close()
            socket.close()

            Result.success(Unit)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
