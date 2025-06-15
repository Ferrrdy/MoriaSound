package Controller;

import DataBase.DbConnection;
import Model.Armada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola data Armada.
 * Versi ini terhubung langsung ke database.
 */
public class ArmadaController {

    /**
     * Mengambil semua armada yang berstatus 'Tersedia' dari database.
     * Digunakan untuk mengisi daftar pilihan di form event.
     * @return List dari objek Armada.
     * @throws SQLException jika terjadi error database.
     */
    public List<Armada> getAllAvailableArmada() throws SQLException {
        List<Armada> armadaList = new ArrayList<>();
        String sql = "SELECT id_armada, nama_armada, status FROM armada WHERE status = 'Tersedia' ORDER BY nama_armada";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Armada armada = new Armada(
                    rs.getInt("id_armada"),
                    rs.getString("nama_armada")
                );
                armada.setStatus(rs.getString("status"));
                armadaList.add(armada);
            }
        }
        return armadaList;
    }

    /**
     * [BARU] Mengambil ID armada yang sedang digunakan untuk sebuah event.
     * @param eventId ID dari event.
     * @return List dari Integer yang berisi ID armada.
     * @throws SQLException jika terjadi error database.
     */
    public List<Integer> getUsedArmadaIds(int eventId) throws SQLException {
        List<Integer> usedIds = new ArrayList<>();
        // Mengambil dari tabel log armada_digunakan
        String sql = "SELECT id_armada FROM armada_digunakan WHERE id_event = ? AND tanggal_masuk IS NULL";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                usedIds.add(rs.getInt("id_armada"));
            }
        }
        return usedIds;
    }

    // Metode CRUD lainnya (jika Anda memiliki fitur manajemen armada terpisah)
    
    public boolean tambahArmada(String namaArmada, String status) throws SQLException {
        String sql = "INSERT INTO armada (nama_armada, status) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaArmada);
            pstmt.setString(2, status);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean perbaruiArmada(int idArmada, String namaArmadaBaru, String statusBaru) throws SQLException {
        String sql = "UPDATE armada SET nama_armada = ?, status = ? WHERE id_armada = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaArmadaBaru);
            pstmt.setString(2, statusBaru);
            pstmt.setInt(3, idArmada);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean hapusArmada(int idArmada) throws SQLException {
        String sql = "DELETE FROM armada WHERE id_armada = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idArmada);
            return pstmt.executeUpdate() > 0;
        }
    }
}