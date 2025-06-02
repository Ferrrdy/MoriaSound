package Model;

import java.util.Date;
import java.sql.Timestamp; // Import Timestamp

public class GajiModel {
    private int id_gaji;
    private int id_crew; // DIUBAH: dari String ke int
    private Date tanggal_gaji;
    private double jumlah_gaji;
    private double bonus;
    private String nomor_rekening; // DIUBAH: dari double ke String
    private Date tanggal_pembayaran; // Bisa tetap Date, atau lebih presisi jika Timestamp
    private Timestamp created_at; // DIUBAH: dari Date ke Timestamp
    private Timestamp updated_at; // DIUBAH: dari Date ke Timestamp
    private String keterangan;

    // Konstruktor default
    public GajiModel() {
    }

    // Konstruktor dengan parameter lengkap
    public GajiModel(int id_gaji, int id_crew, Date tanggal_gaji, double jumlah_gaji, double bonus, String nomor_rekening, Date tanggal_pembayaran, String keterangan, Timestamp created_at, Timestamp updated_at) {
        this.id_gaji = id_gaji;
        this.id_crew = id_crew;
        this.tanggal_gaji = tanggal_gaji;
        this.jumlah_gaji = jumlah_gaji;
        this.bonus = bonus;
        this.nomor_rekening = nomor_rekening;
        this.tanggal_pembayaran = tanggal_pembayaran;
        this.keterangan = keterangan;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Konstruktor untuk data baru tanpa id_gaji, created_at, updated_at
    public GajiModel(int id_crew, Date tanggal_gaji, double jumlah_gaji, double bonus, String nomor_rekening, Date tanggal_pembayaran, String keterangan) {
        this.id_crew = id_crew;
        this.tanggal_gaji = tanggal_gaji;
        this.jumlah_gaji = jumlah_gaji;
        this.bonus = bonus;
        this.nomor_rekening = nomor_rekening;
        this.tanggal_pembayaran = tanggal_pembayaran;
        this.keterangan = keterangan;
    }

    // Getters and Setters
    public int getId_gaji() { return id_gaji; }
    public void setId_gaji(int id_gaji) { this.id_gaji = id_gaji; }

    public int getId_crew() { return id_crew; } // Getter mengembalikan int
    public void setId_crew(int id_crew) { this.id_crew = id_crew; } // Setter menerima int

    public Date getTanggal_gaji() { return tanggal_gaji; }
    public void setTanggal_gaji(Date tanggal_gaji) { this.tanggal_gaji = tanggal_gaji; }

    public double getJumlah_gaji() { return jumlah_gaji; }
    public void setJumlah_gaji(double jumlah_gaji) { this.jumlah_gaji = jumlah_gaji; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    public String getNomor_rekening() { return nomor_rekening; } // Getter mengembalikan String
    public void setNomor_rekening(String nomor_rekening) { this.nomor_rekening = nomor_rekening; } // Setter menerima String

    public Date getTanggal_pembayaran() { return tanggal_pembayaran; }
    public void setTanggal_pembayaran(Date tanggal_pembayaran) { this.tanggal_pembayaran = tanggal_pembayaran; }
    // Tambahkan setter overload untuk Timestamp jika diperlukan, seperti yang saya sarankan sebelumnya
    public void setTanggal_pembayaran(Timestamp tanggal_pembayaran) {
        this.tanggal_pembayaran = tanggal_pembayaran;
    }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public Timestamp getCreated_at() { return created_at; } // Getter mengembalikan Timestamp
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; } // Setter menerima Timestamp

    public Timestamp getUpdated_at() { return updated_at; } // Getter mengembalikan Timestamp
    public void setUpdated_at(Timestamp updated_at) { this.updated_at = updated_at; } // Setter menerima Timestamp

    @Override
    public String toString() {
        return "GajiModel{" +
                "id_gaji=" + id_gaji +
                ", id_crew='" + id_crew + '\'' +
                ", tanggal_gaji=" + tanggal_gaji +
                ", jumlah_gaji=" + jumlah_gaji +
                ", bonus=" + bonus +
                ", nomor_rekening='" + nomor_rekening + '\'' + // toString juga mencetak String
                ", tanggal_pembayaran=" + tanggal_pembayaran +
                ", keterangan='" + keterangan + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
            '}';
}
}
