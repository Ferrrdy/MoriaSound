package Controller;

import Model.GajiModel;
import DataBase.DbConnection; // Pastikan DbConnection ada di package DataBase

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Untuk created_at dan updated_at dari DB (DATETIME di MySQL)
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Penting jika GajiModel menggunakan java.util.Date untuk tanggal

public class GajiController {

    public boolean addGaji(GajiModel gaji) {
        // Menggunakan nama tabel "gaji"
        // created_at dan updated_at akan diisi oleh default database (ON UPDATE CURRENT_TIMESTAMP)
        String sql = "INSERT INTO gaji (id_crew, tanggal_gaji, jumlah_gaji, bonus, nomor_rekening, tanggal_pembayaran, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // id_crew adalah INT(11)
            pstmt.setInt(1, gaji.getId_crew());

            // tanggal_gaji adalah DATE
            if (gaji.getTanggal_gaji() != null) {
                pstmt.setDate(2, new java.sql.Date(gaji.getTanggal_gaji().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.DATE);
            }
            
            // jumlah_gaji adalah DECIMAL(12,2)
            pstmt.setDouble(3, gaji.getJumlah_gaji());
            
            // bonus adalah DECIMAL(12,2)
            pstmt.setDouble(4, gaji.getBonus());
            
            // nomor_rekening adalah VARCHAR(20)
            pstmt.setString(5, gaji.getNomor_rekening());
            
            // tanggal_pembayaran adalah DATETIME
            if (gaji.getTanggal_pembayaran() != null) {
                pstmt.setTimestamp(6, new java.sql.Timestamp(gaji.getTanggal_pembayaran().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.TIMESTAMP);
            }
            
            // keterangan adalah VARCHAR(255)
            pstmt.setString(7, gaji.getKeterangan());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan gaji: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<GajiModel> getAllGaji() {
        List<GajiModel> daftarGaji = new ArrayList<>();
        // Menggunakan nama tabel "gaji"
        String sql = "SELECT * FROM gaji ORDER BY created_at DESC";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GajiModel gaji = new GajiModel();
                gaji.setId_gaji(rs.getInt("id_gaji"));
                gaji.setId_crew(rs.getInt("id_crew")); // INT(11)
                gaji.setTanggal_gaji(rs.getDate("tanggal_gaji")); // DATE
                gaji.setJumlah_gaji(rs.getDouble("jumlah_gaji")); // DECIMAL(12,2)
                gaji.setBonus(rs.getDouble("bonus")); // DECIMAL(12,2)
                gaji.setNomor_rekening(rs.getString("nomor_rekening")); // VARCHAR(20)
                gaji.setTanggal_pembayaran(rs.getTimestamp("tanggal_pembayaran")); // DATETIME
                gaji.setKeterangan(rs.getString("keterangan")); // VARCHAR(255)
                gaji.setCreated_at(rs.getTimestamp("created_at")); // DATETIME
                gaji.setUpdated_at(rs.getTimestamp("updated_at")); // DATETIME
                daftarGaji.add(gaji);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua data gaji: " + e.getMessage());
            e.printStackTrace();
        }
        return daftarGaji;
    }

    public GajiModel getGajiById(int idGaji) {
        // Menggunakan nama tabel "gaji"
        String sql = "SELECT * FROM gaji WHERE id_gaji = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idGaji);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    GajiModel gaji = new GajiModel();
                    gaji.setId_gaji(rs.getInt("id_gaji"));
                    gaji.setId_crew(rs.getInt("id_crew")); // INT(11)
                    gaji.setTanggal_gaji(rs.getDate("tanggal_gaji")); // DATE
                    gaji.setJumlah_gaji(rs.getDouble("jumlah_gaji")); // DECIMAL(12,2)
                    gaji.setBonus(rs.getDouble("bonus")); // DECIMAL(12,2)
                    gaji.setNomor_rekening(rs.getString("nomor_rekening")); // VARCHAR(20)
                    gaji.setTanggal_pembayaran(rs.getTimestamp("tanggal_pembayaran")); // DATETIME
                    gaji.setKeterangan(rs.getString("keterangan")); // VARCHAR(255)
                    gaji.setCreated_at(rs.getTimestamp("created_at")); // DATETIME
                    gaji.setUpdated_at(rs.getTimestamp("updated_at")); // DATETIME
                    return gaji;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil gaji by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateGaji(GajiModel gaji) {
        // Menggunakan nama tabel "gaji"
        // updated_at = NOW() sudah benar untuk memperbarui timestamp secara otomatis di DB
        String sql = "UPDATE gaji SET id_crew = ?, tanggal_gaji = ?, jumlah_gaji = ?, bonus = ?, nomor_rekening = ?, tanggal_pembayaran = ?, keterangan = ?, updated_at = NOW() WHERE id_gaji = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // id_crew adalah INT(11)
            pstmt.setInt(1, gaji.getId_crew());
            
            // tanggal_gaji adalah DATE
            if (gaji.getTanggal_gaji() != null) {
                pstmt.setDate(2, new java.sql.Date(gaji.getTanggal_gaji().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.DATE);
            }
            
            // jumlah_gaji adalah DECIMAL(12,2)
            pstmt.setDouble(3, gaji.getJumlah_gaji());
            
            // bonus adalah DECIMAL(12,2)
            pstmt.setDouble(4, gaji.getBonus());
            
            // nomor_rekening adalah VARCHAR(20)
            pstmt.setString(5, gaji.getNomor_rekening());
            
            // tanggal_pembayaran adalah DATETIME
            if (gaji.getTanggal_pembayaran() != null) {
                pstmt.setTimestamp(6, new java.sql.Timestamp(gaji.getTanggal_pembayaran().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.TIMESTAMP);
            }
            
            // keterangan adalah VARCHAR(255)
            pstmt.setString(7, gaji.getKeterangan());
            
            // id_gaji (untuk WHERE clause)
            pstmt.setInt(8, gaji.getId_gaji());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate gaji: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGaji(int idGaji) {
        // Menggunakan nama tabel "gaji"
        String sql = "DELETE FROM gaji WHERE id_gaji = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idGaji);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus gaji: " + e.getMessage());
            e.printStackTrace();
            return false;
 }
}
}
