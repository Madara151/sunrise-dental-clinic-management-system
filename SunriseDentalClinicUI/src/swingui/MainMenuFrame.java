package swingui;

import client.HttpClientHelper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * MainMenuFrame - central navigation hub after a successful login.
 */
public class MainMenuFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainMenuFrame(String staffName) {
        setTitle("Sunrise Dental Clinic - Main Menu");
        setSize(460, 560);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new GridBagLayout());

        JPanel card = UITheme.createCardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(380, 480));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel clinicLabel = new JLabel("Sunrise Dental Clinic", SwingConstants.CENTER);
        UITheme.styleTitle(clinicLabel);
        gbc.gridy = 0;
        card.add(clinicLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome, " + staffName, SwingConstants.CENTER);
        UITheme.styleSubtitle(welcomeLabel);
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 8, 20, 8);
        card.add(welcomeLabel, gbc);

        JButton registerBtn = new JButton("Register New Appointment");
        JButton searchBtn = new JButton("Display Appointment Details");
        JButton billBtn = new JButton("Calculate and Print Bill");
        JButton helpBtn = new JButton("Help");
        JButton logoutBtn = new JButton("Logout");
        JButton exitBtn = new JButton("Exit System");

        UITheme.stylePrimaryButton(registerBtn);
        UITheme.stylePrimaryButton(searchBtn);
        UITheme.stylePrimaryButton(billBtn);
        UITheme.styleSecondaryButton(helpBtn);
        UITheme.styleSecondaryButton(logoutBtn);
        UITheme.styleSecondaryButton(exitBtn);
        exitBtn.setForeground(UITheme.DANGER);

        gbc.insets = new Insets(9, 8, 9, 8);
        int row = 2;
        for (JButton b : new JButton[]{registerBtn, searchBtn, billBtn, helpBtn, logoutBtn, exitBtn}) {
            gbc.gridy = row++;
            card.add(b, gbc);
        }

        add(card);

        registerBtn.addActionListener(e -> new RegisterAppointmentFrame().setVisible(true));
        searchBtn.addActionListener(e -> new SearchAppointmentFrame().setVisible(true));
        billBtn.addActionListener(e -> new BillingFrame().setVisible(true));
        helpBtn.addActionListener(e -> new HelpFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    HttpClientHelper.delete("/login");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the system?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    HttpClientHelper.delete("/login");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }
}