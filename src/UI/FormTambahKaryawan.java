package UI;

import Controller.CrewController;
import Model.Crew;
import javax.swing.*;
import java.awt.*;

public class FormTambahKaryawan extends JDialog {
    private JTextField txtNama, txtPosisi, txtGaji, txtNorek;
    private JButton btnSimpan, btnBatal;

    private final CrewController crewController;
    private Crew crewToEdit; // Akan null jika mode Tambah, terisi jika mode Edit
    private boolean isSaved = false;

    // Constructor untuk mode Tambah
    public FormTambahKaryawan(Frame parent, CrewController controller) {
        super(parent, "Tambah Karyawan Baru", true);
        this.crewController = controller;
        this.crewToEdit = null;
        initComponents();
    }

    // Constructor untuk mode Edit
    public FormTambahKaryawan(Frame parent, CrewController controller, Crew crewToEdit) {
        super(parent, "Edit Data Karyawan", true);
        this.crewController = controller;
        this.crewToEdit = crewToEdit;
        initComponents();
        populateForm();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtNama = new JTextField(20);
        txtPosisi = new JTextField(20);
        txtGaji = new JTextField(20);
        txtNorek = new JTextField(20); // [BARU] Field untuk nomor rekening
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");

        formPanel.add(new JLabel("Nama Crew:"));
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Posisi:"));
        formPanel.add(txtPosisi);
        formPanel.add(new JLabel("Gaji Bulanan:"));
        formPanel.add(txtGaji);
        formPanel.add(new JLabel("No. Rekening:"));
        formPanel.add(txtNorek); // [BARU] Ditambahkan ke form
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSimpan.addActionListener(e -> simpanData());
        btnBatal.addActionListener(e -> dispose());

        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }
    
    // Mengisi form dengan data yang ada saat mode edit
    private void populateForm() {
        if (crewToEdit != null) {
            txtNama.setText(crewToEdit.getNamaCrew());
            txtPosisi.setText(crewToEdit.getPosisi());
            txtGaji.setText(String.valueOf(crewToEdit.getGajiBulanan()));
            txtNorek.setText(crewToEdit.getNorek_crew());
        }
    }

    // Logika untuk menyimpan data ke database melalui controller
    private void simpanData() {
        String nama = txtNama.getText().trim();
        String posisi = txtPosisi.getText().trim();
        String gajiStr = txtGaji.getText().trim();
        String norek = txtNorek.getText().trim();

        if (nama.isEmpty() || posisi.isEmpty() || gajiStr.isEmpty() || norek.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double gaji = Double.parseDouble(gajiStr);
            boolean success = false;
            
            Crew crewData = (crewToEdit == null) ? new Crew() : crewToEdit;
            crewData.setNamaCrew(nama);
            crewData.setPosisi(posisi);
            crewData.setGajiBulanan(gaji);
            crewData.setNorek_crew(norek);

            if (crewToEdit == null) { // Mode Tambah
                success = crewController.addCrewInstance(crewData);
            } else { // Mode Edit
                success = crewController.updateCrewInstance(crewData);
            }

            if (success) {
                isSaved = true;
                JOptionPane.showMessageDialog(this, "Data karyawan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Gaji harus berupa angka yang valid.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Metode ini dipanggil oleh DaftarKaryawan untuk tahu apakah tabel perlu di-refresh
    public boolean isSaved() {
        return isSaved;
    }
}