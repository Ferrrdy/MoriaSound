package Model;

import java.time.LocalDateTime;

public class Armada {
    private int idArmada;
    private String namaArmada;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Armada() {}

    public Armada(int id, String nama) {
        this.idArmada = id;
        this.namaArmada = nama;
    }

    @Override
    public String toString() {
        return this.namaArmada;
    }
    
    public int getId() {
        return this.idArmada;
    }

    // Getters & Setters
    public int getIdArmada() { return idArmada; }
    public void setIdArmada(int idArmada) { this.idArmada = idArmada; }
    public String getNamaArmada() { return namaArmada; }
    public void setNamaArmada(String namaArmada) { this.namaArmada = namaArmada; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}