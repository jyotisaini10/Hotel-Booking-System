import java.awt.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Use cross-platform for consistent look
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Global UI overrides
            UIManager.put("Panel.background", UIHelper.BG);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageFont", UIHelper.FONT_REGULAR);
            UIManager.put("OptionPane.buttonFont", UIHelper.FONT_BOLD);
            UIManager.put("Button.font", UIHelper.FONT_BOLD);
            UIManager.put("Label.font", UIHelper.FONT_REGULAR);
            UIManager.put("TextField.font", UIHelper.FONT_REGULAR);
            UIManager.put("ComboBox.font", UIHelper.FONT_REGULAR);
            UIManager.put("Table.font", UIHelper.FONT_REGULAR);
            UIManager.put("TableHeader.font", UIHelper.FONT_BOLD);
            UIManager.put("TabbedPane.font", UIHelper.FONT_REGULAR);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbDarkShadow", UIHelper.CARD_BORDER);
            UIManager.put("ScrollBar.thumb", new Color(200, 210, 220));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseManager.initialize();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
