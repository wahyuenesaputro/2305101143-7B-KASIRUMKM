import 'item_transaksi.dart';

/// Data model untuk Transaksi
class Transaksi {
  final int? id;
  final String tanggal;
  final List<ItemTransaksi> items;
  final int totalHarga;
  final int nominalBayar;
  final int kembalian;

  Transaksi({
    this.id,
    required this.tanggal,
    required this.items,
    required this.totalHarga,
    required this.nominalBayar,
    required this.kembalian,
  });

  Map<String, dynamic> toMap() {
    return {
      if (id != null) 'id': id,
      'tanggal': tanggal,
      'totalHarga': totalHarga,
      'nominalBayar': nominalBayar,
      'kembalian': kembalian,
    };
  }

  factory Transaksi.fromMap(Map<String, dynamic> map, [List<ItemTransaksi> items = const []]) {
    return Transaksi(
      id: map['id'] as int?,
      tanggal: map['tanggal'] as String,
      items: items,
      totalHarga: map['totalHarga'] as int,
      nominalBayar: map['nominalBayar'] as int,
      kembalian: map['kembalian'] as int,
    );
  }
}
