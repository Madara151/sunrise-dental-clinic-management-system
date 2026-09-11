package dao;

import model.Staff;
import util.DatabaseConnector;

import java.sql.*;

/**
 * StaffDAO - Data Access Object for the Staff entity.
 * Handles credential lookup for the Login use case.
 */
public class StaffDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    /**
     * Verifies staff credentials against the database.
     * Corresponds to step 3-4 (verifyCredentials / executeQuery SELECT)
     * in the "Staff Login" sequence diagram.
     *
     * NOTE: For production use, passwords should be hashed (e.g. BCrypt)
     * rather than compared in plain text. This is documented as a
     * design assumption in the report; a plain-text comparison is used
     * here to keep the coursework scope manageable.
     */
    public Staff verifyCredentials(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // no match found
    }

    public Staff getStaffByUsername(String username) {
        String sql = "SELECT * FROM staff WHERE username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
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

    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (staff_id, username, password, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, staff.getStaffId());
            ps.setString(2, staff.getUsername());
            ps.setString(3, staff.getPassword());
            ps.setString(4, staff.getFullName());
            ps.setString(5, staff.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getString("staff_id"));
        s.setUsername(rs.getString("username"));
        s.setPassword(rs.getString("password"));
        s.setFullName(rs.getString("full_name"));
        s.setRole(rs.getString("role"));
        return s;
    }
}