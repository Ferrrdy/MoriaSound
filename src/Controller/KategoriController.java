package Controller;

import DataBase.DbConnection;
import Model.Kategori;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriController {

    public List<Kategori> getAllKategori() throws SQLException {
        List<Kategori> daftarKategori = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori ASC";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kategori kategori = new Kategori();
                kategori.setIdKategori(rs.getInt("id_kategori"));
                kategori.setNamaKategori(rs.getString("nama_kategori"));
                daftarKategori.add(kategori);
            }
        }
        return daftarKategori;
    }
}