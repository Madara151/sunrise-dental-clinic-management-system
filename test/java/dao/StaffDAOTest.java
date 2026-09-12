package dao;

import model.Staff;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaffDAOTest - integration tests for login credential verification.
 * Requires the seeded 'staff' table (username 'admin' / password 'admin123').
 *
 * Directly tests both branches shown in the "Staff Login" sequence diagram:
 * matchFound = true (success) and the [alt: no match] failure path.
 */
@Tag("integration")
public class StaffDAOTest {

    private final StaffDAO staffDAO = new StaffDAO();

    @Test
    @DisplayName("verifyCredentials() returns the Staff object for correct username/password")
    void testVerifyCredentials_correctCredentials_returnsStaff() {
        Staff staff = staffDAO.verifyCredentials("admin", "admin123");

        assertNotNull(staff, "Correct credentials should return a Staff object");
        assertEquals("admin", staff.getUsername());
        assertEquals("Nadeesha Perera", staff.getFullName());
    }

    @Test
    @DisplayName("verifyCredentials() returns null for a correct username but wrong password")
    void testVerifyCredentials_wrongPassword_returnsNull() {
        Staff staff = staffDAO.verifyCredentials("admin", "wrongPassword");

        assertNull(staff, "Wrong password should return null (matchFound = false)");
    }

    @Test
    @DisplayName("verifyCredentials() returns null for a non-existent username")
    void testVerifyCredentials_unknownUsername_returnsNull() {
        Staff staff = staffDAO.verifyCredentials("nonexistentUser", "anyPassword");

        assertNull(staff, "Unknown username should return null");
    }
}