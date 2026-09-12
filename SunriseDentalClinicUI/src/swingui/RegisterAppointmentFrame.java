package swingui;

import client.HttpClientHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * RegisterAppointmentFrame - corresponds to :RegistrationUI in the
 * "Register New Appointment" sequence diagram.
 *
 * Date and time are now selected via JSpinner (calendar/clock-style
 * controls) instead of free-text entry, removing an entire class of
 * "invalid format" user errors at the source - a genuine usability
 * improvement worth citing under "user friendly interfaces" in the brief.
 */
public class RegisterAppointmentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField patientIdField, dentistIdField, treatmentIdField;
    private JSpinner dateSpinner, timeSpinner;
    private JLabel statusLabel;

    public RegisterAppointmentFrame() {
        setTitle("Register New Appointment");
        setSize(500, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new GridBagLayout());

        JPanel card = UITheme.createCardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 480));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Register New Appointment", SwingConstants.CENTER);
        UITheme.styleTitle(titleLabel);
        gbc.gridy = 0;
        card.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        patientIdField = new JTextField(15);
        row = addFieldRow(card, gbc, row, "Patient ID (e.g. P001)", patientIdField);

        dentistIdField = new JTextField(15);
        row = addFieldRow(card, gbc, row, "Dentist ID (e.g. D001)", dentistIdField);

        treatmentIdField = new JTextField(15);
        row = addFieldRow(card, gbc, row, "Treatment ID (e.g. T001)", treatmentIdField);

        // ---- Date spinner (calendar-style, no manual typing needed) ----
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // default to today
        UITheme.styleField(dateEditor.getTextField());
        row = addFieldRow(card, gbc, row, "Appointment Date", dateSpinner);

        // ---- Time spinner (clock-style, 24hr) ----
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        Calendar defaultTime = Calendar.getInstance();
        defaultTime.set(Calendar.HOUR_OF_DAY, 9);
        defaultTime.set(Calendar.MINUTE, 0);
        timeSpinner.setValue(defaultTime.getTime());
        UITheme.styleField(timeEditor.getTextField());
        row = addFieldRow(card, gbc, row, "Appointment Time (24hr)", timeSpinner);

        JButton submitBtn = new JButton("Register Appointment");
        UITheme.stylePrimaryButton(submitBtn);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 8, 8);
        card.add(submitBtn, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_SUBTITLE);
        gbc.gridy = row;
        gbc.insets = new Insets(4, 8, 8, 8);
        card.add(statusLabel, gbc);

        add(card);

        submitBtn.addActionListener(e -> submitAppointment());
    }

    /**
     * Adds a label + input row to the form and returns the next free row index.
     */
    private int addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        UITheme.styleLabel(label);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 2, 8);
        panel.add(label, gbc);

        if (field instanceof JTextField) {
            UITheme.styleField(field);
        }
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 6, 8);
        panel.add(field, gbc);

        return row + 2;
    }

    private void submitAppointment() {
        String patientId = patientIdField.getText().trim();
        String dentistId = dentistIdField.getText().trim();
        String treatmentId = treatmentIdField.getText().trim();

        if (patientId.isEmpty() || dentistId.isEmpty() || treatmentId.isEmpty()) {
            statusLabel.setText("Patient ID, Dentist ID, and Treatment ID are required.");
            return;
        }

        // Convert the spinner's java.util.Date into the ISO strings the servlet expects
        Date selectedDate = (Date) dateSpinner.getValue();
        Date selectedTime = (Date) timeSpinner.getValue();

        LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0).withNano(0);

        String dateStr = localDate.toString();               // yyyy-MM-dd
        String timeStr = localTime.toString().substring(0, 5); // HH:mm

        try {
            String formData = "patientId=" + URLEncoder.encode(patientId, StandardCharsets.UTF_8)
                    + "&dentistId=" + URLEncoder.encode(dentistId, StandardCharsets.UTF_8)
                    + "&treatmentId=" + URLEncoder.encode(treatmentId, StandardCharsets.UTF_8)
                    + "&date=" + URLEncoder.encode(dateStr, StandardCharsets.UTF_8)
                    + "&time=" + URLEncoder.encode(timeStr, StandardCharsets.UTF_8);

            String jsonResponse = HttpClientHelper.post("/appointments", formData);
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            boolean success = json.get("success").getAsBoolean();
            String message = json.has("message") ? json.get("message").getAsString() : "";

            if (success) {
                String appointmentNumber = json.get("appointmentNumber").getAsString();
                JOptionPane.showMessageDialog(this,
                        "Appointment registered successfully!\nAppointment Number: " + appointmentNumber,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                statusLabel.setText(message);
            }

        } catch (IOException ex) {
            statusLabel.setText("Cannot reach server. Is Tomcat running?");
            ex.printStackTrace();
        }
    }
}