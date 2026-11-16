/// Pemformat angka ke string dengan format "Rp 15.000" (titik sebagai pemisah ribuan, tanpa desimal).
String formatRupiah(dynamic number) {
  if (number == null) return "Rp 0";

  int value = 0;
  if (number is int) {
    value = number;
  } else if (number is double) {
    value = number.round();
  } else if (number is String) {
    value = int.tryParse(number) ?? 0;
  }

  String str = value.toString();
  String result = '';
  int count = 0;

  for (int i = str.length - 1; i >= 0; i--) {
    count++;
    result = str[i] + result;
    if (count % 3 == 0 && i != 0) {
      result = '.$result';
    }
  }

  return 'Rp $result';
}
