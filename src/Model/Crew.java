package Model;

import java.util.Date;
import java.math.BigDecimal;

public class Crew extends BaseModel {
    private int idCrew;
    private String namaCrew;
    private String posisi;
    private BigDecimal gajiBulanan;

    public Crew() {}

    public Crew(int idCrew, String namaCrew, String posisi, BigDecimal gajiBulanan,
                Date createdAt, Date updatedAt) {
        this.idCrew = idCrew;
        this.namaCrew = namaCrew;
        this.posisi = posisi;
        this.gajiBulanan = gajiBulanan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getIdCrew() { return idCrew; }
    public void setIdCrew(int idCrew) { this.idCrew = idCrew; }

    public String getNamaCrew() { return namaCrew; }
    public void setNamaCrew(String namaCrew) { this.namaCrew = namaCrew; }

    public String getPosisi() { return posisi; }
    public void setPosisi(String posisi) { this.posisi = posisi; }

    public BigDecimal getGajiBulanan() { return gajiBulanan; }
    public void setGajiBulanan(BigDecimal gajiBulanan) { this.gajiBulanan = gajiBulanan; }
}
