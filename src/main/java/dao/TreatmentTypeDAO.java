package dao;

import model.TreatmentType;
import util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TreatmentTypeDAO - Data Access Object for the TreatmentType lookup entity.
 */
public class TreatmentTypeDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    public boolean addTreatmentType(TreatmentType treatment) {
        String sql = "INSERT INTO treatment_type (treatment_id, treatment_name, consultation_fee) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, treatment.getTreatmentId());
            ps.setString(2, treatment.getTreatmentName());
            ps.setBigDecimal(3, treatment.getConsultationFee());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TreatmentType getTreatmentById(String treatmentId) {
        String sql = "SELECT * FROM treatment_type WHERE treatment_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, treatmentId);
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

    public List<TreatmentType> getAllTreatmentTypes() {
        List<TreatmentType> list = new ArrayList<>();
        String sql = "SELECT * FROM treatment_type";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateTreatmentType(TreatmentType treatment) {
        String sql = "UPDATE treatment_type SET treatment_name = ?, consultation_fee = ? WHERE treatment_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, treatment.getTreatmentName());
            ps.setBigDecimal(2, treatment.getConsultationFee());
            ps.setString(3, treatment.getTreatmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TreatmentType mapRow(ResultSet rs) throws SQLException {
        TreatmentType t = new TreatmentType();
        t.setTreatmentId(rs.getString("treatment_id"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        return t;
    }
}