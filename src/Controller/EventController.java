// [KODE FINAL & LENGKAP - TIDAK ADA SINGKATAN]
package Controller;

import DataBase.DbConnection;
import Model.Event;
import Model.KondisiAset;
import Model.PaketItem;
import Model.RincianKondisiAset;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventController {

    private final CrewController crewController;
    private final ArmadaController armadaController;

    public EventController() {
        this.crewController = new CrewController();
        this.armadaController = new ArmadaController();
    }

    public static class EventSummary {
        private final int id;
        private final String name;
        public EventSummary(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public String toString() { return name; }
    }
    
    public static class UsedAssetInfo {
        private final int logId;
        private final String nama;
        private final int jumlah;
        public UsedAssetInfo(int logId, String nama, int jumlah) { this.logId = logId; this.nama = nama; this.jumlah = jumlah; }
        public int getLogId() { return logId; }
        public String getNama() { return nama; }
        public int getJumlah() { return jumlah; }
    }

    private static class BarangUntukDiproses {
        final int idBarang;
        final String namaBarang;
        final int jumlahDipakai;
        BarangUntukDiproses(int id, String nama, int jumlah) {
            this.idBarang = id; this.namaBarang = nama; this.jumlahDipakai = jumlah;
        }
    }
    
    // --- METODE PUBLIK UNTUK UI ---

    public Map<LocalDate, List<EventSummary>> getEventsForMonth(YearMonth yearMonth) throws SQLException {
        Map<LocalDate, List<EventSummary>> monthEvents = new HashMap<>();
        String sql = "SELECT id_event, tanggal_mulai, nama_event FROM event WHERE tanggal_mulai BETWEEN ? AND ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(yearMonth.atEndOfMonth().atTime(23, 59, 59)));
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    monthEvents.computeIfAbsent(rs.getTimestamp("tanggal_mulai").toLocalDateTime().toLocalDate(), k -> new ArrayList<>())
                               .add(new EventSummary(rs.getInt("id_event"), rs.getString("nama_event")));
                }
            }
        }
        return monthEvents;
    }

    public Event getEventDetailsById(int eventId) throws SQLException {
        String sql = "SELECT e.*, p.id_paket, p.nama_paket " +
                     "FROM event e " +
                     "LEFT JOIN event_paket ep ON e.id_event = ep.id_event " +
                     "LEFT JOIN paket p ON ep.id_paket = p.id_paket " +
                     "WHERE e.id_event = ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Event event = new Event();
                    event.setIdEvent(rs.getInt("id_event"));
                    event.setNamaEvent(rs.getString("nama_event"));
                    event.setTanggalMulai(rs.getTimestamp("tanggal_mulai").toLocalDateTime());
                    event.setTanggalSelesai(rs.getTimestamp("tanggal_selesai").toLocalDateTime());
                    event.setDurasi(rs.getInt("durasi"));
                    event.setLokasi(rs.getString("lokasi"));
                    event.setKeterangan(rs.getString("keterangan"));
                    event.setStatus(rs.getString("status"));

                    // Ambil data paket dan set ke objek Event
                    int idPaket = rs.getInt("id_paket");
                    if (!rs.wasNull()) {
                        String namaPaket = rs.getString("nama_paket");
                        event.setPaket(new PaketItem(idPaket, namaPaket));
                    }
                    return event;
                }
            }
        }
        return null;
    }
    
    public List<PaketItem> getAvailablePaket() throws SQLException {
        List<PaketItem> paketList = new ArrayList<>();
        String sql = "SELECT id_paket, nama_paket FROM paket ORDER BY nama_paket";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paketList.add(new PaketItem(rs.getInt("id_paket"), rs.getString("nama_paket")));
            }
        }
        return paketList;
    }

    public List<Model.Crew> getCrewForEvent(int eventId) throws SQLException {
        List<Model.Crew> crewList = new ArrayList<>();
        // Asumsi: tabel crew memiliki kolom id_crew dan nama_crew
        String sql = "SELECT c.id_crew, c.nama_crew FROM crew c " +
                     "JOIN crew_event ce ON c.id_crew = ce.id_crew " +
                     "WHERE ce.id_event = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Asumsi: Model.Crew memiliki konstruktor atau setter yang sesuai
                Model.Crew crew = new Model.Crew();
                crew.setIdCrew(rs.getInt("id_crew"));
                crew.setNamaCrew(rs.getString("nama_crew"));
                crewList.add(crew);
            }
        }
        return crewList;
    }

    /**
     * [TAMBAHKAN METHOD INI]
     * Mengambil daftar armada yang digunakan untuk event tertentu.
     */
    public List<Model.Armada> getArmadaForEvent(int eventId) throws SQLException {
        List<Model.Armada> armadaList = new ArrayList<>();
        // Asumsi: tabel armada memiliki kolom id_armada dan nama_armada
        String sql = "SELECT a.id_armada, a.nama_armada FROM armada a " +
                     "JOIN armada_digunakan ad ON a.id_armada = ad.id_armada " +
                     "WHERE ad.id_event = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Asumsi: Model.Armada memiliki konstruktor atau setter yang sesuai
                Model.Armada armada = new Model.Armada();
                armada.setIdArmada(rs.getInt("id_armada"));
                armada.setNamaArmada(rs.getString("nama_armada"));
                armadaList.add(armada);
            }
        }
        return armadaList;
    }

    public void createNewEvent(Event eventData, int paketId, List<Integer> crewIds, List<Integer> armadaIds) throws SQLException, InsufficientStockException {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);
            int idEventBaru = insertEvent(conn, eventData);
            tambahPaketKeEvent(conn, idEventBaru, paketId);
            
            // Alokasi kru dan armada dicatat sebagai 'soft booking'
            assignCrewToEvent(conn, idEventBaru, crewIds);
            assignArmadaToEventLog(conn, idEventBaru, armadaIds, eventData.getTanggalMulai());
            
            // Aktivasi sumber daya (kurangi stok, ubah status) hanya jika 'Berlangsung'
            if ("Berlangsung".equals(eventData.getStatus())) {
                aktivasiSumberDaya(conn, idEventBaru, paketId, armadaIds);
            }
            conn.commit();
        } catch (SQLException | InsufficientStockException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    public boolean updateEvent(Event eventData, List<Integer> newCrewIds, List<Integer> newArmadaIds) throws SQLException, InsufficientStockException {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);
            
            String statusLama = getStatusById(conn, eventData.getId());
            String statusBaru = eventData.getStatus();
            
            doUpdateEvent(conn, eventData);
            
            if (!statusLama.equals("Berlangsung") && statusBaru.equals("Berlangsung")) {
                // Aktivasi: Event dari 'Direncanakan' -> 'Berlangsung'
                int paketId = getPaketIdForEvent(conn, eventData.getId());
                updateCrewAssignments(conn, eventData.getId(), newCrewIds); // Update dulu sebelum aktivasi
                updateArmadaAssignments(conn, eventData.getId(), newArmadaIds, eventData.getTanggalMulai());
                aktivasiSumberDaya(conn, eventData.getId(), paketId, newArmadaIds);

            } else if (statusLama.equals("Berlangsung") && (statusBaru.equals("Dibatalkan") || statusBaru.equals("Direncanakan"))) {
                // Deaktivasi: Event dibatalkan atau kembali ke rencana
                kembalikanSumberDayaOtomatis(conn, eventData.getId());
            } else {
                // Tidak ada perubahan status krusial, hanya update daftar penugasan
                updateCrewAssignments(conn, eventData.getId(), newCrewIds);
                updateArmadaAssignments(conn, eventData.getId(), newArmadaIds, eventData.getTanggalMulai());
            }
            
            conn.commit();
            return true;
        } catch (SQLException | InsufficientStockException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    public void selesaikanEventDenganKondisi(Event eventData, List<Model.RincianKondisiAset> daftarKondisiBarang, List<Model.RincianKondisiAset> daftarKondisiArmada) throws SQLException {
    Connection conn = null;
    try {
        conn = DbConnection.getConnection();
        conn.setAutoCommit(false);

        // Langkah 1: Update detail event utama (nama, lokasi, dll) jika ada perubahan.
        doUpdateEvent(conn, eventData);

        // Langkah 2: Set status event menjadi 'Selesai' secara eksplisit.
        String sqlUpdateStatus = "UPDATE event SET status = 'Selesai', updated_at = NOW() WHERE id_event = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateStatus)) {
            pstmt.setInt(1, eventData.getId());
            pstmt.executeUpdate();
        }

        // Langkah 3: Panggil helper untuk memproses dan mengembalikan aset.
        // Perhatikan bahwa kita mengirim eventData.getId() sebagai parameter eventId.
        kembalikanAsetDenganKondisi(conn, eventData.getId(), daftarKondisiBarang, daftarKondisiArmada);

        // Jika semua langkah berhasil, simpan perubahan ke database.
        conn.commit();
        
    } catch (SQLException e) {
        if (conn != null) conn.rollback();
        throw e;
    } finally {
        closeConnection(conn);
    }
}
    
    public List<UsedAssetInfo> getUsedBarangForEvent(int eventId) {
        List<UsedAssetInfo> assetList = new ArrayList<>();
        String sql = "SELECT bd.id_barang_digunakan, b.nama_barang, bd.jumlah_keluar FROM barang_digunakan bd JOIN barang b ON bd.id_barang = b.id_barang WHERE bd.id_event = ? AND bd.tanggal_masuk IS NULL";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    assetList.add(new UsedAssetInfo(rs.getInt("id_barang_digunakan"), rs.getString("nama_barang"), rs.getInt("jumlah_keluar")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return assetList;
    }

    public List<UsedAssetInfo> getUsedArmadaForEvent(int eventId) {
        List<UsedAssetInfo> assetList = new ArrayList<>();
        String sql = "SELECT ad.id_armada_digunakan, a.nama_armada FROM armada_digunakan ad JOIN armada a ON ad.id_armada = a.id_armada WHERE ad.id_event = ? AND ad.tanggal_masuk IS NULL";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    assetList.add(new UsedAssetInfo(rs.getInt("id_armada_digunakan"), rs.getString("nama_armada"), 1));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return assetList;
    }

    public boolean deleteEvent(int eventId) throws SQLException {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);
            if ("Berlangsung".equals(getStatusById(conn, eventId))) {
                kembalikanSumberDayaOtomatis(conn, eventId);
            }
            String[] deleteQueries = {"DELETE FROM crew_event WHERE id_event = ?", "DELETE FROM armada_digunakan WHERE id_event = ?", "DELETE FROM barang_digunakan WHERE id_event = ?", "DELETE FROM event_paket WHERE id_event = ?", "DELETE FROM event WHERE id_event = ?"};
            for (String sql : deleteQueries) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, eventId);
                    stmt.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    // --- METODE PRIVATE HELPER ---

    private int insertEvent(Connection conn, Event event) throws SQLException {
        String sql = "INSERT INTO event (nama_event, tanggal_mulai, tanggal_selesai, durasi, lokasi, keterangan, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getNamaEvent());
            stmt.setTimestamp(2, Timestamp.valueOf(event.getTanggalMulai()));
            stmt.setTimestamp(3, Timestamp.valueOf(event.getTanggalSelesai()));
            stmt.setInt(4, event.getDurasi());
            stmt.setString(5, event.getLokasi());
            stmt.setString(6, event.getKeterangan());
            stmt.setString(7, event.getStatus());
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
            }
            throw new SQLException("Gagal menyimpan event, tidak ada ID yang dihasilkan.");
        }
    }

    private void tambahPaketKeEvent(Connection conn, int eventId, int paketId) throws SQLException {
        String sql = "INSERT INTO event_paket (id_event, id_paket) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, paketId);
            if(stmt.executeUpdate() == 0) throw new SQLException("Gagal menambahkan paket ke event.");
        }
    }
    
    private void assignCrewToEvent(Connection conn, int eventId, List<Integer> crewIds) throws SQLException {
        if (crewIds == null || crewIds.isEmpty()) return;
        String sql = "INSERT INTO crew_event (id_event, id_crew) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Integer crewId : crewIds) {
                pstmt.setInt(1, eventId);
                pstmt.setInt(2, crewId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void assignArmadaToEventLog(Connection conn, int eventId, List<Integer> armadaIds, LocalDateTime tanggalKeluar) throws SQLException {
        if (armadaIds == null || armadaIds.isEmpty()) return;
        String sql = "INSERT INTO armada_digunakan (id_event, id_armada, tanggal_keluar) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Integer armadaId : armadaIds) {
                pstmt.setInt(1, eventId);
                pstmt.setInt(2, armadaId);
                pstmt.setTimestamp(3, Timestamp.valueOf(tanggalKeluar));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private void aktivasiSumberDaya(Connection conn, int eventId, int idPaket, List<Integer> armadaIds) throws SQLException, InsufficientStockException {
        // 1. Aktivasi Barang
        String queryBarangPaket = "SELECT pb.id_barang, b.nama_barang, pb.jumlah FROM paket_barang pb JOIN barang b ON pb.id_barang = b.id_barang WHERE pb.id_paket = ?";
        List<BarangUntukDiproses> daftarBarang = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(queryBarangPaket)) {
            stmt.setInt(1, idPaket);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                daftarBarang.add(new BarangUntukDiproses(rs.getInt("id_barang"), rs.getString("nama_barang"), rs.getInt("jumlah")));
            }
        }
        if (!daftarBarang.isEmpty()) {
            for (BarangUntukDiproses barang : daftarBarang) {
                cekStokCukupDenganLock(conn, barang.idBarang, barang.jumlahDipakai, barang.namaBarang);
                String updateStokSql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia - ? WHERE id_barang = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStokSql)) {
                    updateStmt.setInt(1, barang.jumlahDipakai);
                    updateStmt.setInt(2, barang.idBarang);
                    if (updateStmt.executeUpdate() == 0) throw new SQLException("Gagal mengurangi stok untuk " + barang.namaBarang);
                }
                String insertLogSql = "INSERT INTO barang_digunakan (id_event, id_barang, jumlah_keluar, tanggal_keluar) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertLogSql)) {
                    insertStmt.setInt(1, eventId);
                    insertStmt.setInt(2, barang.idBarang);
                    insertStmt.setInt(3, barang.jumlahDipakai);
                    insertStmt.executeUpdate();
                }
            }
        }
        
        // 2. Aktivasi Armada
        if (armadaIds != null && !armadaIds.isEmpty()) {
            String sqlUpdateStatus = "UPDATE armada SET status = 'Digunakan' WHERE id_armada = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateStatus)) {
                for (Integer armadaId : armadaIds) {
                    pstmt.setInt(1, armadaId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    private void kembalikanSumberDayaOtomatis(Connection conn, int eventId) throws SQLException {
    // Buat list dengan model baru: RincianKondisiAset
    List<RincianKondisiAset> barangBaik = new ArrayList<>();
    for(UsedAssetInfo barang : getUsedBarangForEvent(eventId)) {
        // Buat satu objek rincian untuk setiap grup barang
        RincianKondisiAset rincian = new RincianKondisiAset(barang.getLogId());
        // Set semua kuantitas dalam kondisi "Baik"
        rincian.setJumlahUntukKondisi("Baik", barang.getJumlah());
        barangBaik.add(rincian);
    }
    
    // Lakukan hal yang sama untuk armada
    List<RincianKondisiAset> armadaBaik = new ArrayList<>();
    for(UsedAssetInfo armada : getUsedArmadaForEvent(eventId)) {
        RincianKondisiAset rincian = new RincianKondisiAset(armada.getLogId());
        rincian.setJumlahUntukKondisi("Baik", 1); // Armada selalu 1
        armadaBaik.add(rincian);
    }
    
    // Panggil helper dengan parameter yang sudah benar
    kembalikanAsetDenganKondisi(conn, eventId, barangBaik, armadaBaik);
}

    // Ganti seluruh isi method ini di EventController.java

private void kembalikanAsetDenganKondisi(Connection conn, int eventId, List<RincianKondisiAset> daftarKondisiBarang, List<RincianKondisiAset> daftarKondisiArmada) throws SQLException {
    // [FIX] Hapus penugasan kru menggunakan eventId yang sekarang sudah ada
    String sqlDeleteCrew = "DELETE FROM crew_event WHERE id_event = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteCrew)) {
        pstmt.setInt(1, eventId);
        pstmt.executeUpdate();
    }

    // Proses Barang dengan Rincian Kuantitas
    for (RincianKondisiAset rincian : daftarKondisiBarang) {
        String getInfoSql = "SELECT id_barang FROM barang_digunakan WHERE id_barang_digunakan = ?";
        try (PreparedStatement getStmt = conn.prepareStatement(getInfoSql)) {
            getStmt.setInt(1, rincian.getLogId());
            ResultSet rs = getStmt.executeQuery();
            if (rs.next()) {
                int idBarang = rs.getInt("id_barang");

                // [PERBAIKAN] Ekstrak setiap jumlah kondisi ke dalam variabel terpisah untuk kejelasan
                int jumlahBaik = rincian.getJumlahUntukKondisi("Baik");
                int jumlahRusakRingan = rincian.getJumlahUntukKondisi("Rusak Ringan");
                int jumlahRusakBerat = rincian.getJumlahUntukKondisi("Rusak Berat");
                int jumlahHilang = rincian.getJumlahUntukKondisi("Hilang");

                String sqlUpdateBarang = """
                    UPDATE barang
                    SET
                        jumlah_tersedia = jumlah_tersedia + ?,
                        jumlah_rusak_ringan = jumlah_rusak_ringan + ?,
                        jumlah_rusak_berat = jumlah_rusak_berat + ?,
                        jumlah_hilang = jumlah_hilang + ?
                    WHERE id_barang = ?
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateBarang)) {
                    // [PERBAIKAN] Gunakan variabel yang sudah diekstrak
                    pstmt.setInt(1, jumlahBaik);
                    pstmt.setInt(2, jumlahRusakRingan);
                    pstmt.setInt(3, jumlahRusakBerat);
                    pstmt.setInt(4, jumlahHilang);
                    pstmt.setInt(5, idBarang);
                    pstmt.executeUpdate();
                }

                // Bagian ini untuk memperbarui log, sudah benar
                StringBuilder summary = new StringBuilder();
                for (Map.Entry<String, Integer> entry : rincian.getRincianJumlah().entrySet()) {
                    if (entry.getValue() > 0) {
                        if (summary.length() > 0) summary.append(", ");
                        summary.append(entry.getKey()).append(": ").append(entry.getValue());
                    }
                }

                String sqlUpdateLog = "UPDATE barang_digunakan SET tanggal_masuk = NOW(), kondisi_setelah_masuk = ? WHERE id_barang_digunakan = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateLog)) {
                    pstmt.setString(1, summary.toString());
                    pstmt.setInt(2, rincian.getLogId());
                    pstmt.executeUpdate();
                }
            }
        }
    }

    // Proses Armada (bagian ini tampaknya sudah benar)
    for (RincianKondisiAset rincian : daftarKondisiArmada) {
        String kondisiArmada = rincian.getRincianJumlah().keySet().stream().findFirst().orElse("Baik");
        String getArmadaIdSql = "SELECT id_armada FROM armada_digunakan WHERE id_armada_digunakan = ?";
        try (PreparedStatement getStmt = conn.prepareStatement(getArmadaIdSql)) {
           getStmt.setInt(1, rincian.getLogId());
           ResultSet rs = getStmt.executeQuery();
           if(rs.next()) {
               int idArmada = rs.getInt("id_armada");
               String sqlUpdateLog = "UPDATE armada_digunakan SET tanggal_masuk = NOW(), kondisi_setelah_masuk = ? WHERE id_armada_digunakan = ?";
               try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateLog)) {
                   pstmt.setString(1, kondisiArmada);
                   pstmt.setInt(2, rincian.getLogId());
                   pstmt.executeUpdate();
               }
               String statusArmadaBaru = "Baik".equals(kondisiArmada) ? "Tersedia" : "Perlu Perbaikan";
               String sqlUpdateArmada = "UPDATE armada SET status = ? WHERE id_armada = ?";
               try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateArmada)) {
                   pstmt.setString(1, statusArmadaBaru);
                   pstmt.setInt(2, idArmada);
                   pstmt.executeUpdate();
               }
           }
        }
    }
}
    
    private void updateCrewAssignments(Connection conn, int eventId, List<Integer> newCrewIds) throws SQLException {
        String deleteSql = "DELETE FROM crew_event WHERE id_event = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        }
        assignCrewToEvent(conn, eventId, newCrewIds);
    }
    
    private void updateArmadaAssignments(Connection conn, int eventId, List<Integer> newArmadaIds, LocalDateTime tanggalKeluar) throws SQLException {
        List<Integer> oldArmadaIds = new ArrayList<>();
        String findOldArmada = "SELECT id_armada FROM armada_digunakan WHERE id_event = ? AND tanggal_masuk IS NULL";
        try(PreparedStatement pstmt = conn.prepareStatement(findOldArmada)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) oldArmadaIds.add(rs.getInt("id_armada"));
        }
        
        if(!oldArmadaIds.isEmpty()){
            String updateStatusSql = "UPDATE armada SET status = 'Tersedia' WHERE id_armada = ?";
            try(PreparedStatement pstmt = conn.prepareStatement(updateStatusSql)) {
                for(Integer oldId : oldArmadaIds) {
                     pstmt.setInt(1, oldId);
                     pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }

        String deleteSql = "DELETE FROM armada_digunakan WHERE id_event = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        }
        
        assignArmadaToEventLog(conn, eventId, newArmadaIds, tanggalKeluar);
    }
    
    private String getStatusById(Connection conn, int eventId) throws SQLException {
        String sql = "SELECT status FROM event WHERE id_event = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("status");
            throw new SQLException("Event dengan ID " + eventId + " tidak ditemukan.");
        }
    }

    private int getPaketIdForEvent(Connection conn, int eventId) throws SQLException {
        String sql = "SELECT id_paket FROM event_paket WHERE id_event = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_paket");
            throw new SQLException("Paket untuk event ID " + eventId + " tidak ditemukan.");
        }
    }

    private boolean doUpdateEvent(Connection conn, Event event) throws SQLException {
        String sql = "UPDATE event SET nama_event=?, tanggal_mulai=?, tanggal_selesai=?, durasi=?, lokasi=?, keterangan=?, status=?, updated_at=NOW() WHERE id_event=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getNamaEvent());
            stmt.setTimestamp(2, Timestamp.valueOf(event.getTanggalMulai()));
            stmt.setTimestamp(3, Timestamp.valueOf(event.getTanggalSelesai()));
            stmt.setInt(4, event.getDurasi());
            stmt.setString(5, event.getLokasi());
            stmt.setString(6, event.getKeterangan());
            stmt.setString(7, event.getStatus());
            stmt.setInt(8, event.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void cekStokCukupDenganLock(Connection conn, int idBarang, int jumlahDibutuhkan, String namaBarang) throws SQLException, InsufficientStockException {
        String sql = "SELECT jumlah_tersedia FROM barang WHERE id_barang = ? FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBarang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int stokTersedia = rs.getInt("jumlah_tersedia");
                if (stokTersedia < jumlahDibutuhkan) {
                    throw new InsufficientStockException(idBarang, namaBarang, jumlahDibutuhkan, stokTersedia);
                }
            } else {
                throw new SQLException("Barang '" + namaBarang + "' tidak ditemukan.");
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}