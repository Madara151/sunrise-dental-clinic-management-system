package factory;

import dao.AppointmentDAO;
import dao.BillDAO;
import model.Appointment;
import model.Bill;

import java.time.LocalDate;

/**
 * BillFactory - Factory design pattern.
 *
 * Centralizes the construction of a Bill object for a given appointment:
 * - Looks up the Appointment (with nested Patient/Dentist/TreatmentType)
 * - Generates a unique bill ID
 * - Calculates the total amount based on the treatment's consultation fee
 *
 * Why Factory here:
 * - Bill creation always follows the same sequence of steps (lookup ->
 *   calculate -> assemble), which matches the "Calculate and Print Bill"
 *   sequence diagram exactly. Keeping this in one factory method avoids
 *   duplicating that logic in every servlet/UI class that needs a bill.
 */
public class BillFactory {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();

    /**
     * Builds a ready-to-persist Bill for the given appointment number.
     *
     * @param appointmentNumber the appointment to bill
     * @return a fully-populated Bill with totalAmount already calculated,
     *         or null if the appointment does not exist
     */
    public Bill createBill(String appointmentNumber) {
        Appointment appointment = appointmentDAO.getAppointmentByNumber(appointmentNumber);

        if (appointment == null) {
            return null;
        }

        String billId = billDAO.generateNextBillId();

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setAppointment(appointment);
        bill.setIssueDate(LocalDate.now());
        bill.calculateTotal(); // populates totalAmount from treatment.getFee()

        return bill;
    }
}