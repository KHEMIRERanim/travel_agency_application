package utils;

import java.sql.*;

public class MyDatabase {
    private static MyDatabase instance;
    private Connection cnx;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/travillian";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Private constructor (Singleton pattern)
    private MyDatabase() {
        try {
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Singleton access method
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Public method to access the connection
    public Connection getConnection() {
        return cnx;
    }

    // Execute SELECT queries
    public ResultSet executeQuery(String query) {
        try (Statement stmt = cnx.createStatement()) {
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Query execution failed: " + e.getMessage());
            return null;
        }
    }

    // Execute INSERT, UPDATE, DELETE
    public int executeUpdate(String query) {
        try (Statement stmt = cnx.createStatement()) {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Update execution failed: " + e.getMessage());
            return 0;
        }
    }

    // Create a prepared statement for executing parameterized queries
    public PreparedStatement prepareStatement(String sql) {
        try {
            return cnx.prepareStatement(sql);
        } catch (SQLException e) {
            System.err.println("Failed to prepare statement: " + e.getMessage());
            return null;
        }
    }

    // Close the connection gracefully
    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}
