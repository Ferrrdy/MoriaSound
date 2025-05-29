package Model;

import java.util.Date;

public class ArmadaDigunakan extends BaseModel {
    private int idArmadaDigunakan;
    private int idEvent;
    private int idArmada;
    private Date tanggalKeluar;
    private Date tanggalMasuk;
    private String kondisiSetelahMasuk;

    public ArmadaDigunakan() {}

    public ArmadaDigunakan(int idArmadaDigunakan, int idEvent, int idArmada, Date tanggalKeluar,
                           Date tanggalMasuk, String kondisiSetelahMasuk, Date createdAt, Date updatedAt) {
        this.idArmadaDigunakan = idArmadaDigunakan;
        this.idEvent = idEvent;
        this.idArmada = idArmada;
        this.tanggalKeluar = tanggalKeluar;
        this.tanggalMasuk = tanggalMasuk;
        this.kondisiSetelahMasuk = kondisiSetelahMasuk;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getIdArmadaDigunakan() { return idArmadaDigunakan; }
    public void setIdArmadaDigunakan(int idArmadaDigunakan) { this.idArmadaDigunakan = idArmadaDigunakan; }

    public int getIdEvent() { return idEvent; }
    public void setIdEvent(int idEvent) { this.idEvent = idEvent; }

    public int getIdArmada() { return idArmada; }
    public void setIdArmada(int idArmada) { this.idArmada = idArmada; }

    public Date getTanggalKeluar() { return tanggalKeluar; }
    public void setTanggalKeluar(Date tanggalKeluar) { this.tanggalKeluar = tanggalKeluar; }

    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }

    public String getKondisiSetelahMasuk() { return kondisiSetelahMasuk; }
    public void setKondisiSetelahMasuk(String kondisiSetelahMasuk) { this.kondisiSetelahMasuk = kondisiSetelahMasuk; }
}
