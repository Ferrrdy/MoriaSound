/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;
import java.awt.geom.RoundRectangle2D;
import DataBase.DbConnection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author nabil
 */
public class DaftarBarang extends javax.swing.JFrame {
    int xMouse, yMouse;
    /**
     * Creates new form DaftarBarang
     */
    public DaftarBarang() {
        initComponents();
        setSize(1320, 720); // Tetapkan ukuran tetap
        setMinimumSize(new Dimension(1320, 720));
        setMaximumSize(new Dimension(1320, 720));
        setPreferredSize(new Dimension(1320, 720));
        setResizable(false);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        
        loadTableBarang(); 
    }

    private void loadTableBarang() {
    // Langkah 2: Perbarui Nama Kolom untuk menyertakan data kerusakan
    String[] kolom = {"Select", "ID", "Nama Barang", "Nama Kategori", "Kondisi", "Jumlah Total", "Jumlah Tersedia", "Rusak Ringan", "Rusak Berat", "Hilang", "Created At", "Updated At"};
    DefaultTableModel model = new DefaultTableModel(null, kolom) {
        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : super.getColumnClass(column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            // Hanya checkbox yang bisa di-klik langsung di tabel
            return column == 0;
        }
    };
    jTable1.setModel(model);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);

    // Langkah 1: Ubah Query SQL untuk mengambil kolom kerusakan
    String sql = "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.kondisi, b.jumlah_total, b.jumlah_tersedia, " +
                 "b.jumlah_rusak_ringan, b.jumlah_rusak_berat, b.jumlah_hilang, b.created_at, b.updated_at " + // <-- TAMBAHKAN KOLOM INI
                 "FROM barang b " +
                 "JOIN kategori k ON b.id_kategori = k.id_kategori " +
                 "ORDER BY b.id_barang ASC";

