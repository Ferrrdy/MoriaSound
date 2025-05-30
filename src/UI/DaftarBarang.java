/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;
import java.awt.geom.RoundRectangle2D;
import DataBase.DbConnection;
import java.awt.Color;
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
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        
    loadTableBarang(); // Panggil fungsi untuk load data ke tabel
    }

    private void loadTableBarang() {
    String[] kolom = {"Select", "ID", "Nama Barang", "ID Kategori", "Kondisi", "Jumlah Total", "Jumlah Tersedia", "Created At", "Updated At"};
    DefaultTableModel model = new DefaultTableModel(null, kolom) {
        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : super.getColumnClass(column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) return true;
            if (column == 2 || column == 5) return true;
            return false;
        }
    };
    jTable1.setModel(model);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    
    String sql = "SELECT id_barang, nama_barang, id_kategori, kondisi, jumlah_total, jumlah_tersedia, created_at, updated_at FROM barang ORDER BY id_barang ASC";

        try (Connection conn = DbConnection.getConnection(); // Mendapatkan koneksi dari DbConnection
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Looping untuk membaca setiap baris hasil query
            while (rs.next()) {
                // Mengambil data dari setiap kolom di baris saat ini
                boolean isSelected = false; // Default untuk checkbox
                int idBarang = rs.getInt("id_barang");
                String namaBarang = rs.getString("nama_barang");
                int idKategori = rs.getInt("id_kategori");
                String kondisi = rs.getString("kondisi");
                int jumlahTotal = rs.getInt("jumlah_total");
                int jumlahTersedia = rs.getInt("jumlah_tersedia");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp updatedAt = rs.getTimestamp("updated_at");

                // Menambahkan baris baru ke model tabel dengan data yang sudah diambil
                model.addRow(new Object[]{isSelected, idBarang, namaBarang, idKategori, kondisi, jumlahTotal, jumlahTersedia, createdAt, updatedAt});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data barang dari database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Mencetak stack trace error ke console untuk debugging
        }

// Kolom yang rata tengah (kecuali 0 = checkbox, 2 = nama, 6 & 7 = created/updated)
for (int i = 1; i < jTable1.getColumnCount(); i++) {
    if (i != 2 && i != 7 && i != 8) {
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
    selectColumn.setResizable(false); 
   // Checkbox
    columnModel.getColumn(1).setPreferredWidth(30);   // ID
    columnModel.getColumn(2).setPreferredWidth(80);   // Nama Barang
    columnModel.getColumn(3).setPreferredWidth(120);  // ID Kategori
    columnModel.getColumn(4).setPreferredWidth(150);  // Kondisi
    columnModel.getColumn(5).setPreferredWidth(150);  // Jumlah Total
    columnModel.getColumn(6).setPreferredWidth(150);  // Jumlah Tersedia
    columnModel.getColumn(7).setPreferredWidth(150);  // Created At
    columnModel.getColumn(8).setPreferredWidth(150);  // Updated At
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

       javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundedPanel5Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(12, 12, 12)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 973, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1))
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
                .addContainerGap(58, Short.MAX_VALUE))
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
        jLabel3.setText("D A F T A R   B A R A N G");

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
                .addGap(79, 79, 79)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(roundedPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator3)
                    .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(roundedPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
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
        // Simpan posisi awal mouse saat diklik
    xMouse = evt.getX();
    yMouse = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // Geser window mengikuti pergerakan mouse
    this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }//GEN-LAST:event_formMouseDragged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       System.out.println("DaftarBarang: Tombol Hapus diklik.");

       DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
       java.util.ArrayList<Integer> idsToDelete = new java.util.ArrayList<>();

       // Kumpulkan ID barang dari baris yang checkbox "Select"-nya dicentang
       for (int i = 0; i < model.getRowCount(); i++) {
           Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Kolom 0 adalah checkbox "Select"
           if (isSelected != null && isSelected) {
               idsToDelete.add((Integer) model.getValueAt(i, 1)); // Kolom 1 adalah ID Barang
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
           Connection conn = null; // Deklarasikan di luar try-catch-finally agar bisa diakses di finally
           PreparedStatement pstmt = null;

           try {
               conn = DbConnection.getConnection();
               // Penting untuk efisiensi jika menghapus banyak: nonaktifkan auto-commit
               conn.setAutoCommit(false); 

               pstmt = conn.prepareStatement(sql);

               for (Integer idBarang : idsToDelete) {
                   pstmt.setInt(1, idBarang);
                   pstmt.addBatch(); // Tambahkan ke batch untuk eksekusi sekaligus
               }

               int[] batchResults = pstmt.executeBatch(); // Eksekusi batch
               conn.commit(); // Commit transaksi jika semua berhasil

               for (int result : batchResults) {
                   if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) { // SUCCESS_NO_INFO untuk beberapa driver jika jumlah baris tidak diketahui
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
                       conn.rollback(); // Rollback jika terjadi error
                   } catch (SQLException ex) {
                       ex.printStackTrace();
                   }
               }
               JOptionPane.showMessageDialog(this, "Gagal menghapus barang dari database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
               e.printStackTrace();
           } finally {
               // Pastikan PreparedStatement dan Connection ditutup
               if (pstmt != null) {
                   try {
                       pstmt.close();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
               if (conn != null) {
                   try {
                       conn.setAutoCommit(true); // Kembalikan ke mode auto-commit default
                       conn.close();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
               loadTableBarang(); // Muat ulang tabel untuk menampilkan perubahan, baik berhasil maupun gagal (untuk refresh)
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
        DaftarPaket paket = new DaftarPaket();
        paket.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        MenuInventaris inventaris = new MenuInventaris();
        inventaris.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        DaftarKaryawan crew = new DaftarKaryawan();
        crew.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        MenuGaji gaji = new MenuGaji();
        gaji.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        System.out.println("DaftarBarang: Tombol Tambah diklik."); // Debugging

        // Membuat dan menampilkan form dalam mode TAMBAH.
        // Pastikan nama kelasnya FormKelolaBarang atau sesuai dengan yang Anda buat.
        FormKelolaBarang formKelola = new FormKelolaBarang(this, true); // Memanggil konstruktor mode Tambah
        formKelola.setVisible(true);

        // Setelah formKelola ditutup, cek apakah operasi INSERT di dalamnya berhasil
        System.out.println("DaftarBarang: FormKelolaBarang (Tambah) ditutup. Status DB Op Sukses: " + formKelola.isDBOperationSuccess()); // Debugging
        if (formKelola.isDBOperationSuccess()) {
            loadTableBarang(); // Jika berhasil, muat ulang tabel untuk menampilkan data baru
            System.out.println("DaftarBarang: Tabel dimuat ulang setelah tambah berhasil."); // Debugging
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        System.out.println("DaftarBarang: Tombol Edit diklik.");

        int selectedRow = -1;
        int selectedRowCount = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if ((Boolean) jTable1.getValueAt(i, 0)) { // Cek checkbox
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

        int idBarang = (Integer) jTable1.getValueAt(selectedRow, 1); // Kolom ID Barang
        String nama = (String) jTable1.getValueAt(selectedRow, 2);   // Kolom Nama Barang
        int idKat = (Integer) jTable1.getValueAt(selectedRow, 3);  // Kolom ID Kategori
        String kondisi = (String) jTable1.getValueAt(selectedRow, 4); // Kolom Kondisi
        int jumlahTotal = (Integer) jTable1.getValueAt(selectedRow, 5); // Kolom Jumlah Total
        int jumlahTersedia = (Integer) jTable1.getValueAt(selectedRow, 6); // Kolom Jumlah Total


        System.out.println("DaftarBarang: Membuka FormKelolaBarang mode EDIT untuk ID: " + idBarang);

        FormKelolaBarang formKelola = new FormKelolaBarang(this, true, idBarang, nama, idKat, jumlahTotal, jumlahTersedia, kondisi);

        formKelola.setVisible(true);

        System.out.println("DaftarBarang: FormKelolaBarang (Edit) ditutup. Status DB Op Sukses: " + formKelola.isDBOperationSuccess());
        if (formKelola.isDBOperationSuccess()) {
            loadTableBarang();
            System.out.println("DaftarBarang: Tabel dimuat ulang setelah edit berhasil.");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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
