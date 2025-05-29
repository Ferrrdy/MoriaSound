package Model;

import java.util.Date;

public class Event extends BaseModel {
    private int idEvent;
    private String namaEvent;
    private Date tanggalMulai;
    private Date tanggalSelesai;
    private int durasi;
    private String lokasi;
    private String keterangan;
    private String status;

    public Event() {}

    public Event(int idEvent, String namaEvent, Date tanggalMulai, Date tanggalSelesai,
                 int durasi, String lokasi, String keterangan, String status,
                 Date createdAt, Date updatedAt) {
        this.idEvent = idEvent;
        this.namaEvent = namaEvent;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.durasi = durasi;
        this.lokasi = lokasi;
        this.keterangan = keterangan;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getIdEvent() { return idEvent; }
    public void setIdEvent(int idEvent) { this.idEvent = idEvent; }

    public String getNamaEvent() { return namaEvent; }
    public void setNamaEvent(String namaEvent) { this.namaEvent = namaEvent; }

    public Date getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(Date tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public Date getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(Date tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }

    public int getDurasi() { return durasi; }
    public void setDurasi(int durasi) { this.durasi = durasi; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
