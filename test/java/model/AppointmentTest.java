package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AppointmentTest - unit tests for Appointment's getAppointmentInfo() method,
 * used by the "Display Appointment Details" use case.
 */
public class AppointmentTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient("P002", "Ishara Silva", "45 Kandy Road, Kadawatha", "0719876543");
        Dentist dentist = new Dentist("D002", "Dr. Anusha Jayawardena", "Orthodontics");
        TreatmentType treatment = new TreatmentType("T005", "Braces Consultation", new BigDecimal("2000.00"));

        appointment = new Appointment();
        appointment.setAppointmentNumber("APT00002");
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(LocalDate.of(2026, 9, 11));
        appointment.setAppointmentTime(LocalTime.of(14, 0));
        appointment.setStatus("Scheduled");
    }

    @Test
    @DisplayName("getAppointmentInfo() includes appointment number, patient, dentist and treatment")
    void testGetAppointmentInfo_containsAllFields() {
        String info = appointment.getAppointmentInfo();

        assertTrue(info.contains("APT00002"));
        assertTrue(info.contains("Ishara Silva"));
        assertTrue(info.contains("Dr. Anusha Jayawardena"));
        assertTrue(info.contains("Braces Consultation"));
        assertTrue(info.contains("Scheduled"));
    }

    @Test
    @DisplayName("getAppointmentInfo() handles a null patient gracefully")
    void testGetAppointmentInfo_nullPatient_doesNotThrow() {
        appointment.setPatient(null);

        assertDoesNotThrow(() -> {
            String info = appointment.getAppointmentInfo();
            assertTrue(info.contains("N/A"));
        });
    }
}