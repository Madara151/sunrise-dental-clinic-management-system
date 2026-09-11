package servlet;

import com.google.gson.Gson;
import dao.BillDAO;
import factory.BillFactory;
import model.Bill;

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
 * BillServlet - Controller for "Calculate and Print Bill" use case.
 *
 * REST-style routing:
 *   POST /bills?appointmentNumber=X   -> generate (calculate + save) a bill
 *   GET  /bills?appointmentNumber=X   -> retrieve the bill/receipt text
 */
@WebServlet("/bills")
public class BillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final BillDAO billDAO = new BillDAO();
    private final BillFactory billFactory = new BillFactory();
    private final Gson gson = util.GsonUtil.getGson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();

        if (!isAuthorized(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "You must be logged in to perform this action.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");

        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Appointment number is required.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        // Avoid generating a duplicate bill for the same appointment
        Bill existing = billDAO.getBillByAppointmentNumber(appointmentNumber);
        if (existing != null) {
            result.put("success", true);
            result.put("message", "Bill already exists for this appointment.");
            result.put("bill", existing);
            response.getWriter().write(gson.toJson(result));
            return;
        }

        // Build via Factory pattern - handles lookup + calculateTotal()
        Bill bill = billFactory.createBill(appointmentNumber);

        if (bill == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "No appointment found with number: " + appointmentNumber);
            response.getWriter().write(gson.toJson(result));
            return;
        }

        boolean saved = billDAO.addBill(bill);

        if (saved) {
            result.put("success", true);
            result.put("message", "Bill generated successfully.");
            result.put("bill", bill);
            result.put("receiptText", bill.printReceipt());
        } else {
            result.put("success", false);
            result.put("message", "Failed to save bill. Please try again.");
        }

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String appointmentNumber = request.getParameter("appointmentNumber");
        Map<String, Object> result = new HashMap<>();

        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Appointment number is required.");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        Bill bill = billDAO.getBillByAppointmentNumber(appointmentNumber);

        if (bill != null) {
            result.put("success", true);
            result.put("bill", bill);
            result.put("receiptText", bill.printReceipt());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "No bill found for appointment: " + appointmentNumber);
        }

        response.getWriter().write(gson.toJson(result));
    }

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("staffId") != null;
    }
}