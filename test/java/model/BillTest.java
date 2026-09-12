package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BillTest - unit tests for Bill's business logic.
 *
 * TDD NOTE: calculateTotal() was written test-first. This test was
 * authored against the expected contract (total = treatment fee)
 * before the method body was finalized, then the implementation was
 * confirmed against it. Screenshot the initial failing run and the
 * passing run as evidence of TDD in the report.
 */
public class BillTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient("P001", "Kasun Fernando", "12 Galle Road, Colombo 03", "0771234567");
        Dentist dentist = new Dentist("D001", "Dr. Ramesh Gunawardena", "General Dentistry");
        TreatmentType treatment = new TreatmentType("T003", "Root Canal Treatment", new BigDecimal("12000.00"));

        appointment = new Appointment();
        appointment.setAppointmentNumber("APT00099");
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(LocalDate.of(2026, 9, 15));
        appointment.setAppointmentTime(LocalTime.of(10, 30));
        appointment.setStatus("Scheduled");
    }

    @Test
    @DisplayName("calculateTotal() returns the treatment's consultation fee")
    void testCalculateTotal_matchesTreatmentFee() {
        Bill bill = new Bill("BILL00099", appointment, LocalDate.of(2026, 9, 15));

        BigDecimal total = bill.calculateTotal();

        assertEquals(new BigDecimal("12000.00"), total,
                "Bill total should equal the treatment's consultation fee");
    }

    @Test
    @DisplayName("calculateTotal() returns zero when appointment has no treatment set")
    void testCalculateTotal_noTreatment_returnsZero() {
        appointment.setTreatment(null);
        Bill bill = new Bill("BILL00098", appointment, LocalDate.of(2026, 9, 15));

        BigDecimal total = bill.calculateTotal();

        assertEquals(BigDecimal.ZERO, total,
                "Bill total should be zero when no treatment is linked");
    }

    @Test
    @DisplayName("calculateTotal() returns zero when appointment is null")
    void testCalculateTotal_nullAppointment_returnsZero() {
        Bill bill = new Bill("BILL00097", null, LocalDate.of(2026, 9, 15));

        BigDecimal total = bill.calculateTotal();

        assertEquals(BigDecimal.ZERO, total,
                "Bill total should be zero when appointment is null");
    }

    @Test
    @DisplayName("printReceipt() includes the bill ID, patient name, and total amount")
    void testPrintReceipt_containsKeyFields() {
        Bill bill = new Bill("BILL00099", appointment, LocalDate.of(2026, 9, 15));
        bill.calculateTotal();

        String receipt = bill.printReceipt();

        assertTrue(receipt.contains("BILL00099"), "Receipt should contain the bill ID");
        assertTrue(receipt.contains("Kasun Fernando"), "Receipt should contain the patient name");
        assertTrue(receipt.contains("12000.00"), "Receipt should contain the total amount");
    }
}