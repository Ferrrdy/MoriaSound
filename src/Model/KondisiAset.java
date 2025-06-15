package Model;

public class KondisiAset {
    private final int id;
    private final String kondisi; // contoh: "Baik", "Rusak", "Hilang"

    public KondisiAset(int id, String kondisi) {
        this.id = id;
        this.kondisi = kondisi;
    }

    public int getId() {
        return id;
    }

    public String getKondisi() {
        return kondisi;
    }
}