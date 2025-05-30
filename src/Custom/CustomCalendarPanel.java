package Custom;

import UI.EventManager;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.List;

public class CustomCalendarPanel extends JPanel {
    private YearMonth currentYearMonth;
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private final EventManager eventManager = new EventManager();

    public CustomCalendarPanel() {
        setLayout(new BorderLayout());

        // Font default
        Font sansFont = new Font("SansSerif", Font.PLAIN, 12);
        UIManager.put("Label.font", sansFont);
        UIManager.put("Button.font", sansFont);
        UIManager.put("ComboBox.font", sansFont);
        UIManager.put("TextField.font", sansFont);
        UIManager.put("List.font", sansFont);

        currentYearMonth = YearMonth.now();
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 30));

        // Tombol navigasi
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        JButton addButton = new JButton("+");

        Color greenColor = new Color(85, 107, 47);
        for (JButton btn : new JButton[]{prevButton, nextButton, addButton}) {
            btn.setBackground(greenColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(45, 35));
        }

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(prevButton);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(monthLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(addButton);
        rightPanel.add(nextButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Tambahkan header ke atas
        add(headerPanel, BorderLayout.NORTH);

        // Panel pembungkus kalender
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 


        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(calendarPanel, BorderLayout.CENTER);

        // Tambahkan wrapper ke CENTER
        add(centerWrapper, BorderLayout.CENTER);

        // Tombol aksi
        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        addButton.addActionListener(e ->
            eventManager.showAddEventDialog(this, LocalDate.now(), this::updateCalendar)
        );

        updateCalendar();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();
        LocalDate firstDay = currentYearMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int lengthOfMonth = currentYearMonth.lengthOfMonth();

        monthLabel.setText(
            currentYearMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
            + " " + currentYearMonth.getYear()
        );

        // Label hari
        String[] days = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        for (String d : days) {
            JLabel dayLabel = new JLabel(d, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
            calendarPanel.add(dayLabel);
        }

        // Kosongkan sel sebelum tanggal 1
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));

            List<String> dayEvents = eventManager.getEventsForDate(date);
            if (!dayEvents.isEmpty()) {
                dayBtn.setBackground(new Color(251, 200, 42));
                dayBtn.setToolTipText("<html>" + String.join("<br>", dayEvents) + "</html>");
            }

            dayBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dayBtn.setPreferredSize(new Dimension(50, 40));

            dayBtn.addActionListener(e -> {
                List<String> events = eventManager.getEventsForDate(date);
                if (!events.isEmpty()) {
                    JList<String> eventList = new JList<>(events.toArray(new String[0]));
                    eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    JScrollPane scrollPane = new JScrollPane(eventList);

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(scrollPane, BorderLayout.CENTER);

                    JButton editButton = new JButton("Edit");
                    JButton deleteButton = new JButton("Delete");

                    JPanel btnPanel = new JPanel();
                    btnPanel.add(editButton);
                    btnPanel.add(deleteButton);
                    panel.add(btnPanel, BorderLayout.SOUTH);

                    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Event Detail - " + date, true);
                    dialog.getContentPane().add(panel);
                    dialog.setSize(300, 200);
                    dialog.setLocationRelativeTo(this);

                    editButton.addActionListener(ae -> {
                        String selected = eventList.getSelectedValue();
                        if (selected != null) {
                            String newTitle = JOptionPane.showInputDialog(this, "Edit Event", selected);
                            if (newTitle != null && !newTitle.trim().isEmpty()) {
                                eventManager.editEvent(date, selected, newTitle.trim());
                                dialog.dispose();
                                updateCalendar();
                            }
                        }
                    });

                    deleteButton.addActionListener(de -> {
                        String selected = eventList.getSelectedValue();
                        if (selected != null) {
                            eventManager.deleteEvent(date, selected);
                            dialog.dispose();
                            updateCalendar();
                        }
                    });

                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Tidak ada event untuk tanggal ini.");
                }
            });

            calendarPanel.add(dayBtn);
        }

        revalidate();
        repaint();
    }
}