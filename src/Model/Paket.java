// Di dalam file Model/Paket.java Anda
package Model;

import java.math.BigDecimal;
import java.text.NumberFormat; // Import ini
import java.util.Date;
import java.util.Locale;    // Import ini
import java.util.Objects;

public class Paket extends BaseModel {
    private int idPaket;
    private String namaPaket;
    private BigDecimal harga;
    private String keterangan;

    public Paket() {}

    public Paket(int idPaket, String namaPaket, BigDecimal harga, String keterangan,
                 Date createdAt, Date updatedAt) {
        this.idPaket = idPaket;
        this.namaPaket = namaPaket;
        this.harga = harga;
        this.keterangan = keterangan;
        this.createdAt = createdAt; // Pastikan BaseModel menangani ini atau set di sini
        this.updatedAt = updatedAt; // Pastikan BaseModel menangani ini atau set di sini
    }
    
    public int getIdPaket() { return idPaket; }
    public void setIdPaket(int idPaket) { this.idPaket = idPaket; }

    public String getNamaPaket() { return namaPaket; }
    public void setNamaPaket(String namaPaket) { this.namaPaket = namaPaket; }

    public BigDecimal getHarga() { return harga; }
    public void setHarga(BigDecimal harga) { this.harga = harga; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    @Override
    public String toString() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        if (harga != null) {
            return namaPaket + " (" + currencyFormatter.format(harga) + ")";
        }
        return namaPaket; // Jika harga null, tampilkan nama saja
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paket paket = (Paket) o;
        return idPaket == paket.idPaket;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaket);
    }
}