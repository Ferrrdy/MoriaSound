package Custom;

import DataBase.DbConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class ItemStokUIData_Kartu {
    String idBarang;
    String namaBarang;
    int jumlahDibutuhkan;
    int stokTersedia;

    public ItemStokUIData_Kartu(String id, String nama, int butuh, int ada) {
        this.idBarang = id; this.namaBarang = nama; this.jumlahDibutuhkan = butuh; this.stokTersedia = ada;
    }
}

public class KartuPaketUI extends JPanel {

    private JLabel lblNamaPaket;
    private JTextArea txtDetailIsiPaket;
    private JLabel lblHargaPaket;
    private JButton btnPesan;
    private InfoPaketSound dataInfoPaketSound;

    public KartuPaketUI(InfoPaketSound infoPaket) {
        this.dataInfoPaketSound = infoPaket;
        initComponents();
        setDataToUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        lblNamaPaket = new JLabel();
        lblNamaPaket.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNamaPaket.setHorizontalAlignment(SwingConstants.CENTER);
        lblNamaPaket.setOpaque(true);
        lblNamaPaket.setBackground(new Color(240, 240, 240));
        lblNamaPaket.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        add(lblNamaPaket, BorderLayout.NORTH);

        txtDetailIsiPaket = new JTextArea();
        txtDetailIsiPaket.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDetailIsiPaket.setEditable(false);
        txtDetailIsiPaket.setLineWrap(true);
        txtDetailIsiPaket.setWrapStyleWord(true);
        txtDetailIsiPaket.setOpaque(false);
        JScrollPane scrollPaneDetail = new JScrollPane(txtDetailIsiPaket);
        scrollPaneDetail.setBorder(BorderFactory.createTitledBorder("Isi Paket:"));
        add(scrollPaneDetail, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout(10, 0));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        lblHargaPaket = new JLabel();
        lblHargaPaket.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelBawah.add(lblHargaPaket, BorderLayout.WEST);

        btnPesan = new JButton("Pesan Paket");
        btnPesan.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnPesan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPesan.setBackground(new Color(251, 200, 42));
        btnPesan.setForeground(Color.BLACK);
        btnPesan.setFocusPainted(false);
        btnPesan.setMargin(new Insets(5, 15, 5, 15)); 
        panelBawah.add(btnPesan, BorderLayout.EAST);
        
        add(panelBawah, BorderLayout.SOUTH);

        btnPesan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataInfoPaketSound != null) {
                    int konfirmasi = JOptionPane.showConfirmDialog(
                        KartuPaketUI.this,
                        "Anda yakin ingin memesan paket: " + dataInfoPaketSound.getNamaPaket() + "?",
                        "Konfirmasi Pemesanan",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (konfirmasi == JOptionPane.YES_OPTION) {
                        prosesPemesananPaket(dataInfoPaketSound.getIdPaket());
                    }
                }
            }
        });
    }

    private void setDataToUI() {
        if (dataInfoPaketSound != null) {
            lblNamaPaket.setText(dataInfoPaketSound.getNamaPaket());
            txtDetailIsiPaket.setText(dataInfoPaketSound.getDaftarItemFormatted());
            lblHargaPaket.setText("Rp " + String.format("%,.0f", dataInfoPaketSound.getHargaSewaHarian()));
        } else {
            lblNamaPaket.setText("Nama Paket Tidak Tersedia");
            txtDetailIsiPaket.setText("Detail item tidak tersedia.");
            lblHargaPaket.setText("Harga: -");
        }
    }

    private List<ItemStokUIData_Kartu> getItemDanStokUntukPaketInternal(String idPaket, Connection conn) throws SQLException {
        List<ItemStokUIData_Kartu> items = new ArrayList<>();
        String sql = "SELECT pb.id_barang, b.nama_barang, pb.jumlah AS jumlah_dibutuhkan, b.jumlah_tersedia AS stok_saat_ini " +
                     "FROM paket_barang pb " +
                     "JOIN barang b ON pb.id_barang = b.id_barang " +
                     "WHERE pb.id_paket = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idPaket);
            System.out.println("Executing query getItemDanStok: " + pstmt); 
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ItemStokUIData_Kartu item = new ItemStokUIData_Kartu(
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_dibutuhkan"),
                    rs.getInt("stok_saat_ini")
                );
                items.add(item);
                System.out.println("  Fetched item: " + item.namaBarang + ", Butuh: " + item.jumlahDibutuhkan + ", Ada: " + item.stokTersedia); 
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        if (items.isEmpty()) {
            System.out.println("Tidak ada item ditemukan di paket_barang untuk id_paket: " + idPaket); 
        }
        return items;
    }

    private boolean updateStokBarangInternal(Connection conn, String idBarang, int jumlahDikurangi) throws SQLException {
        String sql = "UPDATE barang SET jumlah_tersedia = jumlah_tersedia - ? WHERE id_barang = ? AND jumlah_tersedia >= ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jumlahDikurangi);
            pstmt.setString(2, idBarang);
            pstmt.setInt(3, jumlahDikurangi);
            System.out.println("Executing query updateStok: " + pstmt); // DEBUG
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("  Update stok for id_barang " + idBarang + ", rows affected: " + rowsAffected); // DEBUG
            return rowsAffected > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    private void prosesPemesananPaket(String idPaketDipesan) {
        Connection conn = null;
        List<ItemStokUIData_Kartu> itemsDalamPaket;

        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal untuk proses pemesanan.", "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            conn.setAutoCommit(false);

            itemsDalamPaket = getItemDanStokUntukPaketInternal(idPaketDipesan, conn);

            if (itemsDalamPaket.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Tidak ditemukan item detail untuk paket ID: " + idPaketDipesan + ".\nMohon periksa data di tabel paket_barang.", "Error Data Paket", JOptionPane.ERROR_MESSAGE);
                 conn.rollback(); 
                 return;
            }

            boolean semuaStokCukup = true;
            StringBuilder pesanStokKurang = new StringBuilder("Pemesanan gagal. Stok tidak mencukupi untuk:\n");
            for (ItemStokUIData_Kartu item : itemsDalamPaket) {
                if (item.stokTersedia < item.jumlahDibutuhkan) {
                    semuaStokCukup = false;
                    pesanStokKurang.append("- ").append(item.namaBarang)
                                   .append(" (butuh: ").append(item.jumlahDibutuhkan)
                                   .append(", tersedia: ").append(item.stokTersedia).append(")\n");
                }
            }

            if (semuaStokCukup) {
                boolean semuaUpdateBerhasil = true;
                for (ItemStokUIData_Kartu item : itemsDalamPaket) {
                    boolean berhasilUpdate = updateStokBarangInternal(conn, item.idBarang, item.jumlahDibutuhkan);
                    if (!berhasilUpdate) {
                        semuaUpdateBerhasil = false;
                        System.err.println("Gagal update stok untuk barang ID: " + item.idBarang + " saat memesan paket ID: " + idPaketDipesan + ". Stok mungkin tidak cukup pada saat update.");
                        break; 
                    }
                }

                if (semuaUpdateBerhasil) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Pemesanan paket '" + dataInfoPaketSound.getNamaPaket() + "' BERHASIL! Stok telah diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui sebagian stok (kemungkinan stok berubah saat proses). Pemesanan dibatalkan sepenuhnya.", "Error Update Stok", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, pesanStokKurang.toString(), "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException exSQL) {
            try { if (conn != null) conn.rollback(); } catch (SQLException exR) { System.err.println("Rollback failed: " + exR.getMessage()); exR.printStackTrace(); }
            exSQL.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database saat proses pemesanan:\n" + exSQL.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { 
            try { if (conn != null) conn.rollback(); } catch (SQLException exR) { System.err.println("Rollback failed: " + exR.getMessage()); exR.printStackTrace();}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan umum saat proses pemesanan:\n" + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException exClose) {
                exClose.printStackTrace();
                System.err.println("Error saat menutup koneksi: " + exClose.getMessage());
            }
        }
    }
    
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Kartu Paket UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            InfoPaketSound contohPaket = new InfoPaketSound(
                "P00X", "Contoh Paket Uji Coba", "Ini adalah deskripsi untuk paket uji coba.",
                "- 1x Speaker Monitor\n- 1x Mic Kondenser\n- Kabel XLR", 350000
            );
            KartuPaketUI kartu = new KartuPaketUI(contohPaket);
            
            JPanel panelMain = new JPanel(new BorderLayout());
            panelMain.add(kartu, BorderLayout.CENTER);
            panelMain.setBorder(BorderFactory.createEmptyBorder(20,20,20,20)); 
            
            frame.add(panelMain);
            frame.pack(); 
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}