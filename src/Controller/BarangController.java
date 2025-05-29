package Controller;

import Model.Barang;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BarangController {
    private List<Barang> daftarBarang;
    private int nextIdBarang = 1; 

    public BarangController() {
        this.daftarBarang = new ArrayList<>();
        // Contoh data awal (opsional)
        // initializeContohData(); 
    }

    /**
     * Inisialisasi data contoh untuk pengujian.
     */
    private void initializeContohData() {
        tambahBarang("Laptop Gaming XYZ", 1, "Baru", 10, 10);
        tambahBarang("Keyboard Mekanik ABC", 2, "Bekas", 5, 3);
        tambahBarang("Mouse Wireless QWE", 1, "Baru", 20, 15);
    }

    /**
     * Menambahkan barang baru ke dalam daftar.
     *
     * @param namaBarang 
     * @param idKategori 
     * @param kondisi
     * @param jumlahTotal 
     * @param jumlahTersedia 
     * @return 
     */
    public Barang tambahBarang(String namaBarang, int idKategori, String kondisi,
                               int jumlahTotal, int jumlahTersedia) {
        if (namaBarang == null || namaBarang.trim().isEmpty()) {
            System.err.println("Nama barang tidak boleh kosong.");
            return null;
        }
        if (idKategori <= 0) {
            System.err.println("ID Kategori tidak valid.");
            return null;
        }
        if (jumlahTotal < 0 || jumlahTersedia < 0 || jumlahTersedia > jumlahTotal) {
            System.err.println("Jumlah total atau jumlah tersedia tidak valid.");
            return null;
        }

        Date sekarang = new Date();
        Barang barangBaru = new Barang(
            nextIdBarang++, 
            namaBarang, 
            idKategori, 
            kondisi, 
            jumlahTotal, 
            jumlahTersedia, 
            sekarang, // createdAt
            sekarang  // updatedAt
        );
        this.daftarBarang.add(barangBaru);
        System.out.println("Barang '" + namaBarang + "' berhasil ditambahkan dengan ID: " + barangBaru.getIdBarang());
        return barangBaru;
    }

    /**
     * Mendapatkan semua barang dari daftar.
     *
     * @return 
     */
    public List<Barang> lihatSemuaBarang() {
        if (this.daftarBarang.isEmpty()) {
            System.out.println("Belum ada barang dalam daftar.");
        }
        return new ArrayList<>(this.daftarBarang); 
    }

    /**
     * Mencari barang berdasarkan ID.
     *
     * @param idBarang 
     * @return
     */
    public Optional<Barang> cariBarangById(int idBarang) {
        return this.daftarBarang.stream()
                .filter(barang -> barang.getIdBarang() == idBarang)
                .findFirst();
    }
    
    /**
     * Mencari barang berdasarkan nama.
     *
     * @param namaBarang 
     * @return 
     */
    public List<Barang> cariBarangByNama(String namaBarang) {
        if (namaBarang == null || namaBarang.trim().isEmpty()) {
            System.out.println("Nama barang pencarian tidak boleh kosong.");
            return new ArrayList<>();
        }
        String namaDicariLower = namaBarang.toLowerCase();
        return this.daftarBarang.stream()
                .filter(barang -> barang.getNamaBarang().toLowerCase().contains(namaDicariLower))
                .collect(Collectors.toList());
    }

    /**
     * Memperbarui data barang yang sudah ada.
     *
     * @param idBarang 
     * @param namaBarangBaru 
     * @param idKategoriBaru 
     * @param kondisiBaru
     * @param jumlahTotalBaru
     * @param jumlahTersediaBaru
     * @return 
     */
    public boolean perbaruiBarang(int idBarang, String namaBarangBaru, int idKategoriBaru, 
                                  String kondisiBaru, int jumlahTotalBaru, int jumlahTersediaBaru) {
        Optional<Barang> barangOptional = cariBarangById(idBarang);
        if (barangOptional.isPresent()) {
            Barang barang = barangOptional.get();

            if (namaBarangBaru != null && !namaBarangBaru.trim().isEmpty()) {
                barang.setNamaBarang(namaBarangBaru);
            } else {
                 System.err.println("Nama barang baru tidak boleh kosong saat pembaruan.");
            }
            if (idKategoriBaru > 0) {
                barang.setIdKategori(idKategoriBaru);
            } else {
                System.err.println("ID Kategori baru tidak valid.");
            }
             if (jumlahTotalBaru < 0 || jumlahTersediaBaru < 0 || jumlahTersediaBaru > jumlahTotalBaru) {
                System.err.println("Jumlah total atau jumlah tersedia baru tidak valid.");
            } else {
                barang.setJumlahTotal(jumlahTotalBaru);
                barang.setJumlahTersedia(jumlahTersediaBaru);
            }

            barang.setKondisi(kondisiBaru);
            barang.setUpdatedAt(new Date());
            
            System.out.println("Barang dengan ID " + idBarang + " berhasil diperbarui.");
            return true;
        } else {
            System.err.println("Gagal memperbarui: Barang dengan ID " + idBarang + " tidak ditemukan.");
            return false;
        }
    }

    /**
     * Menghapus barang dari daftar berdasarkan ID.
     *
     * @param idBarang
     * @return
     */
    public boolean hapusBarang(int idBarang) {
        Optional<Barang> barangOptional = cariBarangById(idBarang);
        if (barangOptional.isPresent()) {
            this.daftarBarang.remove(barangOptional.get());
            System.out.println("Barang dengan ID " + idBarang + " berhasil dihapus.");
            return true;
        } else {
            System.err.println("Gagal menghapus: Barang dengan ID " + idBarang + " tidak ditemukan.");
            return false;
        }
    }

    public static void main(String[] args) {
        BarangController controller = new BarangController();
        controller.initializeContohData();

        System.out.println("--- Daftar Barang Awal ---");
        controller.lihatSemuaBarang().forEach(b -> 
            System.out.println("ID: " + b.getIdBarang() + ", Nama: " + b.getNamaBarang() + ", Tersedia: " + b.getJumlahTersedia())
        );
        System.out.println();

        // Menambah barang baru
        System.out.println("--- Menambah Barang Baru ---");
        controller.tambahBarang("Monitor LED 24 inch", 1, "Baru", 15, 12);
        System.out.println();

        System.out.println("--- Daftar Barang Setelah Penambahan ---");
        controller.lihatSemuaBarang().forEach(b -> 
            System.out.println("ID: " + b.getIdBarang() + ", Nama: " + b.getNamaBarang() + ", Tersedia: " + b.getJumlahTersedia())
        );
        System.out.println();

        // Mencari barang
        System.out.println("--- Mencari Barang ID 2 ---");
        Optional<Barang> barangDitemukan = controller.cariBarangById(2);
        barangDitemukan.ifPresent(b -> System.out.println("Ditemukan: " + b.getNamaBarang()));
        if (!barangDitemukan.isPresent()) {
            System.out.println("Barang dengan ID 2 tidak ditemukan.");
        }
        System.out.println();

        System.out.println("--- Mencari Barang dengan nama 'Laptop' ---");
        List<Barang> hasilCariNama = controller.cariBarangByNama("Laptop");
        hasilCariNama.forEach(b -> System.out.println("Ditemukan: " + b.getNamaBarang()));
         if (hasilCariNama.isEmpty()) {
            System.out.println("Tidak ada barang dengan nama mengandung 'Laptop'.");
        }
        System.out.println();


        // Memperbarui barang
        System.out.println("--- Memperbarui Barang ID 1 ---");
        controller.perbaruiBarang(1, "Laptop Gaming XYZ Pro", 1, "Baru Gress", 8, 7);
        barangDitemukan = controller.cariBarangById(1);
        barangDitemukan.ifPresent(b -> 
            System.out.println("Setelah Update: ID: " + b.getIdBarang() + ", Nama: " + b.getNamaBarang() + ", Kondisi: " + b.getKondisi() + ", Tersedia: " + b.getJumlahTersedia())
        );
        System.out.println();

        // Menghapus barang
        System.out.println("--- Menghapus Barang ID 3 ---");
        controller.hapusBarang(3);
        System.out.println();

        System.out.println("--- Daftar Barang Akhir ---");
        controller.lihatSemuaBarang().forEach(b -> 
            System.out.println("ID: " + b.getIdBarang() + ", Nama: " + b.getNamaBarang() + ", Tersedia: " + b.getJumlahTersedia())
        );
    }
}
