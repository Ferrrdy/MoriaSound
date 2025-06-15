package UI;

import Controller.EventController;
import Model.Event;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final EventController eventController;
    private Map<LocalDate, List<EventController.EventSummary>> eventsCache;

    public EventManager() {
        this.eventController = new EventController();
        this.eventsCache = new HashMap<>();
    }

    public Map<LocalDate, List<EventController.EventSummary>> getEventsForMonth(YearMonth yearMonth) throws SQLException {
        Map<LocalDate, List<EventController.EventSummary>> events = eventController.getEventsForMonth(yearMonth);
        this.eventsCache = events; // Update cache
        return events;
    }

    public void loadEventsForMonth(YearMonth yearMonth, Runnable onFinishedCallback) {
        try {
            this.eventsCache = eventController.getEventsForMonth(yearMonth);
            if (onFinishedCallback != null) {
                onFinishedCallback.run();
            }
        } catch (SQLException e) {
            showErrorDialog(null, "Gagal memuat data event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<EventController.EventSummary> getEventsForDate(LocalDate date) {
        return eventsCache.getOrDefault(date, new ArrayList<>());
    }

    public void showAddEventDialog(Component parent, LocalDate defaultDate, Runnable updateCallback) {
        Frame frameParent = getFrameAncestor(parent);
        try {
            FormTambahEvent dialog = new FormTambahEvent(frameParent, eventController, this, updateCallback);
            if (defaultDate != null) {
                dialog.setTanggalDefault(defaultDate);
            }
            dialog.setVisible(true);
        } catch (Exception e) {
            showErrorDialog(parent, "Gagal membuka form tambah event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showEditEventDialog(Component parent, int eventId, Runnable updateCallback) {
        Frame frameParent = getFrameAncestor(parent);
        try {
            Event eventToEdit = eventController.getEventDetailsById(eventId);
            if (eventToEdit == null) {
                showErrorDialog(parent, "Gagal menemukan detail event.");
                return;
            }

            FormTambahEvent dialog = new FormTambahEvent(frameParent, eventController, this, eventToEdit, updateCallback);
            dialog.setVisible(true);
        } catch (SQLException e) {
            showErrorDialog(parent, "Gagal memuat data untuk diedit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showKonfirmasiKondisiDialog(Component parent, int eventId, Runnable updateCallback) {
    Frame frameParent = getFrameAncestor(parent);
    try {
        // [TAMBAHAN] Ambil objek Event secara lengkap berdasarkan ID-nya
        Event eventToProcess = eventController.getEventDetailsById(eventId);
        if (eventToProcess == null) {
            showErrorDialog(parent, "Gagal menemukan detail event untuk konfirmasi.");
            return;
        }

        // [UBAHAN] Panggil konstruktor baru dengan mengirim objek 'eventToProcess'
        FormKonfirmasiKondisi dialog = new FormKonfirmasiKondisi(frameParent, eventToProcess, eventController, updateCallback);
        dialog.setVisible(true);

    } catch (SQLException e) {
        // [TAMBAHAN] Tangani error SQL dari getEventDetailsById
        showErrorDialog(parent, "Gagal memuat data event: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        showErrorDialog(parent, "Gagal membuka form konfirmasi kondisi: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void deleteEvent(Component parent, int eventId, String eventName, Runnable refreshCallback) {
        int confirm = JOptionPane.showConfirmDialog(
            parent, 
            "Yakin ingin menghapus event '" + eventName + "'?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (eventController.deleteEvent(eventId)) {
                    JOptionPane.showMessageDialog(parent, "Event berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                } else {
                     showErrorDialog(parent, "Gagal menghapus event. Data mungkin tidak ditemukan.");
                }
            } catch (SQLException e) {
                showErrorDialog(parent, "Gagal menghapus event karena error database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private Frame getFrameAncestor(Component c) {
        if (c == null) return null;
        Window windowParent = SwingUtilities.getWindowAncestor(c);
        if (windowParent instanceof Frame) return (Frame) windowParent;
        return (Frame) SwingUtilities.getAncestorOfClass(Frame.class, c);
    }

    private void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public EventController getEventController() {
        return eventController;
    }
}