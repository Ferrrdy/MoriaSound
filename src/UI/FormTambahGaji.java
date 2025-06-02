package UI;

import Controller.GajiController;
import Model.GajiModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormTambahGaji extends JDialog {
    private JTextField txtIdCrew;
    private JTextField txtTanggalGaji;
    private JTextField txtJumlahGaji;
    private JTextField txtBonus;
    private JTextField txtNomorRekening;
    private JTextField txtTanggalPembayaran;
    private JTextArea txtKeterangan;

    private JButton btnSimpan;
    private JButton btnBatal;

    private GajiController gajiController;
    private MenuGaji parentMenu; // Asumsi MenuGaji adalah frame utama

    public FormTambahGaji(Frame parent, boolean modal, MenuGaji parentMenu) {
        super(parent, "Tambah Data Gaji Baru", modal);
        this.gajiController = new GajiController();
        this.parentMenu = parentMenu;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // ID Crew
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Crew:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtIdCrew = new JTextField(15);
        formPanel.add(txtIdCrew, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        // Tanggal Gaji
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tanggal Gaji (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTanggalGaji = new JTextField(15);
        formPanel.add(txtTanggalGaji, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Jumlah Gaji
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Jumlah Gaji:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtJumlahGaji = new JTextField(15);
        formPanel.add(txtJumlahGaji, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Bonus
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Bonus:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtBonus = new JTextField(15);
        formPanel.add(txtBonus, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Nomor Rekening
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Nomor Rekening:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNomorRekening = new JTextField(15);
        formPanel.add(txtNomorRekening, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Tanggal Pembayaran
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tanggal Pembayaran (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTanggalPembayaran = new JTextField(15);
        formPanel.add(txtTanggalPembayaran, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Keterangan
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtKeterangan = new JTextArea(3, 15);
        JScrollPane scrollPaneKeterangan = new JScrollPane(txtKeterangan);
        formPanel.add(scrollPaneKeterangan, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanDataGaji();
            }
        });

        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Penting untuk validasi format ketat
            return sdf.parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    private void simpanDataGaji() {
        String strIdCrew = txtIdCrew.getText().trim(); // Ambil sebagai String dari JTextField
        String strTglGaji = txtTanggalGaji.getText().trim();
        String strJumlahGaji = txtJumlahGaji.getText().trim();
        String strBonus = txtBonus.getText().trim();
        String nomorRekening = txtNomorRekening.getText().trim(); // Ambil sebagai String dari JTextField (sesuai GajiModel baru)
        String strTglPembayaran = txtTanggalPembayaran.getText().trim();
        String keterangan = txtKeterangan.getText().trim();

        // Validasi sederhana: pastikan field yang wajib tidak kosong
        if (strIdCrew.isEmpty() || strTglGaji.isEmpty() || strJumlahGaji.isEmpty() || strBonus.isEmpty() || nomorRekening.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Crew, Tanggal Gaji, Jumlah Gaji, Bonus, dan Nomor Rekening tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Parsing dan Validasi Tanggal ---
        Date tglGaji = parseDate(strTglGaji);
        if (tglGaji == null) {
            JOptionPane.showMessageDialog(this, "Format Tanggal Gaji salah (YYYY-MM-DD).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date tglPembayaran = null;
        if (!strTglPembayaran.isEmpty()) { // Tanggal pembayaran bersifat opsional
            tglPembayaran = parseDate(strTglPembayaran);
            if (tglPembayaran == null) {
                JOptionPane.showMessageDialog(this, "Format Tanggal Pembayaran salah (YYYY-MM-DD).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // --- Parsing Angka ---
        int idCrew; // Sesuai GajiModel yang baru
        double jumlahGaji;
        double bonus;
        try {
            idCrew = Integer.parseInt(strIdCrew); // Parse dari String ke int
            jumlahGaji = Double.parseDouble(strJumlahGaji);
            bonus = Double.parseDouble(strBonus);
            // nomorRekening tidak perlu di-parse Double lagi karena sudah String
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID Crew, Jumlah Gaji, dan Bonus harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buat objek GajiModel dengan data yang sudah di-parse
        // Perhatikan urutan dan tipe data sesuai konstruktor GajiModel yang baru
        GajiModel gajiBaru = new GajiModel(idCrew, tglGaji, jumlahGaji, bonus, nomorRekening, tglPembayaran, keterangan);

        boolean sukses = gajiController.addGaji(gajiBaru);

        if (sukses) {
            JOptionPane.showMessageDialog(this, "Data gaji berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            if (parentMenu != null) {
                // Pastikan MenuGaji memiliki method public void refreshTable() atau sejenisnya
                // parentMenu.refreshTable(); // Uncomment ini jika ada methodnya
            }
            dispose(); // Tutup dialog setelah sukses
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data gaji. Periksa log error pada konsol untuk detail.", "Error", JOptionPane.ERROR_MESSAGE);
 }
}
}
