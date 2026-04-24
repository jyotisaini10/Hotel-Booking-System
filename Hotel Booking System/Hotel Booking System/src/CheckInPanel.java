import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckInPanel extends JPanel {
    private DashboardFrame parent;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public CheckInPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel leftHeader = new JPanel();
        leftHeader.setOpaque(false);
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));

        JButton backBtn = UIHelper.createBackButton("Dashboard");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> parent.switchView("dashboard"));

        JLabel title = UIHelper.createTitleLabel("Check-In");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = UIHelper.createSubtitleLabel("Select a booked reservation and check in the guest");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeader.add(title);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHeader.add(subtitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JButton refreshBtn = UIHelper.createOutlineButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // ===== TABLE =====
        String[] columns = {"Booking ID", "Room No", "Type", "AC", "Customer", "ID Proof", "Persons", "Days", "Booking Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        bookingTable = new JTable(tableModel);
        UIHelper.styleTable(bookingTable);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));
        scrollPane.getViewport().setBackground(UIHelper.CARD_BG);

        // ===== BUTTON =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        btnPanel.setOpaque(false);

        JButton checkInBtn = UIHelper.createSuccessButton("Check-In Guest");
        checkInBtn.setPreferredSize(new Dimension(220, 48));
        checkInBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        checkInBtn.addActionListener(e -> performCheckIn());
        btnPanel.add(checkInBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void performCheckIn() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to check in.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(row, 0);
        String roomNo = (String) tableModel.getValueAt(row, 1);
        String customer = (String) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Check-In Guest?\n\nRoom: %s\nCustomer: %s\nDate: %s",
                roomNo, customer, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)),
            "Confirm Check-In", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String checkInDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                DatabaseManager.checkIn(bookingId, checkInDate);
                JOptionPane.showMessageDialog(this,
                    String.format("Check-In Successful!\nRoom %s is now Occupied.", roomNo),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.refreshAll();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Check-in failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getActiveBookings();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("ac_type"),
                    rs.getString("customer_name"),
                    rs.getString("id_proof"),
                    rs.getInt("num_persons"),
                    rs.getInt("num_days"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                });
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
