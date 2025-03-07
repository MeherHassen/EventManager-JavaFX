package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Singleton Design Pattern
public class MyConnection {
    private static final String DB_NAME = "events";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection;
    private static MyConnection instance;

    private MyConnection() {
        try {
            // Step 1: Ensure the database exists
            createDatabase();

            // Step 2: Establish connection
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Connection to database '" + DB_NAME + "' established successfully!");

            // Step 3: Create tables if they don't exist
            createTables();
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null)
            instance = new MyConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // Method to create database if it doesn't exist
    private void createDatabase() {
        try (Connection tempConnection = DriverManager.getConnection(ROOT_URL, USER, PASS);
             Statement stmt = tempConnection.createStatement()) {
            String createDBQuery = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDBQuery);
            System.out.println("✅ Database '" + DB_NAME + "' is ready!");
        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
        }
    }

    // Method to create tables
    private void createTables() {
        String createLocationTable = "CREATE TABLE IF NOT EXISTS location (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "address VARCHAR(255) NOT NULL, " +
                "capacity INT NOT NULL, " +
                "latitude DOUBLE DEFAULT 0.0, " +
                "longitude DOUBLE DEFAULT 0.0" +
                ");";

        String createEventTable = "CREATE TABLE IF NOT EXISTS event (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "date DATE NOT NULL, " +
                "location_id INT, " +
                "FOREIGN KEY (location_id) REFERENCES location(id) ON DELETE SET NULL " +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createLocationTable);
            stmt.executeUpdate(createEventTable);
            System.out.println("✅ Tables created successfully (if not exist)!");
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }
}
