import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hotel Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(30, 58, 138));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 5));
                g2.fillOval(-200, -200, 600, 600);
                g2.fillOval(getWidth() - 300, getHeight() - 300, 500, 500);
                g2.setColor(new Color(37, 99, 235, 15));
                g2.fillOval(getWidth() / 2 - 250, -100, 500, 500);
                g2.dispose();
            }
        };

        // Login Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Double(4, 6, getWidth() - 4, getHeight() - 4, 24, 24));
                // Card
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 6, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 540));
        card.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Brand icon
        JLabel iconLabel = new JLabel("H") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIHelper.PRIMARY);
                g2.fill(new RoundRectangle2D.Double(0, 0, 60, 60, 16, 16));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 30));
                FontMetrics fm = g2.getFontMetrics();
                int x = (60 - fm.stringWidth("H")) / 2;
                int y = (60 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("H", x, y);
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(60, 60));
        iconLabel.setMinimumSize(new Dimension(60, 60));
        iconLabel.setMaximumSize(new Dimension(60, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Hotel Booking System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(UIHelper.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(UIHelper.FONT_REGULAR);
        subtitleLabel.setForeground(UIHelper.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form
        JLabel userLabel = UIHelper.createLabel("Username");
        userLabel.setFont(UIHelper.FONT_BOLD);
        userLabel.setAlignmentX(Component. CENTER_ALIGNMENT);

        usernameField = UIHelper.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = UIHelper.createLabel("Password");
        passLabel.setFont(UIHelper.FONT_BOLD);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = UIHelper.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = UIHelper.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel hintLabel = new JLabel("Use admin / password to login");
        hintLabel.setFont(UIHelper.FONT_SMALL);
        hintLabel.setForeground(UIHelper.TEXT_MUTED);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 36)));
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(hintLabel);

        mainPanel.add(card);
        setContentPane(mainPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.equals("admin") && password.equals("password")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid credentials.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
