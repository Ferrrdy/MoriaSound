package Model;

public class PaketItem {
    private final int id;
    private final String nama;

    public PaketItem(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }

    @Override
    public String toString() {
        return nama;
    }
}