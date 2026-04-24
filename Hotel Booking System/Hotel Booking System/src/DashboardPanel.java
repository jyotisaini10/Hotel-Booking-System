import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DashboardPanel extends JPanel {
    private DashboardFrame parent;
    private JLabel totalRoomsVal, availableVal, bookedVal, occupiedVal, revenueVal;
    private DefaultTableModel recentTableModel;

    public DashboardPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UIHelper.BG);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // ===== HEADER =====
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = UIHelper.createTitleLabel("Dashboard");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel greetLabel = new JLabel(getGreeting() + ", Receptionist");
        greetLabel.setFont(UIHelper.FONT_REGULAR);
        greetLabel.setForeground(UIHelper.TEXT_SECONDARY);
        greetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBlock.add(greetLabel);
        headerRow.add(titleBlock, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setFont(UIHelper.FONT_REGULAR);
        dateLabel.setForeground(UIHelper.TEXT_MUTED);
        headerRow.add(dateLabel, BorderLayout.EAST);

        scrollContent.add(headerRow);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 28)));

        // ===== STATS CARDS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 18, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel totalCard = UIHelper.createStatCard("TOTAL ROOMS", "0", UIHelper.PRIMARY);
        JPanel availCard = UIHelper.createStatCard("AVAILABLE", "0", UIHelper.SUCCESS);
        JPanel bookedCard = UIHelper.createStatCard("BOOKED", "0", UIHelper.WARNING);
        JPanel occupiedCard = UIHelper.createStatCard("OCCUPIED", "0", UIHelper.DANGER);
        JPanel revenueCard = UIHelper.createStatCard("REVENUE (Rs.)", "0", new Color(124, 58, 237));

        totalRoomsVal = (JLabel) totalCard.getComponent(2);
        availableVal = (JLabel) availCard.getComponent(2);
        bookedVal = (JLabel) bookedCard.getComponent(2);
        occupiedVal = (JLabel) occupiedCard.getComponent(2);
        revenueVal = (JLabel) revenueCard.getComponent(2);

        statsPanel.add(totalCard);
        statsPanel.add(availCard);
        statsPanel.add(bookedCard);
        statsPanel.add(occupiedCard);
        statsPanel.add(revenueCard);

        scrollContent.add(statsPanel);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 28)));

        // ===== QUICK ACTIONS =====
        JPanel actionsCard = UIHelper.createCard();
        actionsCard.setLayout(new BorderLayout());
        actionsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        actionsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(UIHelper.FONT_HEADING);
        actionsTitle.setForeground(UIHelper.TEXT_PRIMARY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);

        JButton viewRoomsBtn = UIHelper.createPrimaryButton("View Rooms");
        JButton bookBtn = UIHelper.createSuccessButton("New Booking");
        JButton checkInBtn = UIHelper.createWarningButton("Check-In");
        JButton checkOutBtn = UIHelper.createDangerButton("Check-Out");
        JButton reportsBtn = UIHelper.createOutlineButton("Reports");

        viewRoomsBtn.addActionListener(e -> parent.switchView("rooms"));
        bookBtn.addActionListener(e -> parent.switchView("rooms"));
        checkInBtn.addActionListener(e -> parent.switchView("checkin"));
        checkOutBtn.addActionListener(e -> parent.switchView("checkout"));
        reportsBtn.addActionListener(e -> parent.switchView("reports"));

        btnRow.add(viewRoomsBtn);
        btnRow.add(bookBtn);
        btnRow.add(checkInBtn);
        btnRow.add(checkOutBtn);
        btnRow.add(reportsBtn);

        actionsCard.add(actionsTitle, BorderLayout.NORTH);
        actionsCard.add(Box.createRigidArea(new Dimension(0, 14)), BorderLayout.CENTER);
        actionsCard.add(btnRow, BorderLayout.SOUTH);

        scrollContent.add(actionsCard);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 28)));

        // ===== RECENT BOOKINGS TABLE =====
        JPanel recentCard = UIHelper.createCard();
        recentCard.setLayout(new BorderLayout(0, 14));
        recentCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recentTitle = new JLabel("Recent Bookings");
        recentTitle.setFont(UIHelper.FONT_HEADING);
        recentTitle.setForeground(UIHelper.TEXT_PRIMARY);

        String[] cols = {"Booking #", "Room", "Customer", "Persons", "Days", "Date", "Status"};
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable recentTable = new JTable(recentTableModel);
        UIHelper.styleTable(recentTable);

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.CARD_BORDER));
        scrollPane.getViewport().setBackground(UIHelper.CARD_BG);
        scrollPane.setPreferredSize(new Dimension(0, 260));

        recentCard.add(recentTitle, BorderLayout.NORTH);
        recentCard.add(scrollPane, BorderLayout.CENTER);

        scrollContent.add(recentCard);

        JScrollPane mainScroll = new JScrollPane(scrollContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);

        refreshData();
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    }

    public void refreshData() {
        totalRoomsVal.setText(String.valueOf(DatabaseManager.getTotalRoomCount()));
        availableVal.setText(String.valueOf(DatabaseManager.getRoomCount("Available")));
        bookedVal.setText(String.valueOf(DatabaseManager.getRoomCount("Booked")));
        occupiedVal.setText(String.valueOf(DatabaseManager.getRoomCount("Occupied")));

        double revenue = DatabaseManager.getTotalRevenue();
        if (revenue >= 100000) {
            revenueVal.setText(String.format("%.1fK", revenue / 1000));
        } else {
            revenueVal.setText(String.format("%.0f", revenue));
        }

        // Recent bookings
        recentTableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getAllBookings();
            int count = 0;
            while (rs.next() && count < 8) {
                count++;
                recentTableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("room_number"),
                    rs.getString("customer_name"),
                    rs.getInt("num_persons"),
                    rs.getInt("num_days"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
