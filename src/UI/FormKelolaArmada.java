package UI; // Sesuaikan dengan package Anda

import DataBase.DbConnection; // Pastikan path ini benar
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormKelolaArmada extends JDialog {

    public enum FormMode {
        TAMBAH,
        EDIT
    }

    private FormMode currentMode;
    private int idArmadaToEdit = -1;

    private JTextField txtNamaArmada;
    private JComboBox<String> cmbStatusArmada; // Pilihan status armada
    private JButton btnSimpan;
    private JButton btnBatal;

    private boolean DBOperationSuccess = false;

    /**
     * Konstruktor untuk Mode TAMBAH.
     */
    public FormKelolaArmada(Frame parent, boolean modal) {
        super(parent, modal);
        this.currentMode = FormMode.TAMBAH;
        setTitle("Tambah Armada Baru");
        initComponentsUI();
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * Konstruktor untuk Mode EDIT.
     */
    public FormKelolaArmada(Frame parent, boolean modal, int idArmada, String nama, String status) {
        super(parent, modal);
        this.currentMode = FormMode.EDIT;
        this.idArmadaToEdit = idArmada;
        setTitle("Edit Data Armada - ID: " + idArmada);
        initComponentsUI();

        txtNamaArmada.setText(nama);
        cmbStatusArmada.setSelectedItem(status); // Set status yang ada

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponentsUI() {
        txtNamaArmada = new JTextField(25);
        // Pilihan status bisa Anda sesuaikan
        cmbStatusArmada = new JComboBox<>(new String[]{"Tersedia", "Dalam Perjalanan", "Dalam Perbaikan", "Tidak Aktif"});

        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int yPos = 0;

        // Baris: Nama Armada
        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Nama Armada:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtNamaArmada, gbc);
        yPos++;

        // Baris: Status Armada
        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = yPos; gbc.fill = GridBagConstraints.HORIZONTAL; add(cmbStatusArmada, gbc);
        yPos++;

        // Baris: Tombol-tombol
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
        String namaInput = txtNamaArmada.getText().trim();
        String statusDipilih = (String) cmbStatusArmada.getSelectedItem();

        if (namaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Armada tidak boleh kosong!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtNamaArmada.requestFocus();
            return;
        }
        if (statusDipilih == null) { // Jika JComboBox bisa kosong, tambahkan pengecekan
             JOptionPane.showMessageDialog(this, "Status Armada harus dipilih!", "Input Error", JOptionPane.ERROR_MESSAGE);
             cmbStatusArmada.requestFocus();
             return;
        }


        if (currentMode == FormMode.TAMBAH) {
            // Logika INSERT untuk tabel 'armada'
            // Kolom: nama_armada, status_armada, created_at, updated_at
            // id_armada diasumsikan auto-increment
            String sql = "INSERT INTO armada (nama_armada, status, created_at, updated_at) VALUES (?, ?, NOW(), NOW())"; // Perhatikan perubahan di sini
            try (Connection conn = DbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaInput);
                pstmt.setString(2, statusDipilih);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Armada baru berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    DBOperationSuccess = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan armada.", "Gagal", JOptionPane.ERROR_MESSAGE);
                    DBOperationSuccess = false;
                }
            } catch (SQLException e) {
                DBOperationSuccess = false;
                JOptionPane.showMessageDialog(this, "Gagal menambahkan armada ke database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (currentMode == FormMode.EDIT) {
            // Logika UPDATE untuk tabel 'armada'
            String sql = "UPDATE armada SET nama_armada = ?, status = ?, updated_at = NOW() WHERE id_armada = ?"; // Perhatikan perubahan di sini
            try (Connection conn = DbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaInput);
                pstmt.setString(2, statusDipilih);
                pstmt.setInt(3, idArmadaToEdit);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Data armada berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    DBOperationSuccess = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui data armada (ID tidak ditemukan atau tidak ada perubahan).", "Gagal", JOptionPane.WARNING_MESSAGE);
                    DBOperationSuccess = false;
                }
            } catch (SQLException e) {
                DBOperationSuccess = false;
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data armada: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public boolean isDBOperationSuccess() {
        return DBOperationSuccess;
    }

    // Main method untuk testing (opsional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame dummyFrame = new JFrame();
            // Tes Mode Tambah
            FormKelolaArmada dialogTambah = new FormKelolaArmada(dummyFrame, true);
            dialogTambah.setVisible(true);
            System.out.println("Tambah Armada Sukses: " + dialogTambah.isDBOperationSuccess());

            // Tes Mode Edit
            FormKelolaArmada dialogEdit = new FormKelolaArmada(dummyFrame, true, 1, "Truk ABC-01", "Tersedia");
            dialogEdit.setVisible(true);
            System.out.println("Edit Armada Sukses: " + dialogEdit.isDBOperationSuccess());

            System.exit(0);
        });
    }
}