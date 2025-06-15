package Model;

import java.util.Date;

public class Barang extends BaseModel {
    private int idBarang;
    private String namaBarang;
    private int idKategori;
    private String kondisi;
    private int jumlahTotal;
    private int jumlahTersedia;
    private int jumlahRusakRingan;
    private int jumlahRusakBerat;
    private int jumlahHilang;

    public Barang() {}

    public Barang(int idBarang, String namaBarang, int idKategori, String kondisi,
                  int jumlahTotal, int jumlahTersedia, int jumlahRusakRingan, int jumlahRusakBerat, int jumlahHilang,Date createdAt, Date updatedAt) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.idKategori = idKategori;
        this.kondisi = kondisi;
        this.jumlahTotal = jumlahTotal;
        this.jumlahTersedia = jumlahTersedia;
        this.jumlahRusakRingan = jumlahRusakRingan;
        this.jumlahRusakBerat = jumlahRusakBerat;
        this.jumlahHilang = jumlahHilang;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return this.idBarang;
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

    public int getJumlahRusakRingan() { return jumlahRusakRingan; }
    public void setJumlahRusakRingan(int jumlahRusakRingan) { this.jumlahRusakRingan = jumlahRusakRingan; }

    public int getJumlahRusakBerat() { return jumlahRusakBerat; }
    public void setJumlahRusakBerat(int jumlahRusakBerat) { this.jumlahRusakBerat = jumlahRusakBerat; }

    public int getJumlahHilang() { return jumlahHilang; }
    public void setJumlahHilang(int jumlahHilang) { this.jumlahHilang = jumlahHilang; }

    @Override
    public String toString() {
    return this.namaBarang;
}
}
