package Controller;

import DataBase.DbConnection;
import Model.Barang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangController {

    public List<Barang> getAllBarang() throws SQLException {
        List<Barang> daftarBarang = new ArrayList<>();
        String sql = "SELECT * FROM barang ORDER BY nama_barang ASC";
        
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Barang barang = new Barang();
                barang.setIdBarang(rs.getInt("id_barang"));
                barang.setNamaBarang(rs.getString("nama_barang"));
                barang.setIdKategori(rs.getInt("id_kategori"));
                barang.setKondisi(rs.getString("kondisi"));
                barang.setJumlahTotal(rs.getInt("jumlah_total"));
                barang.setJumlahTersedia(rs.getInt("jumlah_tersedia"));
                barang.setJumlahRusakRingan(rs.getInt("jumlah_rusak_ringan"));
                barang.setJumlahRusakBerat(rs.getInt("jumlah_rusak_berat"));
                barang.setJumlahHilang(rs.getInt("jumlah_hilang"));
                daftarBarang.add(barang);
            }
        }
        return daftarBarang;
    }

    public int getIdKategoriByName(String namaKategori) throws SQLException {
        String sql = "SELECT id_kategori FROM kategori WHERE nama_kategori = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaKategori);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_kategori");
            } else {
                return -1;
            }
        }
    }

    public boolean hapusBarang(int idBarang) throws SQLException {
        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBarang);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Barang> getBarangBermasalah() throws SQLException {
        List<Barang> daftarBarang = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE jumlah_rusak_ringan > 0 OR jumlah_rusak_berat > 0 OR jumlah_hilang > 0 ORDER BY nama_barang ASC";
        
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Barang barang = new Barang();
                barang.setIdBarang(rs.getInt("id_barang"));
                barang.setNamaBarang(rs.getString("nama_barang"));
                barang.setIdKategori(rs.getInt("id_kategori"));
                barang.setKondisi(rs.getString("kondisi"));
                barang.setJumlahTotal(rs.getInt("jumlah_total"));
                barang.setJumlahTersedia(rs.getInt("jumlah_tersedia"));
                barang.setJumlahRusakRingan(rs.getInt("jumlah_rusak_ringan"));
                barang.setJumlahRusakBerat(rs.getInt("jumlah_rusak_berat"));
                barang.setJumlahHilang(rs.getInt("jumlah_hilang"));
                daftarBarang.add(barang);
            }
        }
        return daftarBarang;
    }

    public boolean perbaikiBarang(int idBarang, int jumlahBerapaBanyak, String kondisiAsal) throws SQLException {
        String kolomKondisiAsal;
        switch (kondisiAsal) {
            case "Rusak Ringan":
                kolomKondisiAsal = "jumlah_rusak_ringan";
                break;
            case "Rusak Berat":
                kolomKondisiAsal = "jumlah_rusak_berat";
                break;
            default:
                throw new SQLException("Kondisi asal '" + kondisiAsal + "' tidak valid untuk perbaikan.");
        }

        String sql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia + ?, " + kolomKondisiAsal + " = " + kolomKondisiAsal + " - ? WHERE id_barang = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jumlahBerapaBanyak);
            pstmt.setInt(2, jumlahBerapaBanyak);
            pstmt.setInt(3, idBarang);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean hapusBukuBarang(int idBarang, int jumlahBerapaBanyak, String kondisiAsal) throws SQLException {
         String kolomKondisiAsal;
        switch (kondisiAsal) {
            case "Rusak Berat":
                kolomKondisiAsal = "jumlah_rusak_berat";
                break;
            case "Hilang":
                kolomKondisiAsal = "jumlah_hilang";
                break;
            default:
                throw new SQLException("Kondisi '" + kondisiAsal + "' tidak valid untuk hapus buku.");
        }

        String sql = "UPDATE barang SET jumlah_total = jumlah_total - ?, " + kolomKondisiAsal + " = " + kolomKondisiAsal + " - ? WHERE id_barang = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jumlahBerapaBanyak);
            pstmt.setInt(2, jumlahBerapaBanyak);
            pstmt.setInt(3, idBarang);
            return pstmt.executeUpdate() > 0;
        }
    }
}