package Controller;

import DataBase.DbConnection;
import java.sql.*;
import java.time.LocalDateTime;

public class EventController {

    /**
     * Menyisipkan event baru ke database.
     * @return ID event yang baru dibuat.
     * @throws SQLException jika terjadi kesalahan database atau event gagal disimpan.
     */
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
                        return rs.getInt(1); // id_event
                    } else {
                        throw new SQLException("Gagal mendapatkan ID event yang baru dibuat setelah insert.");
                    }
                }
            } else {
                throw new SQLException("Gagal menyimpan event ke database, tidak ada baris yang terpengaruh.");
            }
        } catch (SQLException e) {
            // Sebaiknya gunakan logger di aplikasi production
            // e.printStackTrace();
            throw new SQLException("Gagal menyimpan event: " + e.getMessage(), e);
        }
    }

    /**
     * Menambahkan hubungan antara event dan paket.
     * @return true jika berhasil, false jika gagal (misalnya, karena unique constraint atau tidak ada baris terpengaruh).
     * @throws SQLException jika terjadi kesalahan database lainnya.
     */
    public static boolean tambahPaketKeEvent(int idEvent, int idPaket) throws SQLException {
        String sql = "INSERT INTO event_paket (id_event, id_paket) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.setInt(2, idPaket);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            // Bisa jadi error karena unique constraint (misal: MySQL error code 1062, PostgreSQL 23505)
            // Jika ingin menangani kasus "sudah ada" secara berbeda, cek e.getErrorCode()
            // e.printStackTrace();
            throw new SQLException("Gagal menambahkan paket ke event: " + e.getMessage(), e);
        }
    }

    /**
     * Mengurangi stok barang yang terkait dengan paket tertentu.
     * Operasi ini bersifat transaksional: semua stok barang dalam paket berhasil dikurangi, atau tidak sama sekali.
     * @return true jika semua stok berhasil dikurangi atau jika paket tidak memiliki barang.
     * @throws SQLException jika terjadi kesalahan database.
     * @throws InsufficientStockException jika stok salah satu barang tidak mencukupi.
     */
    public static boolean kurangiStokBarangDariPaket(int idPaket) throws SQLException, InsufficientStockException {
        String queryBarangPaket = "SELECT pb.id_barang, b.nama_barang, pb.jumlah FROM paket_barang pb JOIN barang b ON pb.id_barang = b.id_barang WHERE pb.id_paket = ?";
        String updateSql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia - ? WHERE id_barang = ?";
        
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            try (PreparedStatement stmtBarangPaket = conn.prepareStatement(queryBarangPaket)) {
                stmtBarangPaket.setInt(1, idPaket);
                ResultSet rs = stmtBarangPaket.executeQuery();

                if (!rs.isBeforeFirst() ) { 
                     // Tidak ada barang dalam paket ini, anggap "sukses" karena tidak ada stok yang perlu dikurangi.
                     conn.commit();
                     return true;
                }

                while (rs.next()) {
                    int idBarang = rs.getInt("id_barang");
                    String namaBarang = rs.getString("nama_barang");
                    int jumlahDipakai = rs.getInt("jumlah");

                    // Cek stok (melempar InsufficientStockException jika kurang)
                    cekStokCukupDenganLock(conn, idBarang, jumlahDipakai, namaBarang);

                    // Kurangi stok
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, jumlahDipakai);
                        updateStmt.setInt(2, idBarang);
                        int affectedRows = updateStmt.executeUpdate();
                        if (affectedRows == 0) {
                            throw new SQLException("Gagal mengurangi stok untuk barang '" + namaBarang + "' (ID: " + idBarang + "). Barang tidak ditemukan saat update.");
                        }
                    }
                }
            }
            conn.commit(); // Semua operasi berhasil, commit transaksi
            return true;

        } catch (SQLException | InsufficientStockException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException exRollback) {
                    e.addSuppressed(exRollback); // Tambahkan error rollback ke exception utama
                }
            }
            // e.printStackTrace();
            throw e; 
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    /**
     * Helper internal untuk cek stok dalam konteks transaksi yang sudah ada dengan row-level lock.
     * @throws SQLException jika barang tidak ditemukan atau error DB lain.
     * @throws InsufficientStockException jika stok tidak cukup.
     */
    private static void cekStokCukupDenganLock(Connection conn, int idBarang, int jumlahDibutuhkan, String namaBarang) throws SQLException, InsufficientStockException {
        // FOR UPDATE untuk locking (sesuaikan dengan DB Anda jika perlu, mis. SQL Server pakai hint lain)
        String sql = "SELECT jumlah_tersedia FROM barang WHERE id_barang = ? FOR UPDATE"; 
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBarang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int tersedia = rs.getInt("jumlah_tersedia");
                if (tersedia < jumlahDibutuhkan) {
                     throw new InsufficientStockException(idBarang, namaBarang, jumlahDibutuhkan, tersedia);
                }
                // Jika cukup, tidak melakukan apa-apa, hanya memastikan lock.
            } else {
                throw new SQLException("Barang '" + namaBarang + "' (ID: " + idBarang + ") tidak ditemukan untuk pengecekan stok.");
            }
        }
    }
}