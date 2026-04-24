import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.table.*;

public class UIHelper {

    // ========== COLOR PALETTE ==========
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);
    public static final Color HEADER_BG = new Color(15, 23, 42);

    public static final Color BG = new Color(241, 245, 249);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(226, 232, 240);

    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);

    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color ACCENT_HOVER = new Color(217, 119, 6);

    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color SUCCESS_LIGHT = new Color(209, 250, 229);

    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color WARNING_HOVER = new Color(217, 119, 6);
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);

    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color TEXT_WHITE = Color.WHITE;

    public static final Color TABLE_HEADER_BG = new Color(248, 250, 252);
    public static final Color TABLE_HEADER_FG = new Color(71, 85, 105);
    public static final Color TABLE_ALT_ROW = new Color(248, 250, 252);
    public static final Color TABLE_HOVER = new Color(237, 242, 255);
    public static final Color TABLE_BORDER = new Color(226, 232, 240);
    public static final Color TABLE_SELECTED = new Color(219, 234, 254);

    public static final Color INPUT_BORDER = new Color(203, 213, 225);
    public static final Color INPUT_FOCUS = new Color(37, 99, 235);

    // ========== FONTS ==========
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_STAT_VALUE = new Font("SansSerif", Font.BOLD, 32);
    public static final Font FONT_SIDEBAR = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SIDEBAR_LABEL = new Font("SansSerif", Font.PLAIN, 13);

    // ========== CUSTOM BUTTON ==========
    public static JButton createButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hovering = false;
            private boolean pressing = false;
            {
                setContentAreaFilled(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovering = false; pressing = false; repaint(); }
                    public void mousePressed(MouseEvent e) { pressing = true; repaint(); }
                    public void mouseReleased(MouseEvent e) { pressing = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = pressing ? hoverBg.darker() : hovering ? hoverBg : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, PRIMARY_HOVER, TEXT_WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(71, 85, 105), new Color(51, 65, 85), TEXT_WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, DANGER_HOVER, TEXT_WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, SUCCESS_HOVER, TEXT_WHITE);
    }

    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING, WARNING_HOVER, TEXT_WHITE);
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovering = false;
            {
                setContentAreaFilled(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovering) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        return btn;
    }

    // ========== BACK BUTTON ==========
    public static JButton createBackButton(String text) {
        JButton btn = new JButton("<  " + text) {
            private boolean hovering = false;
            {
                setContentAreaFilled(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovering) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_SECONDARY);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return btn;
    }

    // ========== TEXT FIELD ==========
    public static JTextField createTextField() {
        JTextField field = new JTextField() {
            private boolean focused = false;
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focused ? INPUT_FOCUS : INPUT_BORDER);
                g2.setStroke(new BasicStroke(focused ? 2f : 1.2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                g2.dispose();
            }
        };
        field.setFont(FONT_REGULAR);
        field.setPreferredSize(new Dimension(280, 40));
        field.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        field.setBackground(Color.WHITE);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField() {
            private boolean focused = false;
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) { focused = true; repaint(); }
                    public void focusLost(FocusEvent e) { focused = false; repaint(); }
                });
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focused ? INPUT_FOCUS : INPUT_BORDER);
                g2.setStroke(new BasicStroke(focused ? 2f : 1.2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                g2.dispose();
            }
        };
        field.setFont(FONT_REGULAR);
        field.setPreferredSize(new Dimension(280, 40));
        field.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        field.setBackground(Color.WHITE);
        return field;
    }

    // ========== LABELS ==========
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    // ========== CARDS ==========
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fill(new RoundRectangle2D.Double(2, 3, getWidth() - 4, getHeight() - 4, 14, 14));
                // Card
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 2, getHeight() - 3, 14, 14));
                // Border
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 2, getHeight() - 3, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        return card;
    }

    // ========== STAT CARD ==========
    public static JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fill(new RoundRectangle2D.Double(2, 3, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 2, getHeight() - 3, 16, 16));
                // Accent bar top
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 2, 4, 4, 4));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        card.setPreferredSize(new Dimension(200, 110));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_SMALL);
        titleLbl.setForeground(TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_STAT_VALUE);
        valueLbl.setForeground(accentColor);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLbl);
        return card;
    }

    // ========== TABLE STYLING ==========
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(TABLE_BORDER);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(CARD_BG);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TABLE_BORDER));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(TABLE_HEADER_BG);
                setForeground(TABLE_HEADER_FG);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, TABLE_BORDER),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : TABLE_ALT_ROW);
                }
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14)
                ));

                if (value != null) {
                    String val = value.toString();
                    if (val.equals("Available")) {
                        c.setForeground(SUCCESS);
                        setFont(FONT_BOLD);
                    } else if (val.equals("Booked")) {
                        c.setForeground(WARNING);
                        setFont(FONT_BOLD);
                    } else if (val.equals("Occupied")) {
                        c.setForeground(DANGER);
                        setFont(FONT_BOLD);
                    } else if (val.equals("Active")) {
                        c.setForeground(PRIMARY);
                        setFont(FONT_BOLD);
                    } else if (val.equals("CheckedIn")) {
                        c.setForeground(new Color(124, 58, 237));
                        setFont(FONT_BOLD);
                    } else if (val.equals("CheckedOut")) {
                        c.setForeground(TEXT_MUTED);
                        setFont(FONT_BOLD);
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                        setFont(FONT_REGULAR);
                    }
                }
                return c;
            }
        });
    }

    // ========== COMBO BOX ==========
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_REGULAR);
        combo.setPreferredSize(new Dimension(280, 40));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return combo;
    }

    // ========== SIDEBAR BUTTON ==========
    public static JButton createSidebarButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            private boolean hovering = false;
            {
                setContentAreaFilled(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(SIDEBAR_ACTIVE);
                    g2.fill(new RoundRectangle2D.Double(8, 2, getWidth() - 16, getHeight() - 4, 8, 8));
                } else if (hovering) {
                    g2.setColor(SIDEBAR_HOVER);
                    g2.fill(new RoundRectangle2D.Double(8, 2, getWidth() - 16, getHeight() - 4, 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(active ? TEXT_WHITE : new Color(148, 163, 184));
        btn.setFont(FONT_SIDEBAR_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 28, 6, 16));
        return btn;
    }

    // ========== STATUS BADGE ==========
    public static JLabel createStatusBadge(String status) {
        Color bg, fg;
        switch (status) {
            case "Available": bg = SUCCESS_LIGHT; fg = new Color(5, 150, 105); break;
            case "Booked": bg = WARNING_LIGHT; fg = new Color(180, 83, 9); break;
            case "Occupied": bg = DANGER_LIGHT; fg = new Color(185, 28, 28); break;
            default: bg = new Color(241, 245, 249); fg = TEXT_SECONDARY; break;
        }
        JLabel badge = new JLabel(status) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setForeground(fg);
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setOpaque(false);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        badge.setPreferredSize(new Dimension(90, 26));
        return badge;
    }

    // ========== SEPARATOR ==========
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(226, 232, 240));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
