import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../models/barang.dart';
import '../models/transaksi.dart';
import '../models/item_transaksi.dart';

/// Database Helper untuk inisialisasi SQLite database lokal
class DatabaseHelper {
  static final DatabaseHelper instance = DatabaseHelper._init();
  static Database? _database;

  DatabaseHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('kasir_umkm.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 1,
      onCreate: _createDB,
    );
  }

  Future<void> _createDB(Database db, int version) async {
    // Tabel Barang
    await db.execute('''
      CREATE TABLE barang (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nama TEXT NOT NULL,
        harga INTEGER NOT NULL,
        fotoPath TEXT,
        barcode TEXT
      )
    ''');

    // Tabel Transaksi
    await db.execute('''
      CREATE TABLE transaksi (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tanggal TEXT NOT NULL,
        totalHarga INTEGER NOT NULL,
        nominalBayar INTEGER NOT NULL,
        kembalian INTEGER NOT NULL
      )
    ''');

    // Tabel Item Transaksi
    await db.execute('''
      CREATE TABLE item_transaksi (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        transaksiId INTEGER NOT NULL,
        barangId INTEGER NOT NULL,
        namaBarang TEXT NOT NULL,
        hargaBarang INTEGER NOT NULL,
        jumlah INTEGER NOT NULL,
        subtotal INTEGER NOT NULL,
        FOREIGN KEY (transaksiId) REFERENCES transaksi (id) ON DELETE CASCADE
      )
    ''');

    // Isi otomatis 10 data contoh barang warung saat database pertama kali dibuat
    final List<Map<String, dynamic>> initialItems = [
      {'nama': 'Beras 1kg', 'harga': 14000},
      {'nama': 'Minyak Goreng 1L', 'harga': 16500},
      {'nama': 'Mie Instan Goreng', 'harga': 3000},
      {'nama': 'Gula Pasir 1kg', 'harga': 17500},
      {'nama': 'Kopi Sachet', 'harga': 1500},
      {'nama': 'Rokok Eceran', 'harga': 2500},
      {'nama': 'Air Mineral 600ml', 'harga': 3500},
      {'nama': 'Telur Ayam 1kg', 'harga': 28000},
      {'nama': 'Teh Celup', 'harga': 6000},
      {'nama': 'Sabun Cuci', 'harga': 4000},
    ];

    for (var item in initialItems) {
      await db.insert('barang', item);
    }
  }

  // Helper CRUD Barang
  Future<List<Barang>> getAllBarang() async {
    final db = await instance.database;
    final result = await db.query('barang', orderBy: 'nama ASC');
    return result.map((json) => Barang.fromMap(json)).toList();
  }

  Future<int> insertBarang(Barang barang) async {
    final db = await instance.database;
    return await db.insert('barang', barang.toMap());
  }

  // Helper CRUD Transaksi
  Future<int> insertTransaksi(Transaksi transaksi) async {
    final db = await instance.database;
    final id = await db.insert('transaksi', transaksi.toMap());

    for (var item in transaksi.items) {
      await db.insert('item_transaksi', item.toMap(id));
    }

    return id;
  }
}
