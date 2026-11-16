/// Data model untuk Barang
class Barang {
  final int? id;
  final String nama;
  final int harga;
  final String? fotoPath;
  final String? barcode;

  Barang({
    this.id,
    required this.nama,
    required this.harga,
    this.fotoPath,
    this.barcode,
  });

  Map<String, dynamic> toMap() {
    return {
      if (id != null) 'id': id,
      'nama': nama,
      'harga': harga,
      'fotoPath': fotoPath,
      'barcode': barcode,
    };
  }

  factory Barang.fromMap(Map<String, dynamic> map) {
    return Barang(
      id: map['id'] as int?,
      nama: map['nama'] as String,
      harga: map['harga'] as int,
      fotoPath: map['fotoPath'] as String?,
      barcode: map['barcode'] as String?,
    );
  }
}
