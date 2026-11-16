/// Data model untuk ItemTransaksi
class ItemTransaksi {
  final int barangId;
  final String namaBarang;
  final int hargaBarang;
  final int jumlah;
  final int subtotal;

  ItemTransaksi({
    required this.barangId,
    required this.namaBarang,
    required this.hargaBarang,
    required this.jumlah,
    int? subtotal,
  }) : subtotal = subtotal ?? (hargaBarang * jumlah);

  Map<String, dynamic> toMap([int? transaksiId]) {
    return {
      if (transaksiId != null) 'transaksiId': transaksiId,
      'barangId': barangId,
      'namaBarang': namaBarang,
      'hargaBarang': hargaBarang,
      'jumlah': jumlah,
      'subtotal': subtotal,
    };
  }

  factory ItemTransaksi.fromMap(Map<String, dynamic> map) {
    return ItemTransaksi(
      barangId: map['barangId'] as int,
      namaBarang: map['namaBarang'] as String,
      hargaBarang: map['hargaBarang'] as int,
      jumlah: map['jumlah'] as int,
      subtotal: map['subtotal'] as int,
    );
  }
}
