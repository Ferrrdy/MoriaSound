package UI;

import Custom.InfoPaketSound;
import Custom.KartuPaketUI;
import Custom.RoundedPanel;
import DataBase.DbConnection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class DaftarPaket extends javax.swing.JFrame {
    int xMouse, yMouse;
    private JPanel panelKontenPaket;

    // Deklarasi variabel komponen UI
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private Custom.RoundedPanel navPanel;
    private Custom.RoundedPanel panelKontenWrapper;
    private JButton btnTambahPaket;

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
        panelKontenWrapper.setLayout(new BorderLayout());
        panelKontenWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelKontenPaket = new JPanel();
        panelKontenPaket.setLayout(new BoxLayout(panelKontenPaket, BoxLayout.Y_AXIS));
        panelKontenPaket.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(panelKontenPaket);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panelKontenWrapper.add(scrollPane, BorderLayout.CENTER);
    }

    // MENGGUNAKAN KEMBALI LOGIKA LENGKAP ANDA UNTUK MENGAMBIL DATA
    private List<InfoPaketSound> ambilDataPaketLangsungDariDB() {
        List<InfoPaketSound> daftarPaket = new ArrayList<>();
        String queryPaket = "SELECT id_paket, nama_paket, harga, keterangan FROM paket ORDER BY nama_paket ASC";

        try (Connection conn = DbConnection.getConnection();
             Statement stmtPaket = conn.createStatement();
             ResultSet rsPaket = stmtPaket.executeQuery(queryPaket)) {

            while (rsPaket.next()) {
                String idPaket = rsPaket.getString("id_paket");
                String namaPaket = rsPaket.getString("nama_paket");
                double hargaPaket = rsPaket.getDouble("harga");
                String deskripsiPaket = rsPaket.getString("keterangan");

                StringBuilder detailItemFormattedSb = new StringBuilder();
                String queryDetailItem = "SELECT pb.jumlah, b.nama_barang " +
                                         "FROM paket_barang pb " +
                                         "JOIN barang b ON pb.id_barang = b.id_barang " +
                                         "WHERE pb.id_paket = ?";

                try (PreparedStatement pstmtDetail = conn.prepareStatement(queryDetailItem)) {
                    pstmtDetail.setString(1, idPaket);
                    try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                        while (rsDetail.next()) {
                            int jumlahItem = rsDetail.getInt("jumlah");
                            String namaItem = rsDetail.getString("nama_barang");
                            detailItemFormattedSb.append("- ").append(jumlahItem).append("x ").append(namaItem).append("\n");
                        }
                    }
                }

                String detailItemFinal = detailItemFormattedSb.toString().trim();
                if (detailItemFinal.isEmpty()) {
                    detailItemFinal = "(Belum ada item terdaftar dalam paket ini)";
                }

                InfoPaketSound info = new InfoPaketSound(idPaket, namaPaket, deskripsiPaket, detailItemFinal, hargaPaket);
                daftarPaket.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            daftarPaket.clear();
            daftarPaket.add(new InfoPaketSound("ERR-SQL", "Gagal Load Data", "Terjadi kesalahan SQL.", "- Error SQL -", 0));
        }
        return daftarPaket;
    }
    
    // MENGGUNAKAN KEMBALI LOGIKA LENGKAP ANDA UNTUK MENAMPILKAN DATA
    public void muatDanTampilkanPaketDariUIDenganDB() {
        panelKontenPaket.removeAll();
        List<InfoPaketSound> daftarPaketDariDB = ambilDataPaketLangsungDariDB();
        if (daftarPaketDariDB.isEmpty() || (daftarPaketDariDB.size() == 1 && daftarPaketDariDB.get(0).getIdPaket().startsWith("ERR-"))) {
            String pesan = daftarPaketDariDB.isEmpty() ? "Tidak ada paket tersedia." : daftarPaketDariDB.get(0).getNamaPaket();
            JLabel lblKosong = new JLabel("<html><div style='text-align: center;'>" + pesan.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
            lblKosong.setFont(new Font("SansSerif", Font.ITALIC, 16));
            panelKontenPaket.add(lblKosong);
        } else {
            for (InfoPaketSound info : daftarPaketDariDB) {
                KartuPaketUI kartu = new KartuPaketUI(this, info);
                kartu.setAlignmentX(Component.LEFT_ALIGNMENT);
                // Beri tinggi maksimum agar kartu tidak terlalu besar jika deskripsi panjang
                kartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
                panelKontenPaket.add(kartu);
                panelKontenPaket.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        panelKontenPaket.revalidate();
        panelKontenPaket.repaint();
    }
    
    // INI ADALAH KODE TAMPILAN YANG DITULIS ULANG TANPA DEPENDENSI NETBEANS
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        navPanel = new RoundedPanel();
        contentPanel = new JPanel(new BorderLayout());
        headerPanel = new JPanel(new BorderLayout(10,10));
        panelKontenWrapper = new RoundedPanel();
        
        // --- Konfigurasi Panel Navigasi (Sisi Kiri) ---
        navPanel.setBackground(new Color(46, 51, 55));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(230, 0));
        navPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel logoLabel = new JLabel(new ImageIcon(getClass().getResource("/gambar/150 no back.png")));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        
        JButton btnKalender = createNavButton("Kalender Event");
        btnKalender.addActionListener(this::jButton35ActionPerformed);

        JButton btnDaftarPaket = createNavButton("Daftar Paket");
        btnDaftarPaket.addActionListener(this::jButton36ActionPerformed);

        JButton btnInventaris = createNavButton("Inventaris");
        btnInventaris.addActionListener(this::jButton37ActionPerformed);

        JButton btnKaryawan = createNavButton("Karyawan");
        btnKaryawan.addActionListener(this::jButton38ActionPerformed);

        JButton btnPenggajian = createNavButton("Penggajian");
        btnPenggajian.addActionListener(this::jButton39ActionPerformed);
        
        navPanel.add(Box.createVerticalStrut(50));
        navPanel.add(logoLabel);
        navPanel.add(Box.createVerticalStrut(50));
        navPanel.add(new JSeparator());
        navPanel.add(Box.createVerticalStrut(30));
        
        navPanel.add(btnKalender);
        navPanel.add(Box.createVerticalStrut(40));
        navPanel.add(btnDaftarPaket);
        navPanel.add(Box.createVerticalStrut(40));
        navPanel.add(btnInventaris);
        navPanel.add(Box.createVerticalStrut(40));
        navPanel.add(btnKaryawan);
        navPanel.add(Box.createVerticalStrut(40));
        navPanel.add(btnPenggajian);
        navPanel.add(Box.createVerticalGlue());

        // --- Konfigurasi Panel Konten (Sisi Kanan) ---
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        // --- Konfigurasi Header ---
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("DAFTAR PAKET");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 35));
        titleLabel.setForeground(new Color(46, 51, 55));
        
        Custom.RoundedPanel moriaTitlePanel = new RoundedPanel();
        moriaTitlePanel.setBackground(new Color(46, 51, 55));
        moriaTitlePanel.setRoundBottomRight(25);
        moriaTitlePanel.setRoundTopRight(25);
        JLabel moriaTitleLabel = new JLabel("Moria Sound Lighting");
        moriaTitleLabel.setFont(new Font("SansSerif", 3, 20)); 
        moriaTitleLabel.setForeground(new Color(251, 190, 1));
        moriaTitlePanel.add(moriaTitleLabel);
        
        btnTambahPaket = new JButton("+ Tambah Paket");
        btnTambahPaket.setBackground(new Color(52, 152, 219));
        btnTambahPaket.setForeground(Color.WHITE);
        btnTambahPaket.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnTambahPaket.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambahPaket.addActionListener(this::btnTambahPaketActionPerformed);
        
        JButton closeButton = new JButton("X");
        closeButton.setBackground(new Color(251, 190, 1));
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeButton.setPreferredSize(new Dimension(45, 45));
        closeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeButton.addActionListener(this::jButton2ActionPerformed);
        
        JPanel headerLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        headerLeftPanel.setBackground(Color.WHITE);
        headerLeftPanel.add(moriaTitlePanel);
        headerLeftPanel.add(titleLabel);

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRightPanel.setBackground(Color.WHITE);
        headerRightPanel.add(btnTambahPaket);
        headerRightPanel.add(closeButton);
        
        headerPanel.add(headerLeftPanel, BorderLayout.WEST);
        headerPanel.add(headerRightPanel, BorderLayout.EAST);
        
        // --- Konfigurasi Panel Konten Utama ---
        panelKontenWrapper.setBackground(new Color(245, 245, 245));

        // --- Gabungkan semua panel ---
        JPanel centerContentPanel = new JPanel(new BorderLayout(0, 10));
        centerContentPanel.setBackground(Color.WHITE);
        centerContentPanel.add(new JSeparator(), BorderLayout.NORTH);
        centerContentPanel.add(panelKontenWrapper, BorderLayout.CENTER);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(centerContentPanel, BorderLayout.CENTER);

        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Pengaturan Frame Utama
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setContentPane(mainPanel);
        this.setPreferredSize(new Dimension(1320, 720));
        this.pack();

        // Mouse Listeners untuk memindahkan window
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(150, 35));
        button.setBackground(new Color(251, 200, 42));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

