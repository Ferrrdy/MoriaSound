/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;
import java.awt.geom.RoundRectangle2D;
import DataBase.DbConnection;
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
import java.text.ParseException; // Import untuk parsing tanggal
import java.text.SimpleDateFormat; // Import untuk format tanggal
import java.util.Date; // Import Date dari java.util

/**
 *
 * @author nabil
 */
public class MenuGaji extends javax.swing.JFrame {
int xMouse, yMouse;
    /**
     * Creates new form MenuGaji
     */
    public MenuGaji() {
        initComponents();
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
    
        loadTableGaji(); // Panggil fungsi untuk load konfigurasi tabel
        populateTable(); // Panggil fungsi untuk mengisi data ke tabel
    }

    // Metode untuk mengkonfigurasi tampilan tabel (kolom, lebar, renderer)
    private void loadTableGaji() {
        String[] kolom = {"Select", "ID", "ID Crew", "Tanggal Gaji", "Jumlah Gaji","Bonus", "Tanggal Pembayaran", "Keterangan", "Created At", "Updated At"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : super.getColumnClass(column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Hanya kolom "Select" yang editable untuk pemilihan
                // Jika ingin membuat sel tertentu editable langsung, ubah di sini
                return column == 0; 
            }
        };
        jTable1.setModel(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Kolom yang rata tengah (kecuali 0 = checkbox, 7 & 8 = created/updated)
        for (int i = 1; i < jTable1.getColumnCount(); i++) {
            if (i != 7 && i != 8) { // Kolom keterangan dan created/updated mungkin lebih baik rata kiri
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        // Set warna latar belakang tabel jadi putih
        jTable1.setBackground(Color.WHITE);

        // Set tampilan header kolom
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);


        TableColumnModel columnModel = jTable1.getColumnModel();
        TableColumn selectColumn = columnModel.getColumn(0);
        selectColumn.setPreferredWidth(50);
        selectColumn.setMaxWidth(50);
        selectColumn.setMinWidth(50);
        selectColumn.setResizable(false);     // Checkbox
        columnModel.getColumn(1).setPreferredWidth(30);   // ID
        columnModel.getColumn(2).setPreferredWidth(100);  // ID Crew
        columnModel.getColumn(3).setPreferredWidth(80);   // Tanggal Gaji
        columnModel.getColumn(4).setPreferredWidth(120);  // Jumlah Gaji
        columnModel.getColumn(5).setPreferredWidth(120);  // Bonus
        columnModel.getColumn(6).setPreferredWidth(150);  // Tanggal Pembayaran
        columnModel.getColumn(7).setPreferredWidth(150);  // Keterangan
        columnModel.getColumn(8).setPreferredWidth(150);  // Created At
        columnModel.getColumn(9).setPreferredWidth(150);  // Updated At
    }

    // Fungsi untuk mengisi data ke tabel dari database
    public void populateTable() { // Ubah aksesibilitas menjadi public agar bisa dipanggil dari luar
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Kosongkan tabel sebelum mengisi ulang

        String query = "SELECT id_gaji, id_crew, tanggal_gaji, jumlah_gaji, bonus, tanggal_pembayaran, keterangan, created_at, updated_at FROM gaji ORDER BY created_at DESC"; // Ambil semua data dari tabel gaji
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[10];
                row[0] = false; // Kolom "Select" (checkbox)
                row[1] = rs.getInt("id_gaji");
                row[2] = rs.getString("id_crew");
                row[3] = rs.getDate("tanggal_gaji");
                row[4] = rs.getDouble("jumlah_gaji");
                row[5] = rs.getDouble("bonus");
                row[6] = rs.getDate("tanggal_pembayaran");
                row[7] = rs.getString("keterangan");
                row[8] = rs.getTimestamp("created_at");
                row[9] = rs.getTimestamp("updated_at");
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        roundedPanel5 = new Custom.RoundedPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonTambah = new javax.swing.JButton(); // Mengganti nama jButton4
        jButtonEdit = new javax.swing.JButton();   // Mengganti nama jButton3
        jButtonHapus = new javax.swing.JButton();  // Mengganti nama jButton1
        roundedPanel8 = new Custom.RoundedPanel();
        jLabel8 = new javax.swing.JLabel();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        roundedPanel6 = new Custom.RoundedPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();

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

        roundedPanel5.setBackground(new java.awt.Color(124, 124, 124));
        roundedPanel5.setRoundTopLeft(25);
        roundedPanel5.setRoundTopRight(25);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}, // Tambah satu kolom untuk "Select"
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Select", "ID", "ID Crew", "Tanggal Gaji", "Jumlah Gaji", "Bonus", "Tanggal Pembayaran", "Keterangan", "Created At", "Update At" // Tambah "Select" dan "Bonus"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false // Kolom "Select" bisa diedit
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        // Tombol "Tambah" (sebelumnya jButton4)
        jButtonTambah.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonTambah.setForeground(new java.awt.Color(46, 51, 55));
        jButtonTambah.setText("Tambah");
        jButtonTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahActionPerformed(evt); // Menambahkan aksi untuk tombol Tambah
            }
        });

        // Tombol "Edit" (sebelumnya jButton3)
        jButtonEdit.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonEdit.setForeground(new java.awt.Color(46, 51, 55));
        jButtonEdit.setText("Edit");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt); // Menambahkan aksi untuk tombol Edit
            }
        });

        // Tombol "Hapus" (sebelumnya jButton1)
        jButtonHapus.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonHapus.setForeground(new java.awt.Color(46, 51, 55));
        jButtonHapus.setText("Hapus");
        jButtonHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusActionPerformed(evt); // Menggunakan nama yang lebih jelas
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
                        .addComponent(jButtonTambah)
                        .addGap(12, 12, 12)
                        .addComponent(jButtonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonHapus))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 966, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonHapus)
                    .addComponent(jButtonEdit)
                    .addComponent(jButtonTambah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1))
        );

        roundedPanel8.setBackground(new java.awt.Color(46, 51, 55));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/150 no back.png"))); // NOI18N
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jButton30.setBackground(new java.awt.Color(251, 200, 42));
        jButton30.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton30.setText("Kalender Event");
        jButton30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setBackground(new java.awt.Color(251, 200, 42));
        jButton31.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton31.setText("Daftar Paket");
        jButton31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setBackground(new java.awt.Color(251, 200, 42));
        jButton32.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton32.setText("Inventaris");
        jButton32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setBackground(new java.awt.Color(251, 200, 42));
        jButton33.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton33.setText("Karyawan");
        jButton33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton34.setBackground(new java.awt.Color(251, 200, 42));
        jButton34.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton34.setText("Penggajian");
        jButton34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel8Layout = new javax.swing.GroupLayout(roundedPanel8);
        roundedPanel8.setLayout(roundedPanel8Layout);
        roundedPanel8Layout.setHorizontalGroup(
            roundedPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel8Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(roundedPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(15, 15, 15))
        );
        roundedPanel8Layout.setVerticalGroup(
            roundedPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel8)
                .addGap(55, 55, 55)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        roundedPanel6.setBackground(new java.awt.Color(46, 51, 55));
        roundedPanel6.setRoundBottomRight(25);
        roundedPanel6.setRoundTopRight(25);

        jLabel9.setFont(new java.awt.Font("SansSerif", 3, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(251, 190, 1));
        jLabel9.setText("Moria Sound Lighting");

        javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel9)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("SansSerif", 3, 35)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(46, 51, 55));
        jLabel3.setText("P E N G G A J I A N");

        jButton2.setBackground(new java.awt.Color(251, 190, 1));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jButton2.setText("X");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(119, 119, 119)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(roundedPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator3)
                    .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(roundedPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // Simpan posisi awal mouse saat diklik
    xMouse = evt.getX();
    yMouse = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // Geser window mengikuti pergerakan mouse
    this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }//GEN-LAST:event_formMouseDragged

    // Aksi untuk tombol Hapus (sebelumnya jButton1ActionPerformed)
    private void jButtonHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHapusActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int rowCount = model.getRowCount();
        boolean anySelected = false;
        
        java.util.List<Integer> idsToDelete = new java.util.ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Kolom "Select" adalah kolom 0
            if (isSelected != null && isSelected) {
                idsToDelete.add((Integer) model.getValueAt(i, 1)); // Kolom "ID" adalah kolom 1
                anySelected = true;
            }
        }

        if (!anySelected) {
            JOptionPane.showMessageDialog(this, "Pilih setidaknya satu gaji untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus gaji yang dipilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DbConnection.getConnection()) {
                String deleteQuery = "DELETE FROM gaji WHERE id_gaji = ?";
                PreparedStatement pst = conn.prepareStatement(deleteQuery);
                boolean success = true;
                for (Integer id : idsToDelete) {
                    pst.setInt(1, id);
                    int affectedRows = pst.executeUpdate();
                    if (affectedRows == 0) {
                        success = false;
                        JOptionPane.showMessageDialog(this, "Gagal menghapus gaji dengan ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (success) {
                    JOptionPane.showMessageDialog(this, "Gaji berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    populateTable(); // Refresh tabel setelah penghapusan
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saat menghapus data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonHapusActionPerformed

    // Aksi untuk tombol Tambah
    private void jButtonTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahActionPerformed
        // Logika untuk membuka form tambah gaji
        // Karena Anda ingin tetap di UI yang sama, Anda bisa menggunakan JOptionPane untuk input
        // atau membuat panel tersembunyi yang muncul.
        // Untuk contoh ini, saya akan menggunakan JOptionPane.

        // Meminta input untuk setiap field
        String idCrew = JOptionPane.showInputDialog(this, "Masukkan ID Crew:");
        if (idCrew == null || idCrew.trim().isEmpty()) return; // Jika dibatalkan atau kosong

        String tanggalGajiStr = JOptionPane.showInputDialog(this, "Masukkan Tanggal Gaji (YYYY-MM-DD):");
        if (tanggalGajiStr == null || tanggalGajiStr.trim().isEmpty()) return;
        java.sql.Date tanggalGaji = null;
        try {
            tanggalGaji = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(tanggalGajiStr).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format Tanggal Gaji salah. Gunakan YYYY-MM-DD.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String jumlahGajiStr = JOptionPane.showInputDialog(this, "Masukkan Jumlah Gaji:");
        if (jumlahGajiStr == null || jumlahGajiStr.trim().isEmpty()) return;
        double jumlahGaji;
        try {
            jumlahGaji = Double.parseDouble(jumlahGajiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Gaji harus angka.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String bonusStr = JOptionPane.showInputDialog(this, "Masukkan Bonus:");
        if (bonusStr == null || bonusStr.trim().isEmpty()) return;
        double bonus;
        try {
            bonus = Double.parseDouble(bonusStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bonus harus angka.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tanggalPembayaranStr = JOptionPane.showInputDialog(this, "Masukkan Tanggal Pembayaran (YYYY-MM-DD):");
        if (tanggalPembayaranStr == null || tanggalPembayaranStr.trim().isEmpty()) return;
        java.sql.Date tanggalPembayaran = null;
        try {
            tanggalPembayaran = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(tanggalPembayaranStr).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format Tanggal Pembayaran salah. Gunakan YYYY-MM-DD.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String keterangan = JOptionPane.showInputDialog(this, "Masukkan Keterangan:");
        if (keterangan == null) keterangan = ""; // Jika dibatalkan, set kosong

        // Lakukan insert ke database
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO gaji (id_crew, tanggal_gaji, jumlah_gaji, bonus, tanggal_pembayaran, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idCrew);
            pst.setDate(2, tanggalGaji);
            pst.setDouble(3, jumlahGaji);
            pst.setDouble(4, bonus);
            pst.setDate(5, tanggalPembayaran);
            pst.setString(6, keterangan);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data gaji berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                populateTable(); // Refresh tabel setelah penambahan
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data gaji.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonTambahActionPerformed

    // Aksi untuk tombol Edit (Logika edit langsung di dalam MenuGaji)
    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        int selectedRow = -1;
        // Cari baris yang dicentang
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            Boolean isSelected = (Boolean) jTable1.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                if (selectedRow != -1) { // Jika lebih dari satu baris dicentang
                    JOptionPane.showMessageDialog(this, "Pilih hanya satu gaji untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedRow = i;
            }
        }

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satu gaji untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ambil data yang ada dari baris yang dipilih
        int idGaji = (int) jTable1.getValueAt(selectedRow, 1);
        String currentIdCrew = (String) jTable1.getValueAt(selectedRow, 2);
        Date currentTanggalGaji = (Date) jTable1.getValueAt(selectedRow, 3);
        double currentJumlahGaji = (double) jTable1.getValueAt(selectedRow, 4);
        double currentBonus = (double) jTable1.getValueAt(selectedRow, 5);
        Date currentTanggalPembayaran = (Date) jTable1.getValueAt(selectedRow, 6);
        String currentKeterangan = (String) jTable1.getValueAt(selectedRow, 7);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Meminta input baru untuk setiap field
        String newIdCrew = JOptionPane.showInputDialog(this, "Edit ID Crew:", currentIdCrew);
        if (newIdCrew == null) return; // Jika dibatalkan

        String newTanggalGajiStr = JOptionPane.showInputDialog(this, "Edit Tanggal Gaji (YYYY-MM-DD):", sdf.format(currentTanggalGaji));
        if (newTanggalGajiStr == null) return;
        java.sql.Date newTanggalGaji = null;
        try {
            newTanggalGaji = new java.sql.Date(sdf.parse(newTanggalGajiStr).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format Tanggal Gaji salah. Gunakan YYYY-MM-DD.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newJumlahGajiStr = JOptionPane.showInputDialog(this, "Edit Jumlah Gaji:", String.valueOf(currentJumlahGaji));
        if (newJumlahGajiStr == null) return;
        double newJumlahGaji;
        try {
            newJumlahGaji = Double.parseDouble(newJumlahGajiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Gaji harus angka.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newBonusStr = JOptionPane.showInputDialog(this, "Edit Bonus:", String.valueOf(currentBonus));
        if (newBonusStr == null) return;
        double newBonus;
        try {
            newBonus = Double.parseDouble(newBonusStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bonus harus angka.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newTanggalPembayaranStr = JOptionPane.showInputDialog(this, "Edit Tanggal Pembayaran (YYYY-MM-DD):", sdf.format(currentTanggalPembayaran));
        if (newTanggalPembayaranStr == null) return;
        java.sql.Date newTanggalPembayaran = null;
        try {
            newTanggalPembayaran = new java.sql.Date(sdf.parse(newTanggalPembayaranStr).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format Tanggal Pembayaran salah. Gunakan YYYY-MM-DD.", "Error Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newKeterangan = JOptionPane.showInputDialog(this, "Edit Keterangan:", currentKeterangan);
        if (newKeterangan == null) newKeterangan = currentKeterangan; // Jika dibatalkan, gunakan nilai lama

        // Lakukan update ke database
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE gaji SET id_crew = ?, tanggal_gaji = ?, jumlah_gaji = ?, bonus = ?, tanggal_pembayaran = ?, keterangan = ?, updated_at = CURRENT_TIMESTAMP WHERE id_gaji = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newIdCrew);
            pst.setDate(2, newTanggalGaji);
            pst.setDouble(3, newJumlahGaji);
            pst.setDouble(4, newBonus);
            pst.setDate(5, newTanggalPembayaran);
            pst.setString(6, newKeterangan);
            pst.setInt(7, idGaji);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data gaji berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                populateTable(); // Refresh tabel setelah update
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data gaji.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonEditActionPerformed


    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        BerandaBaru menu = new BerandaBaru();
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        MenuJadwal jadwal = new MenuJadwal();
        jadwal.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        DaftarPaket paket = new DaftarPaket();
        paket.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        MenuInventaris inventaris = new MenuInventaris();
        inventaris.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        MenuGaji gaji = new MenuGaji();
        gaji.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        DaftarKaryawan crew = new DaftarKaryawan();
        crew.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton33ActionPerformed

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
            java.util.logging.Logger.getLogger(MenuGaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuGaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuGaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuGaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuGaji().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonHapus; // Mengganti nama jButton1
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonEdit;   // Mengganti nama jButton3
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButtonTambah; // Mengganti nama jButton4
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTable jTable1;
    private Custom.RoundedPanel roundedPanel5;
    private Custom.RoundedPanel roundedPanel6;
    private Custom.RoundedPanel roundedPanel8;
    // End of variables declaration//GEN-END:variables
}