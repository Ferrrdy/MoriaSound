package Model;

import java.util.Date;

public class Barang extends BaseModel {
    private int idBarang;
    private String namaBarang;
    private int idKategori;
    private String kondisi;
    private int jumlahTotal;
    private int jumlahTersedia;

    public Barang() {}

    public Barang(int idBarang, String namaBarang, int idKategori, String kondisi,
                  int jumlahTotal, int jumlahTersedia, Date createdAt, Date updatedAt) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.idKategori = idKategori;
        this.kondisi = kondisi;
        this.jumlahTotal = jumlahTotal;
        this.jumlahTersedia = jumlahTersedia;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getIdBarang() { return idBarang; }
    public void setIdBarang(int idBarang) { this.idBarang = idBarang; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }

    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }

    public int getJumlahTotal() { return jumlahTotal; }
    public void setJumlahTotal(int jumlahTotal) { this.jumlahTotal = jumlahTotal; }

    public int getJumlahTersedia() { return jumlahTersedia; }
    public void setJumlahTersedia(int jumlahTersedia) { this.jumlahTersedia = jumlahTersedia; }

}
