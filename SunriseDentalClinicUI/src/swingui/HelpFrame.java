package swingui;

import javax.swing.*;
import java.awt.*;

/**
 * HelpFrame - "Help Section" use case.
 */
public class HelpFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String HELP_TEXT =
        "SUNRISE DENTAL CLINIC - STAFF USER GUIDE\n" +
        "=========================================\n\n" +
        "1. LOGGING IN\n" +
        "   - Enter your username and password on the login screen.\n" +
        "   - Only authorized staff accounts can access the system.\n\n" +
        "2. REGISTERING A NEW APPOINTMENT\n" +
        "   - Click 'Register New Appointment' from the main menu.\n" +
        "   - Enter the Patient ID, Dentist ID, and Treatment ID\n" +
        "     (ask a colleague if you don't know these codes).\n" +
        "   - Use the date and time pickers to select the\n" +
        "     appointment date and time - no need to type them.\n" +
        "   - Click 'Register Appointment'. A unique appointment\n" +
        "     number will be generated and displayed - write this\n" +
        "     down or give it to the patient.\n\n" +
        "3. FINDING AN APPOINTMENT\n" +
        "   - Click 'Display Appointment Details' from the main menu.\n" +
        "   - Type the appointment number and click 'Search'.\n" +
        "   - Full patient, dentist, and treatment details will appear.\n\n" +
        "4. GENERATING A BILL\n" +
        "   - Click 'Calculate and Print Bill' from the main menu.\n" +
        "   - Enter the appointment number and click 'Generate Bill'.\n" +
        "   - The total is calculated automatically from the\n" +
        "     treatment's consultation fee.\n" +
        "   - Click 'Print Receipt' to send it to a printer.\n\n" +
        "5. LOGGING OUT / EXITING\n" +
        "   - Use 'Logout' to return to the login screen without\n" +
        "     closing the application.\n" +
        "   - Use 'Exit System' to safely close the application.\n\n" +
        "If you encounter an error, confirm Tomcat is running and\n" +
        "that you are connected to the clinic network, then contact\n" +
        "your system administrator.";

    public HelpFrame() {
        setTitle("Help - Sunrise Dental Clinic System");
        setSize(520, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Help & User Guide");
        UITheme.styleTitle(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(HELP_TEXT);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UITheme.FONT_LABEL);
        textArea.setBackground(UITheme.CARD_BACKGROUND);
        textArea.setMargin(new Insets(18, 18, 18, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        add(scrollPane, BorderLayout.CENTER);
    }
}