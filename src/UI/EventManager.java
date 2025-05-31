package UI;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;


public class EventManager {
    private final Map<LocalDate, List<String>> events = new HashMap<>();

    public Map<LocalDate, List<String>> getEvents() {
        return events;
    }

    public void showAddEventDialog(Component parent, LocalDate currentDate, Runnable updateCallback) {
        JTextField titleField = new JTextField();
        JComboBox<Integer> yearCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>();
        JComboBox<Integer> dayCombo = new JComboBox<>();

        int currentYear = currentDate.getYear();
        for (int i = 0; i < 10; i++) yearCombo.addItem(currentYear - 5 + i);
        yearCombo.setSelectedItem(currentDate.getYear());

        for (int i = 1; i <= 12; i++)
            monthCombo.addItem(java.time.Month.of(i).getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()));
        monthCombo.setSelectedIndex(currentDate.getMonthValue() - 1);

        updateDayCombo(dayCombo, currentDate.getYear(), currentDate.getMonthValue());
        dayCombo.setSelectedItem(currentDate.getDayOfMonth());

        yearCombo.addActionListener(e -> updateDayCombo(dayCombo, (Integer) yearCombo.getSelectedItem(), monthCombo.getSelectedIndex() + 1));
        monthCombo.addActionListener(e -> updateDayCombo(dayCombo, (Integer) yearCombo.getSelectedItem(), monthCombo.getSelectedIndex() + 1));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.add(new JLabel("Judul Event:"));
        form.add(titleField);
        JPanel datePanel = new JPanel();
        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);
        form.add(new JLabel("Tanggal:"));
        form.add(datePanel);

        int result = JOptionPane.showConfirmDialog(parent, form, "Tambah Event", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (!title.isEmpty()) {
                LocalDate date = LocalDate.of((Integer) yearCombo.getSelectedItem(), monthCombo.getSelectedIndex() + 1, (Integer) dayCombo.getSelectedItem());
                events.computeIfAbsent(date, d -> new ArrayList<>()).add(title);
                updateCallback.run(); // refresh calendar
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
    }

    public void editEvent(LocalDate date, String oldTitle, String newTitle) {
        List<String> list = events.get(date);
        if (list != null && list.contains(oldTitle)) {
            list.set(list.indexOf(oldTitle), newTitle);
        }
    }

    private void updateDayCombo(JComboBox<Integer> dayCombo, int year, int month) {
        dayCombo.removeAllItems();
        int max = YearMonth.of(year, month).lengthOfMonth();
        for (int i = 1; i <= max; i++) dayCombo.addItem(i);
    }
}
