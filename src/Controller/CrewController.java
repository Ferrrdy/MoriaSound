package Controller;

import Model.Crew; // Pastikan Model.Crew dapat diakses
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;



public class CrewController {

   
    private List<Crew> daftarCrew;
    private int nextIdCrew; // Untuk auto-increment ID sederhana

    
    public CrewController() {
        this.daftarCrew = new ArrayList<>();
        this.nextIdCrew = 1; // ID dimulai dari 1
       
    }

    /**
     * Menambahkan crew baru ke dalam daftar.
     *
     * @param namaCrew Nama dari crew.
     * @param posisi Posisi dari crew.
     * @param gajiBulanan Gaji bulanan crew.
     * @return Objek Crew yang baru dibuat dan ditambahkan.
     */
    public Crew tambahCrew(String namaCrew, String posisi, BigDecimal gajiBulanan) {
        Date currentDate = new Date();
        Crew crewBaru = new Crew();
        crewBaru.setIdCrew(nextIdCrew++); // Tetapkan ID dan increment untuk ID berikutnya
        crewBaru.setNamaCrew(namaCrew);
        crewBaru.setPosisi(posisi);
        crewBaru.setGajiBulanan(gajiBulanan);
        crewBaru.setCreatedAt(currentDate); // Tetapkan waktu pembuatan
        crewBaru.setUpdatedAt(currentDate); // Tetapkan waktu pembaruan awal sama dengan waktu pembuatan

        this.daftarCrew.add(crewBaru);
        System.out.println("Crew berhasil ditambahkan: " + namaCrew + " (ID: " + crewBaru.getIdCrew() + ")");
        return crewBaru;
    }

    /**
     * Mendapatkan semua data crew.
     *
     * @return List dari semua objek Crew.
     */
    public List<Crew> getAllCrew() {
        if (daftarCrew.isEmpty()) {
            System.out.println("Tidak ada data crew.");
        }
        return new ArrayList<>(this.daftarCrew); // Mengembalikan salinan untuk mencegah modifikasi eksternal
    }

    /**
     * Mendapatkan data crew berdasarkan ID.
     *
     * @param idCrew ID dari crew yang dicari.
     * @return Optional yang berisi objek Crew jika ditemukan, atau Optional.empty() jika tidak.
     */
    public Optional<Crew> getCrewById(int idCrew) {
        return this.daftarCrew.stream()
                .filter(crew -> crew.getIdCrew() == idCrew)
                .findFirst();
    }

    /**
     * Memperbarui data crew yang sudah ada.
     *
     * @param idCrew ID dari crew yang akan diperbarui.
     * @param namaCrewBaru Nama baru untuk crew (null jika tidak ingin diubah).
     * @param posisiBaru Posisi baru untuk crew (null jika tidak ingin diubah).
     * @param gajiBulananBaru Gaji bulanan baru untuk crew (null jika tidak ingin diubah).
     * @return true jika pembaruan berhasil, false jika crew tidak ditemukan.
     */
    public boolean updateCrew(int idCrew, String namaCrewBaru, String posisiBaru, BigDecimal gajiBulananBaru) {
        Optional<Crew> crewOptional = getCrewById(idCrew);
        if (crewOptional.isPresent()) {
            Crew crewUntukUpdate = crewOptional.get();
            boolean updated = false;

            if (namaCrewBaru != null && !namaCrewBaru.isEmpty()) {
                crewUntukUpdate.setNamaCrew(namaCrewBaru);
                updated = true;
            }
            if (posisiBaru != null && !posisiBaru.isEmpty()) {
                crewUntukUpdate.setPosisi(posisiBaru);
                updated = true;
            }
            if (gajiBulananBaru != null) {
                crewUntukUpdate.setGajiBulanan(gajiBulananBaru);
                updated = true;
            }

            if (updated) {
                crewUntukUpdate.setUpdatedAt(new Date()); // Perbarui timestamp updatedAt
                System.out.println("Crew dengan ID " + idCrew + " berhasil diperbarui.");
                return true;
            } else {
                System.out.println("Tidak ada perubahan data untuk crew dengan ID " + idCrew + ".");
                return false; // Tidak ada field yang diupdate
            }
        } else {
            System.out.println("Gagal memperbarui: Crew dengan ID " + idCrew + " tidak ditemukan.");
            return false;
        }
    }

