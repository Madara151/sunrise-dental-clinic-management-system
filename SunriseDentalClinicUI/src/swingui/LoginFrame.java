package swingui;

import client.HttpClientHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LoginFrame - the entry point of the Swing client.
 * Corresponds to :LoginUI in the "Staff Login" sequence diagram.
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Sunrise Dental Clinic - Staff Login");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new GridBagLayout());

        JPanel card = UITheme.createCardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 320));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Sunrise Dental Clinic", SwingConstants.CENTER);
        UITheme.styleTitle(titleLabel);
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Staff Login", SwingConstants.CENTER);
        UITheme.styleSubtitle(subtitleLabel);
        gbc.gridy = 1;
        card.add(subtitleLabel, gbc);

        JLabel userLabel = new JLabel("Username");
        UITheme.styleLabel(userLabel);
        gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(userLabel, gbc);

        usernameField = new JTextField(18);
        UITheme.styleField(usernameField);
        gbc.gridy = 3;
        card.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password");
        UITheme.styleLabel(passLabel);
        gbc.gridy = 4;
        card.add(passLabel, gbc);

        passwordField = new JPasswordField(18);
        UITheme.styleField(passwordField);
        gbc.gridy = 5;
        card.add(passwordField, gbc);

        JButton loginButton = new JButton("Log In");
        UITheme.stylePrimaryButton(loginButton);
        gbc.gridy = 6;
        gbc.insets = new Insets(18, 8, 8, 8);
        card.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_SUBTITLE);
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 8, 8, 8);
        card.add(statusLabel, gbc);

        add(card);

        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

            String jsonResponse = HttpClientHelper.post("/login", formData);
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            boolean success = json.get("success").getAsBoolean();

            if (success) {
                String staffName = json.has("staffName") ? json.get("staffName").getAsString() : username;
                new MainMenuFrame(staffName).setVisible(true);
                this.dispose();
            } else {
                String message = json.has("message") ? json.get("message").getAsString() : "Login failed.";
                statusLabel.setText(message);
            }

        } catch (IOException ex) {
            statusLabel.setText("Cannot reach server. Is Tomcat running?");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}