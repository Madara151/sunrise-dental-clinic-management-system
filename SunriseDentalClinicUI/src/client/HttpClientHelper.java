package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HttpClientHelper - thin wrapper around HttpURLConnection for talking
 * to the SunriseDentalClinic servlet backend over HTTP.
 *
 * This is the piece that makes the Swing client a genuinely separate,
 * distributed process: it communicates with the server only through
 * HTTP requests/JSON responses, never by calling server-side classes
 * directly.
 *
 * A simple cookie-jar is kept so the session created by LoginServlet
 * (JSESSIONID) is reused on subsequent requests - this is required for
 * the AppointmentServlet/BillServlet authorization checks to pass.
 */
public class HttpClientHelper {

    private static final String BASE_URL = "http://localhost:8080/SunriseDentalClinic";

    private static String sessionCookie = null;

    /**
     * Sends a POST request with form-encoded parameters.
     * @param path e.g. "/login", "/appointments", "/bills"
     * @param formData e.g. "username=admin&password=admin123"
     * @return the raw JSON response body
     */
    public static String post(String path, String formData) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        if (sessionCookie != null) {
            conn.setRequestProperty("Cookie", sessionCookie);
        }

        try (OutputStream os = conn.getOutputStream()) {
            os.write(formData.getBytes(StandardCharsets.UTF_8));
        }

        captureSessionCookie(conn);
        return readResponse(conn);
    }

    /**
     * Sends a GET request with an optional query string.
     * @param path e.g. "/appointments?number=APT00001"
     */
    public static String get(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (sessionCookie != null) {
            conn.setRequestProperty("Cookie", sessionCookie);
        }

        captureSessionCookie(conn);
        return readResponse(conn);
    }

    /**
     * Sends a DELETE request (used for logout).
     */
    public static String delete(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");

        if (sessionCookie != null) {
            conn.setRequestProperty("Cookie", sessionCookie);
        }

        String result = readResponse(conn);
        sessionCookie = null; // clear local session on logout
        return result;
    }

    private static void captureSessionCookie(HttpURLConnection conn) {
        String cookieHeader = conn.getHeaderField("Set-Cookie");
        if (cookieHeader != null) {
            // Keep only the JSESSIONID=... portion
            sessionCookie = cookieHeader.split(";", 2)[0];
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStreamReader isr = (status >= 200 && status < 300)
                ? new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
                : new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}