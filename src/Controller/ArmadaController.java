package Controller;

import Model.Armada; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArmadaController {
    private List<Armada> daftarArmada;
    private int nextIdArmada = 1;

    public ArmadaController() {
        this.daftarArmada = new ArrayList<>();
        // Contoh data awal (opsional)
        // initializeContohData(); 
    }

    /**
     * Inisialisasi data contoh untuk pengujian.
     */
    private void initializeContohData() {
        tambahArmada("Truk Box A-001", "Tersedia");
        tambahArmada("Mobil Van B-002", "Dalam Perbaikan");
        tambahArmada("Truk Kontainer C-003", "Tersedia");
    }

    /**
     *
     *
     * @param namaArmada 
     * @param status
     * @return 
     */
    public Armada tambahArmada(String namaArmada, String status) {
        if (namaArmada == null || namaArmada.trim().isEmpty()) {
            System.err.println("Nama armada tidak boleh kosong.");
            return null;
        }
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Status armada tidak boleh kosong.");
            return null;
        }

        Date sekarang = new Date();
        Armada armadaBaru = new Armada(
            nextIdArmada++, 
            namaArmada, 
            status, 
            sekarang, // createdAt
            sekarang  // updatedAt
        );
        this.daftarArmada.add(armadaBaru);
        System.out.println("Armada '" + namaArmada + "' berhasil ditambahkan dengan ID: " + armadaBaru.getIdArmada());
        return armadaBaru;
    }

    /**
     * Mendapatkan semua armada dari daftar.
     *
     * @return 
     */
    public List<Armada> lihatSemuaArmada() {
        if (this.daftarArmada.isEmpty()) {
            System.out.println("Belum ada armada dalam daftar.");
        }
        return new ArrayList<>(this.daftarArmada);
    }

    /**
     * Mencari armada berdasarkan ID.
     *
     * @param idArmada 
     * @return 
     */
    public Optional<Armada> cariArmadaById(int idArmada) {
        return this.daftarArmada.stream()
                .filter(armada -> armada.getIdArmada() == idArmada)
                .findFirst();
    }
    
    /**
     * Mencari armada berdasarkan nama.
     *
     * @param namaArmada
     * @return 
     */
    public List<Armada> cariArmadaByNama(String namaArmada) {
        if (namaArmada == null || namaArmada.trim().isEmpty()) {
            System.out.println("Nama armada pencarian tidak boleh kosong.");
            return new ArrayList<>();
        }
        String namaDicariLower = namaArmada.toLowerCase();
        return this.daftarArmada.stream()
                .filter(armada -> armada.getNamaArmada().toLowerCase().contains(namaDicariLower))
                .collect(Collectors.toList());
    }

    /**
     * Memperbarui data armada yang sudah ada.
     *
     * @param idArmada 
     * @param namaArmadaBaru
     * @param statusBaru 
     * @return 
     */
    public boolean perbaruiArmada(int idArmada, String namaArmadaBaru, String statusBaru) {
        Optional<Armada> armadaOptional = cariArmadaById(idArmada);
        if (armadaOptional.isPresent()) {
            Armada armada = armadaOptional.get();

            boolean adaPerubahan = false;

            if (namaArmadaBaru != null && !namaArmadaBaru.trim().isEmpty() && !namaArmadaBaru.equals(armada.getNamaArmada())) {
                armada.setNamaArmada(namaArmadaBaru);
                adaPerubahan = true;
            } else if (namaArmadaBaru != null && namaArmadaBaru.trim().isEmpty()){
                 System.err.println("Nama armada baru tidak boleh kosong saat pembaruan.");
            }

            if (statusBaru != null && !statusBaru.trim().isEmpty() && !statusBaru.equals(armada.getStatus())) {
                armada.setStatus(statusBaru);
                adaPerubahan = true;
            } else if (statusBaru != null && statusBaru.trim().isEmpty()){
                System.err.println("Status baru tidak boleh kosong saat pembaruan.");
            }
            
            if (adaPerubahan) {
                armada.setUpdatedAt(new Date());
                System.out.println("Armada dengan ID " + idArmada + " berhasil diperbarui.");
            } else {
                System.out.println("Tidak ada perubahan data untuk Armada dengan ID " + idArmada + ".");
            }
            return true;
        } else {
            System.err.println("Gagal memperbarui: Armada dengan ID " + idArmada + " tidak ditemukan.");
            return false;
        }
    }

    /**
     * Menghapus armada dari daftar berdasarkan ID.
     *
     * @param idArmada
     * @return
     */
    public boolean hapusArmada(int idArmada) {
        Optional<Armada> armadaOptional = cariArmadaById(idArmada);
        if (armadaOptional.isPresent()) {
            this.daftarArmada.remove(armadaOptional.get());
            System.out.println("Armada dengan ID " + idArmada + " berhasil dihapus.");
            return true;
        } else {
            System.err.println("Gagal menghapus: Armada dengan ID " + idArmada + " tidak ditemukan.");
            return false;
        }
    }

    public static void main(String[] args) {
        ArmadaController controller = new ArmadaController();
        controller.initializeContohData();

        System.out.println("--- Daftar Armada Awal ---");
        controller.lihatSemuaArmada().forEach(a -> 
            System.out.println("ID: " + a.getIdArmada() + ", Nama: " + a.getNamaArmada() + ", Status: " + a.getStatus())
        );
        System.out.println();

        // Menambah armada baru
        System.out.println("--- Menambah Armada Baru ---");
        controller.tambahArmada("Motor Bebek X-004", "Tersedia");
        System.out.println();

        System.out.println("--- Daftar Armada Setelah Penambahan ---");
        controller.lihatSemuaArmada().forEach(a -> 
            System.out.println("ID: " + a.getIdArmada() + ", Nama: " + a.getNamaArmada() + ", Status: " + a.getStatus())
        );
        System.out.println();

        // Mencari armada
        System.out.println("--- Mencari Armada ID 2 ---");
        Optional<Armada> armadaDitemukan = controller.cariArmadaById(2);
        armadaDitemukan.ifPresent(a -> System.out.println("Ditemukan: " + a.getNamaArmada() + " (" + a.getStatus() + ")"));
        if (!armadaDitemukan.isPresent()) {
            System.out.println("Armada dengan ID 2 tidak ditemukan.");
        }
        System.out.println();

        System.out.println("--- Mencari Armada dengan nama 'Truk' ---");
        List<Armada> hasilCariNama = controller.cariArmadaByNama("Truk");
        hasilCariNama.forEach(a -> System.out.println("Ditemukan: " + a.getNamaArmada() + " (" + a.getStatus() + ")"));
         if (hasilCariNama.isEmpty()) {
            System.out.println("Tidak ada armada dengan nama mengandung 'Truk'.");
        }
        System.out.println();

        // Memperbarui armada
        System.out.println("--- Memperbarui Armada ID 1 ---");
        controller.perbaruiArmada(1, "Truk Box A-001 Super", "Digunakan");
        armadaDitemukan = controller.cariArmadaById(1);
        armadaDitemukan.ifPresent(a -> 
            System.out.println("Setelah Update: ID: " + a.getIdArmada() + ", Nama: " + a.getNamaArmada() + ", Status: " + a.getStatus())
        );
        System.out.println();
        
        System.out.println("--- Mencoba Memperbarui Armada ID 1 tanpa perubahan data ---");
        controller.perbaruiArmada(1, "Truk Box A-001 Super", "Digunakan"); // Data sama
        System.out.println();

        // Menghapus armada
        System.out.println("--- Menghapus Armada ID 3 ---");
        controller.hapusArmada(3);
        System.out.println();

        System.out.println("--- Daftar Armada Akhir ---");
        controller.lihatSemuaArmada().forEach(a -> 
            System.out.println("ID: " + a.getIdArmada() + ", Nama: " + a.getNamaArmada() + ", Status: " + a.getStatus())
        );
    }
}
