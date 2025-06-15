package UI; // Sesuaikan dengan nama package Anda

import java.awt.geom.RoundRectangle2D;
import DataBase.DbConnection; // Sesuaikan dengan nama package DbConnection Anda
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

// Import jika Custom.RoundedPanel berada di package berbeda
// import Custom.RoundedPanel; 

/**
 *
 * @author nabil
 */
public class MenuGaji extends javax.swing.JFrame {
    int xMouse, yMouse;

    // Tambahkan variabel untuk komponen UI pengeditan (sesuaikan dengan yang Anda tambahkan di Designer)
    // Contoh:
    private javax.swing.JTextField txtEditIdCrew;
    private javax.swing.JTextField txtEditTanggalGaji; // DIGANTI DARI JDateChooser
    private javax.swing.JTextField txtEditJumlahGaji;
    private javax.swing.JTextField txtEditBonus;
    private javax.swing.JTextField txtEditNomorRekening;
    private javax.swing.JTextField txtEditTanggalPembayaran; // DIGANTI DARI JDateChooser
    private javax.swing.JTextField txtEditKeterangan;
    private javax.swing.JButton jButtonSimpanEdit; // Tombol untuk menyimpan perubahan
    private javax.swing.JButton jButtonBatalEdit; // Tombol untuk membatalkan edit (opsional)
    private javax.swing.JLabel lblHiddenIdGaji; // Untuk menyimpan ID gaji yang sedang diedit (atau JTextField tersembunyi)

