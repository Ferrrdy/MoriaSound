package UI;

import Controller.BarangController;
import Model.Barang;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FormPengelolaanAset extends javax.swing.JFrame {

    private final BarangController barangController;

    public FormPengelolaanAset() {
        this.barangController = new BarangController();
        initComponents();
        setLocationRelativeTo(null);
        loadDataAsetBermasalah();
    }

    private void loadDataAsetBermasalah() {
        String[] kolom = {"ID", "Nama Barang", "Jml Rusak Ringan", "Jml Rusak Berat", "Jml Hilang"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelAsetBermasalah.setModel(model);

        try {
            List<Barang> daftarAset = barangController.getBarangBermasalah();
            if (daftarAset.isEmpty()) {
                model.addRow(new Object[]{"-", "Tidak ada aset yang perlu dikelola", "-", "-", "-"});
                return;
            }
            for (Barang barang : daftarAset) {
                model.addRow(new Object[]{
                    barang.getId(),
                    barang.getNamaBarang(),
                    barang.getJumlahRusakRingan(),
                    barang.getJumlahRusakBerat(),
                    barang.getJumlahHilang()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data aset bermasalah: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tabelAsetBermasalah = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnPerbaiki = new javax.swing.JButton();
        btnHapusBuku = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pengelolaan Aset Bermasalah");

        tabelAsetBermasalah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelAsetBermasalah);

        btnRefresh.setText("Refresh Data");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnPerbaiki.setText("Perbaiki Aset...");
        btnPerbaiki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerbaikiActionPerformed(evt);
            }
        });

        btnHapusBuku.setText("Hapus Buku Aset...");
        btnHapusBuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusBukuActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PENGELOLAAN ASET BERMASALAH");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPerbaiki)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapusBuku))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnPerbaiki)
                    .addComponent(btnHapusBuku))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        loadDataAsetBermasalah();
    }

    private void btnPerbaikiActionPerformed(java.awt.event.ActionEvent evt) {                                            
    int selectedRow = tabelAsetBermasalah.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih salah satu aset dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil data dari baris yang dipilih di tabel
    int idBarang = (int) tabelAsetBermasalah.getValueAt(selectedRow, 0);
    String namaBarang = (String) tabelAsetBermasalah.getValueAt(selectedRow, 1);
    int jmlRusakRingan = (int) tabelAsetBermasalah.getValueAt(selectedRow, 2);
    int jmlRusakBerat = (int) tabelAsetBermasalah.getValueAt(selectedRow, 3);

    // Opsi kondisi yang bisa diperbaiki
    Object[] opsiKondisi = {"Rusak Ringan", "Rusak Berat"};
    String kondisiDipilih = (String) JOptionPane.showInputDialog(
            this,
            "Pilih kondisi aset yang sudah diperbaiki:",
            "Pilih Kondisi",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opsiKondisi,
            opsiKondisi[0]
    );

    if (kondisiDipilih == null) {
        return; // User menekan cancel
    }

    int maksDiperbaiki = kondisiDipilih.equals("Rusak Ringan") ? jmlRusakRingan : jmlRusakBerat;

    if (maksDiperbaiki == 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada barang dengan kondisi '" + kondisiDipilih + "' untuk diperbaiki.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Minta input jumlah yang diperbaiki
    String jumlahInputStr = JOptionPane.showInputDialog(this, "Berapa banyak '" + namaBarang + "' (" + kondisiDipilih + ") yang sudah diperbaiki?", "Jumlah Diperbaiki", JOptionPane.QUESTION_MESSAGE);

    if (jumlahInputStr == null || jumlahInputStr.trim().isEmpty()) {
        return; // User menekan cancel atau tidak mengisi apa-apa
    }

    try {
        int jumlahDiperbaiki = Integer.parseInt(jumlahInputStr);
        if (jumlahDiperbaiki <= 0 || jumlahDiperbaiki > maksDiperbaiki) {
            JOptionPane.showMessageDialog(this, "Jumlah input tidak valid. Harus antara 1 dan " + maksDiperbaiki + ".", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panggil controller untuk melakukan update
        boolean sukses = barangController.perbaikiBarang(idBarang, jumlahDiperbaiki, kondisiDipilih);

        if (sukses) {
            JOptionPane.showMessageDialog(this, "Berhasil memperbarui " + jumlahDiperbaiki + " unit " + namaBarang + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadDataAsetBermasalah(); // Refresh tabel untuk melihat hasilnya
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data di database.", "Gagal", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Input Error", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Terjadi error database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void btnHapusBukuActionPerformed(java.awt.event.ActionEvent evt) {                                             
    int selectedRow = tabelAsetBermasalah.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih salah satu aset dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int idBarang = (int) tabelAsetBermasalah.getValueAt(selectedRow, 0);
    String namaBarang = (String) tabelAsetBermasalah.getValueAt(selectedRow, 1);
    int jmlRusakBerat = (int) tabelAsetBermasalah.getValueAt(selectedRow, 3);
    int jmlHilang = (int) tabelAsetBermasalah.getValueAt(selectedRow, 4);

    Object[] opsiKondisi = {"Rusak Berat", "Hilang"};
    String kondisiDipilih = (String) JOptionPane.showInputDialog(
            this,
            "Pilih kondisi aset yang akan dihapus buku:",
            "Pilih Kondisi",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opsiKondisi,
            opsiKondisi[0]
    );

    if (kondisiDipilih == null) {
        return;
    }

    int maksDihapus = kondisiDipilih.equals("Rusak Berat") ? jmlRusakBerat : jmlHilang;

    if (maksDihapus == 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada barang dengan kondisi '" + kondisiDipilih + "' untuk dihapus buku.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    String jumlahInputStr = JOptionPane.showInputDialog(this, "Berapa banyak '" + namaBarang + "' (" + kondisiDipilih + ") yang akan dihapus buku?", "Jumlah Dihapus", JOptionPane.QUESTION_MESSAGE);

    if (jumlahInputStr == null || jumlahInputStr.trim().isEmpty()) {
        return;
    }

    try {
        int jumlahDihapus = Integer.parseInt(jumlahInputStr);
        if (jumlahDihapus <= 0 || jumlahDihapus > maksDihapus) {
            JOptionPane.showMessageDialog(this, "Jumlah input tidak valid. Harus antara 1 dan " + maksDihapus + ".", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi terakhir karena ini aksi permanen
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Anda yakin ingin menghapus permanen " + jumlahDihapus + " unit '" + namaBarang + "' dari sistem?\nJumlah total aset akan berkurang.",
            "Konfirmasi Hapus Buku",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean sukses = barangController.hapusBukuBarang(idBarang, jumlahDihapus, kondisiDipilih);
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Berhasil menghapus buku " + jumlahDihapus + " unit " + namaBarang + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataAsetBermasalah();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data di database.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Input Error", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Terjadi error database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
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
            java.util.logging.Logger.getLogger(FormPengelolaanAset.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPengelolaanAset().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusBuku;
    private javax.swing.JButton btnPerbaiki;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelAsetBermasalah;
    // End of variables declaration//GEN-END:variables
}