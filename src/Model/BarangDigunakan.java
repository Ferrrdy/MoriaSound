package Model;

import java.util.Date;

public class BarangDigunakan extends BaseModel {
    private int idBarangDigunakan;
    private int idEvent;
    private int idBarang;
    private int jumlahKeluar;
    private Date tanggalKeluar;
    private Date tanggalMasuk;
    private String kondisiSetelahMasuk;

    public BarangDigunakan() {}

    public BarangDigunakan(int idBarangDigunakan, int idEvent, int idBarang, int jumlahKeluar,
                           Date tanggalKeluar, Date tanggalMasuk, String kondisiSetelahMasuk,
                           Date createdAt, Date updatedAt) {
        this.idBarangDigunakan = idBarangDigunakan;
        this.idEvent = idEvent;
        this.idBarang = idBarang;
        this.jumlahKeluar = jumlahKeluar;
        this.tanggalKeluar = tanggalKeluar;
        this.tanggalMasuk = tanggalMasuk;
        this.kondisiSetelahMasuk = kondisiSetelahMasuk;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getIdBarangDigunakan() { return idBarangDigunakan; }
    public void setIdBarangDigunakan(int idBarangDigunakan) { this.idBarangDigunakan = idBarangDigunakan; }

    public int getIdEvent() { return idEvent; }
    public void setIdEvent(int idEvent) { this.idEvent = idEvent; }

    public int getIdBarang() { return idBarang; }
    public void setIdBarang(int idBarang) { this.idBarang = idBarang; }

    public int getJumlahKeluar() { return jumlahKeluar; }
    public void setJumlahKeluar(int jumlahKeluar) { this.jumlahKeluar = jumlahKeluar; }

    public Date getTanggalKeluar() { return tanggalKeluar; }
    public void setTanggalKeluar(Date tanggalKeluar) { this.tanggalKeluar = tanggalKeluar; }

    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }

    public String getKondisiSetelahMasuk() { return kondisiSetelahMasuk; }
    public void setKondisiSetelahMasuk(String kondisiSetelahMasuk) { this.kondisiSetelahMasuk = kondisiSetelahMasuk; }
}
