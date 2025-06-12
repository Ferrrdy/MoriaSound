package UI;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import Model.Event;

public class EventManager {
    private final Map<LocalDate, List<String>> events = new HashMap<>();
    // Map untuk menyimpan mapping nama event ke ID event
    private final Map<String, Integer> eventNameToIdMap = new HashMap<>();
    private EventController eventController;

    public EventManager() {
        System.out.println("EventManager instance created. Map 'events' is initially empty.");
        this.eventController = new EventController();
    }

    public Map<LocalDate, List<String>> getEvents() {
        return events;
    }

    public void loadEventsForMonth(YearMonth yearMonth, Runnable refreshCallback) {
        System.out.println("EventManager: MEMUAT EVENTS untuk bulan: " + yearMonth + "...");
        this.events.clear(); 
        this.eventNameToIdMap.clear(); // Clear mapping juga
        
        try {
            // Load events dengan ID untuk mapping
            loadEventsWithIdMapping(yearMonth);
            System.out.println("EventManager: Events dimuat dari DB. Jumlah tanggal dengan event: " + this.events.size());
        } catch (SQLException e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(null, 
                "Gagal memuat data event dari database untuk bulan " + yearMonth.toString() + ":\n" + e.getMessage(), 
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        if (refreshCallback != null) {
            System.out.println("EventManager: Menjalankan refreshCallback setelah loadEventsForMonth.");
            refreshCallback.run(); 
        } else {
            System.out.println("EventManager: refreshCallback adalah null di loadEventsForMonth.");
        }
    }

    private void loadEventsWithIdMapping(YearMonth yearMonth) throws SQLException {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        String sql = "SELECT id_event, tanggal_mulai, nama_event FROM event " +
                     "WHERE tanggal_mulai >= ? AND tanggal_mulai <= ? ORDER BY tanggal_mulai";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(firstDay.atStartOfDay()));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(lastDay.atTime(23, 59, 59))); 
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("id_event");
                LocalDate eventDate = rs.getTimestamp("tanggal_mulai").toLocalDateTime().toLocalDate();
                String eventName = rs.getString("nama_event");
                
                // Simpan ke map events
                events.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(eventName);
                
                // Simpan mapping nama ke ID
                eventNameToIdMap.put(eventName, eventId);
            }
        } 
    }

    public void showAddEventDialog(Component parent, LocalDate currentDate, Runnable updateCallbackSetelahSimpan) {
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
                System.out.println("EventManager (showAddEventDialog): Event baru '" + namaEventBaru + "' ditambahkan ke map in-memory.");
            }
            
            if (updateCallbackSetelahSimpan != null) {
                 System.out.println("EventManager (showAddEventDialog): Menjalankan updateCallbackSetelahSimpan.");
                updateCallbackSetelahSimpan.run();
            }
        }
    }

    public List<String> getEventsForDate(LocalDate date) {
        return events.getOrDefault(date, new ArrayList<>());
    }

    // Method untuk delete event berdasarkan tanggal dan nama event
