package Model; // Sesuai struktur folder Anda

import java.util.Date; // Digunakan jika Anda ingin menyimpan tanggal sebagai objek Date

public class GajiModel {
    private int id_gaji;
    private String id_crew;
    private Date tanggal_gaji; // Atau String jika Anda prefer menangani format secara manual
    private double jumlah_gaji;
    private double bonus;
    private Date tanggal_pembayaran; // Atau String
    private String keterangan;
    private Date created_at;
    private Date updated_at;

    // Konstruktor default
    public GajiModel() {
    }

    // Konstruktor dengan parameter (opsional, bisa digunakan untuk inisialisasi)
    public GajiModel(int id_gaji, String id_crew, Date tanggal_gaji, double jumlah_gaji, double bonus, Date tanggal_pembayaran, String keterangan, Date created_at, Date updated_at) {
        this.id_gaji = id_gaji;
        this.id_crew = id_crew;
        this.tanggal_gaji = tanggal_gaji;
        this.jumlah_gaji = jumlah_gaji;
        this.bonus = bonus;
        this.tanggal_pembayaran = tanggal_pembayaran;
        this.keterangan = keterangan;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
    
    // Konstruktor untuk data baru tanpa id_gaji, created_at, updated_at (biasanya di-handle DB)
    public GajiModel(String id_crew, Date tanggal_gaji, double jumlah_gaji, double bonus, Date tanggal_pembayaran, String keterangan) {
        this.id_crew = id_crew;
        this.tanggal_gaji = tanggal_gaji;
        this.jumlah_gaji = jumlah_gaji;
        this.bonus = bonus;
        this.tanggal_pembayaran = tanggal_pembayaran;
        this.keterangan = keterangan;
    }


    // Getters and Setters
    public int getId_gaji() {
        return id_gaji;
    }

    public void setId_gaji(int id_gaji) {
        this.id_gaji = id_gaji;
    }

    public String getId_crew() {
        return id_crew;
    }

    public void setId_crew(String id_crew) {
        this.id_crew = id_crew;
    }

    public Date getTanggal_gaji() {
        return tanggal_gaji;
    }

    public void setTanggal_gaji(Date tanggal_gaji) {
        this.tanggal_gaji = tanggal_gaji;
    }

    public double getJumlah_gaji() {
        return jumlah_gaji;
    }

    public void setJumlah_gaji(double jumlah_gaji) {
        this.jumlah_gaji = jumlah_gaji;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public Date getTanggal_pembayaran() {
        return tanggal_pembayaran;
    }

    public void setTanggal_pembayaran(Date tanggal_pembayaran) {
        this.tanggal_pembayaran = tanggal_pembayaran;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "GajiModel{" +
                "id_gaji=" + id_gaji +
                ", id_crew='" + id_crew + '\'' +
                ", tanggal_gaji=" + tanggal_gaji +
                ", jumlah_gaji=" + jumlah_gaji +
                ", bonus=" + bonus +
                ", tanggal_pembayaran=" + tanggal_pembayaran +
                ", keterangan='" + keterangan + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}