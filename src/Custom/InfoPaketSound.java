package Custom;

public class InfoPaketSound {
    private String idPaket;
    private String namaPaket;
    private String deskripsiSingkat;
    private String daftarItemFormatted;
    private double hargaSewaHarian;

    public InfoPaketSound(String id, String nama, String deskripsi, String itemFormatted, double harga) {
        this.idPaket = id;
        this.namaPaket = nama;
        this.deskripsiSingkat = deskripsi;
        this.daftarItemFormatted = itemFormatted;
        this.hargaSewaHarian = harga;
    }

    // Getters
    public String getIdPaket() { return idPaket; }
    public String getNamaPaket() { return namaPaket; }
    public String getDeskripsiSingkat() { return deskripsiSingkat; }
    public String getDaftarItemFormatted() { return daftarItemFormatted; }
    public double getHargaSewaHarian() { return hargaSewaHarian; }
}