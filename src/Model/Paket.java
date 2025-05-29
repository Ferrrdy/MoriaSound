package Model;

import java.math.BigDecimal;
import java.util.Date;

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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getIdPaket() { return idPaket; }
    public void setIdPaket(int idPaket) { this.idPaket = idPaket; }

    public String getNamaPaket() { return namaPaket; }
    public void setNamaPaket(String namaPaket) { this.namaPaket = namaPaket; }

    public BigDecimal getHarga() { return harga; }
    public void setHarga(BigDecimal harga) { this.harga = harga; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
}
