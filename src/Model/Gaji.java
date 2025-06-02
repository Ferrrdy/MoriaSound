package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Gaji extends BaseModel {
    private int idGaji;
    private int idCrew;
    private Date tanggalGaji;
    private BigDecimal jumlahGaji;
    private BigDecimal bonus;
    private String nomor_rekening;
    private Date tanggalPembayaran;
    private String keterangan;
    private Date createdAt;
    private Date updatedAt;

    public Gaji() {}

    public Gaji(int idGaji, int idCrew, Date tanggalGaji, BigDecimal jumlahGaji, BigDecimal bonus, String nomor_rekening,
                Date tanggalPembayaran, String keterangan, Date createdAt, Date updatedAt) {
        this.idGaji = idGaji;
        this.idCrew = idCrew;
        this.tanggalGaji = tanggalGaji;
        this.jumlahGaji = jumlahGaji;
        this.bonus = bonus;
        this.nomor_rekening = nomor_rekening;
        this.tanggalPembayaran = tanggalPembayaran;
        this.keterangan = keterangan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getIdGaji() {
        return idGaji;
    }

    public void setIdGaji(int idGaji) {
        this.idGaji = idGaji;
    }

    public int getIdCrew() {
        return idCrew;
    }

    public void setIdCrew(int idCrew) {
        this.idCrew = idCrew;
    }

    public Date getTanggalGaji() {
        return tanggalGaji;
    }

    public void setTanggalGaji(Date tanggalGaji) {
        this.tanggalGaji = tanggalGaji;
    }

    public BigDecimal getJumlahGaji() {
        return jumlahGaji;
    }

    public void setJumlahGaji(BigDecimal jumlahGaji) {
        this.jumlahGaji = jumlahGaji;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public String getNomor_rekening() {
        return nomor_rekening;
    }

    public void setNomor_rekening(String nomor_rekening) {
        this.nomor_rekening = nomor_rekening;
    }

    public Date getTanggalPembayaran() {
        return tanggalPembayaran;
    }

    public void setTanggalPembayaran(Date tanggalPembayaran) {
        this.tanggalPembayaran = tanggalPembayaran;
    }
    
    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
