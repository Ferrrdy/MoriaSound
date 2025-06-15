package Controller;

import DataBase.DbConnection;
import Model.Crew;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrewController {

    // --- METODE BARU YANG NON-STATIC (INSTANCE METHODS) ---

    public List<Crew> getAllCrewInstance() throws SQLException {
        List<Crew> crews = new ArrayList<>();
        // --- UBAH QUERY SQL: Ambil semua kolom yang relevan dari tabel crew ---
        String sql = "SELECT id_crew, nama_crew, posisi, gaji_bulanan, norek_crew, created_at, updated_at FROM crew ORDER BY nama_crew";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // --- UBAH: Gunakan konstruktor Crew yang lengkap ---
                crews.add(new Crew(
                    rs.getInt("id_crew"),
                    rs.getString("nama_crew"),
                    rs.getString("posisi"),
                    rs.getDouble("gaji_bulanan"), // Ambil data gaji_bulanan
                    rs.getString("norek_crew"), // Ambil data norek_crew
                    // Ambil data timestamp, pastikan tidak null sebelum toLocalDateTime()
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
                ));
            }
        }
        return crews;
    }

    public Crew getCrewByIdInstance(int idCrew) throws SQLException {
        // --- UBAH QUERY SQL: Ambil semua kolom yang relevan dari tabel crew ---
        String sql = "SELECT id_crew, nama_crew, posisi, gaji_bulanan, norek_crew, created_at, updated_at FROM crew WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCrew);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Crew crew = new Crew(
                        rs.getInt("id_crew"),
                        rs.getString("nama_crew"),
                        rs.getString("posisi"),
                        rs.getDouble("gaji_bulanan"), // Ambil data gaji_bulanan
                        rs.getString("norek_crew"), // Ambil data norek_crew
                        // Ambil data timestamp, pastikan tidak null
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
                    );
                    return crew;
                }
            }
        }
        return null;
    }

    public boolean addCrewInstance(Crew crew) throws SQLException {
        String sql = "INSERT INTO crew (nama_crew, posisi, gaji_bulanan, norek_crew) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, crew.getNamaCrew());
            pstmt.setString(2, crew.getPosisi());
            pstmt.setDouble(3, crew.getGajiBulanan());
            pstmt.setString(4, crew.getNorek_crew()); // [DIPERBAIKI] Menggunakan getter yang benar
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateCrewInstance(Crew crew) throws SQLException {
        String sql = "UPDATE crew SET nama_crew = ?, posisi = ?, gaji_bulanan = ?, norek_crew = ? WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, crew.getNamaCrew());
            pstmt.setString(2, crew.getPosisi());
            pstmt.setDouble(3, crew.getGajiBulanan());
            pstmt.setString(4, crew.getNorek_crew()); // [DIPERBAIKI] Menggunakan getter yang benar
            pstmt.setInt(5, crew.getIdCrew());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCrewInstance(int idCrew) throws SQLException {
        String sql = "DELETE FROM crew WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCrew);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Integer> getAssignedCrewIds(int eventId) throws SQLException {
        List<Integer> assignedIds = new ArrayList<>();
        String sql = "SELECT id_crew FROM crew_event WHERE id_event = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                assignedIds.add(rs.getInt("id_crew"));
            }
        }
        return assignedIds;
    }


    // --- METODE STATIC LAMA (UNTUK KOMPATIBILITAS) ---

    @Deprecated
    public static boolean addCrew(Crew crew) {
        try {
            return new CrewController().addCrewInstance(crew);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deprecated
    public static List<Crew> getAllCrew() {
        try {
            return new CrewController().getAllCrewInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Deprecated
    public static Crew getCrewById(int idCrew) {
        try {
            return new CrewController().getCrewByIdInstance(idCrew);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static boolean updateCrew(Crew crew) {
        try {
            return new CrewController().updateCrewInstance(crew);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deprecated
    public static boolean deleteCrew(int idCrew) {
        try {
            return new CrewController().deleteCrewInstance(idCrew);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}