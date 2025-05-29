package Model;

import java.util.Date;

public class Armada extends BaseModel {
    private int idArmada;
    private String namaArmada;
    private String status;

    public Armada() {}

    public Armada(int idArmada, String namaArmada, String status, Date createdAt, Date updatedAt) {
        this.idArmada = idArmada;
        this.namaArmada = namaArmada;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getIdArmada() { return idArmada; }
    public void setIdArmada(int idArmada) { this.idArmada = idArmada; }

    public String getNamaArmada() { return namaArmada; }
    public void setNamaArmada(String namaArmada) { this.namaArmada = namaArmada; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
