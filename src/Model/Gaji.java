package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Gaji extends BaseModel {
    private int idGaji;
    private int idKaryawan;
    private BigDecimal jumlahGaji;
    private Date tanggalGajian;
    private String keterangan;
    private String status;

    public Gaji() {}

    public Gaji(int idGaji, int idKaryawan, BigDecimal jumlahGaji, Date tanggalGajian, 
                String keterangan, String status, Date createdAt, Date updatedAt) {
        this.idGaji = idGaji;
        this.idKaryawan = idKaryawan;
        this.jumlahGaji = jumlahGaji;
        this.tanggalGajian = tanggalGajian;
        this.keterangan = keterangan;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getIdGaji() {
        return idGaji;
    }

    public void setIdGaji(int idGaji) {
        this.idGaji = idGaji;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public BigDecimal getJumlahGaji() {
        return jumlahGaji;
    }

    public void setJumlahGaji(BigDecimal jumlahGaji) {
        this.jumlahGaji = jumlahGaji;
    }

    public Date getTanggalGajian() {
        return tanggalGajian;
    }

    public void setTanggalGajian(Date tanggalGajian) {
        this.tanggalGajian = tanggalGajian;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
