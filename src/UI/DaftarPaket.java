package UI;

import Custom.InfoPaketSound;
import Custom.KartuPaketUI;   
import DataBase.DbConnection; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon; 
import javax.swing.JFrame;   
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DaftarPaket extends javax.swing.JFrame {
    int xMouse, yMouse;
    private JPanel panelKontenPaket;

    public DaftarPaket() {
        initComponents();
        setLocationRelativeTo(null);
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        } catch (Exception e) {
            System.err.println("Error setting shape: " + e.getMessage());
        }
        setupPanelDaftarPaket();
        muatDanTampilkanPaketDariUIDenganDB();
    }

    private void setupPanelDaftarPaket() {
        roundedPanel3.setLayout(new BorderLayout());
        roundedPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelKontenPaket = new JPanel();
        panelKontenPaket.setLayout(new BoxLayout(panelKontenPaket, BoxLayout.Y_AXIS));
        panelKontenPaket.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(panelKontenPaket);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        roundedPanel3.add(scrollPane, BorderLayout.CENTER);
    }

    private List<InfoPaketSound> ambilDataPaketLangsungDariDB() {
        List<InfoPaketSound> daftarPaket = new ArrayList<>();
        Connection conn = null;
        Statement stmtPaket = null;
        ResultSet rsPaket = null;
        PreparedStatement pstmtDetail = null;
        ResultSet rsDetail = null;

        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                System.err.println("Gagal mendapatkan koneksi dari DbConnection.getConnection() di DaftarPaket");
                daftarPaket.add(new InfoPaketSound("ERR-CONN", "Koneksi DB Gagal", "Tidak bisa ambil data paket.", "- Error koneksi -", 0));
                return daftarPaket;
            }

            String queryPaket = "SELECT id_paket, nama_paket, harga, keterangan FROM paket ORDER BY nama_paket ASC";
            stmtPaket = conn.createStatement();
            rsPaket = stmtPaket.executeQuery(queryPaket);

            while (rsPaket.next()) {
                String idPaket = rsPaket.getString("id_paket");
                String namaPaket = rsPaket.getString("nama_paket");
                double hargaPaket = rsPaket.getDouble("harga");
                String deskripsiPaket = rsPaket.getString("keterangan");

                System.out.println("Memproses paket dari DB: " + namaPaket + " (ID: " + idPaket + ")"); 

                StringBuilder detailItemFormattedSb = new StringBuilder();
                String queryDetailItem = "SELECT pb.jumlah, b.nama_barang " +
                                         "FROM paket_barang pb " +
                                         "JOIN barang b ON pb.id_barang = b.id_barang " +
                                         "WHERE pb.id_paket = ?";
                
                pstmtDetail = conn.prepareStatement(queryDetailItem);
                pstmtDetail.setString(1, idPaket);
                System.out.println("  Executing query detail item: " + pstmtDetail); 
                rsDetail = pstmtDetail.executeQuery();

                boolean adaItemDiPaketIni = false;
                while (rsDetail.next()) {
                    adaItemDiPaketIni = true;
                    int jumlahItem = rsDetail.getInt("jumlah");
                    String namaItem = rsDetail.getString("nama_barang");
                    System.out.println("    -> Item ditemukan: " + jumlahItem + "x " + namaItem); 
                    detailItemFormattedSb.append("- ").append(jumlahItem).append("x ").append(namaItem).append("\n");
                }
                if (!adaItemDiPaketIni) {
                    System.out.println("    -> TIDAK ADA ITEM ditemukan di tabel paket_barang untuk paket ID: " + idPaket); 
                }
                
                if (rsDetail != null) rsDetail.close();
                if (pstmtDetail != null) pstmtDetail.close();

                String detailItemFinal = detailItemFormattedSb.toString().trim();
                if (detailItemFinal.isEmpty()) {
                    detailItemFinal = "(Belum ada item terdaftar dalam paket ini)";
                }
                 System.out.println("  -> Detail Item Final: " + detailItemFinal.replace("\n", " | ")); // DEBUG

                InfoPaketSound info = new InfoPaketSound(
                    idPaket,
                    namaPaket,
                    deskripsiPaket,
                    detailItemFinal,
                    hargaPaket
                );
                daftarPaket.add(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error saat mengambil data paket di DaftarPaket.java: " + e.getMessage());
            daftarPaket.clear();
            daftarPaket.add(new InfoPaketSound("ERR-SQL", "Gagal Load Data", "Terjadi kesalahan SQL.", "- Error SQL -", 0));
        } finally {
            try {
                if (rsDetail != null && !rsDetail.isClosed()) rsDetail.close();
                if (pstmtDetail != null && !pstmtDetail.isClosed()) pstmtDetail.close();
                if (rsPaket != null && !rsPaket.isClosed()) rsPaket.close();
                if (stmtPaket != null && !stmtPaket.isClosed()) stmtPaket.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error saat menutup resource JDBC di DaftarPaket.java: " + e.getMessage());
            }
        }
        System.out.println("Total paket yang diambil dari DB: " + daftarPaket.size()); 
        return daftarPaket;
    }

    private void muatDanTampilkanPaketDariUIDenganDB() {
        panelKontenPaket.removeAll(); 

        List<InfoPaketSound> daftarPaketDariDB = ambilDataPaketLangsungDariDB();

        if (daftarPaketDariDB == null || daftarPaketDariDB.isEmpty() || 
            (daftarPaketDariDB.size() == 1 && daftarPaketDariDB.get(0).getIdPaket().startsWith("ERR-"))) {
            
            JPanel panelPesanKosong = new JPanel(new BorderLayout());
            String pesan = "Tidak ada paket tersedia.";
            if (daftarPaketDariDB != null && !daftarPaketDariDB.isEmpty() && daftarPaketDariDB.get(0).getIdPaket().startsWith("ERR-")){
                pesan = daftarPaketDariDB.get(0).getNamaPaket() + "\n" + daftarPaketDariDB.get(0).getDeskripsiSingkat();
            }
            JLabel lblKosong = new JLabel("<html><div style='text-align: center;'>" + pesan.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
            lblKosong.setFont(new Font("SansSerif", Font.ITALIC, 16));
            panelPesanKosong.add(lblKosong, BorderLayout.CENTER);
            panelPesanKosong.setOpaque(false);
            panelKontenPaket.add(panelPesanKosong);
        } else {
            for (InfoPaketSound info : daftarPaketDariDB) {
                 if (info.getIdPaket().startsWith("ERR-")) continue; 
                KartuPaketUI kartu = new KartuPaketUI(info);
                kartu.setAlignmentX(Component.LEFT_ALIGNMENT);
                kartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, kartu.getPreferredSize().height + 20));
                panelKontenPaket.add(kartu);
                panelKontenPaket.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        panelKontenPaket.revalidate();
        panelKontenPaket.repaint();
    }

    private void bukaFrame(JFrame frameTujuan) {
        try {
            frameTujuan.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error membuka halaman: " + e.getMessage(), "Error Navigasi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        roundedPanel3 = new Custom.RoundedPanel();
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
        jButton2 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1320, 720));
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
        jPanel1.setPreferredSize(new java.awt.Dimension(1320, 720));

        roundedPanel3.setBackground(new java.awt.Color(124, 124, 124));
        roundedPanel3.setRoundTopLeft(25);
        roundedPanel3.setRoundTopRight(25);

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );

        roundedPanel9.setBackground(new java.awt.Color(46, 51, 55));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/150 no back.png"))); 
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jButton35.setBackground(new java.awt.Color(251, 200, 42));
        jButton35.setFont(new java.awt.Font("SansSerif", 3, 12)); 
        jButton35.setText("Kalender Event");
        jButton35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton35.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton36.setBackground(new java.awt.Color(251, 200, 42));
        jButton36.setFont(new java.awt.Font("SansSerif", 3, 12)); 
        jButton36.setText("Daftar Paket");
        jButton36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton36.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setBackground(new java.awt.Color(251, 200, 42));
        jButton37.setFont(new java.awt.Font("SansSerif", 3, 12)); 
        jButton37.setText("Inventaris");
        jButton37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton37.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton38.setBackground(new java.awt.Color(251, 200, 42));
        jButton38.setFont(new java.awt.Font("SansSerif", 3, 12)); 
        jButton38.setText("Karyawan");
        jButton38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton38.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton39.setBackground(new java.awt.Color(251, 200, 42));
        jButton39.setFont(new java.awt.Font("SansSerif", 3, 12)); 
        jButton39.setText("Penggajian");
        jButton39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jButton39.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addContainerGap(69, Short.MAX_VALUE)) 
        );

        roundedPanel6.setBackground(new java.awt.Color(46, 51, 55));
        roundedPanel6.setRoundBottomRight(25);
        roundedPanel6.setRoundTopRight(25);

        jLabel10.setFont(new java.awt.Font("SansSerif", 3, 20)); 
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

        jLabel3.setFont(new java.awt.Font("SansSerif", 3, 35)); 
        jLabel3.setForeground(new java.awt.Color(46, 51, 55));
        jLabel3.setText("D A F T A R   P A K E T");

        jButton2.setBackground(new java.awt.Color(251, 190, 1));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 18)); 
        jButton2.setText("X");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addComponent(roundedPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
                            .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(jLabel3)
                        .addGap(288, 288, 288)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3)
                        .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1320, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {                                  
        xMouse = evt.getX();
        yMouse = evt.getY();
    }                                 

    private void formMouseDragged(java.awt.event.MouseEvent evt) {                                  
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }                                 

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

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DaftarPaket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DaftarPaket().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator8;
    private Custom.RoundedPanel roundedPanel3;
    private Custom.RoundedPanel roundedPanel6;
    private Custom.RoundedPanel roundedPanel9;
    // End of variables declaration//GEN-END:variables
}