    try (Connection conn = DbConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            boolean isSelected = false; // Default untuk checkbox
            int idBarang = rs.getInt("id_barang");
            String namaBarang = rs.getString("nama_barang");
            String namaKategori = rs.getString("nama_kategori");
            String kondisi = rs.getString("kondisi");
            int jumlahTotal = rs.getInt("jumlah_total");
            int jumlahTersedia = rs.getInt("jumlah_tersedia");

            // Langkah 3: Ambil Data Baru dari ResultSet
            int jumlahRusakRingan = rs.getInt("jumlah_rusak_ringan");
            int jumlahRusakBerat = rs.getInt("jumlah_rusak_berat");
            int jumlahHilang = rs.getInt("jumlah_hilang");

            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp updatedAt = rs.getTimestamp("updated_at");

            // Langkah 4: Masukkan semua data, termasuk yang baru, ke dalam baris tabel
            model.addRow(new Object[]{isSelected, idBarang, namaBarang, namaKategori, kondisi, jumlahTotal, jumlahTersedia, jumlahRusakRingan, jumlahRusakBerat, jumlahHilang, createdAt, updatedAt});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data barang dari database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Langkah 5: Sesuaikan perataan (centering) dengan indeks kolom yang baru
    for (int i = 1; i < jTable1.getColumnCount(); i++) {
        // Kolom 'Nama Barang' (indeks 2), 'Created At' (indeks 10), 'Updated At' (indeks 11) tidak di-center
        if (i != 2 && i != 10 && i != 11) { 
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    jTable1.setBackground(Color.WHITE);

    JTableHeader header = jTable1.getTableHeader();
    header.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
    ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

    TableColumnModel columnModel = jTable1.getColumnModel();
    TableColumn selectColumn = columnModel.getColumn(0);
    selectColumn.setPreferredWidth(50);
    selectColumn.setMaxWidth(50);
    selectColumn.setMinWidth(50);
    selectColumn.setResizable(false);

    // Langkah 5: Sesuaikan lebar kolom dengan adanya kolom baru
    columnModel.getColumn(1).setPreferredWidth(30);   // ID
    columnModel.getColumn(2).setPreferredWidth(120);  // Nama Barang
    columnModel.getColumn(3).setPreferredWidth(100);  // Nama Kategori
    columnModel.getColumn(4).setPreferredWidth(80);   // Kondisi
    columnModel.getColumn(5).setPreferredWidth(85);   // Jumlah Total
    columnModel.getColumn(6).setPreferredWidth(100);  // Jumlah Tersedia
    columnModel.getColumn(7).setPreferredWidth(85);   // Rusak Ringan (BARU)
    columnModel.getColumn(8).setPreferredWidth(85);   // Rusak Berat (BARU)
    columnModel.getColumn(9).setPreferredWidth(60);   // Hilang (BARU)
    columnModel.getColumn(10).setPreferredWidth(150); // Created At (Indeks berubah)
    columnModel.getColumn(11).setPreferredWidth(150); // Updated At (Indeks berubah)
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
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        roundedPanel9 = new Custom.RoundedPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        roundedPanel6 = new Custom.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
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

        jButton4.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(46, 51, 55));
        jButton4.setText("Tambah");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(46, 51, 55));
        jButton3.setText("Edit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(46, 51, 55));
        jButton1.setText("Hapus");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(251, 200, 42));
        jButton5.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton5.setText("<");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(roundedPanel5Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(12, 12, 12)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 968, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
        );

        roundedPanel9.setBackground(new java.awt.Color(46, 51, 55));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/150 no back.png"))); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jButton35.setBackground(new java.awt.Color(251, 200, 42));
        jButton35.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton35.setText("Kalender Event");
        jButton35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton36.setBackground(new java.awt.Color(251, 200, 42));
        jButton36.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton36.setText("Daftar Paket");
        jButton36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setBackground(new java.awt.Color(251, 200, 42));
        jButton37.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton37.setText("Inventaris");
        jButton37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton38.setBackground(new java.awt.Color(251, 200, 42));
        jButton38.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton38.setText("Karyawan");
        jButton38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton39.setBackground(new java.awt.Color(251, 200, 42));
        jButton39.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton39.setText("Penggajian");
        jButton39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel9Layout = new javax.swing.GroupLayout(roundedPanel9);
        roundedPanel9.setLayout(roundedPanel9Layout);
        roundedPanel9Layout.setHorizontalGroup(
            roundedPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel9Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(roundedPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(15, 15, 15))
        );
        roundedPanel9Layout.setVerticalGroup(
            roundedPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel9Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel9)
                .addGap(55, 55, 55)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        jLabel3.setText("D A F T A R   B A R A N G   T E R S E D I A");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(roundedPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator3)
                            .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(35, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(194, 194, 194))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(9, 9, 9)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(roundedPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }//GEN-LAST:event_formMouseDragged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("DaftarBarang: Tombol Hapus diklik.");

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        java.util.ArrayList<Integer> idsToDelete = new java.util.ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0); 
            if (isSelected != null && isSelected) {
                idsToDelete.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (idsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih setidaknya satu barang untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus " + idsToDelete.size() + " barang yang dipilih?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM barang WHERE id_barang = ?";
            int successfulDeletes = 0;
            Connection conn = null; 
            PreparedStatement pstmt = null;

            try {
                conn = DbConnection.getConnection();
                conn.setAutoCommit(false); 

                pstmt = conn.prepareStatement(sql);

                for (Integer idBarang : idsToDelete) {
                    pstmt.setInt(1, idBarang);
                    pstmt.addBatch();
                }

                int[] batchResults = pstmt.executeBatch(); 
                conn.commit(); 
                for (int result : batchResults) {
                    if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) { 
                        successfulDeletes++;
                    }
                }

                if (successfulDeletes > 0) {
                    JOptionPane.showMessageDialog(this, successfulDeletes + " barang berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Tidak ada barang yang dihapus (mungkin sudah terhapus atau ID tidak ditemukan).", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        System.err.println("Transaksi di-rollback karena error.");
                        conn.rollback(); 
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(this, "Gagal menghapus barang dari database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); 
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                loadTableBarang(); 
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        BerandaBaru menu = new BerandaBaru();
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        MenuJadwal jadwal = new MenuJadwal();
        jadwal.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        DaftarPaketBaru paket = new DaftarPaketBaru();
        paket.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        MenuInventarisBaru inventaris = new MenuInventarisBaru();
        inventaris.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        DaftarKaryawanBaru crew = new DaftarKaryawanBaru();
        crew.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        MenuGajiBaru gaji = new MenuGajiBaru();
        gaji.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        System.out.println("DaftarBarang: Tombol Tambah diklik."); 

        FormKelolaBarang formKelola = new FormKelolaBarang(this, true);
        formKelola.setVisible(true);

        System.out.println("DaftarBarang: FormKelolaBarang (Tambah) ditutup. Status DB Op Sukses: " + formKelola.isDBOperationSuccess());
        if (formKelola.isDBOperationSuccess()) {
            loadTableBarang(); 
            System.out.println("DaftarBarang: Tabel dimuat ulang setelah tambah berhasil."); 
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        System.out.println("DaftarBarang: Tombol Edit diklik.");

        int selectedRow = -1;
        int selectedRowCount = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if ((Boolean) jTable1.getValueAt(i, 0)) {
                selectedRowCount++;
                selectedRow = i;
            }
        }

        if (selectedRowCount == 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu barang untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRowCount > 1) {
            JOptionPane.showMessageDialog(this, "Hanya bisa mengedit satu barang dalam satu waktu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- PERUBAHAN UNTUK FUNGSI EDIT ---
        
        int idBarang = (Integer) jTable1.getValueAt(selectedRow, 1);
        String nama = (String) jTable1.getValueAt(selectedRow, 2);
        String namaKategoriDariTabel = (String) jTable1.getValueAt(selectedRow, 3); // Ambil NAMA kategori (String)
        String kondisi = (String) jTable1.getValueAt(selectedRow, 4);
        int jumlahTotal = (Integer) jTable1.getValueAt(selectedRow, 5);
        int jumlahTersedia = (Integer) jTable1.getValueAt(selectedRow, 6);
        
        int idKat = -1; // Nilai default jika tidak ditemukan

        // Query untuk mendapatkan ID Kategori dari nama kategori
        String getIdSql = "SELECT id_kategori FROM kategori WHERE nama_kategori = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getIdSql)) {
            
            pstmt.setString(1, namaKategoriDariTabel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                idKat = rs.getInt("id_kategori");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mendapatkan ID Kategori: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return; // Hentikan jika gagal
        }

        if (idKat == -1) {
            JOptionPane.showMessageDialog(this, "Kategori '" + namaKategoriDariTabel + "' tidak ditemukan di database.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Hentikan jika ID tidak valid
        }

        System.out.println("DaftarBarang: Membuka FormKelolaBarang mode EDIT untuk ID: " + idBarang);
        
        // Kirim idKat (int) yang sudah didapatkan ke form
        FormKelolaBarang formKelola = new FormKelolaBarang(this, true, idBarang, nama, idKat, jumlahTotal, jumlahTersedia, kondisi);
        formKelola.setVisible(true);
        
        // --- AKHIR PERUBAHAN ---

        System.out.println("DaftarBarang: FormKelolaBarang (Edit) ditutup. Status DB Op Sukses: " + formKelola.isDBOperationSuccess());
        if (formKelola.isDBOperationSuccess()) {
            loadTableBarang();
            System.out.println("DaftarBarang: Tabel dimuat ulang setelah edit berhasil.");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        MenuInventarisBaru inventaris = new MenuInventarisBaru();
        inventaris.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(DaftarBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DaftarBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DaftarBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DaftarBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DaftarBarang().setVisible(true);
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
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTable jTable1;
    private Custom.RoundedPanel roundedPanel5;
    private Custom.RoundedPanel roundedPanel6;
    private Custom.RoundedPanel roundedPanel9;
    // End of variables declaration//GEN-END:variables
}