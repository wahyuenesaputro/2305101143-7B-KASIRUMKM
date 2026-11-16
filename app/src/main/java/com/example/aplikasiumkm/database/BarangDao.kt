package com.example.aplikasiumkm.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplikasiumkm.models.Barang
import kotlinx.coroutines.flow.Flow

@Dao
interface BarangDao {
    @Query("SELECT * FROM barang ORDER BY nama ASC")
    fun getAllBarang(): Flow<List<Barang>>

    @Query("SELECT * FROM barang ORDER BY nama ASC")
    suspend fun getBarangListSync(): List<Barang>

    @Query("SELECT * FROM barang WHERE id = :id LIMIT 1")
    suspend fun getBarangById(id: Long): Barang?

    @Query("SELECT * FROM barang WHERE barcode = :barcode LIMIT 1")
    suspend fun getBarangByBarcode(barcode: String): Barang?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarang(barang: Barang): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBarang(barangList: List<Barang>)

    @Update
    suspend fun updateBarang(barang: Barang)

    @Delete
    suspend fun deleteBarang(barang: Barang)
}
