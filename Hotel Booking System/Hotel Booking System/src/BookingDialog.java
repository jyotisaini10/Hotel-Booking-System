import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingDialog extends JDialog {
    private boolean bookingMade = false;
    private int roomId;
    private double pricePerDay;
    private int maxOccupancy;

    private JTextField nameField, idProofField, addressField, personsField, daysField;
    private JLabel estimateValueLabel;

    public BookingDialog(JFrame owner, int roomId, String roomNumber, String roomType,
                         String acType, double pricePerDay, int maxOccupancy) {
        super(owner, "Book Room " + roomNumber, true);
        this.roomId = roomId;
        this.pricePerDay = pricePerDay;
        this.maxOccupancy = maxOccupancy;

        // Full screen dialog
        setSize(owner.getSize());
        setLocationRelativeTo(owner);
        setUndecorated(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(241, 245, 249));

        // Center card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(4, 6, getWidth() - 4, getHeight() - 4, 20, 20));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 6, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(580, 660));
        card.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIHelper.SIDEBAR_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 20, 20, 20));
                g2.fillRect(0, 20, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 20, 32));
        header.setPreferredSize(new Dimension(580, 90));

        JLabel headerTitle = new JLabel("New Booking");
        headerTitle.setFont(UIHelper.FONT_SUBTITLE);
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roomInfo = new JLabel(String.format("Room %s  |  %s  |  %s  |  Rs. %.0f/day  |  Max: %d persons",
            roomNumber, roomType, acType, pricePerDay, maxOccupancy));
        roomInfo.setFont(UIHelper.FONT_SMALL);
        roomInfo.setForeground(new Color(148, 163, 184));
        roomInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(headerTitle);
        header.add(Box.createRigidArea(new Dimension(0, 6)));
        header.add(roomInfo);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 10, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = UIHelper.createTextField();
        idProofField = UIHelper.createTextField();
        addressField = UIHelper.createTextField();
        personsField = UIHelper.createTextField();
        daysField = UIHelper.createTextField();

        addFormRow(formPanel, gbc, 0, "Customer Name", nameField);
        addFormRow(formPanel, gbc, 1, "ID Proof Number", idProofField);
        addFormRow(formPanel, gbc, 2, "Address", addressField);
        addFormRow(formPanel, gbc, 3, "Number of Persons", personsField);
        addFormRow(formPanel, gbc, 4, "Number of Days", daysField);

        // Live estimate
        JPanel estimatePanel = new JPanel(new BorderLayout());
        estimatePanel.setOpaque(false);
        estimatePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel estCard = new JPanel(new BorderLayout());
        estCard.setBackground(new Color(239, 246, 255));
        estCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel estLabel = new JLabel("Estimated Cost:");
        estLabel.setFont(UIHelper.FONT_BOLD);
        estLabel.setForeground(UIHelper.PRIMARY);

        estimateValueLabel = new JLabel("Enter days to calculate");
        estimateValueLabel.setFont(UIHelper.FONT_BOLD);
        estimateValueLabel.setForeground(UIHelper.PRIMARY);

        estCard.add(estLabel, BorderLayout.WEST);
        estCard.add(estimateValueLabel, BorderLayout.EAST);
        estimatePanel.add(estCard, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = formRow; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 6, 0);
        formPanel.add(estimatePanel, gbc);

        // Live cost calculator
        daysField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateEstimate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateEstimate(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateEstimate(); }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 32, 24, 32));

        JButton cancelBtn = UIHelper.createOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton bookBtn = UIHelper.createSuccessButton("Confirm Booking");
        bookBtn.setPreferredSize(new Dimension(190, 44));
        bookBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        bookBtn.addActionListener(e -> performBooking());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(bookBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(card);
        setContentPane(mainPanel);
    }

    private int formRow = 0;

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int idx, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = formRow; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(idx == 0 ? 0 : 4, 0, 2, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIHelper.FONT_BOLD);
        lbl.setForeground(UIHelper.TEXT_PRIMARY);
        panel.add(lbl, gbc);
        formRow++;

        gbc.gridx = 0; gbc.gridy = formRow; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.add(field, gbc);
        formRow++;
    }

    private void updateEstimate() {
        try {
            int days = Integer.parseInt(daysField.getText().trim());
            if (days > 0) {
                double est = days * pricePerDay;
                estimateValueLabel.setText(String.format("Rs. %.0f", est));
            } else {
                estimateValueLabel.setText("--");
            }
        } catch (NumberFormatException e) {
            estimateValueLabel.setText("Enter valid days");
        }
    }

    private void performBooking() {
        String name = nameField.getText().trim();
        String idProof = idProofField.getText().trim();
        String address = addressField.getText().trim();
        String personsStr = personsField.getText().trim();
        String daysStr = daysField.getText().trim();

        if (name.isEmpty() || idProof.isEmpty() || address.isEmpty() || personsStr.isEmpty() || daysStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numPersons, numDays;
        try {
            numPersons = Integer.parseInt(personsStr);
            numDays = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Persons and days must be valid numbers!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (numPersons <= 0 || numDays <= 0) {
            JOptionPane.showMessageDialog(this, "Persons and days must be positive!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (numPersons > maxOccupancy) {
            JOptionPane.showMessageDialog(this,
                String.format("Persons (%d) exceeds room capacity (%d)!\nPlease choose a larger room.", numPersons, maxOccupancy),
                "Occupancy Exceeded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double estimatedCost = numDays * pricePerDay;
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirm Booking?\n\nCustomer: %s\nPersons: %d\nDays: %d\nEstimated Cost: Rs. %.0f",
                name, numPersons, numDays, estimatedCost),
            "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String bookingDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                int bookingId = DatabaseManager.createBooking(roomId, name, idProof, address, numPersons, numDays, bookingDate);
                bookingMade = true;
                JOptionPane.showMessageDialog(this,
                    String.format("Booking Successful!\nBooking ID: %d", bookingId),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isBookingMade() {
        return bookingMade;
    }
}
