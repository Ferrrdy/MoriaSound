// package, import, dan deklarasi kelas tetap sama
package UI;

import java.awt.geom.RoundRectangle2D;
import DataBase.DbConnection;
import Model.Crew;
import Controller.CrewController;

import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.Spring;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Import kelas-kelas UI lainnya yang mungkin diperlukan untuk navigasi
import UI.BerandaBaru;
import UI.DaftarPaket;
import UI.MenuGaji;
import UI.MenuInventaris;
import UI.MenuJadwal;


public class DaftarKaryawan extends javax.swing.JFrame {
    int xMouse, yMouse;
    
    // --- DIUBAH --- Menambahkan formatter untuk tanggal
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public DaftarKaryawan() {
        initComponents();
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));

        // Listener tombol-tombol CRUD (sudah ada di code Anda, hanya memastikan)
        jButton3.addActionListener(evt -> jButton3ActionPerformed(evt)); // Edit
        jButton4.addActionListener(evt -> jButton4ActionPerformed(evt)); // Tambah
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt)); // Hapus

        // Listener navigasi (sudah ada)
        jButton17.addActionListener(evt -> jButton17ActionPerformed(evt));
        jButton18.addActionListener(evt -> jButton18ActionPerformed(evt));
        jButton19.addActionListener(evt -> jButton19ActionPerformed(evt));
        jButton20.addActionListener(evt -> jButton20ActionPerformed(evt));
        jButton21.addActionListener(evt -> jButton21ActionPerformed(evt));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        loadTableCrew(); // Panggil fungsi untuk load data ke tabel
    }

    private void loadTableCrew() {
        // --- DIUBAH --- Menambahkan kolom "No. Rekening"
        String[] kolom = {"Select", "ID", "Nama", "Posisi", "Gaji Bulanan", "No. Rekening", "Created At", "Updated At"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(column);
            }

            @Override
            public boolean isCellEditable(int row, int intcolumn) {
                return intcolumn == 0; // Hanya checkbox yang bisa diedit
            }
        };
        jTable1.setModel(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Center alignment untuk kolom-kolom tertentu
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
             if (i != 0 && i != 2) { // Kolom 'Nama' tidak di-center
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
             }
        }
        
        jTable1.setBackground(Color.WHITE);
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // --- DIUBAH --- Menyesuaikan lebar kolom setelah menambah kolom baru
        TableColumnModel columnModel = jTable1.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Select
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(30);  // ID
        columnModel.getColumn(2).setPreferredWidth(120); // Nama
        columnModel.getColumn(3).setPreferredWidth(80);  // Posisi
        columnModel.getColumn(4).setPreferredWidth(100); // Gaji
        columnModel.getColumn(5).setPreferredWidth(120); // No. Rekening
        columnModel.getColumn(6).setPreferredWidth(140); // Created At
        columnModel.getColumn(7).setPreferredWidth(140); // Updated At

        List<Crew> crews = CrewController.getAllCrew();
        if (crews != null) {
            for (Crew crew : crews) {
                // --- DIUBAH --- Menambahkan crew.getNorek_crew() dan format tanggal
                Object[] row = {
                    false, // checkbox
                    crew.getIdCrew(),
                    crew.getNamaCrew(),
                    crew.getPosisi(),
                    crew.getGajiBulanan(),
                    crew.getNorek_crew(), // Data baru
                    crew.getCreatedAt() != null ? crew.getCreatedAt().format(formatter) : null,
                    crew.getUpdatedAt() != null ? crew.getUpdatedAt().format(formatter) : null
                };
                model.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memuat data crew dari database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- DIUBAH TOTAL --- Logika Tombol Hapus berdasarkan Checkbox
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        List<Integer> selectedIds = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        // Kumpulkan semua ID dari baris yang dicentang
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                selectedIds.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih (centang) data karyawan yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin menghapus " + selectedIds.size() + " data karyawan terpilih?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            for (Integer id : selectedIds) {
                if (CrewController.deleteCrew(id)) {
                    successCount++;
                }
            }

            if (successCount == selectedIds.size()) {
                JOptionPane.showMessageDialog(this, "Semua data karyawan terpilih berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Berhasil menghapus " + successCount + " dari " + selectedIds.size() + " data.\nBeberapa data mungkin gagal dihapus.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
            loadTableCrew(); // Refresh tabel
        }
    }
    
    // --- DIUBAH TOTAL --- Logika Tombol Edit berdasarkan Checkbox
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        List<Integer> selectedIds = new ArrayList<>();
        int selectedRowIndex = -1;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 0)) {
                selectedIds.add((Integer) model.getValueAt(i, 1));
                selectedRowIndex = i;
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih (centang) satu data karyawan yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedIds.size() > 1) {
            JOptionPane.showMessageDialog(this, "Hanya bisa mengedit satu data sekali waktu. Harap centang satu saja.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int crewId = selectedIds.get(0);
        Crew crewToEdit = CrewController.getCrewById(crewId);

        if (crewToEdit != null) {
            JDialog editDialog = new JDialog(this, "Edit Data Crew", true);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(400, 300); // Perbesar sedikit untuk field baru
            editDialog.setLocationRelativeTo(this);

            // --- DIUBAH --- Menambah panel untuk norek_crew
            JPanel formPanel = new JPanel(new SpringLayout());
            
            formPanel.add(new JLabel("Nama Crew:", JLabel.TRAILING));
            JTextField namaField = new JTextField(crewToEdit.getNamaCrew());
            formPanel.add(namaField);

            formPanel.add(new JLabel("Posisi:", JLabel.TRAILING));
            JTextField posisiField = new JTextField(crewToEdit.getPosisi());
            formPanel.add(posisiField);
            
            formPanel.add(new JLabel("Gaji Bulanan:", JLabel.TRAILING));
            JTextField gajiField = new JTextField(String.valueOf(crewToEdit.getGajiBulanan()));
            formPanel.add(gajiField);
            
            formPanel.add(new JLabel("No. Rekening:", JLabel.TRAILING));
            JTextField norekField = new JTextField(crewToEdit.getNorek_crew());
            formPanel.add(norekField);

            SpringUtilities.makeCompactGrid(formPanel, 4, 2, 6, 6, 6, 6);
            editDialog.add(formPanel, BorderLayout.CENTER);

            JButton saveButton = new JButton("Simpan Perubahan");
            saveButton.addActionListener(e -> {
                try {
                    String nama = namaField.getText().trim();
                    String posisi = posisiField.getText().trim();
                    String gajiText = gajiField.getText().trim();
                    String norek = norekField.getText().trim();

                    if (nama.isEmpty() || posisi.isEmpty() || gajiText.isEmpty() || norek.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "Semua field harus diisi.", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    double gaji = Double.parseDouble(gajiText);
                    
                    // --- DIUBAH --- Set semua data termasuk norek_crew
                    crewToEdit.setNamaCrew(nama);
                    crewToEdit.setPosisi(posisi);
                    crewToEdit.setGajiBulanan(gaji);
                    crewToEdit.setNorek_crew(norek);

                    if (CrewController.updateCrew(crewToEdit)) {
                        JOptionPane.showMessageDialog(editDialog, "Data karyawan berhasil diperbarui!");
                        loadTableCrew();
                        editDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Gagal memperbarui data karyawan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Input Gaji Bulanan tidak valid. Harap masukkan angka.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);

            editDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mendapatkan data crew untuk diedit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- DIUBAH TOTAL --- Logika Tombol Tambah dengan field norek_crew
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog addDialog = new JDialog(this, "Tambah Data Crew Baru", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new SpringLayout());

        formPanel.add(new JLabel("Nama Crew:", JLabel.TRAILING));
        JTextField namaField = new JTextField(20);
        formPanel.add(namaField);

        formPanel.add(new JLabel("Posisi:", JLabel.TRAILING));
        JTextField posisiField = new JTextField(20);
        formPanel.add(posisiField);

        formPanel.add(new JLabel("Gaji Bulanan:", JLabel.TRAILING));
        JTextField gajiField = new JTextField(20);
        formPanel.add(gajiField);
        
        formPanel.add(new JLabel("No. Rekening:", JLabel.TRAILING));
        JTextField norekField = new JTextField(20);
        formPanel.add(norekField);

        SpringUtilities.makeCompactGrid(formPanel, 4, 2, 6, 6, 6, 6);
        addDialog.add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Simpan Crew");
        saveButton.addActionListener(e -> {
            try {
                String nama = namaField.getText().trim();
                String posisi = posisiField.getText().trim();
                String gajiText = gajiField.getText().trim();
                String norek = norekField.getText().trim();

                if (nama.isEmpty() || posisi.isEmpty() || gajiText.isEmpty() || norek.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Semua field harus diisi.", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double gaji = Double.parseDouble(gajiText);
                
                // --- DIUBAH --- Menggunakan constructor yang sesuai dengan field
                Crew newCrew = new Crew(nama, posisi, gaji, norek); // Menggunakan constructor yang lebih sesuai

                if (CrewController.addCrew(newCrew)) {
                    JOptionPane.showMessageDialog(addDialog, "Karyawan berhasil ditambahkan!");
                    loadTableCrew();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Gagal menambahkan karyawan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Input Gaji Bulanan tidak valid. Harap masukkan angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);

        addDialog.setVisible(true);
    }
    
    // =========================================================================
    // SISA KODE (NAVIGASI, MOUSE DRAG, MAIN METHOD, DLL) TETAP SAMA SEPERTI ASLINYA
    // ANDA TIDAK PERLU MENGUBAH APAPUN DI BAWAH INI
    // =========================================================================

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {
        new MenuJadwal().setVisible(true);
        this.dispose();
    }

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {
        new DaftarPaket().setVisible(true);
        this.dispose();
    }

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {
        new MenuInventaris().setVisible(true);
        this.dispose();
    }

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {
        loadTableCrew();
        JOptionPane.showMessageDialog(this, "Halaman Karyawan sudah di-refresh.");
    }

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {
        new MenuGaji().setVisible(true);
        this.dispose();
    }

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {
        new BerandaBaru().setVisible(true);
        this.dispose();
    }
    
    private void formMousePressed(java.awt.event.MouseEvent evt) {
        xMouse = evt.getX();
        yMouse = evt.getY();
    }

    private void formMouseDragged(java.awt.event.MouseEvent evt) {
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        roundedPanel3 = new Custom.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        roundedPanel6 = new Custom.RoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        roundedPanel5 = new Custom.RoundedPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setBackground(new java.awt.Color(251, 190, 1));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton2.setText("X");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        roundedPanel3.setBackground(new java.awt.Color(46, 51, 55));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/150 no back.png"))); // NOI18N

        jButton17.setBackground(new java.awt.Color(251, 200, 42));
        jButton17.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton17.setText("Kalender Event");
        jButton17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton18.setBackground(new java.awt.Color(251, 200, 42));
        jButton18.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton18.setText("Daftar Paket");
        jButton18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton19.setBackground(new java.awt.Color(251, 200, 42));
        jButton19.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton19.setText("Inventaris");
        jButton19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton20.setBackground(new java.awt.Color(251, 200, 42));
        jButton20.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton20.setText("Karyawan");
        jButton20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton21.setBackground(new java.awt.Color(251, 200, 42));
        jButton21.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton21.setText("Penggajian");
        jButton21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel3))
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        roundedPanel6.setBackground(new java.awt.Color(46, 51, 55));
        roundedPanel6.setRoundBottomRight(25);
        roundedPanel6.setRoundTopRight(25);

        jLabel4.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(251, 190, 1));
        jLabel4.setText("Moria Sound Lighting");

        javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addContainerGap())
        );

        roundedPanel5.setBackground(new java.awt.Color(124, 124, 124));
        roundedPanel5.setRoundTopLeft(25);
        roundedPanel5.setRoundTopRight(25);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nama", "Posisi", "Gaji Bulanan", "Create At", "Edited At"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(46, 51, 55));
        jButton1.setText("Hapus");

        jButton3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(46, 51, 55));
        jButton3.setText("Edit");

        jButton4.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(46, 51, 55));
        jButton4.setText("Tambah");

        javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundedPanel5Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(12, 12, 12)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 704, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel5Layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setFont(new java.awt.Font("SansSerif", 3, 30)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(46, 51, 55));
        jLabel6.setText("K A R Y A W A N");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                            .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(37, 37, 37))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(289, 289, 289)
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DaftarKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new DaftarKaryawan().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTable jTable1;
    private Custom.RoundedPanel roundedPanel3;
    private Custom.RoundedPanel roundedPanel5;
    private Custom.RoundedPanel roundedPanel6;
    // End of variables declaration//GEN-END:variables
    
    // Anda mungkin perlu kelas SpringUtilities ini jika belum ada
    static class SpringUtilities {
        public static void makeCompactGrid(Container parent,
                                           int rows, int cols,
                                           int initialX, int initialY,
                                           int xPad, int yPad) {
            SpringLayout layout;
            try {
                layout = (SpringLayout)parent.getLayout();
            } catch (ClassCastException exc) {
                System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
                return;
            }

            Spring x = Spring.constant(initialX);
            for (int c = 0; c < cols; c++) {
                Spring width = Spring.constant(0);
                for (int r = 0; r < rows; r++) {
                    width = Spring.max(width,
                                       layout.getConstraints(parent.getComponent(r * cols + c)).
                                           getWidth());
                }
                for (int r = 0; r < rows; r++) {
                    SpringLayout.Constraints constraints = layout.getConstraints(
                                                parent.getComponent(r * cols + c));
                    constraints.setX(x);
                    constraints.setWidth(width);
                }
                x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
            }

            Spring y = Spring.constant(initialY);
            for (int r = 0; r < rows; r++) {
                Spring height = Spring.constant(0);
                for (int c = 0; c < cols; c++) {
                    height = Spring.max(height,
                                        layout.getConstraints(parent.getComponent(r * cols + c)).
                                            getHeight());
                }
                for (int c = 0; c < cols; c++) {
                    SpringLayout.Constraints constraints = layout.getConstraints(
                                                parent.getComponent(r * cols + c));
                    constraints.setY(y);
                    constraints.setHeight(height);
                }
                y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
            }

            SpringLayout.Constraints pCons = layout.getConstraints(parent);
            pCons.setConstraint(SpringLayout.EAST, x);
            pCons.setConstraint(SpringLayout.SOUTH, y);
        }
    }
}