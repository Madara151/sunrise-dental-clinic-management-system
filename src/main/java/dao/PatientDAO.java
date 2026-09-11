package dao;

import model.Patient;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO - Data Access Object for the Patient entity.
 * Implements the DAO design pattern: isolates all JDBC/SQL logic
 * from business logic (servlets) and presentation (Swing UI).
 */
public class PatientDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    /**
     * Inserts a new patient record. Returns true if successful.
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patient (patient_id, name, address, contact_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getPatientId());
            ps.setString(2, patient.getName());
            ps.setString(3, patient.getAddress());
            ps.setString(4, patient.getContactNumber());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a patient by ID. Returns null if not found.
     */
    public Patient getPatientById(String patientId) {
        String sql = "SELECT * FROM patient WHERE patient_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
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

    /**
     * Returns all patients in the system.
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Updates an existing patient's details.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patient SET name = ?, address = ?, contact_number = ? WHERE patient_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setString(2, patient.getAddress());
            ps.setString(3, patient.getContactNumber());
            ps.setString(4, patient.getPatientId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a patient by ID.
     */
    public boolean deletePatient(String patientId) {
        String sql = "DELETE FROM patient WHERE patient_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates the next patient ID in sequence (e.g. P001, P002...).
     * Used by the registration workflow so staff don't type IDs manually.
     */
    public String generateNextPatientId() {
        String sql = "SELECT patient_id FROM patient ORDER BY patient_id DESC LIMIT 1";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("patient_id"); // e.g. "P002"
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("P%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "P001";
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getString("patient_id"));
        p.setName(rs.getString("name"));
        p.setAddress(rs.getString("address"));
        p.setContactNumber(rs.getString("contact_number"));
        return p;
    }
}