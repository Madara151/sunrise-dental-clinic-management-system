package dao;

import model.Appointment;
import model.Dentist;
import model.Patient;
import model.TreatmentType;
import util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - Data Access Object for the Appointment entity.
 *
 * Joins across patient, dentist, and treatment_type tables so callers
 * receive a fully-populated Appointment object (with nested Patient,
 * Dentist, TreatmentType), matching the object model used in the
 * class diagram and sequence diagrams.
 */
public class AppointmentDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    private static final String JOIN_SELECT =
        "SELECT a.appointment_number, a.appointment_date, a.appointment_time, a.status, " +
        "       p.patient_id, p.name AS patient_name, p.address, p.contact_number, " +
        "       d.dentist_id, d.name AS dentist_name, d.specialization, " +
        "       t.treatment_id, t.treatment_name, t.consultation_fee " +
        "FROM appointment a " +
        "JOIN patient p ON a.patient_id = p.patient_id " +
        "JOIN dentist d ON a.dentist_id = d.dentist_id " +
        "JOIN treatment_type t ON a.treatment_id = t.treatment_id ";

    /**
     * Registers a new appointment. Corresponds to step 6 in the
     * "Register New Appointment" sequence diagram (saveAppointment).
     */
    public boolean addAppointment(Appointment appt) {
        String sql = "INSERT INTO appointment (appointment_number, patient_id, dentist_id, treatment_id, " +
                     "appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appt.getAppointmentNumber());
            ps.setString(2, appt.getPatient().getPatientId());
            ps.setString(3, appt.getDentist().getDentistId());
            ps.setString(4, appt.getTreatment().getTreatmentId());
            ps.setDate(5, Date.valueOf(appt.getAppointmentDate()));
            ps.setTime(6, Time.valueOf(appt.getAppointmentTime()));
            ps.setString(7, appt.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for an appointment by number, returning a fully-populated
     * Appointment object. Corresponds to "Display Appointment Details"
     * and step 3 of the Billing sequence diagram.
     */
    public Appointment getAppointmentByNumber(String appointmentNumber) {
        String sql = JOIN_SELECT + "WHERE a.appointment_number = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(JOIN_SELECT)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAppointmentStatus(String appointmentNumber, String status) {
        String sql = "UPDATE appointment SET status = ? WHERE appointment_number = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAppointment(String appointmentNumber) {
        String sql = "DELETE FROM appointment WHERE appointment_number = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates the next appointment number in sequence (e.g. APT00001, APT00002...).
     * Corresponds to step 5 (generateAppointmentNumber) in the Registration sequence diagram.
     */
    public String generateNextAppointmentNumber() {
        String sql = "SELECT appointment_number FROM appointment ORDER BY appointment_number DESC LIMIT 1";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastNum = rs.getString("appointment_number"); // e.g. "APT00002"
                int num = Integer.parseInt(lastNum.substring(3)) + 1;
                return String.format("APT%05d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "APT00001";
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getString("patient_id"),
                rs.getString("patient_name"),
                rs.getString("address"),
                rs.getString("contact_number")
        );

        Dentist dentist = new Dentist(
                rs.getString("dentist_id"),
                rs.getString("dentist_name"),
                rs.getString("specialization")
        );

        TreatmentType treatment = new TreatmentType(
                rs.getString("treatment_id"),
                rs.getString("treatment_name"),
                rs.getBigDecimal("consultation_fee")
        );

        Appointment appt = new Appointment();
        appt.setAppointmentNumber(rs.getString("appointment_number"));
        appt.setPatient(patient);
        appt.setDentist(dentist);
        appt.setTreatment(treatment);

        Date sqlDate = rs.getDate("appointment_date");
        appt.setAppointmentDate(sqlDate != null ? sqlDate.toLocalDate() : null);

        Time sqlTime = rs.getTime("appointment_time");
        appt.setAppointmentTime(sqlTime != null ? sqlTime.toLocalTime() : null);

        appt.setStatus(rs.getString("status"));
        return appt;
    }
}