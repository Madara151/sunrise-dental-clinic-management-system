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
 * BillingFrame - "Calculate and Print Bill" use case.
 */
public class BillingFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField numberField;
    private JTextArea receiptArea;

    public BillingFrame() {
        setTitle("Calculate and Print Bill");
        setSize(560, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Calculate and Print Bill");
        UITheme.styleTitle(titleLabel);
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = UITheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel numLabel = new JLabel("Appointment Number:");
        UITheme.styleLabel(numLabel);
        numberField = new JTextField(14);
        UITheme.styleField(numberField);

        JButton generateBtn = new JButton("Generate Bill");
        JButton printBtn = new JButton("Print Receipt");
        UITheme.stylePrimaryButton(generateBtn);
        UITheme.styleSecondaryButton(printBtn);

        topPanel.add(numLabel);
        topPanel.add(numberField);
        topPanel.add(generateBtn);
        topPanel.add(printBtn);

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(UITheme.FONT_MONO);
        receiptArea.setBackground(UITheme.CARD_BACKGROUND);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(UITheme.BACKGROUND);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        generateBtn.addActionListener(e -> generateBill());
        printBtn.addActionListener(e -> printReceipt());
    }

    private void generateBill() {
        String number = numberField.getText().trim();
        if (number.isEmpty()) {
            receiptArea.setText("Please enter an appointment number.");
            return;
        }

        try {
            String encoded = URLEncoder.encode(number, StandardCharsets.UTF_8);
            String formData = "appointmentNumber=" + encoded;
            String jsonResponse = HttpClientHelper.post("/bills", formData);
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            boolean success = json.get("success").getAsBoolean();

            if (success) {
                String receiptText = json.has("receiptText")
                        ? json.get("receiptText").getAsString()
                        : "Bill generated, but no receipt text was returned.";
                receiptArea.setText(receiptText);
            } else {
                receiptArea.setText(json.get("message").getAsString());
            }

        } catch (IOException ex) {
            receiptArea.setText("Cannot reach server. Is Tomcat running?");
            ex.printStackTrace();
        }
    }

    private void printReceipt() {
        if (receiptArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Generate a bill first.",
                    "Nothing to Print", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean printed = receiptArea.print();
            if (!printed) {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.",
                        "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}