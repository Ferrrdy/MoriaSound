package UI;

import Controller.EventController;
import Model.Event;
import Model.RincianKondisiAset;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormKonfirmasiKondisi extends JDialog {
    private final Event event;
    private final EventController eventController;
    private final Runnable refreshCallback;

    // Map untuk menyimpan referensi ke komponen input
    private final Map<Integer, JComboBox<String>> singleItemConditionMap = new HashMap<>();
    private final Map<Integer, MultiItemInputPanel> multiItemConditionMap = new HashMap<>();

    private final String[] kondisiOptions = {"Baik", "Rusak Ringan", "Rusak Berat", "Hilang"};
    private JButton btnSelesaikan;

    public FormKonfirmasiKondisi(Frame owner, Event event, EventController controller, Runnable refreshCallback) {
        super(owner, "Konfirmasi Kondisi Aset Setelah Event", true);
        this.event = event;
        this.eventController = controller;
        this.refreshCallback = refreshCallback;
        initComponents();
        validateInputs(); // Validasi awal
    }

    // GANTI SELURUH METHOD INI
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // [FIX] Buat tombol dan panelnya TERLEBIH DAHULU
        btnSelesaikan = new JButton("Selesaikan Event & Proses Aset");
        btnSelesaikan.addActionListener(e -> prosesPenyelesaian());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(btnSelesaikan);
        
        // --- Setelah tombol dibuat, baru buat sisa komponen ---
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("<html>Mohon perbarui kondisi setiap aset yang kembali setelah event selesai. <br>Stok barang hanya akan dikembalikan jika kondisinya 'Baik'.</html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        mainPanel.add(infoLabel);
        
        // Gabungkan list barang dan armada untuk ditampilkan
        List<EventController.UsedAssetInfo> allAssets = new ArrayList<>();
        allAssets.addAll(eventController.getUsedBarangForEvent(event.getId()));
        allAssets.addAll(eventController.getUsedArmadaForEvent(event.getId()));

        // Pembuatan panel ini akan memicu ChangeListener,
        // tapi sekarang btnSelesaikan sudah ada, jadi tidak akan error.
        JPanel contentPanel = createAsetPanel(allAssets);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        
        mainPanel.add(scrollPane);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); // Gunakan panel tombol yang sudah dibuat

        pack();
        setSize(new Dimension(650, 550));
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(getParent());
    }

    private JPanel createAsetPanel(List<EventController.UsedAssetInfo> asetList) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        if (asetList.isEmpty()) {
            panel.add(new JLabel("Tidak ada aset yang dialokasikan untuk event ini."));
        } else {
            for (EventController.UsedAssetInfo aset : asetList) {
                gbc.gridx = 0;
                gbc.gridy = y;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                if (aset.getJumlah() > 1) {
                    // --- TAMPILAN UNTUK ITEM JAMAK (MULTI-ITEM) ---
                    MultiItemInputPanel inputPanel = new MultiItemInputPanel(aset.getNama(), aset.getJumlah());
                    panel.add(inputPanel, gbc);
                    multiItemConditionMap.put(aset.getLogId(), inputPanel);
                } else {
                    // --- TAMPILAN UNTUK ITEM TUNGGAL (SINGLE-ITEM) ---
                    JPanel singleItemPanel = new JPanel(new BorderLayout(15, 0));
                    singleItemPanel.add(new JLabel(aset.getNama()), BorderLayout.CENTER);
                    
                    JComboBox<String> cmbKondisi = new JComboBox<>(kondisiOptions);
                    singleItemPanel.add(cmbKondisi, BorderLayout.EAST);
                    
                    panel.add(singleItemPanel, gbc);
                    singleItemConditionMap.put(aset.getLogId(), cmbKondisi);
                }
                y++;
            }
        }
        return panel;
    }

    private void prosesPenyelesaian() {
        if (!validateInputs()) {
            JOptionPane.showMessageDialog(this, "Jumlah rincian kondisi ada yang tidak sesuai dengan total barang yang digunakan.", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<RincianKondisiAset> kondisiBarangList = new ArrayList<>();
        List<RincianKondisiAset> kondisiArmadaList = new ArrayList<>();

        // Proses item jamak (hanya untuk barang)
        for (Map.Entry<Integer, MultiItemInputPanel> entry : multiItemConditionMap.entrySet()) {
            RincianKondisiAset rincian = new RincianKondisiAset(entry.getKey());
            Map<String, Integer> jumlahKondisi = entry.getValue().getJumlahPerKondisi();
            for(Map.Entry<String, Integer> kondisiEntry : jumlahKondisi.entrySet()) {
                rincian.setJumlahUntukKondisi(kondisiEntry.getKey(), kondisiEntry.getValue());
            }
            kondisiBarangList.add(rincian);
        }

        // Proses item tunggal (bisa barang atau armada)
        for (Map.Entry<Integer, JComboBox<String>> entry : singleItemConditionMap.entrySet()) {
            RincianKondisiAset rincian = new RincianKondisiAset(entry.getKey());
            rincian.setJumlahUntukKondisi((String) entry.getValue().getSelectedItem(), 1);
            
            boolean isArmada = eventController.getUsedArmadaForEvent(event.getId())
                                     .stream().anyMatch(a -> a.getLogId() == entry.getKey());
            if (isArmada) {
                kondisiArmadaList.add(rincian);
            } else {
                kondisiBarangList.add(rincian);
            }
        }
        
        try {
            eventController.selesaikanEventDenganKondisi(event, kondisiBarangList, kondisiArmadaList);
            JOptionPane.showMessageDialog(this, "Event berhasil diselesaikan dan aset telah diproses.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyelesaikan event: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean validateInputs() {
        boolean allValid = true;
        for (MultiItemInputPanel panel : multiItemConditionMap.values()) {
            if (!panel.isTotalValid()) {
                allValid = false;
                break;
            }
        }
        btnSelesaikan.setEnabled(allValid);
        return allValid;
    }
    
    // ==================================================================
    //  INNER CLASS UNTUK PANEL INPUT ITEM JAMAK
    // ==================================================================
    private class MultiItemInputPanel extends JPanel implements ChangeListener {
        private final int totalItems;
        private final JLabel statusLabel;
        private final Map<String, JSpinner> spinnerMap = new HashMap<>();

        // GANTI SELURUH ISI KONSTRUKTOR INI
        MultiItemInputPanel(String itemName, int totalItems) {
            this.totalItems = totalItems;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createTitledBorder(totalItems + "x " + itemName));

            // [FIX] Buat statusLabel TERLEBIH DAHULU
            statusLabel = new JLabel();
            statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

            JPanel spinnersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
            for (String kondisi : kondisiOptions) {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, totalItems, 1));
                spinner.addChangeListener(this);
                spinnerMap.put(kondisi, spinner);
                
                spinnersPanel.add(new JLabel(kondisi + ":"));
                spinnersPanel.add(spinner);
            }
            
            // Setelah semua dibuat, baru atur nilai awal spinner
            spinnerMap.get("Baik").setValue(totalItems);

            add(spinnersPanel);
            add(statusLabel); // Tambahkan statusLabel yang sudah dibuat
            
            // Panggil updateStatusLabel() sekali di akhir untuk set teks awal
            updateStatusLabel(); 
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            updateStatusLabel();
            FormKonfirmasiKondisi.this.validateInputs(); // Validasi form utama
        }
        
        private void updateStatusLabel() {
            int currentTotal = 0;
            for (JSpinner s : spinnerMap.values()) {
                currentTotal += (Integer) s.getValue();
            }

            if (currentTotal == totalItems) {
                statusLabel.setText("Total: " + currentTotal + "/" + totalItems + " (OK)");
                statusLabel.setForeground(new Color(0, 128, 0)); // Hijau tua
            } else {
                statusLabel.setText("Total: " + currentTotal + "/" + totalItems + " (Jumlah tidak sesuai!)");
                statusLabel.setForeground(Color.RED);
            }
        }
        
        public boolean isTotalValid() {
            int currentTotal = 0;
            for (JSpinner s : spinnerMap.values()) {
                currentTotal += (Integer) s.getValue();
            }
            return currentTotal == totalItems;
        }
        
        public Map<String, Integer> getJumlahPerKondisi() {
            Map<String, Integer> result = new HashMap<>();
            for(Map.Entry<String, JSpinner> entry : spinnerMap.entrySet()) {
                result.put(entry.getKey(), (Integer) entry.getValue().getValue());
            }
            return result;
        }
    }
}