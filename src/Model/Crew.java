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
    
    // Konstruktor asli yang Anda miliki (hanya id, nama, posisi) - tetap pertahankan jika masih digunakan
    public Crew(int id, String nama, String posisi) {
        this.idCrew = id;
        this.namaCrew = nama;
        this.posisi = posisi;
    }

    // --- BARU: Konstruktor untuk membuat objek Crew baru (sebelum insert ke DB) ---
    // ID, createdAt, updatedAt akan diatur oleh DB setelah insert
    public Crew(String namaCrew, String posisi, double gajiBulanan, String norek_crew) {
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.norek_crew = norek_crew;
    }

    // --- BARU: Konstruktor lengkap untuk memuat data dari database ---
    // Ini yang akan digunakan di getAllCrewInstance dan getCrewByIdInstance
    public Crew(int idCrew, String namaCrew, String posisi, double gajiBulanan, String norek_crew, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idCrew = idCrew;
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.norek_crew = norek_crew;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return this.idCrew; // Atau return getIdCrew();
    }

    @Override
    public String toString() {
        return this.namaCrew + " (" + this.posisi + ")";
    }
    
    // Getter dan Setter yang sudah ada (pastikan sudah benar semua)
    public int getIdCrew() { return idCrew; } // Perbaiki nama metode jika sebelumnya getId()
    public void setIdCrew(int idCrew) { this.idCrew = idCrew; }
    public String getNamaCrew() { return namaCrew; }
    public void setNamaCrew(String namaCrew) { this.namaCrew = namaCrew; }
    public String getPosisi() { return posisi; }
    public void setPosisi(String posisi) { this.posisi = posisi; }
    public double getGajiBulanan() { return gajiBulanan; }
    public void setGajiBulanan(double gajiBulanan) { this.gajiBulanan = gajiBulanan; }
    public String getNorek_crew() { return norek_crew; }
    public void setNorek_crew(String norek_crew) { this.norek_crew = norek_crew; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}