import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {
    private DashboardFrame parent;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo;
    private JLabel reportInfoLabel;

    public ReportsPanel(DashboardFrame parent) {
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

        JLabel title = UIHelper.createTitleLabel("Reports");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = UIHelper.createSubtitleLabel("View hotel statistics and historical data");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeader.add(title);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHeader.add(subtitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(UIHelper.createLabel("Report:"));

        reportTypeCombo = UIHelper.createComboBox(new String[]{
            "Room Status Summary", "All Bookings", "Active Bookings", "Billing History",
            "Available Rooms", "Booked Rooms", "Occupied Rooms"
        });
        reportTypeCombo.setPreferredSize(new Dimension(200, 40));
        reportTypeCombo.addActionListener(e -> loadReport());
        filterPanel.add(reportTypeCombo);

        JButton refreshBtn = UIHelper.createOutlineButton("Refresh");
        refreshBtn.addActionListener(e -> loadReport());
        filterPanel.add(refreshBtn);

        headerPanel.add(filterPanel, BorderLayout.EAST);

        // ===== TABLE =====
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        reportTable = new JTable(tableModel);
        UIHelper.styleTable(reportTable);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));
        scrollPane.getViewport().setBackground(UIHelper.CARD_BG);

        // ===== FOOTER =====
        reportInfoLabel = new JLabel(" ");
        reportInfoLabel.setFont(UIHelper.FONT_BOLD);
        reportInfoLabel.setForeground(UIHelper.PRIMARY);
        reportInfoLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(reportInfoLabel, BorderLayout.SOUTH);

        loadReport();
    }

    private void loadReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        try {
            switch (reportType) {
                case "Room Status Summary": loadRoomStatusSummary(); break;
                case "All Bookings": loadAllBookings(); break;
                case "Active Bookings": loadActiveBookings(); break;
                case "Billing History": loadBillingHistory(); break;
                case "Available Rooms": loadRoomsByStatus("Available"); break;
                case "Booked Rooms": loadRoomsByStatus("Booked"); break;
                case "Occupied Rooms": loadRoomsByStatus("Occupied"); break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading report: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRoomStatusSummary() throws SQLException {
        String[] cols = {"Status", "Count", "Percentage"};
        for (String col : cols) tableModel.addColumn(col);

        int total = DatabaseManager.getTotalRoomCount();
        int available = DatabaseManager.getRoomCount("Available");
        int booked = DatabaseManager.getRoomCount("Booked");
        int occupied = DatabaseManager.getRoomCount("Occupied");

        if (total > 0) {
            tableModel.addRow(new Object[]{"Available", available, String.format("%.1f%%", (available * 100.0 / total))});
            tableModel.addRow(new Object[]{"Booked", booked, String.format("%.1f%%", (booked * 100.0 / total))});
            tableModel.addRow(new Object[]{"Occupied", occupied, String.format("%.1f%%", (occupied * 100.0 / total))});
            tableModel.addRow(new Object[]{"Total", total, "100.0%"});
        }

        double revenue = DatabaseManager.getTotalRevenue();
        reportInfoLabel.setText(String.format("Total Rooms: %d  |  Total Revenue: Rs. %,.0f", total, revenue));
    }

    private void loadAllBookings() throws SQLException {
        String[] cols = {"Booking ID", "Room No", "Type", "Customer", "Persons", "Days", "Booking Date", "Check-In", "Check-Out", "Status"};
        for (String col : cols) tableModel.addColumn(col);

        ResultSet rs = DatabaseManager.getAllBookings();
        int count = 0;
        while (rs.next()) {
            count++;
            tableModel.addRow(new Object[]{
                rs.getInt("booking_id"),
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getString("customer_name"),
                rs.getInt("num_persons"),
                rs.getInt("num_days"),
                rs.getString("booking_date"),
                rs.getString("check_in_date") != null ? rs.getString("check_in_date") : "--",
                rs.getString("check_out_date") != null ? rs.getString("check_out_date") : "--",
                rs.getString("status")
            });
        }
        rs.close();
        reportInfoLabel.setText("Total Bookings: " + count);
    }

    private void loadActiveBookings() throws SQLException {
        String[] cols = {"Booking ID", "Room No", "Type", "AC", "Customer", "ID Proof", "Persons", "Days", "Booking Date"};
        for (String col : cols) tableModel.addColumn(col);

        ResultSet rs = DatabaseManager.getActiveBookings();
        int count = 0;
        while (rs.next()) {
            count++;
            tableModel.addRow(new Object[]{
                rs.getInt("booking_id"),
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getString("ac_type"),
                rs.getString("customer_name"),
                rs.getString("id_proof"),
                rs.getInt("num_persons"),
                rs.getInt("num_days"),
                rs.getString("booking_date")
            });
        }
        rs.close();
        reportInfoLabel.setText("Active Bookings (Pending Check-In): " + count);
    }

    private void loadBillingHistory() throws SQLException {
        String[] cols = {"Bill ID", "Room No", "Customer", "Room Charges", "Extra Charges", "Total Amount", "Payment Date"};
        for (String col : cols) tableModel.addColumn(col);

        ResultSet rs = DatabaseManager.getAllBillingRecords();
        int count = 0;
        double totalRevenue = 0;
        while (rs.next()) {
            count++;
            double total = rs.getDouble("total_amount");
            totalRevenue += total;
            tableModel.addRow(new Object[]{
                rs.getInt("bill_id"),
                rs.getString("room_number"),
                rs.getString("customer_name"),
                String.format("Rs. %,.0f", rs.getDouble("room_charges")),
                String.format("Rs. %,.0f", rs.getDouble("extra_charges")),
                String.format("Rs. %,.0f", total),
                rs.getString("payment_date")
            });
        }
        rs.close();
        reportInfoLabel.setText(String.format("Total Bills: %d  |  Total Revenue: Rs. %,.0f", count, totalRevenue));
    }

    private void loadRoomsByStatus(String status) throws SQLException {
        String[] cols = {"Room No", "Type", "AC/Non-AC", "Price/Day (Rs.)", "Max Occupancy"};
        for (String col : cols) tableModel.addColumn(col);

        ResultSet rs;
        switch (status) {
            case "Available": rs = DatabaseManager.getAvailableRooms(); break;
            case "Booked": rs = DatabaseManager.getBookedRooms(); break;
            case "Occupied": rs = DatabaseManager.getOccupiedRooms(); break;
            default: return;
        }

        int count = 0;
        while (rs.next()) {
            count++;
            tableModel.addRow(new Object[]{
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getString("ac_type"),
                String.format("Rs. %,.0f", rs.getDouble("price_per_day")),
                rs.getInt("max_occupancy")
            });
        }
        rs.close();
        reportInfoLabel.setText(status + " Rooms: " + count);
    }

    public void refreshData() {
        loadReport();
    }
}