    // Untuk format tanggal string (misalnya "YYYY-MM-DD")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


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

    
    private void loadTableGaji() {
        String[] kolom = {"Select", "ID", "ID Crew", "Tanggal Gaji", "Jumlah Gaji", "Bonus", "Nomor Rekening", "Tanggal Pembayaran", "Keterangan", "Created At", "Updated At"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class; // Kolom "Select" adalah Boolean
                if (column == 1) return Integer.class; // ID Gaji
                if (column == 2) return Integer.class; // ID Crew
                // Untuk kolom lain, biarkan DefaultTableModel yang menentukan atau tentukan secara eksplisit jika perlu
                return super.getColumnClass(column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Hanya kolom "Select" yang editable
            }
        };
        jTable1.setModel(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < jTable1.getColumnCount(); i++) {
            if (i != 8) { // Kolom keterangan (indeks 8) mungkin lebih baik rata kiri
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        jTable1.setBackground(new java.awt.Color(255, 255, 255)); // Menggunakan warna dari desainer
        jTable1.setGridColor(new java.awt.Color(255, 255, 255)); // Menggunakan warna dari desainer

        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = jTable1.getColumnModel();
        TableColumn selectColumn = columnModel.getColumn(0);
        selectColumn.setPreferredWidth(50);
        selectColumn.setMaxWidth(50);
        selectColumn.setMinWidth(50);
        selectColumn.setResizable(false); // Checkbox
        columnModel.getColumn(1).setPreferredWidth(30); // ID
        columnModel.getColumn(2).setPreferredWidth(70);  // ID Crew (Integer)
        columnModel.getColumn(3).setPreferredWidth(80); // Tanggal Gaji
        columnModel.getColumn(4).setPreferredWidth(120); // Jumlah Gaji
        columnModel.getColumn(5).setPreferredWidth(80);  // Bonus
        columnModel.getColumn(6).setPreferredWidth(150); // Nomor Rekening (String)
        columnModel.getColumn(7).setPreferredWidth(100); // Tanggal Pembayaran
        columnModel.getColumn(8).setPreferredWidth(200); // Keterangan
        columnModel.getColumn(9).setPreferredWidth(150); // Created At
        columnModel.getColumn(10).setPreferredWidth(150);// Updated At
    }

    // Fungsi untuk mengisi data ke tabel dari database
    public void populateTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Kosongkan tabel sebelum mengisi ulang

        // Menggunakan nama tabel "gaji"
        String query = "SELECT id_gaji, id_crew, tanggal_gaji, jumlah_gaji, bonus, nomor_rekening, tanggal_pembayaran, keterangan, created_at, updated_at FROM gaji ORDER BY created_at DESC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[11];
                row[0] = false; // Kolom "Select" (checkbox)
                row[1] = rs.getInt("id_gaji");
                row[2] = rs.getInt("id_crew");
                row[3] = rs.getDate("tanggal_gaji");
                row[4] = rs.getDouble("jumlah_gaji");
                row[5] = rs.getDouble("bonus");
                row[6] = rs.getString("nomor_rekening");
                row[7] = rs.getDate("tanggal_pembayaran");
                row[8] = rs.getString("keterangan");
                row[9] = rs.getTimestamp("created_at");
                row[10] = rs.getTimestamp("updated_at");
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        roundedPanel5 = new Custom.RoundedPanel(); // Pastikan Custom.RoundedPanel ada
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonTambah = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonHapus = new javax.swing.JButton();
        roundedPanel8 = new Custom.RoundedPanel(); // Pastikan Custom.RoundedPanel ada
        jLabel8 = new javax.swing.JLabel(); // Logo Moria Sound Lighting
        jButton30 = new javax.swing.JButton(); // Kalender Event
        jButton31 = new javax.swing.JButton(); // Daftar Paket
        jButton32 = new javax.swing.JButton(); // Inventaris
        jButton33 = new javax.swing.JButton(); // Karyawan
        jButton34 = new javax.swing.JButton(); // Penggajian (Current Page)
        jSeparator7 = new javax.swing.JSeparator();
        roundedPanel6 = new Custom.RoundedPanel(); // Pastikan Custom.RoundedPanel ada
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton(); // Close Button

        // <editor-fold defaultstate="collapsed" desc="Tambahkan Deklarasi Komponen UI Edit di sini">
        // Deklarasikan variabel untuk komponen edit jika belum ada di sini
        // Misalnya:
        txtEditIdCrew = new javax.swing.JTextField();
        txtEditTanggalGaji = new javax.swing.JTextField(); // <--- DIGANTI
        txtEditJumlahGaji = new javax.swing.JTextField();
        txtEditBonus = new javax.swing.JTextField();
        txtEditNomorRekening = new javax.swing.JTextField();
        txtEditTanggalPembayaran = new javax.swing.JTextField(); // <--- DIGANTI
        txtEditKeterangan = new javax.swing.JTextField();
        jButtonSimpanEdit = new javax.swing.JButton();
        jButtonBatalEdit = new javax.swing.JButton();
        lblHiddenIdGaji = new javax.swing.JLabel(); // Untuk menyimpan ID gaji yang sedang diedit
        // </editor-fold>

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
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Select", "ID", "ID Crew", "Tanggal Gaji", "Jumlah Gaji", "Bonus", "Nomor Rekening", "Tanggal Pembayaran", "Keterangan", "Created At", "Updated At"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        jButtonTambah.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonTambah.setForeground(new java.awt.Color(46, 51, 55));
        jButtonTambah.setText("Tambah");
        jButtonTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahActionPerformed(evt);
            }
        });

        jButtonEdit.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonEdit.setForeground(new java.awt.Color(46, 51, 55));
        jButtonEdit.setText("Edit");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        jButtonHapus.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButtonHapus.setForeground(new java.awt.Color(46, 51, 55));
        jButtonHapus.setText("Hapus");
        jButtonHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusActionPerformed(evt);
            }
        });

        // <editor-fold defaultstate="collapsed" desc="Layout untuk komponen edit">
        // Anda perlu menambahkan layout untuk komponen edit di sini secara manual
        // atau melalui NetBeans Designer.
        // Contoh sederhana penambahan ke roundedPanel5 (ini hanya contoh, sesuaikan dengan desain Anda):
        // roundedPanel5Layout.setHorizontalGroup(
        //     roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        //     .addGroup(roundedPanel5Layout.createSequentialGroup()
        //         .addContainerGap(24, Short.MAX_VALUE)
        //         .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
        //             .addGroup(roundedPanel5Layout.createSequentialGroup()
        //                 .addComponent(jButtonTambah)
        //                 .addGap(12, 12, 12)
        //                 .addComponent(jButtonEdit)
        //                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        //                 .addComponent(jButtonHapus))
        //             .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 966, javax.swing.GroupLayout.PREFERRED_SIZE))
        //         .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Contoh penambahan area edit
        //             .addGroup(roundedPanel5Layout.createSequentialGroup()
        //                 .addComponent(txtEditIdCrew, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        //                 // ... tambahkan komponen edit lainnya
        //             )
        //             .addGroup(roundedPanel5Layout.createSequentialGroup()
        //                 .addComponent(jButtonSimpanEdit)
        //                 .addComponent(jButtonBatalEdit))
        //         )
        //         .addContainerGap(25, Short.MAX_VALUE))
        // );
        // roundedPanel5Layout.setVerticalGroup(
        //     roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
        //     .addGroup(roundedPanel5Layout.createSequentialGroup()
        //         .addGap(16, 16, 16)
        //         .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        //             .addComponent(jButtonHapus)
        //             .addComponent(jButtonEdit)
        //             .addComponent(jButtonTambah))
        //         .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        //         .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        //             .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
        //             .addGroup(roundedPanel5Layout.createSequentialGroup()
        //                 .addComponent(txtEditIdCrew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        //                 // ... tambahkan komponen edit lainnya
        //                 .addGap(18, 18, 18)
        //                 .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        //                     .addComponent(jButtonSimpanEdit)
        //                     .addComponent(jButtonBatalEdit))
        //             )
        //         )
        //         .addContainerGap())
        // );
        // </editor-fold>

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
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(roundedPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonHapus)
                    .addComponent(jButtonEdit)
                    .addComponent(jButtonTambah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE) // Sesuaikan jika perlu
                .addContainerGap())
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Disesuaikan agar konsisten
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
                .addComponent(roundedPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Diletakkan di kiri
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3)
                            .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adjusted for centering title area more generally
                        .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Adjusted
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED) // Disesuaikan dari 48 ke nilai yang lebih standar
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()) // Tambahkan ini jika roundedPanel5 adalah komponen terakhir di vertikal group ini
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
    }// </editor-fold>                        

    private void formMousePressed(java.awt.event.MouseEvent evt) {                                  
        xMouse = evt.getX();
        yMouse = evt.getY();
    }                                 

    private void formMouseDragged(java.awt.event.MouseEvent evt) {                                  
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }                                 

    private void jButtonHapusActionPerformed(java.awt.event.ActionEvent evt) {                                             
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int rowCount = model.getRowCount();
        boolean anySelected = false;

        List<Integer> idsToDelete = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                idsToDelete.add((Integer) model.getValueAt(i, 1)); // Kolom ID (id_gaji) adalah kolom ke-1
                anySelected = true;
            }
        }

        if (!anySelected) {
            JOptionPane.showMessageDialog(this, "Pilih setidaknya satu gaji untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus gaji yang dipilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Menggunakan nama tabel "gaji"
            String deleteQuery = "DELETE FROM gaji WHERE id_gaji = ?";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(deleteQuery)) {
                boolean allSuccess = true;
                for (Integer id : idsToDelete) {
                    pst.setInt(1, id);
                    int affectedRows = pst.executeUpdate();
                    if (affectedRows == 0) {
                        allSuccess = false;
                        // Mungkin tidak perlu pesan error per ID jika banyak, cukup satu di akhir
                        System.err.println("Gagal menghapus gaji dengan ID: " + id + ". Tidak ditemukan atau sudah terhapus.");
                    }
                }
                if (allSuccess && !idsToDelete.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Gaji yang dipilih berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else if (!idsToDelete.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Beberapa atau semua gaji gagal dihapus atau tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
                populateTable(); // Refresh tabel setelah penghapusan
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saat menghapus data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }                                            

    private void jButtonTambahActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // Membuka Form Tambah Gaji
        // Pastikan Anda memiliki kelas FormTambahGaji.java sebagai JDialog atau JFrame
        FormTambahGaji formTambah = new FormTambahGaji(this, true, this);
        formTambah.setVisible(true);
        // Setelah FormTambahGaji ditutup, refresh data di tabel MenuGaji
        populateTable();
    }                                             

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // Dapatkan baris yang dipilih
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Dapatkan data dari baris yang dipilih
        int idGaji = (Integer) jTable1.getValueAt(selectedRow, 1);
        int idCrew = (Integer) jTable1.getValueAt(selectedRow, 2);
        // Ambil Date dari tabel, lalu format ke String untuk JTextField
        Date tanggalGajiDate = (Date) jTable1.getValueAt(selectedRow, 3);
        String tanggalGajiStr = (tanggalGajiDate != null) ? dateFormat.format(tanggalGajiDate) : "";
        
        double jumlahGaji = (Double) jTable1.getValueAt(selectedRow, 4);
        double bonus = (Double) jTable1.getValueAt(selectedRow, 5);
        String nomorRekening = (String) jTable1.getValueAt(selectedRow, 6);
        
        // Ambil Date dari tabel, lalu format ke String untuk JTextField
        Date tanggalPembayaranDate = (Date) jTable1.getValueAt(selectedRow, 7);
        String tanggalPembayaranStr = (tanggalPembayaranDate != null) ? dateFormat.format(tanggalPembayaranDate) : "";
        
        String keterangan = (String) jTable1.getValueAt(selectedRow, 8);
        
        // Isi komponen UI edit dengan data yang didapatkan
        lblHiddenIdGaji.setText(String.valueOf(idGaji)); // Simpan ID Gaji di label tersembunyi
        txtEditIdCrew.setText(String.valueOf(idCrew));
        txtEditTanggalGaji.setText(tanggalGajiStr); // <--- Isi JTextField dengan string tanggal
        txtEditJumlahGaji.setText(String.valueOf(jumlahGaji));
        txtEditBonus.setText(String.valueOf(bonus));
        txtEditNomorRekening.setText(nomorRekening);
        txtEditTanggalPembayaran.setText(tanggalPembayaranStr); // <--- Isi JTextField dengan string tanggal
        txtEditKeterangan.setText(keterangan);

        // Tampilkan panel/komponen edit jika sebelumnya disembunyikan
        // Misalnya: panelEditGaji.setVisible(true);
        // Atau atur fokus ke salah satu field edit
        txtEditIdCrew.requestFocusInWindow();

        // Tidak perlu memanggil formEdit.setVisible(true); karena kita edit di frame ini
    }                                           

    // Metode baru untuk menyimpan perubahan setelah edit
    private void jButtonSimpanEditActionPerformed(java.awt.event.ActionEvent evt) {                                            
        try {
            int idGaji = Integer.parseInt(lblHiddenIdGaji.getText()); // Ambil ID gaji dari label tersembunyi
            int idCrew = Integer.parseInt(txtEditIdCrew.getText());
            
            // Konversi String dari JTextField ke java.sql.Date untuk Tanggal Gaji
            java.sql.Date sqlTanggalGaji = null;
            String tanggalGajiText = txtEditTanggalGaji.getText().trim();
            if (!tanggalGajiText.isEmpty()) {
                try {
                    Date parsedDate = dateFormat.parse(tanggalGajiText);
                    sqlTanggalGaji = new java.sql.Date(parsedDate.getTime());
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Format Tanggal Gaji tidak valid. Gunakan format YYYY-MM-DD.", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal Gaji tidak boleh kosong.", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double jumlahGaji = Double.parseDouble(txtEditJumlahGaji.getText());
            double bonus = Double.parseDouble(txtEditBonus.getText());
            String nomorRekening = txtEditNomorRekening.getText();
            
            // Konversi String dari JTextField ke java.sql.Date untuk Tanggal Pembayaran
            java.sql.Date sqlTanggalPembayaran = null;
            String tanggalPembayaranText = txtEditTanggalPembayaran.getText().trim();
            if (!tanggalPembayaranText.isEmpty()) {
                try {
                    Date parsedDate = dateFormat.parse(tanggalPembayaranText);
                    sqlTanggalPembayaran = new java.sql.Date(parsedDate.getTime());
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Format Tanggal Pembayaran tidak valid. Gunakan format YYYY-MM-DD (biarkan kosong jika tidak ada).", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            // Jika tanggalPembayaranText kosong, sqlTanggalPembayaran akan tetap null, sesuai kebutuhan

            String keterangan = txtEditKeterangan.getText();

            // Query UPDATE ke database
            String updateQuery = "UPDATE gaji SET id_crew = ?, tanggal_gaji = ?, jumlah_gaji = ?, bonus = ?, nomor_rekening = ?, tanggal_pembayaran = ?, keterangan = ?, updated_at = NOW() WHERE id_gaji = ?";
            
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(updateQuery)) {
                
                pst.setInt(1, idCrew);
                pst.setDate(2, sqlTanggalGaji);
                pst.setDouble(3, jumlahGaji);
                pst.setDouble(4, bonus);
                pst.setString(5, nomorRekening);
                pst.setDate(6, sqlTanggalPembayaran); // Ini akan menerima null jika kosong
                pst.setString(7, keterangan);
                pst.setInt(8, idGaji);

                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Data gaji berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    populateTable(); // Refresh tabel setelah update
                    // Opsional: Sembunyikan/bersihkan komponen edit setelah simpan
                    clearEditFields();
                    // panelEditGaji.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengupdate data gaji. Data tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error database saat update: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid untuk ID Crew, Jumlah Gaji, atau Bonus.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metode untuk membersihkan field edit (opsional)
    private void clearEditFields() {
        lblHiddenIdGaji.setText("");
        txtEditIdCrew.setText("");
        txtEditTanggalGaji.setText("");
        txtEditJumlahGaji.setText("");
        txtEditBonus.setText("");
        txtEditNomorRekening.setText("");
        txtEditTanggalPembayaran.setText("");
        txtEditKeterangan.setText("");
    }

    private void jButtonBatalEditActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        clearEditFields();
        // panelEditGaji.setVisible(false); // Sembunyikan panel edit jika ada
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Tombol Close (X)
        System.exit(0);
    }                                        

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {                                     
        // Klik pada logo "Moria Sound Lighting" di sidebar kiri
        // Ini akan kembali ke Dashboard/Beranda
        this.dispose(); // Tutup frame MenuGaji saat ini
        BerandaBaru beranda = new BerandaBaru(); // Membuka frame BerandaBaru
        beranda.setVisible(true);
        beranda.setLocationRelativeTo(null); // Posisi di tengah layar
    }                                    

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Tombol "Kalender Event"
        this.dispose(); // Tutup frame MenuGaji saat ini
        MenuJadwal kalender = new MenuJadwal(); // Buka MenuJadwal.java (asumsi ini adalah Kalender Event)
        kalender.setVisible(true);
        kalender.setLocationRelativeTo(null);
    }                                         

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Tombol "Daftar Paket"
        this.dispose(); // Tutup frame MenuGaji saat ini
        DaftarPaket daftarPaket = new DaftarPaket(); // Buka DaftarPaket.java
        daftarPaket.setVisible(true);
        daftarPaket.setLocationRelativeTo(null);
    }                                         

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Tombol "Inventaris"
        this.dispose(); // Tutup frame MenuGaji saat ini
        MenuInventaris inventaris = new MenuInventaris(); // Buka MenuInventaris.java
        inventaris.setVisible(true);
        inventaris.setLocationRelativeTo(null);
    }                                         

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Tombol "Karyawan"
        this.dispose(); // Tutup frame MenuGaji saat ini
        DaftarKaryawan karyawan = new DaftarKaryawan(); // Buka DaftarKaryawan.java
        karyawan.setVisible(true);
        karyawan.setLocationRelativeTo(null);
    }                                         

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Tombol "Penggajian" (Tombol ini mengarahkan ke halaman saat ini, MenuGaji)
        // Tidak perlu melakukan apa-apa selain memberi feedback atau me-refresh tabel
        JOptionPane.showMessageDialog(this, "Anda sudah berada di halaman Penggajian.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        populateTable(); // Refresh tabel jika ada perubahan di latar belakang
    }                                         

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

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonHapus;
    private javax.swing.JButton jButtonTambah;
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
    // End of variables declaration                   
}
