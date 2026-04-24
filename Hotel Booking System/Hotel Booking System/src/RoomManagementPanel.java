import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class RoomManagementPanel extends JPanel {
    private DashboardFrame parent;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    public RoomManagementPanel(DashboardFrame parent) {
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

        JLabel title = UIHelper.createTitleLabel("Room Management");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = UIHelper.createSubtitleLabel("Add, edit, or remove rooms from the system");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeader.add(title);
        leftHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        leftHeader.add(subtitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton addBtn = UIHelper.createSuccessButton("+ Add Room");
        addBtn.addActionListener(e -> addRoom());

        JButton editBtn = UIHelper.createPrimaryButton("Edit Room");
        editBtn.addActionListener(e -> editRoom());

        JButton deleteBtn = UIHelper.createDangerButton("Delete Room");
        deleteBtn.addActionListener(e -> deleteRoom());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        headerPanel.add(actionPanel, BorderLayout.EAST);

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

        // ===== FOOTER =====
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JLabel info = new JLabel("Note: Only rooms with 'Available' status can be edited or deleted.");
        info.setFont(UIHelper.FONT_SMALL);
        info.setForeground(UIHelper.TEXT_MUTED);
        infoPanel.add(info);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void addRoom() {
        JPanel panel = createRoomFormPanel(null);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Room",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String roomNumber = getFieldValue(panel, 0);
                String roomType = getComboValue(panel, 1);
                String acType = getComboValue(panel, 2);
                double price = Double.parseDouble(getFieldValue(panel, 3));
                int maxOcc = Integer.parseInt(getFieldValue(panel, 4));

                if (roomNumber.isEmpty()) throw new Exception("Room number is required!");
                if (price <= 0) throw new Exception("Price must be positive!");
                if (maxOcc <= 0) throw new Exception("Max occupancy must be positive!");

                DatabaseManager.addRoom(roomNumber, roomType, acType, price, maxOcc);
                JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.refreshAll();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 6);

        if (!"Available".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only Available rooms can be edited!",
                "Cannot Edit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] currentValues = {
            tableModel.getValueAt(row, 1),
            tableModel.getValueAt(row, 2),
            tableModel.getValueAt(row, 3),
            String.valueOf(tableModel.getValueAt(row, 4)),
            String.valueOf(tableModel.getValueAt(row, 5))
        };

        JPanel panel = createRoomFormPanel(currentValues);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Room",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String roomNumber = getFieldValue(panel, 0);
                String roomType = getComboValue(panel, 1);
                String acType = getComboValue(panel, 2);
                double price = Double.parseDouble(getFieldValue(panel, 3));
                int maxOcc = Integer.parseInt(getFieldValue(panel, 4));

                if (roomNumber.isEmpty()) throw new Exception("Room number is required!");
                if (price <= 0) throw new Exception("Price must be positive!");
                if (maxOcc <= 0) throw new Exception("Max occupancy must be positive!");

                DatabaseManager.updateRoom(roomId, roomNumber, roomType, acType, price, maxOcc);
                JOptionPane.showMessageDialog(this, "Room updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.refreshAll();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNo = (String) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 6);

        if (!"Available".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete room " + roomNo + ". Only Available rooms can be deleted.",
                "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete Room " + roomNo + "? This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseManager.deleteRoom(roomId);
                JOptionPane.showMessageDialog(this, "Room deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.refreshAll();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createRoomFormPanel(Object[] values) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 14, 12));
        panel.setPreferredSize(new Dimension(450, 260));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTextField roomNoField = UIHelper.createTextField();
        JComboBox<String> typeCombo = UIHelper.createComboBox(new String[]{"Single", "Double", "Triple", "Suite", "Deluxe"});
        JComboBox<String> acCombo = UIHelper.createComboBox(new String[]{"AC", "Non-AC"});
        JTextField priceField = UIHelper.createTextField();
        JTextField occField = UIHelper.createTextField();

        if (values != null) {
            roomNoField.setText(String.valueOf(values[0]));
            typeCombo.setSelectedItem(String.valueOf(values[1]));
            acCombo.setSelectedItem(String.valueOf(values[2]));
            priceField.setText(String.valueOf(values[3]));
            occField.setText(String.valueOf(values[4]));
        }

        panel.add(UIHelper.createLabel("Room Number:"));
        panel.add(roomNoField);
        panel.add(UIHelper.createLabel("Room Type:"));
        panel.add(typeCombo);
        panel.add(UIHelper.createLabel("AC Type:"));
        panel.add(acCombo);
        panel.add(UIHelper.createLabel("Price per Day (Rs.):"));
        panel.add(priceField);
        panel.add(UIHelper.createLabel("Max Occupancy:"));
        panel.add(occField);

        return panel;
    }

    private String getFieldValue(JPanel panel, int fieldIndex) {
        Component comp = panel.getComponent(fieldIndex * 2 + 1);
        if (comp instanceof JTextField) return ((JTextField) comp).getText().trim();
        return "";
    }

    private String getComboValue(JPanel panel, int fieldIndex) {
        Component comp = panel.getComponent(fieldIndex * 2 + 1);
        if (comp instanceof JComboBox) return (String) ((JComboBox<?>) comp).getSelectedItem();
        return "";
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getAllRooms();
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
