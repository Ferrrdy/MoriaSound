/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;
import Controller.CrewController;
import Model.Crew;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter; // Import ini
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author nabil
 */
public class DaftarKaryawanBaru extends javax.swing.JFrame {
int xMouse, yMouse;
// Inisialisasi formatter untuk tanggal
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 

    // [DIUBAH] Controller sekarang menjadi properti final kelas ini
    private final CrewController crewController;
    /**
     * Creates new form DaftarKaryawanBaru
     */
    public DaftarKaryawanBaru() {
        this.crewController = new CrewController();
        initComponents();
        setSize(1320, 720); // Tetapkan ukuran tetap
        setMinimumSize(new Dimension(1320, 720));
        setMaximumSize(new Dimension(1320, 720));
        setPreferredSize(new Dimension(1320, 720));
        setResizable(false);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        loadTableData(); // Memuat data saat frame pertama kali dibuat
        aturTampilanTabel();
    }
    
    private void loadTableData() {
        // Sesuaikan kolom untuk menampilkan Created At dan Updated At jika diperlukan
        String[] kolom = {"Select", "ID", "Nama", "Posisi", "Gaji Bulanan", "No. Rekening", "Created At", "Updated At"}; 
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                // Mengembalikan super.getColumnClass untuk tipe data default lainnya
                return super.getColumnClass(column); 
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        jTable1.setModel(model);

        // Pengaturan Tampilan Tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Loop untuk mengatur renderer agar lebih aman dari IndexOutOfBounds
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            // Kecualikan kolom 'Select' (0) dan 'Nama' (2) jika tidak ingin di-center
            if (i != 0 && i != 2) { 
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = jTable1.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // Select
        columnModel.getColumn(1).setPreferredWidth(40); // ID
        columnModel.getColumn(2).setPreferredWidth(200); // Nama
        columnModel.getColumn(3).setPreferredWidth(100); // Posisi
        columnModel.getColumn(4).setPreferredWidth(120); // Gaji
        columnModel.getColumn(5).setPreferredWidth(150); // No Rekening
        
        // Tambahan lebar kolom untuk Created At dan Updated At
        if (kolom.length > 6) { 
            columnModel.getColumn(6).setPreferredWidth(140); // Created At
            columnModel.getColumn(7).setPreferredWidth(140); // Updated At
        }

        try {
            // [DIUBAH] Mengambil data melalui instance controller, bukan metode static
            List<Crew> crews = crewController.getAllCrewInstance();
            model.setRowCount(0); // Kosongkan tabel sebelum mengisi
            if (crews != null) { // Pastikan crews tidak null
                for (Crew crew : crews) {
                    model.addRow(new Object[]{
                        false,
                        crew.getIdCrew(),
                        crew.getNamaCrew(),
                        crew.getPosisi(),
                        crew.getGajiBulanan(),
                        crew.getNorek_crew(),
                        // Pastikan createdAt dan updatedAt tidak null sebelum format
                        crew.getCreatedAt() != null ? crew.getCreatedAt().format(formatter) : null,
                        crew.getUpdatedAt() != null ? crew.getUpdatedAt().format(formatter) : null
                    });
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Tidak ada data crew yang ditemukan atau terjadi masalah saat mengambil data.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data crew dari database: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi error tak terduga saat memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleAdd() {
        // Membuka dialog FormTambahKaryawan dalam mode Tambah
        FormTambahKaryawan form = new FormTambahKaryawan(this, crewController);
        form.setVisible(true);
        // Jika form ditutup setelah menyimpan, refresh tabel
        if (form.isSaved()) {
            loadTableData();
        }
    }

    private void handleEdit() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris data dari tabel untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Ambil ID dari kolom yang benar (kolom 1 untuk ID)
            int crewId = (int) jTable1.getValueAt(selectedRow, 1); 
            // [DIUBAH] Mengambil detail lengkap dari controller
            Crew crewToEdit = crewController.getCrewByIdInstance(crewId);

            if (crewToEdit != null) {
                FormTambahKaryawan form = new FormTambahKaryawan(this, crewController, crewToEdit);
                form.setVisible(true);
                if (form.isSaved()) {
                    loadTableData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengambil detail data karyawan dari database (ID: " + crewId + ")", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi error saat mengambil data untuk diedit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        List<Integer> idsToDelete = new ArrayList<>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (Boolean.TRUE.equals(jTable1.getValueAt(i, 0))) { // Kolom 0 adalah checkbox
                idsToDelete.add((Integer) jTable1.getValueAt(i, 1)); // Kolom 1 adalah ID
            }
        }

        if (idsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih (centang) data yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus " + idsToDelete.size() + " data karyawan?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int successCount = 0;
                for (Integer id : idsToDelete) {
                    if (crewController.deleteCrewInstance(id)) {
                        successCount++;
                    }
                }
                JOptionPane.showMessageDialog(this, "Berhasil menghapus " + successCount + " data.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void aturTampilanTabel() {
    // Hilangkan belang striping
    // Hapus striping: atur renderer default ke putih untuk SEMUA KOLOM
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            // Jangan timpa renderer checkbox
            if (jTable1.getColumnClass(i) == Boolean.class) continue;

            jTable1.getColumnModel().getColumn(i).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    } else {
                        c.setBackground(java.awt.Color.WHITE);
                        c.setForeground(java.awt.Color.BLACK);
                    }
                    return c;
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        roundedPanel6 = new Custom.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        roundedPanel5 = new Custom.RoundedPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        roundedPanel10 = new Custom.RoundedPanel();
        jLabel11 = new javax.swing.JLabel();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JSeparator();

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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setBackground(new java.awt.Color(251, 190, 1));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jButton2.setText("X");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        roundedPanel6.setBackground(new java.awt.Color(46, 51, 55));
        roundedPanel6.setRoundBottomRight(25);
        roundedPanel6.setRoundTopRight(25);

        jLabel10.setFont(new java.awt.Font("SansSerif", 3, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(251, 190, 1));
        jLabel10.setText("Moria Sound Lighting");

        javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel10)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("SansSerif", 3, 35)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(46, 51, 55));
        jLabel3.setText("D A F T A R   K A R Y A W A N");

        roundedPanel5.setBackground(new java.awt.Color(124, 124, 124));
        roundedPanel5.setRoundTopLeft(25);
        roundedPanel5.setRoundTopRight(25);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton7.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(46, 51, 55));
        jButton7.setText("Tambah");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton8.setForeground(new java.awt.Color(46, 51, 55));
        jButton8.setText("Edit");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(46, 51, 55));
        jButton9.setText("Hapus");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundedPanel5Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(12, 12, 12)
                        .addComponent(jButton8)
                        .addGap(12, 12, 12)
                        .addComponent(jButton9))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 968, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton8)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
        );

