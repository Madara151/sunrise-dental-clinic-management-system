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
 * SearchAppointmentFrame - "Display Appointment Details" use case.
 */
public class SearchAppointmentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField numberField;
    private JTextArea resultArea;

    public SearchAppointmentFrame() {
        setTitle("Search Appointment");
        setSize(560, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(0, 15));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Display Appointment Details");
        UITheme.styleTitle(titleLabel);
        add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = UITheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel numLabel = new JLabel("Appointment Number:");
        UITheme.styleLabel(numLabel);
        numberField = new JTextField(15);
        UITheme.styleField(numberField);
        JButton searchBtn = new JButton("Search");
        UITheme.stylePrimaryButton(searchBtn);

        searchPanel.add(numLabel);
        searchPanel.add(numberField);
        searchPanel.add(searchBtn);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(UITheme.FONT_MONO);
        resultArea.setBackground(UITheme.CARD_BACKGROUND);
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(UITheme.BACKGROUND);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch());
        numberField.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String number = numberField.getText().trim();
        if (number.isEmpty()) {
            resultArea.setText("Please enter an appointment number.");
            return;
        }

        try {
            String encoded = URLEncoder.encode(number, StandardCharsets.UTF_8);
            String jsonResponse = HttpClientHelper.get("/appointments?number=" + encoded);
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (json.has("success") && !json.get("success").getAsBoolean()) {
                resultArea.setText(json.get("message").getAsString());
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Appointment No : ").append(getStr(json, "appointmentNumber")).append("\n");
            sb.append("Status         : ").append(getStr(json, "status")).append("\n");
            sb.append("Date           : ").append(getStr(json, "appointmentDate")).append("\n");
            sb.append("Time           : ").append(getStr(json, "appointmentTime")).append("\n\n");

            if (json.has("patient")) {
                JsonObject p = json.getAsJsonObject("patient");
                sb.append("-- Patient --\n");
                sb.append("Name    : ").append(getStr(p, "name")).append("\n");
                sb.append("Address : ").append(getStr(p, "address")).append("\n");
                sb.append("Contact : ").append(getStr(p, "contactNumber")).append("\n\n");
            }

            if (json.has("dentist")) {
                JsonObject d = json.getAsJsonObject("dentist");
                sb.append("-- Dentist --\n");
                sb.append("Name           : ").append(getStr(d, "name")).append("\n");
                sb.append("Specialization : ").append(getStr(d, "specialization")).append("\n\n");
            }

            if (json.has("treatment")) {
                JsonObject t = json.getAsJsonObject("treatment");
                sb.append("-- Treatment --\n");
                sb.append("Name             : ").append(getStr(t, "treatmentName")).append("\n");
                sb.append("Consultation Fee : Rs. ").append(getStr(t, "consultationFee")).append("\n");
            }

            resultArea.setText(sb.toString());

        } catch (IOException ex) {
            resultArea.setText("Cannot reach server. Is Tomcat running?");
            ex.printStackTrace();
        }
    }

    private String getStr(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "N/A";
    }
}