package Model;

public class ItemDalamPaket {
    private Barang barang;
    private int jumlah;

    public ItemDalamPaket(Barang barang, int jumlah) {
        this.barang = barang;
        this.jumlah = jumlah;
    }

    public Barang getBarang() { return barang; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    // Metode ini akan membuat format tampilan "10x Speaker" di JList
    @Override
    public String toString() {
        return jumlah + "x " + barang.getNamaBarang();
    }
}