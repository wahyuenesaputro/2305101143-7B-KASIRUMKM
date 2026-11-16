package com.example.aplikasiumkm.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_transaksi",
    foreignKeys = [
        ForeignKey(
            entity = Transaksi::class,
            parentColumns = ["id"],
            childColumns = ["transaksiId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["transaksiId"])]
)
data class ItemTransaksi(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transaksiId: Long = 0,
    val barangId: Long,
    val namaBarang: String,
    val hargaBarang: Long,
    val jumlah: Int,
    val subtotal: Long = hargaBarang * jumlah
)
