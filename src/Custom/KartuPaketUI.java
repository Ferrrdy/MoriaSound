package Custom;

import DataBase.DbConnection;
import UI.DaftarPaketBaru;
import UI.FormPaketBaru;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KartuPaketUI extends JPanel {

    private JLabel lblNamaPaket;
    private JTextArea txtDetailIsiPaket;
    private JLabel lblHargaPaket;
    private JButton btnEdit;
    private JButton btnHapus;
    private InfoPaketSound dataInfoPaketSound;
    private DaftarPaketBaru parentFrame; // Referensi ke frame utama

    public KartuPaketUI(DaftarPaketBaru parent, InfoPaketSound infoPaket) {
        this.parentFrame = parent;
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
        add(lblNamaPaket, BorderLayout.NORTH);

        txtDetailIsiPaket = new JTextArea();
        txtDetailIsiPaket.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDetailIsiPaket.setEditable(false);
        JScrollPane scrollPaneDetail = new JScrollPane(txtDetailIsiPaket);
        scrollPaneDetail.setBorder(BorderFactory.createTitledBorder("Isi Paket:"));
        add(scrollPaneDetail, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout(10, 0));
        lblHargaPaket = new JLabel();
        lblHargaPaket.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelBawah.add(lblHargaPaket, BorderLayout.WEST);

        JPanel panelTombolAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        // Styling buttons...
        btnEdit.setBackground(new Color(60, 179, 113));
        btnEdit.setForeground(Color.WHITE);
        btnHapus.setBackground(new Color(220, 20, 60));
        btnHapus.setForeground(Color.WHITE);

        panelTombolAksi.add(btnEdit);
        panelTombolAksi.add(btnHapus);
        panelBawah.add(panelTombolAksi, BorderLayout.EAST);
        add(panelBawah, BorderLayout.SOUTH);

        btnEdit.addActionListener(e -> {
            FormPaketBaru formEdit = new FormPaketBaru(parentFrame, dataInfoPaketSound.getIdPaket());
            formEdit.setVisible(true);
        });

        btnHapus.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Anda yakin ingin menghapus paket: " + dataInfoPaketSound.getNamaPaket() + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (konfirmasi == JOptionPane.YES_OPTION) {
                hapusPaketDariDB(dataInfoPaketSound.getIdPaket());
            }
        });
    }

    private void setDataToUI() {
        lblNamaPaket.setText(dataInfoPaketSound.getNamaPaket());
        txtDetailIsiPaket.setText(dataInfoPaketSound.getDaftarItemFormatted());
        lblHargaPaket.setText(String.format(java.util.Locale.forLanguageTag("id-ID"), "Rp %,.0f", dataInfoPaketSound.getHargaSewaHarian()));
    }

    private void hapusPaketDariDB(String idPaket) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtRelasi = conn.prepareStatement("DELETE FROM paket_barang WHERE id_paket = ?")) {
                pstmtRelasi.setString(1, idPaket);
                pstmtRelasi.executeUpdate();
            }

            try (PreparedStatement pstmtPaket = conn.prepareStatement("DELETE FROM paket WHERE id_paket = ?")) {
                pstmtPaket.setString(1, idPaket);
                if (pstmtPaket.executeUpdate() > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Paket berhasil dihapus.");
                    parentFrame.muatDanTampilkanPaketDariUIDenganDB(); // Refresh list
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Paket tidak ditemukan.", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}