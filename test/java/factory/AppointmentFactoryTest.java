package factory;

import model.Appointment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AppointmentFactoryTest - integration tests for the Factory pattern
 * implementation. Requires the seeded database (same fixtures as
 * AppointmentDAOTest).
 *
 * Directly tests the "[alt: invalid]" branch shown in the Registration
 * sequence diagram: createAppointment() should return null rather than
 * throw when given a non-existent patient/dentist/treatment ID.
 */
@Tag("integration")
public class AppointmentFactoryTest {

    private final AppointmentFactory factory = new AppointmentFactory();

    @Test
    @DisplayName("createAppointment() builds a fully-populated Appointment for valid IDs")
    void testCreateAppointment_validIds_returnsPopulatedAppointment() {
        Appointment appt = factory.createAppointment(
                "P001", "D001", "T001", LocalDate.of(2026, 12, 1), LocalTime.of(9, 0));

        assertNotNull(appt, "Appointment should be created for valid patient/dentist/treatment IDs");
        assertNotNull(appt.getAppointmentNumber(), "A unique appointment number should be generated");
        assertEquals("P001", appt.getPatient().getPatientId());
        assertEquals("D001", appt.getDentist().getDentistId());
        assertEquals("T001", appt.getTreatment().getTreatmentId());
        assertEquals("Scheduled", appt.getStatus());
    }

    @Test
    @DisplayName("createAppointment() returns null for a non-existent patient ID")
    void testCreateAppointment_invalidPatientId_returnsNull() {
        Appointment appt = factory.createAppointment(
                "P999", "D001", "T001", LocalDate.of(2026, 12, 1), LocalTime.of(9, 0));

        assertNull(appt, "Should return null when the patient ID does not exist");
    }

    @Test
    @DisplayName("createAppointment() returns null for a non-existent dentist ID")
    void testCreateAppointment_invalidDentistId_returnsNull() {
        Appointment appt = factory.createAppointment(
                "P001", "D999", "T001", LocalDate.of(2026, 12, 1), LocalTime.of(9, 0));

        assertNull(appt, "Should return null when the dentist ID does not exist");
    }

    @Test
    @DisplayName("createAppointment() returns null for a non-existent treatment ID")
    void testCreateAppointment_invalidTreatmentId_returnsNull() {
        Appointment appt = factory.createAppointment(
                "P001", "D001", "T999", LocalDate.of(2026, 12, 1), LocalTime.of(9, 0));

        assertNull(appt, "Should return null when the treatment ID does not exist");
    }
}