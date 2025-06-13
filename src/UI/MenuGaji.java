package UI; // Sesuaikan dengan nama package Anda

import DataBase.DbConnection; // Sesuaikan dengan nama package DbConnection Anda
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;

// --- BARU --- Impor untuk komponen-komponen dialog
import java.awt.Container;
import javax.swing.Spring;


public class MenuGaji extends javax.swing.JFrame {
    int xMouse, yMouse;

    // --- DIHAPUS ---
    // Semua variabel untuk komponen UI pengeditan yang lama sudah dihapus
    // karena kita akan membuatnya di dalam dialog pop-up.

    // Untuk format tanggal string (misalnya "YYYY-MM-DD")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MenuGaji() {
        initComponents();
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        
        loadTableGaji(); // Panggil fungsi untuk load konfigurasi tabel
        populateTable(); // Panggil fungsi untuk mengisi data ke tabel
    }

    private void loadTableGaji() {
        // ... (Fungsi ini sudah benar, tidak ada perubahan)
        String[] kolom = {"Select", "ID", "ID Crew", "Tanggal Gaji", "Jumlah Gaji", "Bonus", "Nomor Rekening", "Tanggal Pembayaran", "Keterangan", "Created At", "Updated At"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return super.getColumnClass(column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        jTable1.setModel(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < jTable1.getColumnCount(); i++) {
            if (i != 8) { 
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));

        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = jTable1.getColumnModel();
        TableColumn selectColumn = columnModel.getColumn(0);
        selectColumn.setPreferredWidth(50);
        selectColumn.setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(30); 
        columnModel.getColumn(2).setPreferredWidth(70);  
        columnModel.getColumn(3).setPreferredWidth(80); 
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(80); 
        columnModel.getColumn(6).setPreferredWidth(150);
        columnModel.getColumn(7).setPreferredWidth(100);
        columnModel.getColumn(8).setPreferredWidth(200);
        columnModel.getColumn(9).setPreferredWidth(150);
        columnModel.getColumn(10).setPreferredWidth(150);
    }

    public void populateTable() {
        // ... (Fungsi ini sudah benar, tidak ada perubahan)
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); 

        String query = "SELECT id_gaji, id_crew, tanggal_gaji, jumlah_gaji, bonus, nomor_rekening, tanggal_pembayaran, keterangan, created_at, updated_at FROM gaji ORDER BY created_at DESC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    false, // Checkbox
                    rs.getInt("id_gaji"),
                    rs.getInt("id_crew"),
                    rs.getDate("tanggal_gaji"),
                    rs.getDouble("jumlah_gaji"),
                    rs.getDouble("bonus"),
                    rs.getString("nomor_rekening"),
                    rs.getDate("tanggal_pembayaran"),
                    rs.getString("keterangan"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- DIUBAH TOTAL: Menggunakan konsep JDialog pop-up ---
    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Dapatkan baris yang dipilih (di-highlight)
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris data gaji yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ambil ID Gaji dari tabel
        int idGajiToEdit = (Integer) jTable1.getValueAt(selectedRow, 1);

        // 3. Buat Dialog Pop-up untuk Edit
        JDialog editDialog = new JDialog(this, "Edit Data Gaji", true); // true = modal
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(450, 400); // Sesuaikan ukuran
        editDialog.setLocationRelativeTo(this);

        // 4. Buat Panel Form dengan SpringLayout
        JPanel formPanel = new JPanel(new SpringLayout());
        
        // Ambil data awal dari tabel untuk ditampilkan di form
        String idCrew = jTable1.getValueAt(selectedRow, 2).toString();
        Date tglGajiDate = (Date) jTable1.getValueAt(selectedRow, 3);
        String tglGajiStr = (tglGajiDate != null) ? dateFormat.format(tglGajiDate) : "";
        String jumlahGaji = jTable1.getValueAt(selectedRow, 4).toString();
        String bonus = jTable1.getValueAt(selectedRow, 5).toString();
        String norek = (jTable1.getValueAt(selectedRow, 6) != null) ? jTable1.getValueAt(selectedRow, 6).toString() : "";
        Date tglBayarDate = (Date) jTable1.getValueAt(selectedRow, 7);
        String tglBayarStr = (tglBayarDate != null) ? dateFormat.format(tglBayarDate) : "";
        String keterangan = (jTable1.getValueAt(selectedRow, 8) != null) ? jTable1.getValueAt(selectedRow, 8).toString() : "";
        
        // 5. Buat dan Tambahkan Komponen ke Form Panel
        JTextField idCrewField = new JTextField(idCrew);
        JTextField tglGajiField = new JTextField(tglGajiStr);
        JTextField jumlahGajiField = new JTextField(jumlahGaji);
        JTextField bonusField = new JTextField(bonus);
        JTextField norekField = new JTextField(norek);
        JTextField tglBayarField = new JTextField(tglBayarStr);
        JTextField keteranganField = new JTextField(keterangan);
        
        formPanel.add(new JLabel("ID Crew:", JLabel.TRAILING));
        formPanel.add(idCrewField);
        formPanel.add(new JLabel("Tanggal Gaji (YYYY-MM-DD):", JLabel.TRAILING));
        formPanel.add(tglGajiField);
        formPanel.add(new JLabel("Jumlah Gaji:", JLabel.TRAILING));
        formPanel.add(jumlahGajiField);
        formPanel.add(new JLabel("Bonus:", JLabel.TRAILING));
        formPanel.add(bonusField);
        formPanel.add(new JLabel("Nomor Rekening:", JLabel.TRAILING));
        formPanel.add(norekField);
        formPanel.add(new JLabel("Tanggal Pembayaran (YYYY-MM-DD):", JLabel.TRAILING));
        formPanel.add(tglBayarField);
        formPanel.add(new JLabel("Keterangan:", JLabel.TRAILING));
        formPanel.add(keteranganField);

        SpringUtilities.makeCompactGrid(formPanel, 7, 2, 10, 10, 10, 10);
        editDialog.add(formPanel, BorderLayout.CENTER);

        // 6. Buat Tombol Simpan di bagian bawah dialog
        JButton saveButton = new JButton("Simpan Perubahan");
        saveButton.addActionListener(e -> {
            try {
                // Ambil data dari field
                int newIdCrew = Integer.parseInt(idCrewField.getText().trim());
                double newJumlahGaji = Double.parseDouble(jumlahGajiField.getText().trim());
                double newBonus = Double.parseDouble(bonusField.getText().trim());
                String newNorek = norekField.getText().trim();
                String newKeterangan = keteranganField.getText().trim();

                // Validasi dan konversi tanggal
                java.sql.Date newSqlTglGaji = new java.sql.Date(dateFormat.parse(tglGajiField.getText().trim()).getTime());
                
                java.sql.Date newSqlTglBayar = null;
                if (!tglBayarField.getText().trim().isEmpty()) {
                    newSqlTglBayar = new java.sql.Date(dateFormat.parse(tglBayarField.getText().trim()).getTime());
                }

                // Query UPDATE ke database
                String updateQuery = "UPDATE gaji SET id_crew = ?, tanggal_gaji = ?, jumlah_gaji = ?, bonus = ?, nomor_rekening = ?, tanggal_pembayaran = ?, keterangan = ?, updated_at = NOW() WHERE id_gaji = ?";
                
                try (Connection conn = DbConnection.getConnection();
                     PreparedStatement pst = conn.prepareStatement(updateQuery)) {
                    
                    pst.setInt(1, newIdCrew);
                    pst.setDate(2, newSqlTglGaji);
                    pst.setDouble(3, newJumlahGaji);
                    pst.setDouble(4, newBonus);
                    pst.setString(5, newNorek);
                    pst.setObject(6, newSqlTglBayar); // Gunakan setObject untuk handle null
                    pst.setString(7, newKeterangan);
                    pst.setInt(8, idGajiToEdit);

                    int affectedRows = pst.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(editDialog, "Data gaji berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        populateTable();      // Refresh tabel di jendela utama
                        editDialog.dispose(); // Tutup dialog pop-up
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Gagal mengupdate data. ID tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Error database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Format angka tidak valid untuk ID Crew, Jumlah Gaji, atau Bonus.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(editDialog, "Format tanggal tidak valid. Gunakan format YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 7. Tampilkan dialog ke layar
        editDialog.setVisible(true);
    }
    
    private void jButtonHapusActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // ... (Fungsi ini sudah benar, tidak ada perubahan)
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        List<Integer> idsToDelete = new ArrayList<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 0)) {
                idsToDelete.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (idsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih setidaknya satu gaji untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus " + idsToDelete.size() + " data yang dipilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String deleteQuery = "DELETE FROM gaji WHERE id_gaji = ?";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(deleteQuery)) {
                int successCount = 0;
                for (Integer id : idsToDelete) {
                    pst.setInt(1, id);
                    if (pst.executeUpdate() > 0) {
                        successCount++;
                    }
                }
                JOptionPane.showMessageDialog(this, successCount + " data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                populateTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saat menghapus data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }                                            

    // Sisa kode di bawah ini TIDAK PERLU DIUBAH
    // Ini adalah kode dari NetBeans Designer dan navigasi Anda
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        roundedPanel5 = new Custom.RoundedPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonTambah = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonHapus = new javax.swing.JButton();
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(roundedPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3)
                            .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void formMousePressed(java.awt.event.MouseEvent evt) {                                  
        xMouse = evt.getX();
        yMouse = evt.getY();
    }                                 

    private void formMouseDragged(java.awt.event.MouseEvent evt) {                                  
        this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
    }                                 

    private void jButtonTambahActionPerformed(java.awt.event.ActionEvent evt) {                                              
        FormTambahGaji formTambah = new FormTambahGaji(this, true, this);
        formTambah.setVisible(true);
        populateTable();
    }                                             

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        System.exit(0);
    }                                        

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {                                     
        this.dispose();
        new BerandaBaru().setVisible(true);
    }                                    

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.dispose();
        new MenuJadwal().setVisible(true);
    }                                         

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.dispose();
        new DaftarPaket().setVisible(true);
    }                                         

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.dispose();
        new MenuInventaris().setVisible(true);
    }                                         

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.dispose();
        new DaftarKaryawan().setVisible(true);
    }                                         

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        JOptionPane.showMessageDialog(this, "Anda sudah berada di halaman Penggajian.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }                                         

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuGaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new MenuGaji().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables

    // --- BARU: Kelas helper untuk SpringLayout ---
    // Pastikan kelas ini ada agar layout dialog edit berfungsi.
    static class SpringUtilities {
        public static void makeCompactGrid(Container parent,
                                           int rows, int cols,
                                           int initialX, int initialY,
                                           int xPad, int yPad) {
            SpringLayout layout;
            try {
                layout = (SpringLayout) parent.getLayout();
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
                    SpringLayout.Constraints constraints =
                        layout.getConstraints(parent.getComponent(r * cols + c));
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
                    SpringLayout.Constraints constraints =
                        layout.getConstraints(parent.getComponent(r * cols + c));
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