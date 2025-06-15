package Model;

import java.util.HashMap;
import java.util.Map;

public class RincianKondisiAset {
    private final int logId; // ID dari tabel barang_digunakan atau armada_digunakan
    private final Map<String, Integer> rincianJumlah; // Peta dari Kondisi -> Jumlah

    public RincianKondisiAset(int logId) {
        this.logId = logId;
        this.rincianJumlah = new HashMap<>();
    }

    public int getLogId() {
        return logId;
    }

    public void setJumlahUntukKondisi(String kondisi, int jumlah) {
        if (jumlah > 0) {
            rincianJumlah.put(kondisi, jumlah);
        } else {
            rincianJumlah.remove(kondisi);
        }
    }

    public Map<String, Integer> getRincianJumlah() {
        return rincianJumlah;
    }
    
    public int getJumlahUntukKondisi(String kondisi) {
        return rincianJumlah.getOrDefault(kondisi, 0);
    }
}