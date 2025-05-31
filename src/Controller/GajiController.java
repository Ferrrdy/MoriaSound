package Controller; // Sesuai struktur folder Anda

import Model.GajiModel;
import DataBase.DbConnection; // Pastikan DbConnection ada di package DataBase

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Untuk created_at dan updated_at dari DB
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Untuk konversi ke java.util.Date jika diperlukan

public class GajiController {

    public boolean addGaji(GajiModel gaji) {
        String sql = "INSERT INTO gaji (id_crew, tanggal_gaji, jumlah_gaji, bonus, tanggal_pembayaran, keterangan, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gaji.getId_crew());
            // Konversi java.util.Date ke java.sql.Date
            if (gaji.getTanggal_gaji() != null) {
                pstmt.setDate(2, new java.sql.Date(gaji.getTanggal_gaji().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.DATE);
            }
            pstmt.setDouble(3, gaji.getJumlah_gaji());
            pstmt.setDouble(4, gaji.getBonus());
            if (gaji.getTanggal_pembayaran() != null) {
                pstmt.setDate(5, new java.sql.Date(gaji.getTanggal_pembayaran().getTime()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }
            pstmt.setString(6, gaji.getKeterangan());

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
        String sql = "SELECT * FROM gaji_crew ORDER BY id_gaji ASC";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GajiModel gaji = new GajiModel();
                gaji.setId_gaji(rs.getInt("id_gaji"));
                gaji.setId_crew(rs.getString("id_crew"));
                gaji.setTanggal_gaji(rs.getDate("tanggal_gaji")); // Hasilnya java.sql.Date, bisa langsung di-set jika field di model adalah java.util.Date
                gaji.setJumlah_gaji(rs.getDouble("jumlah_gaji"));
                gaji.setBonus(rs.getDouble("bonus"));
                gaji.setTanggal_pembayaran(rs.getDate("tanggal_pembayaran"));
                gaji.setKeterangan(rs.getString("keterangan"));
                gaji.setCreated_at(rs.getTimestamp("created_at")); // Hasilnya java.sql.Timestamp
                gaji.setUpdated_at(rs.getTimestamp("updated_at"));
                daftarGaji.add(gaji);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua data gaji: " + e.getMessage());
            e.printStackTrace();
        }
        return daftarGaji;
    }

    public GajiModel getGajiById(int idGaji) {
        String sql = "SELECT * FROM gaji_crew WHERE id_gaji = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idGaji);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    GajiModel gaji = new GajiModel();
                    gaji.setId_gaji(rs.getInt("id_gaji"));
                    gaji.setId_crew(rs.getString("id_crew"));
                    gaji.setTanggal_gaji(rs.getDate("tanggal_gaji"));
                    gaji.setJumlah_gaji(rs.getDouble("jumlah_gaji"));
                    gaji.setBonus(rs.getDouble("bonus"));
                    gaji.setTanggal_pembayaran(rs.getDate("tanggal_pembayaran"));
                    gaji.setKeterangan(rs.getString("keterangan"));
                    gaji.setCreated_at(rs.getTimestamp("created_at"));
                    gaji.setUpdated_at(rs.getTimestamp("updated_at"));
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
        String sql = "UPDATE gaji_crew SET id_crew = ?, tanggal_gaji = ?, jumlah_gaji = ?, bonus = ?, tanggal_pembayaran = ?, keterangan = ?, updated_at = NOW() WHERE id_gaji = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gaji.getId_crew());
            if (gaji.getTanggal_gaji() != null) {
                pstmt.setDate(2, new java.sql.Date(gaji.getTanggal_gaji().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.DATE);
            }
            pstmt.setDouble(3, gaji.getJumlah_gaji());
            pstmt.setDouble(4, gaji.getBonus());
             if (gaji.getTanggal_pembayaran() != null) {
                pstmt.setDate(5, new java.sql.Date(gaji.getTanggal_pembayaran().getTime()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }
            pstmt.setString(6, gaji.getKeterangan());
            pstmt.setInt(7, gaji.getId_gaji());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate gaji: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGaji(int idGaji) {
        String sql = "DELETE FROM gaji_crew WHERE id_gaji = ?";
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