    /**
     * Menghapus crew berdasarkan ID.
     *
     * @param idCrew ID dari crew yang akan dihapus.
     * @return true jika penghapusan berhasil, false jika crew tidak ditemukan.
     */
    public boolean hapusCrew(int idCrew) {
        boolean isRemoved = this.daftarCrew.removeIf(crew -> crew.getIdCrew() == idCrew);
        if (isRemoved) {
            System.out.println("Crew dengan ID " + idCrew + " berhasil dihapus.");
        } else {
            System.out.println("Gagal menghapus: Crew dengan ID " + idCrew + " tidak ditemukan.");
        }
        return isRemoved;
    }

    /**
     * Metode main untuk demonstrasi fungsionalitas CrewController.
     * @param args Argumen command line (tidak digunakan).
     */
    public static void main(String[] args) {
        CrewController controller = new CrewController();

        System.out.println("--- Menambahkan Crew ---");
        Crew crew1 = controller.tambahCrew("Budi Perkasa", "Nahkoda", new BigDecimal("30000000"));
        Crew crew2 = controller.tambahCrew("Siti Aminah", "Juru Masak", new BigDecimal("15000000"));
        controller.tambahCrew("Agus Setiawan", "Mekanik", new BigDecimal("22000000"));

        System.out.println("\n--- Menampilkan Semua Crew ---");
        controller.getAllCrew().forEach(crew ->
            System.out.println("ID: " + crew.getIdCrew() + ", Nama: " + crew.getNamaCrew() +
                               ", Posisi: " + crew.getPosisi() + ", Gaji: " + crew.getGajiBulanan() +
                               ", Dibuat: " + crew.getCreatedAt() + ", Diperbarui: " + crew.getUpdatedAt())
        );

        System.out.println("\n--- Mencari Crew dengan ID " + crew1.getIdCrew() + " ---");
        Optional<Crew> foundCrew = controller.getCrewById(crew1.getIdCrew());
        foundCrew.ifPresent(crew -> System.out.println("Ditemukan: " + crew.getNamaCrew()));

        System.out.println("\n--- Memperbarui Crew dengan ID " + crew2.getIdCrew() + " ---");
        controller.updateCrew(crew2.getIdCrew(), "Siti Aminah Updated", "Kepala Juru Masak", new BigDecimal("17000000"));
        controller.getCrewById(crew2.getIdCrew()).ifPresent(crew ->
            System.out.println("Setelah Update - Nama: " + crew.getNamaCrew() + ", Posisi: " + crew.getPosisi() + ", Gaji: " + crew.getGajiBulanan())
        );


        System.out.println("\n--- Menghapus Crew dengan ID " + crew1.getIdCrew() + " ---");
        controller.hapusCrew(crew1.getIdCrew());

        System.out.println("\n--- Menampilkan Semua Crew Setelah Perubahan ---");
        controller.getAllCrew().forEach(crew ->
            System.out.println("ID: " + crew.getIdCrew() + ", Nama: " + crew.getNamaCrew() + ", Posisi: " + crew.getPosisi())
        );

        System.out.println("\n--- Mencoba mencari crew yang sudah dihapus (ID: " + crew1.getIdCrew() + ") ---");
        controller.getCrewById(crew1.getIdCrew()).ifPresentOrElse(
            crew -> System.out.println("Ditemukan: " + crew.getNamaCrew()),
            () -> System.out.println("Crew dengan ID " + crew1.getIdCrew() + " tidak ditemukan.")
        );
    }
}