        roundedPanel10.setBackground(new java.awt.Color(46, 51, 55));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/150 no back.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jButton40.setBackground(new java.awt.Color(251, 200, 42));
        jButton40.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton40.setText("Kalender Event");
        jButton40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setBackground(new java.awt.Color(251, 200, 42));
        jButton41.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton41.setText("Daftar Paket");
        jButton41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setBackground(new java.awt.Color(251, 200, 42));
        jButton42.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton42.setText("Inventaris");
        jButton42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setBackground(new java.awt.Color(251, 200, 42));
        jButton43.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton43.setText("Karyawan");
        jButton43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton44.setBackground(new java.awt.Color(251, 200, 42));
        jButton44.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton44.setText("Penggajian");
        jButton44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel10Layout = new javax.swing.GroupLayout(roundedPanel10);
        roundedPanel10.setLayout(roundedPanel10Layout);
        roundedPanel10Layout.setHorizontalGroup(
            roundedPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel10Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(roundedPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(15, 15, 15))
        );
        roundedPanel10Layout.setVerticalGroup(
            roundedPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel10Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel11)
                .addGap(55, 55, 55)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(roundedPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator3)
                            .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(36, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(293, 293, 293))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(roundedPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        BerandaBaru menu = new BerandaBaru();
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        MenuJadwal jadwal = new MenuJadwal();
        jadwal.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        DaftarPaketBaru paket = new DaftarPaketBaru();
        paket.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        MenuInventarisBaru inventaris = new MenuInventarisBaru();
        inventaris.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        DaftarKaryawanBaru crew = new DaftarKaryawanBaru();
        crew.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        MenuGajiBaru gaji = new MenuGajiBaru();
        gaji.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton44ActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
    xMouse = evt.getX();
    yMouse = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }//GEN-LAST:event_formMouseDragged

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        handleAdd();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        handleEdit();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        handleDelete();
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DaftarKaryawanBaru.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DaftarKaryawanBaru.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DaftarKaryawanBaru.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DaftarKaryawanBaru.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DaftarKaryawanBaru().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable jTable1;
    private Custom.RoundedPanel roundedPanel1;
    private Custom.RoundedPanel roundedPanel10;
    private Custom.RoundedPanel roundedPanel5;
    private Custom.RoundedPanel roundedPanel6;
    private Custom.RoundedPanel roundedPanel9;
    // End of variables declaration//GEN-END:variables
}
