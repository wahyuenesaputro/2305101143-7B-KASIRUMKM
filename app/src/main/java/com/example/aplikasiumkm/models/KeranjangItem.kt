package com.example.aplikasiumkm.models

data class KeranjangItem(
    val barang: Barang,
    val jumlah: Int = 1
) {
    val subtotal: Long
        get() = barang.harga * jumlah
}
