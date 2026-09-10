package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnector - Singleton design pattern.
 *
 * Ensures only ONE instance of the database connection manager exists
 * throughout the application lifecycle. All DAO classes obtain the
 * shared connection through getInstance().getConnection().
 *
 * Why Singleton here:
 * - Prevents multiple redundant connection objects being created
 *   across servlets handling concurrent requests.
 * - Centralizes DB configuration (URL, credentials) in one place.
 */
public class DatabaseConnector {

    // The single shared instance
    private static DatabaseConnector instance;

    // JDBC connection details
    private static final String URL = "jdbc:mysql://localhost:3306/sunrise_dental?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "37333733";

    private Connection connection;

    // Private constructor prevents external instantiation
    private DatabaseConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Check WEB-INF/lib.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    /**
     * Global access point for the Singleton instance.
     * Synchronized to be safe under concurrent servlet requests.
     */
    public static synchronized DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    /**
     * Returns the active connection, reconnecting if it was closed.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}