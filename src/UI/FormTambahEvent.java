package UI;

import Controller.*;
import Model.*;
import Model.Event;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FormTambahEvent extends JDialog {
    // Komponen UI
    private JDateChooser dateChooserTanggalMulai, dateChooserTanggalSelesai;
    private JSpinner spinnerJamMulai, spinnerJamSelesai;
    private JLabel lblDurasiOtomatis;
    private JTextField txtNamaEvent, txtLokasi;
    private JTextArea txtAreaKeterangan;
    private JComboBox<String> cmbStatus;
    private JComboBox<PaketItem> comboPaket;
    private JButton btnSimpan, btnBatal;
    private JList<Crew> listKru;
    private JList<Armada> listArmada;

    // Properti State dan Controller
    private boolean isSaved = false;
    private final EventController eventController;
    private final CrewController crewController;
    private final ArmadaController armadaController;
    private final EventManager eventManager;
    private final Runnable refreshCallback;
    private Event eventToEdit;
    private final String[] statusOptions = {"Direncanakan", "Berlangsung", "Selesai", "Dibatalkan"};

    public FormTambahEvent(Frame parent, EventController controller, EventManager manager, Runnable callback) {
        super(parent, "Tambah Event Baru", true);
        this.eventController = controller;
        this.eventManager = manager;
        this.refreshCallback = callback;
        this.crewController = new CrewController();
        this.armadaController = new ArmadaController();
        this.eventToEdit = null;
        initComponents();
        loadSelectionData();
    }

    public FormTambahEvent(Frame parent, EventController controller, EventManager manager, Event eventToEdit, Runnable callback) {
        super(parent, "Edit Event", true);
        this.eventController = controller;
        this.eventManager = manager;
        this.refreshCallback = callback;
        this.eventToEdit = eventToEdit;
        this.crewController = new CrewController();
        this.armadaController = new ArmadaController();
        initComponents();
        loadSelectionData();
        populateFormForEdit();
    }
    
    // [FIX] METHOD YANG HILANG DITAMBAHKAN
    public void setTanggalDefault(LocalDate defaultDate) {
        if (defaultDate != null) {
            Date dateToSet = Date.from(defaultDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateChooserTanggalMulai.setDate(dateToSet);
            dateChooserTanggalSelesai.setDate(dateToSet); // Tambahkan baris ini
        }
    }

    private void initComponents() {
        txtNamaEvent = new JTextField(25);
        txtLokasi = new JTextField(25);
        txtAreaKeterangan = new JTextArea(4, 25);
        txtAreaKeterangan.setLineWrap(true);
        txtAreaKeterangan.setWrapStyleWord(true);
        cmbStatus = new JComboBox<>(statusOptions);
        comboPaket = new JComboBox<>();
        
        dateChooserTanggalMulai = new JDateChooser(new Date());
        dateChooserTanggalMulai.setDateFormatString("dd MMMM yyyy");
        dateChooserTanggalSelesai = new JDateChooser(new Date());
        dateChooserTanggalSelesai.setDateFormatString("dd MMMM yyyy");

        spinnerJamMulai = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de_mulai = new JSpinner.DateEditor(spinnerJamMulai, "HH:mm");
        spinnerJamMulai.setEditor(de_mulai);
        setSpinnerTime(spinnerJamMulai, LocalTime.MIDNIGHT);

        spinnerJamSelesai = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de_selesai = new JSpinner.DateEditor(spinnerJamSelesai, "HH:mm");
        spinnerJamSelesai.setEditor(de_selesai);
        setSpinnerTime(spinnerJamSelesai, LocalTime.MIDNIGHT.plusHours(1));
        
        lblDurasiOtomatis = new JLabel("Durasi: -");
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");

        listKru = new JList<>();
        listKru.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listArmada = new JList<>();
        listArmada.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // ... (Sisa layouting initComponents tetap sama, tidak perlu diubah)
        JPanel panelMulai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelMulai.add(dateChooserTanggalMulai);
        panelMulai.add(new JLabel("Jam:"));
        panelMulai.add(spinnerJamMulai);

        JPanel panelSelesai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSelesai.add(dateChooserTanggalSelesai);
        panelSelesai.add(new JLabel("Jam:"));
        panelSelesai.add(spinnerJamSelesai);

        setLayout(new BorderLayout(10, 10));
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int y = 0;
        // Nama Event
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel("Nama Event:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtNamaEvent, gbc);
        y++;

        // Tanggal Mulai
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel("Mulai:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(panelMulai, gbc);
        y++;

        // Tanggal Selesai
        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Selesai:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(panelSelesai, gbc);
        y++;

        // Durasi
        gbc.gridx = 1; gbc.gridy = y; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(lblDurasiOtomatis, gbc);
        y++;
        
        // Lokasi
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; panelForm.add(new JLabel("Lokasi:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(txtLokasi, gbc);
        y++;
        
        // Status dan Paket
        gbc.gridx = 0; gbc.gridy = y; panelForm.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelForm.add(cmbStatus, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelForm.add(new JLabel("Paket:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelForm.add(comboPaket, gbc);
        y++;

        // Keterangan
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.2;
        panelForm.add(new JScrollPane(txtAreaKeterangan), gbc);
        y++;

        // Kru
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Pilih Kru:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        panelForm.add(new JScrollPane(listKru), gbc);
        y++;

        // Armada
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Pilih Armada:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        panelForm.add(new JScrollPane(listArmada), gbc);

        add(panelForm, BorderLayout.CENTER);
        JPanel panelButtonBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtonBawah.add(btnBatal);
        panelButtonBawah.add(btnSimpan);
        add(panelButtonBawah, BorderLayout.SOUTH);
        
        setupActionListeners();
        finalizeDialog();
    }
    
    private void loadSelectionData() {
        try {
            // Load Paket
            List<PaketItem> allPaket = eventController.getAvailablePaket();
            comboPaket.removeAllItems();
            for (PaketItem paket : allPaket) {
                comboPaket.addItem(paket);
            }
            
            // Load Crew
            List<Crew> allCrew = crewController.getAllCrewInstance();
            listKru.setListData(allCrew.toArray(new Crew[0]));

            // Load Armada
            List<Armada> allArmada = armadaController.getAllAvailableArmada();
            listArmada.setListData(allArmada.toArray(new Armada[0]));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pilihan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error tidak terduga saat memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void setupActionListeners() {
        btnSimpan.addActionListener(e -> simpanEvent());
        btnBatal.addActionListener(e -> dispose());
        
        PropertyChangeListener dateChangeListener = evt -> recalculateAndDisplayDuration();
        dateChooserTanggalMulai.addPropertyChangeListener("date", dateChangeListener);
        dateChooserTanggalSelesai.addPropertyChangeListener("date", dateChangeListener);
        
        ChangeListener timeChangeListener = e -> recalculateAndDisplayDuration();
        spinnerJamMulai.addChangeListener(timeChangeListener);
        spinnerJamSelesai.addChangeListener(timeChangeListener);
    }
    
    // [FIX] METHOD DIPERBAIKI TOTAL
    private void populateFormForEdit() {
        if (eventToEdit == null) return;
    
        setTitle("Edit Event: " + eventToEdit.getNamaEvent());
        txtNamaEvent.setText(eventToEdit.getNamaEvent());
        txtLokasi.setText(eventToEdit.getLokasi());
        txtAreaKeterangan.setText(eventToEdit.getKeterangan());
        cmbStatus.setSelectedItem(eventToEdit.getStatus());
    
        dateChooserTanggalMulai.setDate(Date.from(eventToEdit.getTanggalMulai().atZone(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamMulai, eventToEdit.getTanggalMulai().toLocalTime());
    
        dateChooserTanggalSelesai.setDate(Date.from(eventToEdit.getTanggalSelesai().atZone(ZoneId.systemDefault()).toInstant()));
        setSpinnerTime(spinnerJamSelesai, eventToEdit.getTanggalSelesai().toLocalTime());
    
        PaketItem selectedPaket = eventToEdit.getPaketItem();
        if (selectedPaket != null) {
            for (int i = 0; i < comboPaket.getItemCount(); i++) {
                if (comboPaket.getItemAt(i).getId() == selectedPaket.getId()) {
                    comboPaket.setSelectedIndex(i);
                    break;
                }
            }
        }
    
        SwingUtilities.invokeLater(() -> {
            try {
                // Memanggil eventController, bukan crewController
                List<Crew> eventCrew = eventController.getCrewForEvent(eventToEdit.getId());
                int[] crewIndices = eventCrew.stream()
                    .mapToInt(crew -> {
                        ListModel<Crew> model = listKru.getModel();
                        for (int i = 0; i < model.getSize(); i++) {
                            if (model.getElementAt(i).getId() == crew.getId()) {
                                return i;
                            }
                        }
                        return -1;
                    })
                    .filter(i -> i != -1)
                    .toArray();
                listKru.setSelectedIndices(crewIndices);

                // [FIX] Memanggil eventController, bukan armadaController
                List<Armada> eventArmada = eventController.getArmadaForEvent(eventToEdit.getId());
                int[] armadaIndices = eventArmada.stream()
                     .mapToInt(armada -> {
                        ListModel<Armada> model = listArmada.getModel();
                        for (int i = 0; i < model.getSize(); i++) {
                            if (model.getElementAt(i).getId() == armada.getId()) {
                                return i;
                            }
                        }
                        return -1;
                    })
                    .filter(i -> i != -1)
                    .toArray();
                listArmada.setSelectedIndices(armadaIndices);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat detail kru/armada: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        recalculateAndDisplayDuration();
    }

    private void setSpinnerTime(JSpinner spinner, LocalTime time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
        calendar.set(Calendar.MINUTE, time.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        spinner.setValue(calendar.getTime());
    }

    private LocalTime getSpinnerTime(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private void recalculateAndDisplayDuration() {
        try {
            Date tanggalMulai = dateChooserTanggalMulai.getDate();
            Date tanggalSelesai = dateChooserTanggalSelesai.getDate();
            
            if (tanggalMulai == null || tanggalSelesai == null) {
                lblDurasiOtomatis.setText("Durasi: -");
                return;
            }
            
            LocalDateTime dateTimeMulai = LocalDateTime.of(tanggalMulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamMulai));
            LocalDateTime dateTimeSelesai = LocalDateTime.of(tanggalSelesai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamSelesai));
            
            if (dateTimeSelesai.isBefore(dateTimeMulai) || dateTimeSelesai.isEqual(dateTimeMulai)) {
                lblDurasiOtomatis.setText("Durasi: Waktu selesai harus setelah waktu mulai");
                lblDurasiOtomatis.setForeground(Color.RED);
                return;
            }
            
            Duration duration = Duration.between(dateTimeMulai, dateTimeSelesai);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            
            lblDurasiOtomatis.setText(String.format("Durasi: %d jam %d menit", hours, minutes));
            lblDurasiOtomatis.setForeground(Color.BLACK);
            
        } catch (Exception e) {
            lblDurasiOtomatis.setText("Durasi: Error menghitung");
            lblDurasiOtomatis.setForeground(Color.RED);
        }
    }

    private boolean validateInput() {
        if (txtNamaEvent.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama event tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtLokasi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lokasi tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dateChooserTanggalMulai.getDate() == null || dateChooserTanggalSelesai.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal mulai dan selesai harus dipilih!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Validasi waktu
         try {
            LocalDateTime dateTimeMulai = LocalDateTime.of(dateChooserTanggalMulai.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamMulai));
            LocalDateTime dateTimeSelesai = LocalDateTime.of(dateChooserTanggalSelesai.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamSelesai));
            if (dateTimeSelesai.isBefore(dateTimeMulai) || dateTimeSelesai.isEqual(dateTimeMulai)) {
                JOptionPane.showMessageDialog(this, "Waktu selesai harus setelah waktu mulai!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error validasi waktu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboPaket.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Paket harus dipilih!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

     private void simpanEvent() {
        if (!validateInput()) {
            return;
        }

        try {
            // Kumpulkan semua data dari form
            String namaEvent = txtNamaEvent.getText().trim();
            String lokasi = txtLokasi.getText().trim();
            String keterangan = txtAreaKeterangan.getText().trim();
            String statusBaru = (String) cmbStatus.getSelectedItem();
            PaketItem selectedPaket = (PaketItem) comboPaket.getSelectedItem();
            
            LocalDateTime tanggalMulai = LocalDateTime.of(dateChooserTanggalMulai.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamMulai));
            LocalDateTime tanggalSelesai = LocalDateTime.of(dateChooserTanggalSelesai.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), getSpinnerTime(spinnerJamSelesai));

            List<Integer> crewIds = listKru.getSelectedValuesList().stream().map(Crew::getId).collect(Collectors.toList());
            List<Integer> armadaIds = listArmada.getSelectedValuesList().stream().map(Armada::getId).collect(Collectors.toList());
            
            if (eventToEdit == null) {
                // --- MODE TAMBAH EVENT BARU ---
                Event newEvent = new Event();
                newEvent.setNamaEvent(namaEvent);
                newEvent.setTanggalMulai(tanggalMulai);
                newEvent.setTanggalSelesai(tanggalSelesai);
                newEvent.setLokasi(lokasi);
                newEvent.setKeterangan(keterangan);
                newEvent.setStatus(statusBaru);
                
                eventController.createNewEvent(newEvent, selectedPaket.getId(), crewIds, armadaIds);
                JOptionPane.showMessageDialog(this, "Event berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // --- MODE EDIT EVENT ---
                String statusLama = eventToEdit.getStatus();
                
                // Update object eventToEdit dengan semua data terbaru dari form
                eventToEdit.setNamaEvent(namaEvent);
                eventToEdit.setTanggalMulai(tanggalMulai);
                eventToEdit.setTanggalSelesai(tanggalSelesai);
                eventToEdit.setLokasi(lokasi);
                eventToEdit.setKeterangan(keterangan);
                eventToEdit.setStatus(statusBaru);
                eventToEdit.setPaket(selectedPaket);

                // [PERBAIKAN UTAMA] Menggunakan perbandingan yang aman dari null
                if (!"Selesai".equals(statusLama) && "Selesai".equals(statusBaru)) {
                    
                    this.dispose(); // Tutup form edit
                    
                    FormKonfirmasiKondisi dialogKonfirmasi = new FormKonfirmasiKondisi(
                        (Frame) SwingUtilities.getWindowAncestor(this), 
                        eventToEdit, 
                        eventController, 
                        refreshCallback
                    );
                    dialogKonfirmasi.setVisible(true);
                    return; 
                
                } else {
                    // Jika tidak, lakukan update normal seperti biasa
                    eventController.updateEvent(eventToEdit, crewIds, armadaIds);
                    JOptionPane.showMessageDialog(this, "Event berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            isSaved = true;
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            dispose();

        } catch (InsufficientStockException e) {
            JOptionPane.showMessageDialog(this, "Stok Tidak Cukup", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error tidak terduga: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void finalizeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setMinimumSize(new Dimension(550, 650));
        recalculateAndDisplayDuration();
    }

    public boolean isSaved() {
        return isSaved;
    }
}