public void deleteEvent(LocalDate date, String eventName) {
    try {
        // Dapatkan ID event dari mapping
        Integer eventId = eventNameToIdMap.get(eventName);
        if (eventId == null) {
            JOptionPane.showMessageDialog(null, 
                "ID Event tidak ditemukan untuk: " + eventName, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Apakah Anda yakin ingin menghapus event '" + eventName + "'?\n" +
            "Semua data terkait event ini juga akan dihapus.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Hapus dari database menggunakan controller
        if (eventController.deleteEvent(eventId)) {
            // Hapus dari map lokal jika berhasil dihapus dari database
            List<String> eventsOnDate = events.get(date);
            if (eventsOnDate != null) {
                eventsOnDate.remove(eventName);
                if (eventsOnDate.isEmpty()) {
                    events.remove(date);
                }
            }
            eventNameToIdMap.remove(eventName);
            
            JOptionPane.showMessageDialog(null, 
                "Event berhasil dihapus.", 
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Gagal menghapus event dari database.", 
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Gagal menghapus event: " + e.getMessage(), 
            "Error Database", JOptionPane.ERROR_MESSAGE);
    }
}

// Method untuk edit event berdasarkan tanggal dan nama event lama (update semua field)
public void editEvent(LocalDate date, String oldEventName, Event updatedEvent) {
    try {
        // Dapatkan ID event dari mapping
        Integer eventId = eventNameToIdMap.get(oldEventName);
        if (eventId == null) {
            JOptionPane.showMessageDialog(null, 
                "ID Event tidak ditemukan untuk: " + oldEventName, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set ID untuk event yang akan diupdate
        updatedEvent.setIdEvent(eventId);
        
        // Update di database menggunakan controller
        if (eventController.updateEvent(updatedEvent)) {
            // Update map lokal jika berhasil diupdate di database
            List<String> eventsOnDate = events.get(date);
            if (eventsOnDate != null) {
                int index = eventsOnDate.indexOf(oldEventName);
                if (index != -1) {
                    eventsOnDate.set(index, updatedEvent.getNamaEvent());
                }
            }
            
            // Update mapping jika nama event berubah
            if (!oldEventName.equals(updatedEvent.getNamaEvent())) {
                eventNameToIdMap.remove(oldEventName);
                eventNameToIdMap.put(updatedEvent.getNamaEvent(), eventId);
            }
            
            // Jika tanggal berubah, pindahkan event ke tanggal baru
            LocalDate newDate = updatedEvent.getTanggalMulai().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            if (!date.equals(newDate)) {
                // Hapus dari tanggal lama
                if (eventsOnDate != null) {
                    eventsOnDate.remove(updatedEvent.getNamaEvent());
                    if (eventsOnDate.isEmpty()) {
                        events.remove(date);
                    }
                }
                
                // Tambahkan ke tanggal baru
                events.computeIfAbsent(newDate, k -> new ArrayList<>())
                      .add(updatedEvent.getNamaEvent());
            }
            
            JOptionPane.showMessageDialog(null, 
                "Event berhasil diubah.", 
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Gagal mengubah event di database.", 
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Gagal mengubah event: " + e.getMessage(), 
            "Error Database", JOptionPane.ERROR_MESSAGE);
    }
}

// Method overload untuk backward compatibility (hanya edit nama)
public void editEvent(LocalDate date, String oldEventName, String newEventName) {
    try {
        // Dapatkan ID event dari mapping
        Integer eventId = eventNameToIdMap.get(oldEventName);
        if (eventId == null) {
            JOptionPane.showMessageDialog(null, 
                "ID Event tidak ditemukan untuk: " + oldEventName, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buat event object dengan data minimal untuk update nama saja
        Event updatedEvent = new Event();
        updatedEvent.setIdEvent(eventId);
        updatedEvent.setNamaEvent(newEventName);
        
        // Ambil data existing event dari database jika diperlukan
        // Atau bisa menggunakan method khusus updateEventNameOnly di controller
        
        // Update menggunakan method updateEventNameOnly (perlu ditambahkan di controller)
        if (updateEventNameOnly(eventId, newEventName)) {
            // Update map lokal jika berhasil diupdate di database
            List<String> eventsOnDate = events.get(date);
            if (eventsOnDate != null) {
                int index = eventsOnDate.indexOf(oldEventName);
                if (index != -1) {
                    eventsOnDate.set(index, newEventName);
                }
            }
            
            // Update mapping
            eventNameToIdMap.remove(oldEventName);
            eventNameToIdMap.put(newEventName, eventId);
            
            JOptionPane.showMessageDialog(null, 
                "Event berhasil diubah.", 
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Gagal mengubah event di database.", 
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Gagal mengubah event: " + e.getMessage(), 
            "Error Database", JOptionPane.ERROR_MESSAGE);
    }
}

// Method helper untuk update nama event saja
private boolean updateEventNameOnly(int eventId, String newEventName) {
    String sql = "UPDATE event SET nama_event = ? WHERE id_event = ?";
    try (Connection conn = DbConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, newEventName);
        stmt.setInt(2, eventId);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
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
    // ... (KODE LENGKAP FormTambahEvent yang sudah disederhanakan, tanpa invoice, tanpa klien,
    //      PaketItem hanya ID & nama, JDateChooser + JSpinner HH:mm, dan simpanEvent
    //      yang memanggil aktivasiEventDanProsesBarang secara kondisional,
    //      SAMA SEPERTI DI RESPONS "full code EventManager" SAYA SEBELUM INI) ...
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

    private boolean isSaved = false;
    private String namaEventDisimpan;
    private LocalDateTime tanggalMulaiLocalDateTimeDisimpan;

    private final String[] statusOptions = {"Direncanakan", "Berlangsung", "Selesai", "Dibatalkan"};

    public FormTambahEvent(Frame parent) {
        super(parent, "Tambah Event Baru", true);
        initComponents();
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
        setSpinnerTime(spinnerJamMulai, LocalTime.of(9,0));
        spinnerJamMulai.setPreferredSize(new Dimension(70, spinnerJamMulai.getPreferredSize().height));

        dateChooserTanggalSelesai = new JDateChooser();
        Calendar calSelesaiInit = Calendar.getInstance();
        calSelesaiInit.setTime(dateChooserTanggalMulai.getDate() != null ? dateChooserTanggalMulai.getDate() : new Date());
        LocalTime jamSelesaiDefault = LocalTime.of(10,0);
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
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");

        setLayout(new BorderLayout(10, 10));
        JPanel panelForm = new JPanel(new GridBagLayout());
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
            return LocalTime.MIDNIGHT;
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
        String statusDipilih = (String) cmbStatus.getSelectedItem();
        PaketItem paketDipilih = (PaketItem) comboPaket.getSelectedItem();

        Date tglMulaiUtil = dateChooserTanggalMulai.getDate();
        Date tglSelesaiUtil = dateChooserTanggalSelesai.getDate();
        LocalTime jamMulaiSaatIni = getSpinnerTime(spinnerJamMulai);
        LocalTime jamSelesaiSaatIni = getSpinnerTime(spinnerJamSelesai);

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

        int idEventBaru = -1;
        try {
            idEventBaru = EventController.insertEvent(namaEvent, mulaiLDT, selesaiLDT, durasiJamDatabase, lokasi, keterangan, statusDipilih);

            boolean paketDitambahkan = EventController.tambahPaketKeEvent(idEventBaru, paketDipilih.getId());
            if (!paketDitambahkan) {
                 JOptionPane.showMessageDialog(this, "Event utama tersimpan (ID: "+idEventBaru+"), tetapi GAGAL menghubungkan paket. Proses barang tidak dilanjutkan.", "Peringatan Paket Kritis", JOptionPane.WARNING_MESSAGE);
                 this.namaEventDisimpan = namaEvent;
                 this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT;
                 this.isSaved = true;
                 return;
            }

            if ("Berlangsung".equals(statusDipilih)) {
                EventController.aktivasiEventDanProsesBarang(idEventBaru, paketDipilih.getId());
            }

            this.namaEventDisimpan = namaEvent;
            this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT;
            this.isSaved = true;
            JOptionPane.showMessageDialog(this, "Event berhasil disimpan. Status: " + statusDipilih, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (InsufficientStockException e) {
            JOptionPane.showMessageDialog(this, "Event ("+namaEvent+") berhasil dibuat (ID: "+ (idEventBaru == -1 ? "N/A" : idEventBaru) +"), TETAPI gagal diaktifkan menjadi 'Berlangsung' karena: " + e.getMessage() +
                                              "\nStatus event di database adalah '" + statusDipilih + "'. Harap periksa stok dan status event secara manual.", "Stok Tidak Cukup untuk Aktivasi", JOptionPane.ERROR_MESSAGE);
            if (idEventBaru != -1) { this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        } catch (SQLException e) {
            String pesanError = "Terjadi kesalahan database: " + e.getMessage();
            if (idEventBaru != -1) {
                pesanError += "\nEvent ("+namaEvent+") mungkin sudah tersimpan sebagian (ID: "+idEventBaru+"). Harap periksa konsistensi data.";
            } else {
                pesanError += "\nGagal menyimpan event utama.";
            }
            JOptionPane.showMessageDialog(this, pesanError, "Error Database Kritis", JOptionPane.ERROR_MESSAGE);
            if (idEventBaru != -1) {this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan aplikasi yang tidak terduga: " + e.getMessage(), "Error Aplikasi", JOptionPane.ERROR_MESSAGE);
            if (idEventBaru != -1) {this.namaEventDisimpan = namaEvent; this.tanggalMulaiLocalDateTimeDisimpan = mulaiLDT; this.isSaved = true; }
            e.printStackTrace();
        }
    }

    public boolean isSaved() { return isSaved; }
    public String getNamaEventDisimpan() { return namaEventDisimpan; }
    public LocalDate getTanggalMulaiDisimpanAsLocalDate() {
        return tanggalMulaiLocalDateTimeDisimpan != null ? tanggalMulaiLocalDateTimeDisimpan.toLocalDate() : null;
    }
    
    public void setTanggalMulaiDefault(Date combinedDateTimeFromCalendar) {
        LocalDateTime ldt = LocalDateTime.ofInstant(combinedDateTimeFromCalendar.toInstant(), ZoneId.systemDefault());
        dateChooserTanggalMulai.setDate(Date.from(ldt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamMulai, ldt.toLocalTime()); 
        
        LocalDateTime defaultSelesaiLDT = ldt.plusHours(1); 
        dateChooserTanggalSelesai.setDate(Date.from(defaultSelesaiLDT.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamSelesai, defaultSelesaiLDT.toLocalTime());
        
        recalculateAndDisplayDuration();
    }

    private void finalizeDialog() { setDefaultCloseOperation(DISPOSE_ON_CLOSE); }

    private static class PaketItem {
        private final int id;
        private final String nama;

        public PaketItem(int id, String nama) { 
            this.id = id;
            this.nama = nama;
        }
        public int getId() { return id; }
        public String getNama() { return nama; }

        @Override public String toString() { 
            return nama; 
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return id == ((PaketItem) o).id;
        }
        @Override public int hashCode() { return Integer.hashCode(id); }
    }
}