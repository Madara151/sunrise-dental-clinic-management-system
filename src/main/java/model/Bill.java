package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bill - represents a generated bill/receipt for a completed appointment.
 * Maps to the 'bill' table.
 *
 * calculateTotal() and printReceipt() correspond directly to the
 * "Calculate and Print Bill" sequence diagram (Task A).
 */
public class Bill {

    private String billId;
    private Appointment appointment;
    private BigDecimal totalAmount;
    private LocalDate issueDate;

    public Bill() {
    }

    public Bill(String billId, Appointment appointment, LocalDate issueDate) {
        this.billId = billId;
        this.appointment = appointment;
        this.issueDate = issueDate;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Calculates the total bill amount based on the treatment's
     * consultation fee. Matches step 5-7 of the Billing sequence diagram.
     */
    public BigDecimal calculateTotal() {
        if (appointment != null && appointment.getTreatment() != null) {
            this.totalAmount = appointment.getTreatment().getFee();
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
        return this.totalAmount;
    }

    /**
     * Builds a printable receipt string.
     * In the Swing UI this can be sent to a JTextArea or a print dialog.
     */
    public String printReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== SUNRISE DENTAL CLINIC ==========\n");
        sb.append("Bill ID: ").append(billId).append("\n");
        sb.append("Issue Date: ").append(issueDate).append("\n");
        sb.append("---------------------------------------------\n");
        if (appointment != null) {
            sb.append("Appointment No: ").append(appointment.getAppointmentNumber()).append("\n");
            sb.append("Patient: ").append(appointment.getPatient().getName()).append("\n");
            sb.append("Dentist: ").append(appointment.getDentist().getName()).append("\n");
            sb.append("Treatment: ").append(appointment.getTreatment().getTreatmentName()).append("\n");
        }
        sb.append("---------------------------------------------\n");
        sb.append("TOTAL AMOUNT: Rs. ").append(totalAmount).append("\n");
        sb.append("===============================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", totalAmount=" + totalAmount +
                ", issueDate=" + issueDate +
                '}';
    }
}