// --- GANTI BLOK EVENT HANDLER LAMA ANDA DENGAN YANG INI ---

private void formMousePressed(java.awt.event.MouseEvent evt) { 
    xMouse = evt.getX(); 
    yMouse = evt.getY(); 
}

private void formMouseDragged(java.awt.event.MouseEvent evt) { 
    this.setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse); 
}

private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) { 
    // Ganti BerandaBaru jika nama kelas frame Beranda Anda berbeda
    // new BerandaBaru().setVisible(true); 
    this.dispose(); 
}

private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) { 
    new MenuJadwal().setVisible(true); // Buka frame MenuJadwal
    this.dispose(); 
}

private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) { 
    // Tidak melakukan apa-apa, karena sudah di halaman ini.
}

private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) { 
    new MenuInventaris().setVisible(true); // Buka frame MenuInventaris
    this.dispose(); 
}

private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) { 
    new DaftarKaryawan().setVisible(true); // Buka frame DaftarKaryawan
    this.dispose(); 
}

private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) { 
    new MenuGaji().setVisible(true); // Buka frame MenuGaji
    this.dispose(); 
}

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) { 
    // Tombol Close 'X'
    System.exit(0); 
}

private void btnTambahPaketActionPerformed(java.awt.event.ActionEvent evt) { 
    // Tombol Tambah Paket
    new FormPaket(this).setVisible(true); 
}
}