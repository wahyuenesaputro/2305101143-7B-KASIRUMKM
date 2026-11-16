package com.example.aplikasiumkm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aplikasiumkm.models.Barang
import com.example.aplikasiumkm.models.ItemTransaksi
import com.example.aplikasiumkm.models.Transaksi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Barang::class, Transaksi::class, ItemTransaksi::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun barangDao(): BarangDao
    abstract fun transaksiDao(): TransaksiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kasir_umkm.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val initialSampleBarang = listOf(
            Barang(nama = "Beras 1kg", harga = 14000),
            Barang(nama = "Minyak Goreng 1L", harga = 16500),
            Barang(nama = "Mie Instan Goreng", harga = 3000),
            Barang(nama = "Gula Pasir 1kg", harga = 17500),
            Barang(nama = "Kopi Sachet", harga = 1500),
            Barang(nama = "Rokok Eceran", harga = 2500),
            Barang(nama = "Air Mineral 600ml", harga = 3500),
            Barang(nama = "Telur Ayam 1kg", harga = 28000),
            Barang(nama = "Teh Celup", harga = 6000),
            Barang(nama = "Sabun Cuci", harga = 4000)
        )

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.barangDao().insertAllBarang(initialSampleBarang)
                    }
                }
            }
        }
    }
}
