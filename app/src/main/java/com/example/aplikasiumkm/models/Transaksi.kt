package com.example.aplikasiumkm.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tanggal: String,
    val totalHarga: Long,
    val nominalBayar: Long,
    val kembalian: Long,
    @Ignore
    val items: List<ItemTransaksi> = emptyList()
) {
    // Secondary constructor for Room persistence
    constructor(
        id: Long = 0,
        tanggal: String,
        totalHarga: Long,
        nominalBayar: Long,
        kembalian: Long
    ) : this(id, tanggal, totalHarga, nominalBayar, kembalian, emptyList())
}
