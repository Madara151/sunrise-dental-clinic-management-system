package dao;

import model.Dentist;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DentistDAO - Data Access Object for the Dentist entity.
 */
public class DentistDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    public boolean addDentist(Dentist dentist) {
        String sql = "INSERT INTO dentist (dentist_id, name, specialization) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dentist.getDentistId());
            ps.setString(2, dentist.getName());
            ps.setString(3, dentist.getSpecialization());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Dentist getDentistById(String dentistId) {
        String sql = "SELECT * FROM dentist WHERE dentist_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dentistId);
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

    public List<Dentist> getAllDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentist";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dentists.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }

    public boolean updateDentist(Dentist dentist) {
        String sql = "UPDATE dentist SET name = ?, specialization = ? WHERE dentist_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dentist.getName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getDentistId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDentist(String dentistId) {
        String sql = "DELETE FROM dentist WHERE dentist_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dentistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(rs.getString("dentist_id"));
        d.setName(rs.getString("name"));
        d.setSpecialization(rs.getString("specialization"));
        return d;
    }
}