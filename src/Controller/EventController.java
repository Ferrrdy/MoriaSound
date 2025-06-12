package Controller;

import DataBase.DbConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Model.Event;

public class EventController {

    public static int insertEvent(String nama, LocalDateTime mulai, LocalDateTime selesai, int durasi, String lokasi, String keterangan, String status) throws SQLException {
        String sql = "INSERT INTO event (nama_event, tanggal_mulai, tanggal_selesai, durasi, lokasi, keterangan, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nama);
            stmt.setTimestamp(2, Timestamp.valueOf(mulai));
            stmt.setTimestamp(3, Timestamp.valueOf(selesai));
            stmt.setInt(4, durasi);
            stmt.setString(5, lokasi);
            stmt.setString(6, keterangan);
            stmt.setString(7, status);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    } else {
                        throw new SQLException("Gagal mendapatkan ID event yang baru dibuat setelah insert.");
                    }
                }
            } else {
                throw new SQLException("Gagal menyimpan event ke database, tidak ada baris yang terpengaruh.");
            }
        } catch (SQLException e) {
            throw new SQLException("Gagal menyimpan event: " + e.getMessage(), e);
        }
    }

    public static boolean tambahPaketKeEvent(int idEvent, int idPaket) throws SQLException {
        String sql = "INSERT INTO event_paket (id_event, id_paket) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.setInt(2, idPaket);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Gagal menambahkan paket ke event (ID Event: " + idEvent + ", ID Paket: " + idPaket + "): " + e.getMessage(), e);
        }
    }
    
    public boolean deleteEvent(int eventId) {
    Connection conn = null;
    try {
        conn = DbConnection.getConnection();
        conn.setAutoCommit(false); // Mulai transaction
        
        // Hapus data terkait di tabel event_paket terlebih dahulu
        String deleteEventPaketQuery = "DELETE FROM event_paket WHERE id_event = ?";
        try (PreparedStatement stmt1 = conn.prepareStatement(deleteEventPaketQuery)) {
            stmt1.setInt(1, eventId);
            stmt1.executeUpdate();
        }
        
        // Kemudian hapus event
        String deleteEventQuery = "DELETE FROM event WHERE id_event = ?";
        try (PreparedStatement stmt2 = conn.prepareStatement(deleteEventQuery)) {
            stmt2.setInt(1, eventId);
            int rowsAffected = stmt2.executeUpdate();
            
            if (rowsAffected > 0) {
                conn.commit(); // Commit transaction jika berhasil
                return true;
            } else {
                conn.rollback(); // Rollback jika tidak ada data yang terhapus
                return false;
            }
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
        try {
            if (conn != null) {
                conn.rollback(); // Rollback jika terjadi error
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        return false;
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true); // Kembalikan ke auto commit
                conn.close();
            }
        } catch (SQLException closeEx) {
            closeEx.printStackTrace();
        }
    }
}

    public boolean updateEvent(Event event) {
        String query = "UPDATE event SET nama_event = ?, tanggal_mulai = ?, tanggal_selesai = ?, keterangan = ? WHERE id_event = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, event.getNamaEvent());
            stmt.setDate(2, new java.sql.Date(event.getTanggalMulai().getTime()));
            stmt.setDate(3, new java.sql.Date(event.getTanggalSelesai().getTime()));
            stmt.setString(4, event.getKeterangan());
            stmt.setInt(5, event.getIdEvent());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void aktivasiEventDanProsesBarang(int idEvent, int idPaket) throws SQLException, InsufficientStockException {
        String queryBarangPaket = "SELECT pb.id_barang, b.nama_barang, pb.jumlah FROM paket_barang pb JOIN barang b ON pb.id_barang = b.id_barang WHERE pb.id_paket = ?";
        String updateStokSql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia - ? WHERE id_barang = ?";
        String insertBarangDigunakanSql = "INSERT INTO barang_digunakan " +
                                          "(id_event, id_barang, jumlah_keluar, tanggal_keluar, created_at, updated_at) " +
                                          "VALUES (?, ?, ?, NOW(), NOW(), NOW())";
        
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); 

            List<BarangUntukDiproses> daftarBarangUntukDiproses = new ArrayList<>();
            try (PreparedStatement stmtBarangPaket = conn.prepareStatement(queryBarangPaket)) {
                stmtBarangPaket.setInt(1, idPaket);
                ResultSet rs = stmtBarangPaket.executeQuery();
                if (!rs.isBeforeFirst() ) { 
                     conn.commit();
                     System.out.println("Tidak ada barang dalam paket ID: " + idPaket + " untuk event ID: " + idEvent + ". Aktivasi dianggap selesai tanpa proses barang.");
                     return; 
                }
                while (rs.next()) {
                    daftarBarangUntukDiproses.add(new BarangUntukDiproses(
                        rs.getInt("id_barang"),
                        rs.getString("nama_barang"),
                        rs.getInt("jumlah")
                    ));
                }
            } 

            for (BarangUntukDiproses barang : daftarBarangUntukDiproses) {
                cekStokCukupDenganLock(conn, barang.idBarang, barang.jumlahDipakai, barang.namaBarang);
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStokSql)) {
                    updateStmt.setInt(1, barang.jumlahDipakai);
                    updateStmt.setInt(2, barang.idBarang);
                    if (updateStmt.executeUpdate() == 0) {
                        throw new SQLException("Gagal mengurangi stok untuk barang '" + barang.namaBarang + "' (ID: " + barang.idBarang + "). Barang tidak ditemukan saat proses update stok.");
                    }
                }
                try (PreparedStatement insertStmt = conn.prepareStatement(insertBarangDigunakanSql)) {
                    insertStmt.setInt(1, idEvent);
                    insertStmt.setInt(2, barang.idBarang);
                    insertStmt.setInt(3, barang.jumlahDipakai);
                    insertStmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException | InsufficientStockException e) {
            if (conn != null) {
                try {
                    System.err.println("Transaksi di-rollback untuk event ID: " + idEvent + " karena error: " + e.getMessage());
                    conn.rollback(); 
                } catch (SQLException exRollback) {
                    e.addSuppressed(exRollback); 
                    System.err.println("Error saat melakukan rollback: " + exRollback.getMessage());
                }
            }
            throw e; 
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error saat menutup koneksi atau mengembalikan autoCommit: " + e.getMessage());
                }
            }
        }
    }

    private static void cekStokCukupDenganLock(Connection conn, int idBarang, int jumlahDibutuhkan, String namaBarang) throws SQLException, InsufficientStockException {
        String sql = "SELECT jumlah_tersedia FROM barang WHERE id_barang = ? FOR UPDATE"; 
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBarang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int tersedia = rs.getInt("jumlah_tersedia");
                if (tersedia < jumlahDibutuhkan) {
                     throw new InsufficientStockException(idBarang, namaBarang, jumlahDibutuhkan, tersedia);
                }
            } else {
                throw new SQLException("Barang '" + (namaBarang != null ? namaBarang : "ID: "+idBarang) + "' tidak ditemukan dalam database untuk pengecekan stok.");
            }
        }
    }

    public static Map<LocalDate, List<String>> getEventTitlesForMonth(YearMonth yearMonth) throws SQLException {
        Map<LocalDate, List<String>> monthEvents = new HashMap<>();
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        String sql = "SELECT tanggal_mulai, nama_event FROM event " +
                     "WHERE tanggal_mulai >= ? AND tanggal_mulai <= ? ORDER BY tanggal_mulai";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(firstDay.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(lastDay.atTime(23, 59, 59))); 
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate eventDate = rs.getTimestamp("tanggal_mulai").toLocalDateTime().toLocalDate();
                String eventName = rs.getString("nama_event");
                monthEvents.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(eventName);
            }
        } 
        return monthEvents;
    }

    private static class BarangUntukDiproses {
        int idBarang;
        String namaBarang;
        int jumlahDipakai;
        public BarangUntukDiproses(int idBarang, String namaBarang, int jumlahDipakai) {
            this.idBarang = idBarang;
            this.namaBarang = namaBarang;
            this.jumlahDipakai = jumlahDipakai;
        }
    }
}