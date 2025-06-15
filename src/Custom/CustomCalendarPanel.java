package Custom;

import Controller.EventController;
import UI.EventManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * [KODE FINAL & LENGKAP - FIXED]
 * Panel kalender yang memanggil EventManager dengan benar.
 */
public class CustomCalendarPanel extends JPanel {
    private YearMonth currentYearMonth;
    private final JPanel calendarPanel;
    private final JLabel monthLabel;
    private final EventManager eventManager;
    private final Component parentComponentForDialog;
    private Map<LocalDate, List<EventController.EventSummary>> eventsForCurrentMonth;

    public CustomCalendarPanel(EventManager eventManager, Component parent) {
        this.eventManager = eventManager;
        this.parentComponentForDialog = parent;
        this.currentYearMonth = YearMonth.now();
        this.eventsForCurrentMonth = new HashMap<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        monthLabel = new JLabel("", SwingConstants.CENTER);
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        
        initUI();
        loadAndRefreshCalendarDisplay(); // Load initial data
    }

    private void initUI() {
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        JButton addButton = new JButton("+ Tambah Event");

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));
        
        addButton.addActionListener(e -> eventManager.showAddEventDialog(this, null, this::loadAndRefreshCalendarDisplay));

        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeaderPanel.add(addButton);
        rightHeaderPanel.add(nextButton);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);
    }
    
    private void changeMonth(int amount) {
        currentYearMonth = currentYearMonth.plusMonths(amount);
        loadAndRefreshCalendarDisplay();
    }

    public void loadAndRefreshCalendarDisplay() {
        try {
            // [DIPERBAIKI] Memanggil metode getEventsForMonth yang sekarang sudah ada di EventManager
            this.eventsForCurrentMonth = eventManager.getEventsForMonth(currentYearMonth);
            updateVisuals();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateVisuals() {
        calendarPanel.removeAll();
        
        monthLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID")) + " " + currentYearMonth.getYear());

        String[] dayNames = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            calendarPanel.add(dayLabel);
        }

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int leadingEmptyCells = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < leadingEmptyCells; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            dayBtn.setFocusPainted(false);
            dayBtn.setHorizontalAlignment(SwingConstants.CENTER);
            dayBtn.setVerticalAlignment(SwingConstants.CENTER);

            List<EventController.EventSummary> dayEvents = eventsForCurrentMonth.getOrDefault(date, new ArrayList<>());
            if (!dayEvents.isEmpty()) {
                dayBtn.setBackground(new Color(255, 224, 130)); // Warna kuning untuk menandai ada event
                dayBtn.setOpaque(true);
                StringBuilder tooltipText = new StringBuilder("<html>");
                for (EventController.EventSummary summary : dayEvents) {
                    tooltipText.append("• ").append(summary.getName()).append("<br>");
                }
                tooltipText.append("</html>");
                dayBtn.setToolTipText(tooltipText.toString());
            }

            if (date.equals(LocalDate.now())) {
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            dayBtn.addActionListener(e -> showDayDetailDialog(date));
            calendarPanel.add(dayBtn);
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    private void showDayDetailDialog(final LocalDate date) {
        List<EventController.EventSummary> events = eventsForCurrentMonth.getOrDefault(date, new ArrayList<>());
        
        if (events.isEmpty()) {
            int option = JOptionPane.showConfirmDialog(this,
                "Tidak ada event pada tanggal ini. Tambah event baru?",
                "Informasi Tanggal", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                eventManager.showAddEventDialog(this, date, this::loadAndRefreshCalendarDisplay);
            }
            return;
        }

        JList<EventController.EventSummary> eventList = new JList<>(events.toArray(new EventController.EventSummary[0]));
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setPreferredSize(new Dimension(300, 120));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Event pada " + date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("id","ID")))), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton addButtonDialog = new JButton("Tambah Baru");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addButtonDialog);
        btnPanel.add(editButton);
        btnPanel.add(deleteButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Event", Dialog.ModalityType.APPLICATION_MODAL);
        
        editButton.addActionListener(ae -> {
            EventController.EventSummary selected = eventList.getSelectedValue();
            if (selected != null) {
                dialog.dispose();
                eventManager.showEditEventDialog(this, selected.getId(), this::loadAndRefreshCalendarDisplay);
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih event yang akan diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(de -> {
            EventController.EventSummary selected = eventList.getSelectedValue();
            if (selected != null) {
                dialog.dispose();
                eventManager.deleteEvent(this, selected.getId(), selected.getName(), this::loadAndRefreshCalendarDisplay);
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih event yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        addButtonDialog.addActionListener(add -> {
            dialog.dispose();
            eventManager.showAddEventDialog(this, date, this::loadAndRefreshCalendarDisplay);
        });

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}