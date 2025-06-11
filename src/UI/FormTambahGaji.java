package UI;

import Controller.GajiController;
import Model.GajiModel;
import com.toedter.calendar.JDateChooser; // <-- IMPORT BARU

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Hapus import yang tidak perlu lagi:
// import java.text.ParseException;
// import java.text.SimpleDateFormat;
import java.util.Date;

public class FormTambahGaji extends JDialog {
    private JTextField txtIdCrew;
    // --- DIUBAH: JTextField menjadi JDateChooser ---
    private JDateChooser dcTanggalGaji;
    private JTextField txtJumlahGaji;
    private JTextField txtBonus;
    private JTextField txtNomorRekening;
    // --- DIUBAH: JTextField menjadi JDateChooser ---
    private JDateChooser dcTanggalPembayaran;
    private JTextArea txtKeterangan;

    private JButton btnSimpan;
    private JButton btnBatal;

    private GajiController gajiController;
    private MenuGaji parentMenu;

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

        // ID Crew (Tetap sama)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Crew:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtIdCrew = new JTextField(15);
        formPanel.add(txtIdCrew, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        // --- DIUBAH: Menggunakan JDateChooser untuk Tanggal Gaji ---
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tanggal Gaji:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dcTanggalGaji = new JDateChooser();
        dcTanggalGaji.setDateFormatString("yyyy-MM-dd"); // Atur format tampilan
        dcTanggalGaji.setDate(new Date()); // Set tanggal hari ini sebagai default
        formPanel.add(dcTanggalGaji, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Jumlah Gaji, Bonus, Nomor Rekening (Tetap sama)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Jumlah Gaji:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtJumlahGaji = new JTextField(15);
        formPanel.add(txtJumlahGaji, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Bonus:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtBonus = new JTextField(15);
        formPanel.add(txtBonus, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Nomor Rekening:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNomorRekening = new JTextField(15);
        formPanel.add(txtNomorRekening, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // --- DIUBAH: Menggunakan JDateChooser untuk Tanggal Pembayaran ---
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tanggal Pembayaran:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dcTanggalPembayaran = new JDateChooser();
        dcTanggalPembayaran.setDateFormatString("yyyy-MM-dd"); // Atur format tampilan
        // Untuk tanggal pembayaran, bisa dikosongkan secara default
        dcTanggalPembayaran.setDate(null);
        formPanel.add(dcTanggalPembayaran, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Keterangan (Tetap sama)
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtKeterangan = new JTextArea(3, 15);
        JScrollPane scrollPaneKeterangan = new JScrollPane(txtKeterangan);
        formPanel.add(scrollPaneKeterangan, gbc);

        // Panel Tombol (Tetap sama)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSimpan.addActionListener(e -> simpanDataGaji());
        btnBatal.addActionListener(e -> dispose());
    }

    // --- DIHAPUS: Method parseDate() tidak diperlukan lagi ---
    // private Date parseDate(String dateStr) { ... }

    private void simpanDataGaji() {
        // Ambil data dari field biasa
        String strIdCrew = txtIdCrew.getText().trim();
        String strJumlahGaji = txtJumlahGaji.getText().trim();
        String strBonus = txtBonus.getText().trim();
        String nomorRekening = txtNomorRekening.getText().trim();
        String keterangan = txtKeterangan.getText().trim();

        // --- DIUBAH: Ambil data tanggal langsung dari JDateChooser ---
        Date tglGaji = dcTanggalGaji.getDate();
        Date tglPembayaran = dcTanggalPembayaran.getDate(); // Bisa null jika tidak diisi

        // Validasi input
        if (strIdCrew.isEmpty() || tglGaji == null || strJumlahGaji.isEmpty() || strBonus.isEmpty() || nomorRekening.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Crew, Tanggal Gaji, Jumlah Gaji, Bonus, dan Nomor Rekening tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parsing Angka
        int idCrew;
        double jumlahGaji;
        double bonus;
        try {
            idCrew = Integer.parseInt(strIdCrew);
            jumlahGaji = Double.parseDouble(strJumlahGaji);
            bonus = Double.parseDouble(strBonus);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID Crew, Jumlah Gaji, dan Bonus harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buat objek GajiModel dengan data yang sudah valid
        // Tanggal sudah dalam tipe Date, tidak perlu parsing manual lagi.
        GajiModel gajiBaru = new GajiModel(idCrew, tglGaji, jumlahGaji, bonus, nomorRekening, tglPembayaran, keterangan);

        boolean sukses = gajiController.addGaji(gajiBaru);

        if (sukses) {
            JOptionPane.showMessageDialog(this, "Data gaji berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            if (parentMenu != null) {
                // parentMenu.refreshTable(); // Panggil method refresh di frame utama
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data gaji.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}