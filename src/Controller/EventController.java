package Controller;

import DataBase.DbConnection;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class EventController {

    public static int insertEvent(String nama, LocalDateTime mulai, LocalDateTime selesai, int durasi, String lokasi, String keterangan, String status) {
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
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // id_event
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan event ke database.");
        }
        return -1;
    }

    public static void tambahPaketKeEvent(int idEvent, int idPaket) {
        String sql = "INSERT INTO event_paket (id_event, id_paket) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.setInt(2, idPaket);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menambahkan paket ke event.");
        }
    }

    public static void kurangiStokBarangDariPaket(int idPaket) {
        String sql = "SELECT id_barang, jumlah FROM paket_barang WHERE id_paket = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPaket);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idBarang = rs.getInt("id_barang");
                int jumlahDipakai = rs.getInt("jumlah");

                if (!cekStokCukup(conn, idBarang, jumlahDipakai)) {
                    JOptionPane.showMessageDialog(null, "Stok barang tidak cukup untuk barang ID: " + idBarang);
                    continue;
                }

                String updateSql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia - ? WHERE id_barang = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, jumlahDipakai);
                    updateStmt.setInt(2, idBarang);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengurangi stok barang dari paket.");
        }
    }

    private static boolean cekStokCukup(Connection conn, int idBarang, int jumlahDibutuhkan) throws SQLException {
        String sql = "SELECT jumlah_tersedia FROM barang WHERE id_barang = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBarang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int tersedia = rs.getInt("jumlah_tersedia");
                return tersedia >= jumlahDibutuhkan;
            }
        }
        return false;
    }
}
