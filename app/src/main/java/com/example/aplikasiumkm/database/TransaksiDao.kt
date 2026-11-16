package com.example.aplikasiumkm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.aplikasiumkm.models.ItemTransaksi
import com.example.aplikasiumkm.models.Transaksi
import com.example.aplikasiumkm.models.TransaksiWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {
    @Transaction
    @Query("SELECT * FROM transaksi ORDER BY id DESC")
    fun getAllTransaksiWithItems(): Flow<List<TransaksiWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemTransaksiList(items: List<ItemTransaksi>)

    @Transaction
    suspend fun insertFullTransaksi(transaksi: Transaksi, items: List<ItemTransaksi>): Long {
        val transaksiId = insertTransaksi(transaksi)
        val itemsWithId = items.map { it.copy(transaksiId = transaksiId) }
        insertItemTransaksiList(itemsWithId)
        return transaksiId
    }

    @androidx.room.Delete
    suspend fun deleteTransaksi(transaksi: Transaksi)
}
