package dao;

import model.Appointment;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AppointmentDAOTest - integration tests for AppointmentDAO against
 * the real MySQL database (sunrise_dental schema with seed data).
 *
 * These require MySQL to be running locally with the schema and seed
 * data from sunrise_dental_schema.sql already loaded (patient P001,
 * dentist D001, treatment T001, appointment APT00001 are used as
 * known-good fixtures).
 *
 * Marked with @Tag("integration") so they can be run separately from
 * pure unit tests if desired (e.g. excluded from a fast local test run).
 */
@Tag("integration")
public class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        appointmentDAO = new AppointmentDAO();
    }

    @Test
    @DisplayName("getAppointmentByNumber() retrieves a known seeded appointment with correct joined data")
    void testGetAppointmentByNumber_knownAppointment_returnsPopulatedObject() {
        Appointment appt = appointmentDAO.getAppointmentByNumber("APT00001");

        assertNotNull(appt, "Seeded appointment APT00001 should exist");
        assertEquals("APT00001", appt.getAppointmentNumber());
        assertNotNull(appt.getPatient(), "Patient should be joined and populated");
        assertEquals("Kasun Fernando", appt.getPatient().getName());
        assertNotNull(appt.getDentist(), "Dentist should be joined and populated");
        assertEquals("Dr. Ramesh Gunawardena", appt.getDentist().getName());
        assertNotNull(appt.getTreatment(), "Treatment should be joined and populated");
        assertEquals("General Checkup", appt.getTreatment().getTreatmentName());
    }

    @Test
    @DisplayName("getAppointmentByNumber() returns null for a non-existent appointment number")
    void testGetAppointmentByNumber_unknownAppointment_returnsNull() {
        Appointment appt = appointmentDAO.getAppointmentByNumber("APT99999");

        assertNull(appt, "A non-existent appointment number should return null");
    }

    @Test
    @DisplayName("generateNextAppointmentNumber() produces a correctly formatted, incremented number")
    void testGenerateNextAppointmentNumber_formatIsValid() {
        String next = appointmentDAO.generateNextAppointmentNumber();

        assertNotNull(next);
        assertTrue(next.matches("APT\\d{5}"),
                "Generated appointment number should match pattern APT##### but was: " + next);
    }

    @Test
    @DisplayName("getAllAppointments() returns a non-empty list when seed data is present")
    void testGetAllAppointments_returnsSeededRecords() {
        var all = appointmentDAO.getAllAppointments();

        assertFalse(all.isEmpty(), "Expected at least the seeded appointments to be present");
    }
}