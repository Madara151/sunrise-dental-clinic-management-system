package dao;

import model.Appointment;
import model.Bill;
import util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BillDAO - Data Access Object for the Bill entity.
 */
public class BillDAO {

    private Connection getConnection() {
        return DatabaseConnector.getInstance().getConnection();
    }

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    /**
     * Persists a generated bill. Corresponds to the final stage of
     * "Calculate and Print Bill" once totalAmount has been computed.
     */
    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO bill (bill_id, appointment_number, total_amount, issue_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, bill.getBillId());
            ps.setString(2, bill.getAppointment().getAppointmentNumber());
            ps.setBigDecimal(3, bill.getTotalAmount());
            ps.setDate(4, Date.valueOf(bill.getIssueDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bill getBillById(String billId) {
        String sql = "SELECT * FROM bill WHERE bill_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, billId);
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
     * Retrieves the bill associated with a given appointment number, if one exists.
     */
    public Bill getBillByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT * FROM bill WHERE appointment_number = ?";
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

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public String generateNextBillId() {
        String sql = "SELECT bill_id FROM bill ORDER BY bill_id DESC LIMIT 1";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("bill_id"); // e.g. "BILL00001"
                int num = Integer.parseInt(lastId.substring(4)) + 1;
                return String.format("BILL%05d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "BILL00001";
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getString("bill_id"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));

        Date sqlDate = rs.getDate("issue_date");
        bill.setIssueDate(sqlDate != null ? sqlDate.toLocalDate() : null);

        // Re-hydrate the linked Appointment (with nested Patient/Dentist/Treatment)
        String appointmentNumber = rs.getString("appointment_number");
        Appointment appt = appointmentDAO.getAppointmentByNumber(appointmentNumber);
        bill.setAppointment(appt);

        return bill;
    }
}