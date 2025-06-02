package UI;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener; // Untuk JDateChooser
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.toedter.calendar.JDateChooser;
import Controller.EventController;
import Controller.InsufficientStockException;
import DataBase.DbConnection;

public class EventManager {
    private final Map<LocalDate, List<String>> events = new HashMap<>();

    public Map<LocalDate, List<String>> getEvents() {
        return events;
    }

    public void showAddEventDialog(Component parent, LocalDate currentDate, Runnable updateCallback) {
        Frame frameParent = null;
        if (parent instanceof Frame) {
            frameParent = (Frame) parent;
        } else {
            Window window = SwingUtilities.getWindowAncestor(parent);
            if (window instanceof Frame) {
                frameParent = (Frame) window;
            }
        }

        FormTambahEvent dialogTambahEvent = new FormTambahEvent(frameParent);
        if (currentDate != null) {
            Date defaultStartDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dialogTambahEvent.setTanggalMulaiDefault(defaultStartDate);
        }
        dialogTambahEvent.setVisible(true);

        if (dialogTambahEvent.isSaved()) {
            String namaEventBaru = dialogTambahEvent.getNamaEventDisimpan();
            LocalDate tanggalMulaiEventBaru = dialogTambahEvent.getTanggalMulaiDisimpanAsLocalDate();
            if (namaEventBaru != null && tanggalMulaiEventBaru != null) {
                events.computeIfAbsent(tanggalMulaiEventBaru, d -> new ArrayList<>()).add(namaEventBaru);
            }
            if (updateCallback != null) {
                 updateCallback.run();
            }
        }
    }

    public List<String> getEventsForDate(LocalDate date) {
        return events.getOrDefault(date, new ArrayList<>());
    }

    public void deleteEvent(LocalDate date, String eventTitle) {
        List<String> list = events.get(date);
        if (list != null) {
            list.remove(eventTitle);
            if (list.isEmpty()) events.remove(date);
        }
        JOptionPane.showMessageDialog(null, "Fungsi HAPUS event dari kalender perlu implementasi interaksi ke database.");
    }

    public void editEvent(LocalDate date, String oldTitle, String newTitle) {
        List<String> list = events.get(date);
        if (list != null && list.contains(oldTitle)) {
            list.set(list.indexOf(oldTitle), newTitle);
        }
        JOptionPane.showMessageDialog(null, "Fungsi EDIT event dari kalender perlu implementasi interaksi ke database.");
    }
    
    @SuppressWarnings("unused")
    private void updateDayCombo(JComboBox<Integer> dayCombo, int year, int month) {
        dayCombo.removeAllItems();
        int selectedDay = -1;
        Object currentItem = dayCombo.getSelectedItem();
        if(currentItem instanceof Integer){
            selectedDay = (Integer) currentItem;
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            dayCombo.addItem(i);
        }
        if(selectedDay != -1 && selectedDay <= daysInMonth){
            dayCombo.setSelectedItem(selectedDay);
        } else if (daysInMonth > 0){
            dayCombo.setSelectedIndex(0); 
        }
    }
}

class FormTambahEvent extends JDialog {
    private JDateChooser dateChooserTanggalMulai;
    private JDateChooser dateChooserTanggalSelesai;
    private JSpinner spinnerJamMulai;
    private JSpinner spinnerJamSelesai;
    private JLabel lblDurasiOtomatis;
    private JTextField txtNamaEvent, txtLokasi;
    private JTextArea txtAreaKeterangan;
    private JComboBox<String> cmbStatus;
    private JComboBox<PaketItem> comboPaket;
    private JButton btnSimpan, btnBatal;
    // Variabel internal untuk menyimpan nilai terpilih
    // tanggalMulaiTerpilih dan tanggalSelesaiTerpilih akan diambil langsung dari JDateChooser
    // Variabel jamMulaiTerpilih dan jamSelesaiTerpilih tidak lagi diperlukan jika logika
    // langsung mengambil dari spinner, atau bisa dipertahankan jika ada alasan lain.
    // Untuk kesederhanaan, kita bisa langsung baca dari spinner di recalculate dan simpan.

    private boolean isSaved = false;
    private String namaEventDisimpan;
    private LocalDateTime tanggalMulaiLocalDateTimeDisimpan;

    private final String[] statusOptions = {"Direncanakan", "Berlangsung", "Selesai", "Dibatalkan"};


