import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckOutPanel extends JPanel {
    private DashboardFrame parent;
    private JTable occupiedTable;
    private DefaultTableModel tableModel;

    public CheckOutPanel(DashboardFrame parent) {
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

        JLabel title = UIHelper.createTitleLabel("Check-Out & Billing");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = UIHelper.createSubtitleLabel("Manage guest check-out and generate final bills");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeader.add(title);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHeader.add(subtitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        // ===== TABLE =====
        String[] columns = {"Booking ID", "Room No", "Type", "AC", "Price/Day", "Customer", "Persons", "Days", "Check-In", "Booked On"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        occupiedTable = new JTable(tableModel);
        UIHelper.styleTable(occupiedTable);
        occupiedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(occupiedTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));
        scrollPane.getViewport().setBackground(UIHelper.CARD_BG);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 20));
        btnPanel.setOpaque(false);

        JButton addServiceBtn = UIHelper.createPrimaryButton("Add Service");
        addServiceBtn.addActionListener(e -> addServiceCharge());

        JButton viewServicesBtn = UIHelper.createOutlineButton("View Services");
        viewServicesBtn.addActionListener(e -> viewServices());

        JButton checkOutBtn = UIHelper.createDangerButton("Check-Out & Bill");
        checkOutBtn.setPreferredSize(new Dimension(220, 48));
        checkOutBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        checkOutBtn.addActionListener(e -> performCheckOut());

        btnPanel.add(addServiceBtn);
        btnPanel.add(viewServicesBtn);
        btnPanel.add(checkOutBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void addServiceCharge() {
        int row = occupiedTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an occupied room first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(row, 0);

        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JComboBox<String> serviceCombo = UIHelper.createComboBox(new String[]{
            "Food & Dining", "Laundry", "Room Service", "Mini Bar", "Spa", "Parking", "Other"
        });
        JTextField chargeField = UIHelper.createTextField();

        panel.add(UIHelper.createLabel("Service Type:"));
        panel.add(serviceCombo);
        panel.add(UIHelper.createLabel("Charge (Rs.):"));
        panel.add(chargeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Service Charge",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double charge = Double.parseDouble(chargeField.getText().trim());
                if (charge <= 0) {
                    JOptionPane.showMessageDialog(this, "Charge must be positive!", "Invalid", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String serviceName = (String) serviceCombo.getSelectedItem();
                String serviceDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                DatabaseManager.addService(bookingId, serviceName, charge, serviceDate);
                JOptionPane.showMessageDialog(this,
                    String.format("Service added: %s - Rs. %.0f", serviceName, charge),
                    "Service Added", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid charge amount!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewServices() {
        int row = occupiedTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an occupied room first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(row, 0);
        String roomNo = (String) tableModel.getValueAt(row, 1);

        try {
            ResultSet rs = DatabaseManager.getServicesByBooking(bookingId);
            String[] cols = {"Service", "Date", "Charge (Rs.)"};
            DefaultTableModel sModel = new DefaultTableModel(cols, 0);
            double total = 0;
            while (rs.next()) {
                double charge = rs.getDouble("charge");
                total += charge;
                sModel.addRow(new Object[]{
                    rs.getString("service_name"),
                    rs.getString("service_date"),
                    String.format("%.0f", charge)
                });
            }
            rs.close();

            if (sModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No services added for Room " + roomNo + " yet.",
                    "Services", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JTable sTable = new JTable(sModel);
            UIHelper.styleTable(sTable);
            JScrollPane sp = new JScrollPane(sTable);
            sp.setPreferredSize(new Dimension(440, 250));

            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.add(sp, BorderLayout.CENTER);
            JLabel totalLabel = new JLabel(String.format("Total Extra Charges: Rs. %.0f", total));
            totalLabel.setFont(UIHelper.FONT_BOLD);
            totalLabel.setForeground(UIHelper.PRIMARY);
            panel.add(totalLabel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel,
                "Services for Room " + roomNo, JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performCheckOut() {
        int row = occupiedTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to check out.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(row, 0);
        String roomNo = (String) tableModel.getValueAt(row, 1);
        double pricePerDay = (double) tableModel.getValueAt(row, 4);
        String customer = (String) tableModel.getValueAt(row, 5);
        int numDays = (int) tableModel.getValueAt(row, 7);

        double roomCharges = numDays * pricePerDay;
        double extraCharges = 0;
        try {
            extraCharges = DatabaseManager.getTotalExtraCharges(bookingId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double totalAmount = roomCharges + extraCharges;

        // Build bill
        StringBuilder bill = new StringBuilder();
        bill.append("================================================\n");
        bill.append("                HOTEL - FINAL INVOICE           \n");
        bill.append("================================================\n\n");
        bill.append(String.format("  Customer     : %s\n", customer));
        bill.append(String.format("  Room         : %s\n", roomNo));
        bill.append(String.format("  Days Stayed  : %d\n", numDays));
        bill.append(String.format("  Rate/Day     : Rs. %.0f\n\n", pricePerDay));
        bill.append("------------------------------------------------\n");
        bill.append(String.format("  Room Charges     : Rs. %,10.0f\n", roomCharges));
        bill.append(String.format("  Service Charges  : Rs. %,10.0f\n", extraCharges));
        bill.append("------------------------------------------------\n");
        bill.append(String.format("  TOTAL PAYABLE    : Rs. %,10.0f\n", totalAmount));
        bill.append("================================================\n");
        bill.append(String.format("  Date : %s\n", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        bill.append("  Thank you for your stay!\n");

        JTextArea billArea = new JTextArea(bill.toString());
        billArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        billArea.setEditable(false);
        billArea.setBackground(new Color(255, 253, 245));
        billArea.setForeground(UIHelper.TEXT_PRIMARY);
        billArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JScrollPane billScroll = new JScrollPane(billArea);
        billScroll.setPreferredSize(new Dimension(480, 370));
        billScroll.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));

        int confirm = JOptionPane.showConfirmDialog(this, billScroll,
            "Check-Out - Final Invoice", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (confirm == JOptionPane.OK_OPTION) {
            try {
                String checkOutDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                DatabaseManager.checkOut(bookingId, checkOutDate, roomCharges, extraCharges, totalAmount);
                JOptionPane.showMessageDialog(this,
                    String.format("Check-Out Complete!\nRoom %s is now Available.\nTotal Billed: Rs. %.0f", roomNo, totalAmount),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.refreshAll();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Check-out failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getCheckedInBookings();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("ac_type"),
                    rs.getDouble("price_per_day"),
                    rs.getString("customer_name"),
                    rs.getInt("num_persons"),
                    rs.getInt("num_days"),
                    rs.getString("check_in_date"),
                    rs.getString("booking_date")
                });
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
