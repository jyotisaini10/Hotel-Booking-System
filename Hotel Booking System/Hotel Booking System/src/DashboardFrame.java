import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String currentView = "dashboard";

    private DashboardPanel dashboardPanel;
    private RoomListPanel roomListPanel;
    private CheckInPanel checkInPanel;
    private CheckOutPanel checkOutPanel;
    private RoomManagementPanel roomManagementPanel;
    private ReportsPanel reportsPanel;

    private static final String[] NAV_ITEMS = {
        "dashboard", "rooms", "checkin", "checkout", "manage", "reports"
    };
    private static final String[] NAV_LABELS = {
        "Dashboard", "Room List", "Check-In", "Check-Out", "Manage Rooms", "Reports"
    };

    public DashboardFrame() {
        setTitle("Hotel Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BG);

        // ===== SIDEBAR =====
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIHelper.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Brand in sidebar
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        brandPanel.setOpaque(false);
        brandPanel.setPreferredSize(new Dimension(230, 72));
        brandPanel.setMaximumSize(new Dimension(230, 72));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 0));

        JLabel brandIcon = new JLabel("H") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIHelper.PRIMARY);
                g2.fill(new RoundRectangle2D.Double(0, 0, 38, 38, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("H", 11, 27);
                g2.dispose();
            }
        };
        brandIcon.setPreferredSize(new Dimension(38, 38));

        JLabel brandLabel = new JLabel("Hotel Booking System");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        brandLabel.setForeground(Color.WHITE);

        brandPanel.add(brandIcon);
        brandPanel.add(brandLabel);
        sidebarPanel.add(brandPanel);

        // Divider
        JPanel divider = new JPanel();
        divider.setBackground(new Color(30, 41, 59));
        divider.setPreferredSize(new Dimension(230, 1));
        divider.setMaximumSize(new Dimension(230, 1));
        sidebarPanel.add(divider);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Nav section label
        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        navLabel.setForeground(new Color(100, 116, 139));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, 8, 0));
        navLabel.setMaximumSize(new Dimension(230, 24));
        sidebarPanel.add(navLabel);

        // Nav buttons
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            String key = NAV_ITEMS[i];
            String label = NAV_LABELS[i];
            JButton navBtn = UIHelper.createSidebarButton(label, key.equals("dashboard"));
            navBtn.setName(key);
            navBtn.addActionListener(e -> switchView(key));
            sidebarPanel.add(navBtn);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        sidebarPanel.add(Box.createVerticalGlue());

        // Logout at bottom
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setOpaque(false);
        logoutPanel.setMaximumSize(new Dimension(230, 60));
        JButton logoutBtn = UIHelper.createButton("Logout", UIHelper.DANGER, UIHelper.DANGER_HOVER, Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(190, 40));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        logoutPanel.add(logoutBtn);
        sidebarPanel.add(logoutPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // ===== CONTENT =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIHelper.BG);

        dashboardPanel = new DashboardPanel(this);
        roomListPanel = new RoomListPanel(this);
        checkInPanel = new CheckInPanel(this);
        checkOutPanel = new CheckOutPanel(this);
        roomManagementPanel = new RoomManagementPanel(this);
        reportsPanel = new ReportsPanel(this);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(roomListPanel, "rooms");
        contentPanel.add(checkInPanel, "checkin");
        contentPanel.add(checkOutPanel, "checkout");
        contentPanel.add(roomManagementPanel, "manage");
        contentPanel.add(reportsPanel, "reports");

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void switchView(String viewName) {
        currentView = viewName;
        cardLayout.show(contentPanel, viewName);
        refreshSidebar();
        refreshView(viewName);
    }

    private void refreshSidebar() {
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton && comp.getName() != null) {
                JButton btn = (JButton) comp;
                String key = btn.getName();
                boolean active = key.equals(currentView);
                // Replace button
                Container parent = btn.getParent();
                if (parent != null) {
                    int idx = -1;
                    for (int i = 0; i < parent.getComponentCount(); i++) {
                        if (parent.getComponent(i) == btn) { idx = i; break; }
                    }
                    if (idx >= 0) {
                        JButton newBtn = UIHelper.createSidebarButton(btn.getText(), active);
                        newBtn.setName(key);
                        newBtn.addActionListener(e -> switchView(key));
                        parent.remove(idx);
                        parent.add(newBtn, idx);
                    }
                }
            }
        }
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void refreshView(String view) {
        switch (view) {
            case "dashboard": dashboardPanel.refreshData(); break;
            case "rooms": roomListPanel.refreshData(); break;
            case "checkin": checkInPanel.refreshData(); break;
            case "checkout": checkOutPanel.refreshData(); break;
            case "manage": roomManagementPanel.refreshData(); break;
            case "reports": reportsPanel.refreshData(); break;
        }
    }

    public void refreshAll() {
        dashboardPanel.refreshData();
        roomListPanel.refreshData();
        checkInPanel.refreshData();
        checkOutPanel.refreshData();
        roomManagementPanel.refreshData();
        reportsPanel.refreshData();
    }
}
