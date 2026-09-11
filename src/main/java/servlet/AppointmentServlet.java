package servlet;

import com.google.gson.Gson;
import dao.AppointmentDAO;
import factory.AppointmentFactory;
import model.Appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AppointmentServlet - Controller for "Register New Appointment" and
 * "Display Appointment Details" use cases.
 *
 * REST-style routing on a single servlet:
 *   POST   /appointments             -> register a new appointment
 *   GET    /appointments?number=X    -> search/display one appointment
 *   GET    /appointments             -> list all appointments
 */
@WebServlet("/appointments")
public class AppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AppointmentFactory appointmentFactory = new AppointmentFactory();
    private final Gson gson = util.GsonUtil.getGson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();

        // Require an authenticated session - only authorized staff may register appointments
        if (!isAuthorized(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "You must be logged in to perform this action.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        String patientId = request.getParameter("patientId");
        String dentistId = request.getParameter("dentistId");
        String treatmentId = request.getParameter("treatmentId");
        String dateStr = request.getParameter("date");   // expected format: yyyy-MM-dd
        String timeStr = request.getParameter("time");   // expected format: HH:mm

        // ---- Validation mechanism (rejects invalid entries per the brief) ----
        StringBuilder errors = new StringBuilder();
        if (isBlank(patientId))   errors.append("Patient ID is required. ");
        if (isBlank(dentistId))   errors.append("Dentist ID is required. ");
        if (isBlank(treatmentId)) errors.append("Treatment ID is required. ");

        LocalDate date = null;
        LocalTime time = null;
        try {
            if (!isBlank(dateStr)) date = LocalDate.parse(dateStr);
            else errors.append("Date is required. ");
        } catch (DateTimeParseException e) {
            errors.append("Date format must be yyyy-MM-dd. ");
        }
        try {
            if (!isBlank(timeStr)) time = LocalTime.parse(timeStr);
            else errors.append("Time is required. ");
        } catch (DateTimeParseException e) {
            errors.append("Time format must be HH:mm. ");
        }

        if (date != null && date.isBefore(LocalDate.now())) {
            errors.append("Appointment date cannot be in the past. ");
        }

        if (errors.length() > 0) {
            result.put("success", false);
            result.put("message", errors.toString().trim());
            response.getWriter().write(gson.toJson(result));
            return;
        }

        // ---- Build via Factory pattern ----
        Appointment appointment = appointmentFactory.createAppointment(
                patientId, dentistId, treatmentId, date, time);

        if (appointment == null) {
            result.put("success", false);
            result.put("message", "Invalid patient, dentist, or treatment ID.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        boolean saved = appointmentDAO.addAppointment(appointment);

        if (saved) {
            result.put("success", true);
            result.put("appointmentNumber", appointment.getAppointmentNumber());
            result.put("message", "Appointment registered successfully.");
        } else {
            result.put("success", false);
            result.put("message", "Failed to save appointment. Please try again.");
        }

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String appointmentNumber = request.getParameter("number");

        if (!isBlank(appointmentNumber)) {
            // Search by appointment number - "Display Appointment Details" use case
            Appointment appt = appointmentDAO.getAppointmentByNumber(appointmentNumber);
            if (appt != null) {
                response.getWriter().write(gson.toJson(appt));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "No appointment found with number: " + appointmentNumber);
                response.getWriter().write(gson.toJson(result));
            }
        } else {
            // No number supplied - return all appointments
            List<Appointment> all = appointmentDAO.getAllAppointments();
            response.getWriter().write(gson.toJson(all));
        }
    }

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("staffId") != null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}