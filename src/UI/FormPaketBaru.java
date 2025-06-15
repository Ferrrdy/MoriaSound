package UI;

import DataBase.DbConnection;
import Model.Barang;
import Model.ItemDalamPaket;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class FormPaketBaru extends javax.swing.JFrame {

    private DaftarPaketBaru parentFrame;
    private String idPaketEdit; 
    
    private DefaultListModel<Barang> modelSemuaBarang;
    private DefaultListModel<ItemDalamPaket> modelBarangPaket;
    
    public FormPaketBaru(DaftarPaketBaru parent) {
        initComponents();
        this.parentFrame = parent;
        this.setTitle("Tambah Paket Baru");
        initLogic();
        muatSemuaBarangDariDB();
    }

    public FormPaketBaru(DaftarPaketBaru parent, String idPaketToEdit) {
        initComponents();
        this.parentFrame = parent;
        this.idPaketEdit = idPaketToEdit;
        this.setTitle("Edit Paket");
        initLogic();
        muatDataUntukEdit(); 
        muatSemuaBarangDariDB(); 
        muatBarangPaketUntukEdit();
    }

    private void initLogic() {
        setLocationRelativeTo(parentFrame);
        this.getContentPane().setBackground(Color.WHITE);
        
        modelSemuaBarang = new DefaultListModel<>();
        listSemuaBarang.setModel(modelSemuaBarang);
        
        modelBarangPaket = new DefaultListModel<>();
        listBarangPaket.setModel(modelBarangPaket);

        spinnerJumlah.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
        
        btnTambahItem.addActionListener(e -> {
            Barang selectedBarang = listSemuaBarang.getSelectedValue();
            if (selectedBarang == null) {
                JOptionPane.showMessageDialog(this, "Pilih barang yang ingin ditambahkan terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int jumlah = (int) spinnerJumlah.getValue();
            ItemDalamPaket itemBaru = new ItemDalamPaket(selectedBarang, jumlah);
            modelBarangPaket.addElement(itemBaru);
            modelSemuaBarang.removeElement(selectedBarang);
        });
        
        btnHapusItem.addActionListener(e -> {
            ItemDalamPaket selectedItemPaket = listBarangPaket.getSelectedValue();
            if (selectedItemPaket == null) {
                JOptionPane.showMessageDialog(this, "Pilih barang dalam paket yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Barang barangAsli = selectedItemPaket.getBarang();
            modelSemuaBarang.addElement(barangAsli);
            modelBarangPaket.removeElement(selectedItemPaket);
        });
    }
    
    // ... Metode muat data (muatDataUntukEdit, muatSemuaBarang, dll) tetap sama ...
    private void muatDataUntukEdit() {
        String sql = "SELECT nama_paket, harga, keterangan FROM paket WHERE id_paket = ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Karena idPaketEdit berasal dari database, ia sudah dalam format yang benar.
            pstmt.setInt(1, Integer.parseInt(idPaketEdit));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.setTitle("Edit Paket: " + rs.getString("nama_paket"));
                txtNamaPaket.setText(rs.getString("nama_paket"));
                txtHarga.setText(String.format("%.0f", rs.getDouble("harga")));
                txtKeterangan.setText(rs.getString("keterangan"));
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data paket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatSemuaBarangDariDB() {
        modelSemuaBarang.clear();
        String sql = "SELECT id_barang, nama_barang FROM barang ORDER BY nama_barang ASC";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_barang");
                String nama = rs.getString("nama_barang");
                Barang barang = new Barang();
                barang.setIdBarang(id);
                barang.setNamaBarang(nama);
                modelSemuaBarang.addElement(barang);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar barang: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatBarangPaketUntukEdit() {
        modelBarangPaket.clear();
        String sql = "SELECT b.id_barang, b.nama_barang, pb.jumlah " +
                     "FROM paket_barang pb " +
                     "JOIN barang b ON pb.id_barang = b.id_barang " +
                     "WHERE pb.id_paket = ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(idPaketEdit));
            ResultSet rs = pstmt.executeQuery();
            List<Integer> idBarangDalamPaket = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id_barang");
                String nama = rs.getString("nama_barang");
                int jumlah = rs.getInt("jumlah");
                Barang barang = new Barang();
                barang.setIdBarang(id);
                barang.setNamaBarang(nama);
                modelBarangPaket.addElement(new ItemDalamPaket(barang, jumlah));
                idBarangDalamPaket.add(id);
            }
            for (int i = modelSemuaBarang.size() - 1; i >= 0; i--) {
                if (idBarangDalamPaket.contains(modelSemuaBarang.get(i).getIdBarang())) {
                    modelSemuaBarang.remove(i);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat item paket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ... Kode initComponents() tetap sama ...
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtNamaPaket = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listSemuaBarang = new javax.swing.JList<>();
        btnTambahItem = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listBarangPaket = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        spinnerJumlah = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setText("Form Data Paket");

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setText("Nama Paket");

        txtNamaPaket.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("Harga");

        txtHarga.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("Keterangan");

        txtKeterangan.setColumns(20);
        txtKeterangan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtKeterangan.setRows(3);
        jScrollPane1.setViewportView(txtKeterangan);

        btnSimpan.setBackground(new java.awt.Color(52, 152, 219));
        btnSimpan.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(153, 153, 153));
        btnBatal.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)));

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel5.setText("Daftar Semua Barang");

        jScrollPane2.setViewportView(listSemuaBarang);

        btnTambahItem.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnTambahItem.setText(">");

        btnHapusItem.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnHapusItem.setText("<");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel6.setText("Barang dalam Paket");

        jScrollPane3.setViewportView(listBarangPaket);

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel7.setText("Jumlah:");

        spinnerJumlah.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTambahItem, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(spinnerJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(btnTambahItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHapusItem)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(txtNamaPaket)
                            .addComponent(jLabel3)
                            .addComponent(txtHarga, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane1))
                        .addGap(30, 30, 30)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNamaPaket, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {                                         
        this.dispose();
    }                                        

    // --- METODE SIMPAN FINAL YANG SUDAH DIPERBAIKI ---
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        String nama = txtNamaPaket.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String keterangan = txtKeterangan.getText().trim();

        if (nama.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Paket dan Harga wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid.", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); 

            int currentPaketId;

            if (idPaketEdit == null) { // Mode Tambah
                String sql = "INSERT INTO paket (nama_paket, harga, keterangan, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
                // Meminta ID yang di-generate oleh database
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, nama);
                    pstmt.setDouble(2, harga);
                    pstmt.setString(3, keterangan);
                    pstmt.executeUpdate();
                    
                    // Ambil ID paket yang baru saja dibuat oleh AUTO_INCREMENT
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        currentPaketId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Gagal membuat paket, tidak ada ID yang didapatkan.");
                    }
                }
            } else { // Mode Edit
                currentPaketId = Integer.parseInt(idPaketEdit);
                String sql = "UPDATE paket SET nama_paket = ?, harga = ?, keterangan = ?, updated_at = NOW() WHERE id_paket = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nama);
                    pstmt.setDouble(2, harga);
                    pstmt.setString(3, keterangan);
                    pstmt.setInt(4, currentPaketId);
                    pstmt.executeUpdate();
                }
                
                // Hapus detail item lama sebelum memasukkan yang baru
                String sqlDelete = "DELETE FROM paket_barang WHERE id_paket = ?";
                try(PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                    pstmtDelete.setInt(1, currentPaketId);
                    pstmtDelete.executeUpdate();
                }
            }
            
            // Masukkan detail item yang baru dari list kanan
            String sqlInsertDetail = "INSERT INTO paket_barang (id_paket, id_barang, jumlah) VALUES (?, ?, ?)";
            try(PreparedStatement pstmtDetail = conn.prepareStatement(sqlInsertDetail)) {
                for (int i = 0; i < modelBarangPaket.size(); i++) {
                    ItemDalamPaket item = modelBarangPaket.getElementAt(i);
                    pstmtDetail.setInt(1, currentPaketId);
                    pstmtDetail.setInt(2, item.getBarang().getIdBarang());
                    pstmtDetail.setInt(3, item.getJumlah());
                    pstmtDetail.addBatch(); 
                }
                pstmtDetail.executeBatch(); 
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Data paket berhasil disimpan!");
            
            parentFrame.muatDanTampilkanPaketDariUIDenganDB();
            this.dispose(); 

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Operasi database gagal, semua perubahan dibatalkan.", "Error Transaksi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList<Barang> listSemuaBarang;
    private javax.swing.JList<ItemDalamPaket> listBarangPaket;
    private javax.swing.JSpinner spinnerJumlah;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtNamaPaket;
    // End of variables declaration                   
}