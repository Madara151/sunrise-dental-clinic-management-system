package servlet;

import com.google.gson.Gson;
import dao.StaffDAO;
import model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginServlet - Controller for the "Staff Login" use case.
 *
 * Exposes a REST-style endpoint (POST /login) that the Swing client
 * calls over HTTP, making this a genuinely distributed web service:
 * the UI process and this server process can run on separate machines.
 *
 * Maps to the "Staff Login" sequence diagram:
 *   Staff -> LoginUI -> [this servlet] -> StaffDAO -> DatabaseConnector
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final StaffDAO staffDAO = new StaffDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Map<String, Object> result = new HashMap<>();

        // Basic validation - reject empty credentials before hitting the DB
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Username and password are required.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        Staff staff = staffDAO.verifyCredentials(username, password);

        if (staff != null) {
            // createSession() step from the sequence diagram
            HttpSession session = request.getSession(true);
            session.setAttribute("staffId", staff.getStaffId());
            session.setAttribute("username", staff.getUsername());
            session.setAttribute("role", staff.getRole());

            result.put("success", true);
            result.put("message", "Login successful.");
            result.put("staffName", staff.getFullName());
            result.put("role", staff.getRole());
        } else {
            result.put("success", false);
            result.put("message", "Invalid username or password.");
        }

        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Allows a client to log out, invalidating the session.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logged out successfully.");
        response.getWriter().write(gson.toJson(result));
    }
}