package Model;
import java.time.LocalDateTime;

public class Crew {
    private int idCrew;
    private String namaCrew;
    private String posisi;
    private double gajiBulanan;
    private String norek_crew;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Crew() {}

    // Constructor yang sudah ada
    public Crew(int idCrew, String namaCrew, String posisi, double gajiBulanan, String norek_crew) {
        this.idCrew = idCrew;
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.norek_crew = norek_crew;
        // createdAt dan updatedAt tidak diinisialisasi di sini, akan null
    }

    // --- KONSTRUKTOR BARU (untuk memuat data lengkap dari database) ---
    public Crew(int idCrew, String namaCrew, String posisi, double gajiBulanan, String norek_crew, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.idCrew = idCrew;
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.norek_crew = norek_crew;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- KONSTRUKTOR BARU (untuk membuat objek Crew baru sebelum disimpan ke DB) ---
    // ID, createdAt, dan updatedAt akan diatur di controller atau setelah insert ke DB
    public Crew(String namaCrew, String posisi, double gajiBulanan, String norek_crew) {
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.norek_crew = norek_crew;
        // createdAt dan updatedAt akan diatur di CrewController saat insert
    }


    public int getIdCrew() {
        return idCrew;
    }

    public void setIdCrew(int idCrew) {
        this.idCrew = idCrew;
    }

    public String getNamaCrew() {
        return namaCrew;
    }

    public void setNamaCrew(String namaCrew) {
        this.namaCrew = namaCrew;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public double getGajiBulanan() {
        return gajiBulanan;
    }

    public void setGajiBulanan(double gajiBulanan) {
        this.gajiBulanan = gajiBulanan;
    }

    public String getNorek_crew() {
        return norek_crew;
    }

    public void setNorek_crew(String norek_crew) {
        this.norek_crew = norek_crew;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}