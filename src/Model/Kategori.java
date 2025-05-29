package Model;

import java.util.Date;

public class Kategori extends BaseModel {
    private int idKategori;
    private String namaKategori;

    public Kategori() {}

    public Kategori(int idKategori, String namaKategori, Date createdAt, Date updatedAt) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }

    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }
}
