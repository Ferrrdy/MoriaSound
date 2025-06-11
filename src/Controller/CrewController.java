package Controller;

import DataBase.DbConnection;
import Model.Crew;

// Tidak perlu import BigDecimal lagi
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CrewController {

    // Metode untuk menambahkan crew baru ke database
    public static boolean addCrew(Crew crew) {
        // Query ini sudah benar, membiarkan DB menangani timestamp
        String sql = "INSERT INTO crew (nama_crew, posisi, gaji_bulanan, norek_crew) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, crew.getNamaCrew());
            pstmt.setString(2, crew.getPosisi());
            // DIKEMBALIKAN: Menggunakan setDouble sesuai model asli Anda
            pstmt.setDouble(3, crew.getGajiBulanan()); 
            pstmt.setString(4, crew.getNorek_crew());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        crew.setIdCrew(generatedKeys.getInt(1));
                        System.out.println("Crew added successfully: " + crew.getNamaCrew());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding crew: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Metode untuk mendapatkan semua kru dari database
    public static List<Crew> getAllCrew() {
        List<Crew> crews = new ArrayList<>();
        // Query ini sudah benar, mengambil semua kolom yang diperlukan
        String sql = "SELECT id_crew, nama_crew, posisi, gaji_bulanan, norek_crew, created_at, updated_at FROM crew";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_crew");
                String nama = rs.getString("nama_crew");
                String posisi = rs.getString("posisi");
                // DIKEMBALIKAN: Menggunakan getDouble sesuai model asli Anda
                double gaji = rs.getDouble("gaji_bulanan"); 
                String norek = rs.getString("norek_crew");

                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                
                // Gunakan konstruktor lengkap dari model asli Anda
                crews.add(new Crew(id, nama, posisi, gaji, norek, createdAt, updatedAt));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all crews: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return crews;
    }
    
    // Metode getCrewById juga disesuaikan
     public static Crew getCrewById(int idCrew) {
        String sql = "SELECT id_crew, nama_crew, posisi, gaji_bulanan, norek_crew, created_at, updated_at FROM crew WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCrew);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_crew");
                    String nama = rs.getString("nama_crew");
                    String posisi = rs.getString("posisi");
                    // DIKEMBALIKAN: Menggunakan getDouble sesuai model asli Anda
                    double gaji = rs.getDouble("gaji_bulanan"); 
                    String norek = rs.getString("norek_crew");

                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                    
                    return new Crew(id, nama, posisi, gaji, norek, createdAt, updatedAt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching crew by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Metode untuk memperbarui data kru di database
    public static boolean updateCrew(Crew crew) {
        // Query ini sudah benar, membiarkan DB menangani timestamp
        String sql = "UPDATE crew SET nama_crew = ?, posisi = ?, gaji_bulanan = ?, norek_crew = ? WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, crew.getNamaCrew());
            pstmt.setString(2, crew.getPosisi());
            // DIKEMBALIKAN: Menggunakan setDouble sesuai model asli Anda
            pstmt.setDouble(3, crew.getGajiBulanan());
            pstmt.setString(4, crew.getNorek_crew());
            pstmt.setInt(5, crew.getIdCrew());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Crew updated successfully: " + crew.getNamaCrew());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating crew: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Metode delete tidak perlu diubah
    public static boolean deleteCrew(int idCrew) {
        // ... (kode ini sudah benar dan tidak bergantung pada tipe gaji atau timestamp)
        String sql = "DELETE FROM crew WHERE id_crew = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCrew);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Crew with ID " + idCrew + " deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting crew: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}