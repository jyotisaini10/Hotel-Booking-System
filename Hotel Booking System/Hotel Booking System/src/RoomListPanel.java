import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class RoomListPanel extends JPanel {
    private DashboardFrame parent;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public RoomListPanel(DashboardFrame parent) {
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

        JLabel title = UIHelper.createTitleLabel("Room List");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = UIHelper.createSubtitleLabel("View all rooms and book available ones");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeader.add(title);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHeader.add(subtitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeader.setOpaque(false);

        rightHeader.add(UIHelper.createLabel("Filter:"));
        filterCombo = UIHelper.createComboBox(new String[]{"All Rooms", "Available", "Booked", "Occupied"});
        filterCombo.setPreferredSize(new Dimension(150, 40));
        filterCombo.addActionListener(e -> refreshData());
        rightHeader.add(filterCombo);

        JButton bookBtn = UIHelper.createPrimaryButton("Book Room");
        bookBtn.addActionListener(e -> openBookingForSelected());
        rightHeader.add(bookBtn);

        headerPanel.add(rightHeader, BorderLayout.EAST);

        // ===== TABLE =====
        String[] columns = {"ID", "Room No", "Type", "AC/Non-AC", "Price/Day (Rs.)", "Max Occupancy", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        roomTable = new JTable(tableModel);
        UIHelper.styleTable(roomTable);
        roomTable.getColumnModel().getColumn(0).setMinWidth(0);
        roomTable.getColumnModel().getColumn(0).setMaxWidth(0);
        roomTable.getColumnModel().getColumn(0).setWidth(0);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));
        scrollPane.getViewport().setBackground(UIHelper.CARD_BG);

        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openBookingForSelected();
            }
        });

        // ===== FOOTER =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JLabel hint = new JLabel("Tip: Double-click an Available room to book it quickly.");
        hint.setFont(UIHelper.FONT_SMALL);
        hint.setForeground(UIHelper.TEXT_MUTED);
        footer.add(hint);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        refreshData();
    }

    private void openBookingForSelected() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(row, 6);
        if (!"Available".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Room is currently " + status + ". Only Available rooms can be booked.",
                "Cannot Book", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        String roomType = (String) tableModel.getValueAt(row, 2);
        String acType = (String) tableModel.getValueAt(row, 3);
        double price = (double) tableModel.getValueAt(row, 4);
        int maxOccupancy = (int) tableModel.getValueAt(row, 5);

        BookingDialog dialog = new BookingDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            roomId, roomNumber, roomType, acType, price, maxOccupancy
        );
        dialog.setVisible(true);

        if (dialog.isBookingMade()) {
            parent.refreshAll();
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All Rooms";

        try {
            ResultSet rs;
            switch (filter) {
                case "Available": rs = DatabaseManager.getAvailableRooms(); break;
                case "Booked": rs = DatabaseManager.getBookedRooms(); break;
                case "Occupied": rs = DatabaseManager.getOccupiedRooms(); break;
                default: rs = DatabaseManager.getAllRooms(); break;
            }
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("ac_type"),
                    rs.getDouble("price_per_day"),
                    rs.getInt("max_occupancy"),
                    rs.getString("status")
                });
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
