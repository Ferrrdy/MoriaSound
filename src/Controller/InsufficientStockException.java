package Controller;

public class InsufficientStockException extends Exception {
    private final int idBarang;
    private final String namaBarang; // Opsional, jika ingin menampilkan nama barang
    private final int jumlahDibutuhkan;
    private final int jumlahTersedia;

    public InsufficientStockException(String message, int idBarang, String namaBarang, int jumlahDibutuhkan, int jumlahTersedia) {
        super(message);
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlahDibutuhkan = jumlahDibutuhkan;
        this.jumlahTersedia = jumlahTersedia;
    }

    // Konstruktor yang lebih sederhana
    public InsufficientStockException(int idBarang, String namaBarang, int jumlahDibutuhkan, int jumlahTersedia) {
        this("Stok untuk barang '" + (namaBarang != null ? namaBarang : "ID: " + idBarang) + "' tidak mencukupi. Dibutuhkan: " + jumlahDibutuhkan + ", Tersedia: " + jumlahTersedia,
             idBarang, namaBarang, jumlahDibutuhkan, jumlahTersedia);
    }

    public int getIdBarang() {
        return idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public int getJumlahDibutuhkan() {
        return jumlahDibutuhkan;
    }

    public int getJumlahTersedia() {
        return jumlahTersedia;
    }
}