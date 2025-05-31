package UI; // Sesuaikan dengan package Anda

import DataBase.DbConnection; // Pastikan ini adalah path yang benar ke DbConnection Anda
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormKelolaBarang extends JDialog {

    public enum FormMode {
        TAMBAH,
        EDIT
    }

    private FormMode currentMode;
    private int idBarangToEdit = -1;

    private JTextField txtNamaBarang;
    private JTextField txtIdKategori;
    private JTextField txtJumlahTotal;
    private JTextField txtJumlahTersedia; // <-- FIELD BARU DITAMBAHKAN
    private JComboBox<String> cmbKondisi;
    private JButton btnSimpan;
    private JButton btnBatal;

    private boolean DBOperationSuccess = false;

    /**
     * Konstruktor untuk Mode TAMBAH.
     */
    public FormKelolaBarang(Frame parent, boolean modal) {
        super(parent, modal);
        this.currentMode = FormMode.TAMBAH;
        setTitle("Tambah Barang Baru");
        initComponentsUI();
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * Konstruktor untuk Mode EDIT.
     * Sekarang menyertakan jumlahTersedia.
     */
    public FormKelolaBarang(Frame parent, boolean modal, int idBarang, String nama, int idKat, int jumlahTotal, int jumlahTersedia, String kondisi) {
        super(parent, modal);
        this.currentMode = FormMode.EDIT;
        this.idBarangToEdit = idBarang;
        setTitle("Edit Data Barang - ID: " + idBarang);
        initComponentsUI();

        txtNamaBarang.setText(nama);
        txtIdKategori.setText(String.valueOf(idKat));
        txtJumlahTotal.setText(String.valueOf(jumlahTotal));
        txtJumlahTersedia.setText(String.valueOf(jumlahTersedia)); // <-- ISI FIELD BARU
        cmbKondisi.setSelectedItem(kondisi);

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponentsUI() {
        txtNamaBarang = new JTextField(25);
        txtIdKategori = new JTextField(10);
        txtJumlahTotal = new JTextField(10);
        txtJumlahTersedia = new JTextField(10); // <-- INISIALISASI FIELD BARU
        cmbKondisi = new JComboBox<>(new String[]{"Baik", "Rusak Ringan", "Perlu Perbaikan", "Rusak Berat"});

        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int yPos = 0;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Nama Barang:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtNamaBarang, gbc);
        yPos++;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("ID Kategori:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtIdKategori, gbc);
        yPos++;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Jumlah Total:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtJumlahTotal, gbc);
        yPos++;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Jumlah Tersedia:"), gbc); // <-- LABEL UNTUK FIELD BARU
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtJumlahTersedia, gbc); // <-- TAMBAHKAN FIELD BARU KE LAYOUT
        yPos++;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Kondisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(cmbKondisi, gbc);
        yPos++;

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.add(btnSimpan);
        panelTombol.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = yPos;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(panelTombol, gbc);

        btnSimpan.addActionListener(e -> prosesSimpanData());
        btnBatal.addActionListener(e -> {
            DBOperationSuccess = false;
            dispose();
        });
    }

    private void prosesSimpanData() {
        String namaInput = txtNamaBarang.getText().trim();
        String idKatInput = txtIdKategori.getText().trim();
        String jmlTotalInput = txtJumlahTotal.getText().trim();
        String jmlTersediaInput = txtJumlahTersedia.getText().trim(); // <-- AMBIL INPUT JUMLAH TERSEDIA

        if (namaInput.isEmpty() || idKatInput.isEmpty() || jmlTotalInput.isEmpty() || jmlTersediaInput.isEmpty()) { // Semua field wajib
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idKatParsed;
        int jmlTotalParsed;
        int jmlTersediaParsed;

        try {
            idKatParsed = Integer.parseInt(idKatInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Kategori harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtIdKategori.requestFocus();
            return;
        }

        try {
            jmlTotalParsed = Integer.parseInt(jmlTotalInput);
            if (jmlTotalParsed < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah Total tidak boleh negatif!", "Input Error", JOptionPane.ERROR_MESSAGE);
                txtJumlahTotal.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Total harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtJumlahTotal.requestFocus();
            return;
        }

        try {
            jmlTersediaParsed = Integer.parseInt(jmlTersediaInput);
            if (jmlTersediaParsed < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah Tersedia tidak boleh negatif!", "Input Error", JOptionPane.ERROR_MESSAGE);
                txtJumlahTersedia.requestFocus();
                return;
            }
            if (jmlTersediaParsed > jmlTotalParsed) {
                JOptionPane.showMessageDialog(this, "Jumlah Tersedia tidak boleh lebih besar dari Jumlah Total!", "Input Error", JOptionPane.ERROR_MESSAGE);
                txtJumlahTersedia.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Tersedia harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtJumlahTersedia.requestFocus();
            return;
        }

        String kondisiDipilih = (String) cmbKondisi.getSelectedItem();

        if (currentMode == FormMode.TAMBAH) {
            String sql = "INSERT INTO barang (nama_barang, id_kategori, kondisi, jumlah_total, jumlah_tersedia, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaInput);
                pstmt.setInt(2, idKatParsed);
                pstmt.setString(3, kondisiDipilih);
                pstmt.setInt(4, jmlTotalParsed);
                pstmt.setInt(5, jmlTersediaParsed); // <-- GUNAKAN INPUT JUMLAH TERSEDIA

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Barang baru berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    DBOperationSuccess = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan barang.", "Gagal", JOptionPane.ERROR_MESSAGE);
                    DBOperationSuccess = false;
                }
            } catch (SQLException e) {
                DBOperationSuccess = false;
                JOptionPane.showMessageDialog(this, "Gagal menambahkan barang ke database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (currentMode == FormMode.EDIT) {
            String sql = "UPDATE barang SET nama_barang = ?, id_kategori = ?, kondisi = ?, jumlah_total = ?, jumlah_tersedia = ?, updated_at = NOW() WHERE id_barang = ?";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaInput);
                pstmt.setInt(2, idKatParsed);
                pstmt.setString(3, kondisiDipilih);
                pstmt.setInt(4, jmlTotalParsed);
                pstmt.setInt(5, jmlTersediaParsed); // <-- GUNAKAN INPUT JUMLAH TERSEDIA
                pstmt.setInt(6, idBarangToEdit);    // idBarangToEdit jadi parameter ke-6

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Data barang berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    DBOperationSuccess = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui data (ID tidak ditemukan atau tidak ada perubahan).", "Gagal", JOptionPane.WARNING_MESSAGE);
                    DBOperationSuccess = false;
                }
            } catch (SQLException e) {
                DBOperationSuccess = false;
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data barang: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public boolean isDBOperationSuccess() {
        return DBOperationSuccess;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame dummyFrame = new JFrame("Parent Frame (Dummy for Test)");
            dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dummyFrame.setSize(400, 300);

            System.out.println("Mencoba buka FormKelolaBarang dalam mode TAMBAH...");
            FormKelolaBarang dialogTambah = new FormKelolaBarang(dummyFrame, true);
            dialogTambah.setVisible(true);
            System.out.println("Dialog Tambah ditutup. Operasi DB Sukses: " + dialogTambah.isDBOperationSuccess());

            System.out.println("\nMencoba buka FormKelolaBarang dalam mode EDIT...");
            FormKelolaBarang dialogEdit = new FormKelolaBarang(dummyFrame, true, 1, "Barang Tes Edit", 1, 10, 8, "Baik");
            dialogEdit.setVisible(true);
            System.out.println("Dialog Edit ditutup. Operasi DB Sukses: " + dialogEdit.isDBOperationSuccess());

            System.exit(0);
        });
    }
}