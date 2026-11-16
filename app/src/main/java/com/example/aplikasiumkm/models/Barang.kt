package com.example.aplikasiumkm.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barang")
data class Barang(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val harga: Long,
    val fotoPath: String? = null,
    val barcode: String? = null
)
