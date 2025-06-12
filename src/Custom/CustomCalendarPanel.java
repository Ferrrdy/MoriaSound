package Custom;

import UI.EventManager; // Pastikan package UI bisa diakses
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomCalendarPanel extends JPanel {
    private YearMonth currentYearMonth;
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private final EventManager eventManager; // EventManager akan di-passing dari luar
    private Component parentComponentForDialog; // Untuk parent dialog

    /**
     * Konstruktor CustomCalendarPanel.
     * @param eventManager Instance EventManager yang akan digunakan.
     * @param parentComponentForDialog Komponen parent untuk dialog (biasanya Frame atau JDialog induk).
     */
    public CustomCalendarPanel(EventManager eventManager, Component parentComponentForDialog) {
        this.eventManager = eventManager;
        this.parentComponentForDialog = parentComponentForDialog;
        
        this.currentYearMonth = YearMonth.now();
        initUI();
        loadAndRefreshCalendarDisplay(); // Muat data saat pertama kali dibuat
    }

    private void initUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); // Ukuran font disesuaikan

        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        JButton addButton = new JButton("+ Tambah Event");

        Font buttonFont = new Font("SansSerif", Font.BOLD, 12);
        Dimension navButtonSize = new Dimension(50, 30);
        Dimension addButtonSize = new Dimension(140, 30);

        for (JButton btn : new JButton[]{prevButton, nextButton}) {
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setPreferredSize(navButtonSize);
        }
        addButton.setFont(buttonFont);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(addButtonSize);

        JPanel headerPanel = new JPanel(new BorderLayout(5,0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        JPanel leftNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        leftNavPanel.add(prevButton);

        JPanel rightNavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,0));
        rightNavPanel.add(addButton);
        rightNavPanel.add(nextButton);
        
        headerPanel.add(leftNavPanel, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(rightNavPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5)); 
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
        add(calendarPanel, BorderLayout.CENTER);

        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            loadAndRefreshCalendarDisplay(); 
        });

        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            loadAndRefreshCalendarDisplay(); 
        });

        addButton.addActionListener(e -> {
            LocalDate defaultDateForForm = currentYearMonth.atDay(1); 
            Window windowParent = SwingUtilities.getWindowAncestor(this.parentComponentForDialog);
            if (!(windowParent instanceof Frame)) { // Jika parent bukan Frame, cari Frame teratas
                windowParent = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this.parentComponentForDialog);
                if (windowParent == null) { // Fallback jika tidak ditemukan
                    windowParent = JOptionPane.getRootFrame();
                }
            }
            
            eventManager.showAddEventDialog(windowParent, defaultDateForForm, () -> {
                System.out.println("CustomCalendarPanel: Callback dari FormTambahEvent, memuat ulang kalender.");
                loadAndRefreshCalendarDisplay(); 
            });
        });
    }

    public void loadAndRefreshCalendarDisplay() {
        if (eventManager == null) {
            System.err.println("CustomCalendarPanel Error: EventManager adalah null!");
            return;
        }
        System.out.println("CustomCalendarPanel: Memanggil eventManager.loadEventsForMonth untuk " + currentYearMonth);
        eventManager.loadEventsForMonth(currentYearMonth, this::updateVisualsAfterLoad);
    }

    private void updateVisualsAfterLoad() {
        System.out.println("CustomCalendarPanel: updateVisualsAfterLoad dipanggil untuk " + currentYearMonth);
        calendarPanel.removeAll();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        
        DayOfWeek firstDayActual = firstDayOfMonth.getDayOfWeek(); // MONDAY (1) to SUNDAY (7)
        int leadingEmptyCells = (firstDayActual == DayOfWeek.SUNDAY) ? 0 : firstDayActual.getValue();

        int lengthOfMonth = currentYearMonth.lengthOfMonth();

        monthLabel.setText(
            currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("id","ID"))
            + " " + currentYearMonth.getYear()
        );

        String[] dayNames = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String d : dayNames) {
            JLabel dayLabel = new JLabel(d, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            calendarPanel.add(dayLabel);
        }

        for (int i = 0; i < leadingEmptyCells; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            final LocalDate date = currentYearMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            dayBtn.setPreferredSize(new Dimension(60, 50)); 
            dayBtn.setFocusPainted(false);
            dayBtn.setMargin(new Insets(1,1,1,1));

            List<String> dayEvents = eventManager.getEventsForDate(date);
            if (dayEvents != null && !dayEvents.isEmpty()) {
                dayBtn.setBackground(new Color(251, 200, 42)); 
                dayBtn.setForeground(Color.BLACK);
                String tooltip = "<html>" + String.join("<br>", dayEvents) + "</html>";
                dayBtn.setToolTipText(tooltip);
            } else {
                dayBtn.setBackground(Color.WHITE);
                dayBtn.setForeground(Color.DARK_GRAY);
            }
            if (date.equals(LocalDate.now())) { 
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            } else {
                dayBtn.setBorder(BorderFactory.createEtchedBorder());
            }

            dayBtn.addActionListener(createDayButtonActionListener(date)); 
            calendarPanel.add(dayBtn);
        }
        
        int totalCellsFilled = dayNames.length + leadingEmptyCells + lengthOfMonth;
        int remainingCells = (7 - (totalCellsFilled % 7)) % 7; 
        for (int i = 0; i < remainingCells; i++) {
            calendarPanel.add(new JLabel(""));
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
        System.out.println("CustomCalendarPanel: Tampilan visual kalender telah diupdate.");
    }
    
    private ActionListener createDayButtonActionListener(final LocalDate date) {
        return e -> {
            List<String> events = eventManager.getEventsForDate(date); 
            Window windowParent = SwingUtilities.getWindowAncestor(this.parentComponentForDialog);
             if (!(windowParent instanceof Frame)) { 
                windowParent = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this.parentComponentForDialog);
                if (windowParent == null) { 
                    windowParent = JOptionPane.getRootFrame();
                }
            }

            if (events != null && !events.isEmpty()) {
                JList<String> eventList = new JList<>(events.toArray(new String[0]));
                eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(eventList);
                scrollPane.setPreferredSize(new Dimension(280, 100));

                JPanel panel = new JPanel(new BorderLayout(5,5));
                panel.add(new JLabel("Event pada " + date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + ":"), BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                JButton editButton = new JButton("Edit");
                JButton deleteButton = new JButton("Hapus");

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btnPanel.add(editButton);
                btnPanel.add(deleteButton);
                panel.add(btnPanel, BorderLayout.SOUTH);
                
                JDialog dialog = new JDialog((Frame) windowParent, "Detail Event", true);
                
                dialog.getContentPane().add(panel);
                dialog.pack(); 
                dialog.setLocationRelativeTo(this.parentComponentForDialog);

                editButton.addActionListener(ae -> {
                    String selected = eventList.getSelectedValue();
                    if (selected != null) {
                        String newTitle = JOptionPane.showInputDialog(dialog, "Edit Judul Event:", selected);
                        if (newTitle != null && !newTitle.trim().isEmpty()) {
                            eventManager.editEvent(date, selected, newTitle.trim());
                            dialog.dispose();
                            loadAndRefreshCalendarDisplay(); 
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Pilih event yang akan diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                });

                deleteButton.addActionListener(de -> {
                    String selected = eventList.getSelectedValue();
                    if (selected != null) {
                        int confirm = JOptionPane.showConfirmDialog(dialog, 
                                        "Anda yakin ingin menghapus event: '" + selected + "'?", 
                                        "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            eventManager.deleteEvent(date, selected);
                            dialog.dispose();
                            loadAndRefreshCalendarDisplay(); 
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Pilih event yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                });
                dialog.setVisible(true);
            } else {
                 JOptionPane.showMessageDialog(windowParent, 
                    "Tidak ada event untuk tanggal ini ("+ date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) +").\n" +
                    "Anda bisa menambah event baru melalui tombol '+ Tambah Event'.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        };
    }
}