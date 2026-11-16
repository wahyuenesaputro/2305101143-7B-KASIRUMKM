package com.example.aplikasiumkm.models

import androidx.room.Embedded
import androidx.room.Relation

data class TransaksiWithItems(
    @Embedded val transaksi: Transaksi,
    @Relation(
        parentColumn = "id",
        entityColumn = "transaksiId"
    )
    val items: List<ItemTransaksi>
)