    public FormTambahEvent(Frame parent) {
        super(parent, "Tambah Event Baru", true);
        // initializeDefaultTime() tidak lagi eksplisit memanipulasi jamMulaiTerpilih/jamSelesaiTerpilih
        // karena spinner akan diinisialisasi langsung.
        initComponents(); // Di sini spinner jam akan diinisialisasi
        setupActionListeners();
        finalizeDialog();
        recalculateAndDisplayDuration(); 
    }

    private void initComponents() {
        txtNamaEvent = new JTextField(25);
        txtLokasi = new JTextField(25);
        txtAreaKeterangan = new JTextArea(5, 25); 
        txtAreaKeterangan.setLineWrap(true);
        txtAreaKeterangan.setWrapStyleWord(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtAreaKeterangan);
        cmbStatus = new JComboBox<>(statusOptions);
        comboPaket = new JComboBox<>();
        isiComboPaket();

        dateChooserTanggalMulai = new JDateChooser();
        dateChooserTanggalMulai.setDate(new Date()); 
        dateChooserTanggalMulai.setDateFormatString("dd MMM yy");
        dateChooserTanggalMulai.setPreferredSize(new Dimension(130, dateChooserTanggalMulai.getPreferredSize().height));

        spinnerJamMulai = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de_spinnerJamMulai = new JSpinner.DateEditor(spinnerJamMulai, "HH:mm");
        spinnerJamMulai.setEditor(de_spinnerJamMulai);
        setSpinnerTime(spinnerJamMulai, LocalTime.of(9,0)); // Default jam 09:00
        spinnerJamMulai.setPreferredSize(new Dimension(70, spinnerJamMulai.getPreferredSize().height));

        dateChooserTanggalSelesai = new JDateChooser();
        // Inisialisasi tanggal selesai berdasarkan tanggal mulai dan jam selesai default
        Calendar calSelesaiInit = Calendar.getInstance();
        calSelesaiInit.setTime(dateChooserTanggalMulai.getDate() != null ? dateChooserTanggalMulai.getDate() : new Date());
        LocalTime jamSelesaiDefault = LocalTime.of(10,0); // Default jam 10:00 (mulai + 1 jam)
        LocalDateTime initialSelesaiLDT = LocalDateTime.of(
            calSelesaiInit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            jamSelesaiDefault
        );
        dateChooserTanggalSelesai.setDate(Date.from(initialSelesaiLDT.atZone(ZoneId.systemDefault()).toInstant()));
        dateChooserTanggalSelesai.setDateFormatString("dd MMM yy");
        dateChooserTanggalSelesai.setPreferredSize(new Dimension(130, dateChooserTanggalSelesai.getPreferredSize().height));

        spinnerJamSelesai = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de_spinnerJamSelesai = new JSpinner.DateEditor(spinnerJamSelesai, "HH:mm");
        spinnerJamSelesai.setEditor(de_spinnerJamSelesai);
        setSpinnerTime(spinnerJamSelesai, jamSelesaiDefault);
        spinnerJamSelesai.setPreferredSize(new Dimension(70, spinnerJamSelesai.getPreferredSize().height));

        lblDurasiOtomatis = new JLabel("Durasi: -");

        btnSimpan = new JButton("Simpan"); // Nama tombol kembali ke "Simpan"
        btnBatal = new JButton("Batal");
        // btnBuatInvoice dihapus

        setLayout(new BorderLayout(10, 10));
        JPanel panelForm = new JPanel(new GridBagLayout()); // Tanpa JTabbedPane
        panelForm.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Nama Event:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(txtNamaEvent, gbc); gbc.weightx = 1.0; y++;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Mulai:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(dateChooserTanggalMulai, gbc);
        gbc.gridx = 2; gbc.gridy = y; gbc.fill = GridBagConstraints.NONE; panelForm.add(new JLabel("Jam:"), gbc);
        gbc.gridx = 3; gbc.gridy = y; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(spinnerJamMulai, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Selesai:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(dateChooserTanggalSelesai, gbc);
        gbc.gridx = 2; gbc.gridy = y; gbc.fill = GridBagConstraints.NONE; panelForm.add(new JLabel("Jam:"), gbc);
        gbc.gridx = 3; gbc.gridy = y; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(spinnerJamSelesai, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Durasi:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; panelForm.add(lblDurasiOtomatis, gbc); y++;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Lokasi:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(txtLokasi, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(cmbStatus, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Paket:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; panelForm.add(comboPaket, gbc); y++;
        
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.NORTHWEST; panelForm.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; panelForm.add(scrollKeterangan, gbc); y++;
        gbc.weighty = 0;
        
        add(panelForm, BorderLayout.CENTER);

        JPanel panelButtonBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtonBawah.add(btnSimpan);
        panelButtonBawah.add(btnBatal);
        add(panelButtonBawah, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(550, getSize().height)); 
        setLocationRelativeTo(getParent());
    }

    private void setSpinnerTime(JSpinner spinner, LocalTime time) {
        Calendar calendar = Calendar.getInstance();
        if (time != null) {
            calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
            calendar.set(Calendar.MINUTE, time.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            spinner.setValue(calendar.getTime());
        }
    }

    private LocalTime getSpinnerTime(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        if (date == null) { 
            return LocalTime.MIDNIGHT; // Default jika spinner kosong (seharusnya tidak terjadi dengan SpinnerDateModel)
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }
    
    private void setupActionListeners() {
        PropertyChangeListener dateChangeListener = evt -> recalculateAndDisplayDuration();
        dateChooserTanggalMulai.addPropertyChangeListener("date", dateChangeListener);
        dateChooserTanggalSelesai.addPropertyChangeListener("date", dateChangeListener);
        
        ChangeListener timeChangeListener = e -> {
            recalculateAndDisplayDuration();
        };
        spinnerJamMulai.addChangeListener(timeChangeListener);
        spinnerJamSelesai.addChangeListener(timeChangeListener);

        btnSimpan.addActionListener(e -> simpanEvent());
        btnBatal.addActionListener(e -> {
            isSaved = false;
            dispose();
        });
        // Listener untuk btnBuatInvoice dihapus
    }
    
    private void recalculateAndDisplayDuration() {
        Date tglMulaiUtil = dateChooserTanggalMulai.getDate();
        Date tglSelesaiUtil = dateChooserTanggalSelesai.getDate();
        LocalTime jamMulaiSaatIni = getSpinnerTime(spinnerJamMulai);
        LocalTime jamSelesaiSaatIni = getSpinnerTime(spinnerJamSelesai);

        if (tglMulaiUtil == null || tglSelesaiUtil == null || jamMulaiSaatIni == null || jamSelesaiSaatIni == null) {
            lblDurasiOtomatis.setText("Durasi: (lengkapi input)");
            lblDurasiOtomatis.setForeground(Color.GRAY);
            return;
        }
        LocalDate tglMulaiLocalDate = tglMulaiUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate tglSelesaiLocalDate = tglSelesaiUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime startLDT = LocalDateTime.of(tglMulaiLocalDate, jamMulaiSaatIni);
        LocalDateTime endLDT = LocalDateTime.of(tglSelesaiLocalDate, jamSelesaiSaatIni);

        if (endLDT.isBefore(startLDT)) {
            lblDurasiOtomatis.setText("Durasi: Error! (Selesai < Mulai)");
            lblDurasiOtomatis.setForeground(Color.RED);
        } else {
            Duration duration = Duration.between(startLDT, endLDT);
            long totalMinutes = duration.toMinutes();
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            lblDurasiOtomatis.setText(String.format("%d jam %d menit", hours, minutes));
            lblDurasiOtomatis.setForeground(Color.BLACK);
        }
    }

    private void isiComboPaket() {
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             // Query hanya mengambil id_paket dan nama_paket
             ResultSet rs = stmt.executeQuery("SELECT id_paket, nama_paket FROM paket")) {
            while (rs.next()) {
                comboPaket.addItem(new PaketItem(
                    rs.getInt("id_paket"), 
                    rs.getString("nama_paket")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data paket: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void simpanEvent() {
        String namaEvent = txtNamaEvent.getText().trim();
        String lokasi = txtLokasi.getText().trim();
        String keterangan = txtAreaKeterangan.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        PaketItem paketDipilih = (PaketItem) comboPaket.getSelectedItem();

        Date tglMulaiUtil = dateChooserTanggalMulai.getDate();
        Date tglSelesaiUtil = dateChooserTanggalSelesai.getDate();
        LocalTime jamMulaiSaatIni = getSpinnerTime(spinnerJamMulai);
        LocalTime jamSelesaiSaatIni = getSpinnerTime(spinnerJamSelesai);

        // Validasi nama klien dihapus
        if (namaEvent.isEmpty() || lokasi.isEmpty() || paketDipilih == null || 
            tglMulaiUtil == null || jamMulaiSaatIni == null || 
            tglSelesaiUtil == null || jamSelesaiSaatIni == null) { 
            JOptionPane.showMessageDialog(this, "Harap lengkapi semua field yang wajib diisi (Nama Event, Lokasi, Paket, Tanggal & Jam).", "Input Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate tglMulaiLocalDate = tglMulaiUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate tglSelesaiLocalDate = tglSelesaiUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime mulaiLDT = LocalDateTime.of(tglMulaiLocalDate, jamMulaiSaatIni);
        LocalDateTime selesaiLDT = LocalDateTime.of(tglSelesaiLocalDate, jamSelesaiSaatIni);

        if (selesaiLDT.isBefore(mulaiLDT)) {
            JOptionPane.showMessageDialog(this, "Tanggal/Waktu Selesai tidak boleh sebelum Tanggal/Waktu Mulai.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Duration duration = Duration.between(mulaiLDT, selesaiLDT);
        int durasiJamDatabase = (int) duration.toHours();

        // idEventDisimpan tidak lagi relevan jika invoice dihapus total
        int idEvent = -1; 
        try {
            idEvent = EventController.insertEvent(namaEvent, mulaiLDT, selesaiLDT, durasiJamDatabase, lokasi, keterangan, status);
            
            boolean paketDitambahkan = EventController.tambahPaketKeEvent(idEvent, paketDipilih.getId());
            if (!paketDitambahkan && idEvent != -1) {
                 JOptionPane.showMessageDialog(this, "Event utama tersimpan, tetapi gagal menambahkan paket (kemungkinan paket sudah ada atau masalah lain).", "Peringatan Paket", JOptionPane.WARNING_MESSAGE);
            }
            EventController.kurangiStokBarangDariPaket(paketDipilih.getId()); 

            this.namaEventDisimpan = namaEvent;
            this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT;
            this.isSaved = true;
            JOptionPane.showMessageDialog(this, "Event berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Event disimpan, dialog bisa ditutup

        } catch (InsufficientStockException e) {
            JOptionPane.showMessageDialog(this, e.getMessage() + "\nEvent ("+namaEvent+") mungkin telah disimpan, tetapi pemrosesan stok gagal.", "Stok Tidak Cukup", JOptionPane.ERROR_MESSAGE);
            if (idEvent != -1) { this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        } catch (SQLException e) {
            String pesanError = "Terjadi kesalahan database: " + e.getMessage();
            if (idEvent != -1) { pesanError += "\nEvent ("+namaEvent+") mungkin telah disimpan. Detail paket atau stok mungkin bermasalah."; }
            JOptionPane.showMessageDialog(this, pesanError, "Error Database Kritis", JOptionPane.ERROR_MESSAGE);
            if (idEvent != -1) { this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan aplikasi yang tidak terduga: " + e.getMessage(), "Error Aplikasi", JOptionPane.ERROR_MESSAGE);
            if (idEvent != -1) { this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        }
    }

    // Metode prosesPembuatanInvoice() DIHAPUS

    public boolean isSaved() { return isSaved; }
    public String getNamaEventDisimpan() { return namaEventDisimpan; }
    public LocalDate getTanggalMulaiDisimpanAsLocalDate() {
        return tanggalMulaiLocalDateTimeDisimpan != null ? tanggalMulaiLocalDateTimeDisimpan.toLocalDate() : null;
    }
    
    public void setTanggalMulaiDefault(Date combinedDateTimeFromCalendar) {
        LocalDateTime ldt = LocalDateTime.ofInstant(combinedDateTimeFromCalendar.toInstant(), ZoneId.systemDefault());
        dateChooserTanggalMulai.setDate(Date.from(ldt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamMulai, ldt.toLocalTime()); 
        
        LocalDateTime defaultSelesaiLDT = ldt.plusHours(1); // Default durasi 1 jam
        dateChooserTanggalSelesai.setDate(Date.from(defaultSelesaiLDT.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamSelesai, defaultSelesaiLDT.toLocalTime());
        
        recalculateAndDisplayDuration();
    }

    private void finalizeDialog() { setDefaultCloseOperation(DISPOSE_ON_CLOSE); }

    // Inner class PaketItem disederhanakan, hanya id dan nama
    private static class PaketItem {
        private final int id;
        private final String nama;
        // private final BigDecimal harga; // Dihapus

        public PaketItem(int id, String nama) { // Harga dihapus dari parameter
            this.id = id;
            this.nama = nama;
        }
        public int getId() { return id; }
        public String getNama() { return nama; }
        // public BigDecimal getHarga() { return harga; } // Dihapus

        @Override public String toString() { 
            return nama; // Hanya nama yang ditampilkan
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return id == ((PaketItem) o).id;
        }
        @Override public int hashCode() { return Integer.hashCode(id); }
    }
}