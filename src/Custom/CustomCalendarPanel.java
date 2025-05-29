package Custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomCalendarPanel extends JPanel {
    private LocalDate currentDate;
    private final Map<LocalDate, List<String>> events;
    private final JPanel calendarGrid;
    private final JLabel monthLabel;

    public CustomCalendarPanel() {
        this.events = new HashMap<>();
        currentDate = LocalDate.now();

        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER with prev, month label, add, next
        JPanel header = new JPanel(new BorderLayout(5, 0));
        header.setBackground(Color.WHITE);

        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        JButton addButton = new JButton("+");

        Dimension btnSize = new Dimension(40, 30);
        for (JButton b : Arrays.asList(prevButton, nextButton, addButton)) {
            b.setPreferredSize(btnSize);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
        }
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        header.add(prevButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setBackground(Color.WHITE);
        right.add(addButton);
        right.add(nextButton);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Calendar grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarGrid.setBackground(Color.WHITE);
        add(calendarGrid, BorderLayout.CENTER);

        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });
        addButton.addActionListener(e -> openAddEventDialog());

        updateCalendar();
    }

        private void updateCalendar() {
        calendarGrid.removeAll();
        YearMonth ym = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        LocalDate firstOfMonth = ym.atDay(1);
        int shift = firstOfMonth.getDayOfWeek().getValue() % 7;

        Locale loc = Locale.getDefault();
                    Locale indo = new Locale("id", "ID");
            DayOfWeek[] days = {
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            };

            for (DayOfWeek dow : days) {
                JLabel lbl = new JLabel(dow.getDisplayName(TextStyle.SHORT, indo), SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                calendarGrid.add(lbl);
            }


        for (int i = 0; i < shift; i++) {
            calendarGrid.add(new JPanel());
        }

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setMargin(new Insets(2, 2, 2, 2));
            dayBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            dayBtn.setBackground(Color.WHITE);
            dayBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            if (events.containsKey(date)) {
                dayBtn.setBackground(new Color(251, 200, 42));
                String tip = String.join("; ", events.get(date));
                dayBtn.setToolTipText("<html>" + tip.replaceAll("; ", "<br>") + "</html>");
            }

            dayBtn.addActionListener(e -> {
                List<String> eventList = events.getOrDefault(date, new ArrayList<>());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = date.format(formatter);

                if (eventList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Belum ada event", "Daftar Event", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Build event list text
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < eventList.size(); i++) {
                    sb.append(i + 1).append(". ").append(eventList.get(i)).append("\n");
                }

                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                area.setBackground(null);
                area.setBorder(null);
                area.setFont(new Font("SansSerif", Font.PLAIN, 13));

                JScrollPane scrollPane = new JScrollPane(area);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding dalam scroll

                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding luar panel
                panel.add(new JLabel("Event pada " + formattedDate), BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                // Custom buttons
                JButton hapusBtn = new JButton("Hapus");
                JButton editBtn = new JButton("Edit");
                JButton okeBtn = new JButton("Oke");

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                buttonPanel.add(hapusBtn);
                buttonPanel.add(editBtn);
                buttonPanel.add(Box.createHorizontalStrut(30)); // spasi besar sebelum OK
                buttonPanel.add(okeBtn);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Daftar Event",
                        Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setResizable(false);

                // Hapus action
                hapusBtn.addActionListener(ev -> {
                    String[] arr = eventList.toArray(new String[0]);
                    String sel = (String) JOptionPane.showInputDialog(
                            this,
                            "Pilih event yang ingin dihapus:",
                            "Hapus Event",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            arr,
                            arr[0]
                    );
                    if (sel != null) {
                        int conf = JOptionPane.showConfirmDialog(
                            this,
                            "Yakin ingin menghapus event: \"" + sel + "\"?" + System.lineSeparator() + "Konfirmasi Hapus",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (conf == JOptionPane.YES_OPTION) {
                            eventList.remove(sel);
                            if (eventList.isEmpty()) {
                                events.remove(date);
                            }
                            dialog.dispose();
                            updateCalendar();
                        }
                    }
                });

                // Edit action
                editBtn.addActionListener(ev -> {
                    String[] arr = eventList.toArray(new String[0]);
                    String sel = (String) JOptionPane.showInputDialog(
                            this,
                            "Pilih event yang ingin diedit:",
                            "Edit Event",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            arr,
                            arr[0]
                    );
                    if (sel != null) {
                        String newTitle = JOptionPane.showInputDialog(
                                this,
                                "Judul baru untuk event:",
                                sel);
                        if (newTitle != null && !newTitle.trim().isEmpty()) {
                            eventList.set(eventList.indexOf(sel), newTitle.trim());
                            dialog.dispose();
                            updateCalendar();
                        }
                    }
                });

                // Oke action
                okeBtn.addActionListener(ev -> dialog.dispose());

                dialog.setVisible(true);
            });

            calendarGrid.add(dayBtn);
        }

        int totalCells = calendarGrid.getComponentCount();
        int toFill = ((totalCells + 6) / 7) * 7 - totalCells;
        for (int i = 0; i < toFill; i++) {
            calendarGrid.add(new JPanel());
        }

        monthLabel.setText(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentDate.getYear());
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }


    private void openAddEventDialog() {
    JTextField titleField = new JTextField();

    int currentYear = LocalDate.now().getYear();
    Integer[] years = new Integer[10];
    for (int i = 0; i < 10; i++) years[i] = currentYear - 5 + i;
    JComboBox<Integer> yearCombo = new JComboBox<>(years);
    yearCombo.setSelectedItem(currentDate.getYear());

    String[] months = new String[12];
    for (int i = 0; i < 12; i++)
        months[i] = Month.of(i + 1).getDisplayName(TextStyle.FULL, Locale.getDefault());
    JComboBox<String> monthCombo = new JComboBox<>(months);
    monthCombo.setSelectedIndex(currentDate.getMonthValue() - 1);

    JComboBox<Integer> dayCombo = new JComboBox<>();
    updateDayCombo(dayCombo, currentDate.getYear(), currentDate.getMonthValue());
    dayCombo.setSelectedItem(currentDate.getDayOfMonth());

    // Listener untuk update hari berdasarkan bulan/tahun
    ActionListener updateDaysListener = e -> {
        int y = (Integer) yearCombo.getSelectedItem();
        int m = monthCombo.getSelectedIndex() + 1;
        int selDay = dayCombo.getItemCount() > 0 ? (Integer) dayCombo.getSelectedItem() : 1;
        updateDayCombo(dayCombo, y, m);
        int max = dayCombo.getItemCount();
        if (selDay > max) selDay = max;
        dayCombo.setSelectedItem(selDay);
    };
    yearCombo.addActionListener(updateDaysListener);
    monthCombo.addActionListener(updateDaysListener);

    // Panel form
    JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
    panel.add(new JLabel("Judul Event:"));
    panel.add(titleField);
    panel.add(new JLabel("Pilih Tanggal:"));

    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    datePanel.add(dayCombo);
    datePanel.add(monthCombo);
    datePanel.add(yearCombo);
    panel.add(datePanel);

    // Buat dialog manual
    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = optionPane.createDialog(this, "Tambah Event");

    // Fokus awal ke combo box tahun supaya kursor tidak langsung muncul di text field
    SwingUtilities.invokeLater(() -> yearCombo.requestFocusInWindow());

    dialog.setVisible(true);

    Object selected = optionPane.getValue();
    if (selected != null && selected.equals(JOptionPane.OK_OPTION)) {
        String title = titleField.getText().trim();
        int y = (Integer) yearCombo.getSelectedItem();
        int m = monthCombo.getSelectedIndex() + 1;
        int d = (Integer) dayCombo.getSelectedItem();
        try {
            LocalDate date = LocalDate.of(y, m, d);
            addEvent(date, title);
        } catch (DateTimeException ex) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void updateDayCombo(JComboBox<Integer> dayCombo, int year, int month) {
        dayCombo.removeAllItems();
        YearMonth ym = YearMonth.of(year, month);
        for (int d = 1; d <= ym.lengthOfMonth(); d++) dayCombo.addItem(d);
    }

    private void addEvent(LocalDate date, String title) {
        if (title.isEmpty()) return;
        events.computeIfAbsent(date, d -> new ArrayList<>()).add(title);
        updateCalendar();
    }
}