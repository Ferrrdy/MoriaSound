package UI;

// import Controller.CrewController;
// import Model.Crew;
// import UI.TampilanUtama;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;

public class FormTambahKaryawan extends JDialog {
    private JTextField tfNama;
    private JTextField tfPosisi; // <- Dikembalikan menjadi JTextField
    private JFormattedTextField tfGaji;
    private JButton btnSimpan, btnBatal;

    // private TampilanUtama frameUtama;

    public FormTambahKaryawan(Frame owner) {
        super(owner, "Form Tambah Crew", true);
        // this.frameUtama = frameUtama;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new TitledBorder("Detail Karyawan")
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 1: Nama Crew
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Crew:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tfNama = new JTextField(20);
        formPanel.add(tfNama, gbc);

        // Baris 2: Posisi (Menggunakan JTextField)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Posisi:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tfPosisi = new JTextField(); // <- Dikembalikan menjadi JTextField
        formPanel.add(tfPosisi, gbc);

        // Baris 3: Gaji Bulanan (Menggunakan JFormattedTextField)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Gaji Bulanan:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        NumberFormat formatGaji = NumberFormat.getNumberInstance();
        formatGaji.setGroupingUsed(false);
        tfGaji = new JFormattedTextField(formatGaji);
        tfGaji.setColumns(15);
        formPanel.add(tfGaji, gbc);

        // === PANEL TOMBOL (BAWAH) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);

        btnSimpan.addActionListener(e -> simpanCrew());
        btnBatal.addActionListener(e -> dispose());

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void simpanCrew() {
        String nama = tfNama.getText().trim();
        String posisi = tfPosisi.getText().trim(); // <- Cara mengambil data disesuaikan lagi
        Object gajiValue = tfGaji.getValue();

        // Validasi disesuaikan untuk JTextField Posisi
        if (nama.isEmpty() || posisi.isEmpty() || gajiValue == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double gaji = ((Number) gajiValue).doubleValue();
        if (gaji <= 0) {
            JOptionPane.showMessageDialog(this, "Gaji harus lebih besar dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Contoh sementara jika Controller belum siap
        String pesan = String.format("Data siap disimpan:\nNama: %s\nPosisi: %s\nGaji: %.2f", nama, posisi, gaji);
        JOptionPane.showMessageDialog(this, pesan, "Sukses", JOptionPane.INFORMATION_MESSAGE);
        
        // if (frameUtama != null) {
        //     frameUtama.refreshTabelKaryawan();
        // }
        dispose();
    }

    // main method untuk testing form secara mandiri
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            FormTambahKaryawan dialog = new FormTambahKaryawan(null);
            dialog.setVisible(true);
        });
    }
}