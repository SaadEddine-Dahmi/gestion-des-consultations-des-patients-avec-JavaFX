package com.patientmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database operations
 */
public class DatabaseUtil {
    
    private static final String DB_URL = "jdbc:sqlite:patient_management.db";
    
    /**
     * Get a connection to the database
     * @return Database connection
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * Initialize the database with required tables
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                         "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "username TEXT UNIQUE NOT NULL, " +
                         "password_hash TEXT NOT NULL, " +
                         "password_salt TEXT NOT NULL, " +
                         "first_name TEXT NOT NULL, " +
                         "last_name TEXT NOT NULL, " +
                         "email TEXT, " +
                         "phone TEXT, " +
                         "role TEXT NOT NULL, " +
                         "created_at TIMESTAMP NOT NULL, " +
                         "updated_at TIMESTAMP NOT NULL)");
            
            // Create patients table
            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                         "patient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "first_name TEXT NOT NULL, " +
                         "last_name TEXT NOT NULL, " +
                         "date_of_birth DATE NOT NULL, " +
                         "gender TEXT NOT NULL, " +
                         "address TEXT, " +
                         "phone TEXT, " +
                         "email TEXT, " +
                         "emergency_contact TEXT, " +
                         "insurance_info TEXT, " +
                         "created_at TIMESTAMP NOT NULL, " +
                         "updated_at TIMESTAMP NOT NULL)");
            
            // Create doctors table
            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                         "doctor_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER, " +
                         "first_name TEXT NOT NULL, " +
                         "last_name TEXT NOT NULL, " +
                         "specialization TEXT NOT NULL, " +
                         "phone TEXT, " +
                         "email TEXT, " +
                         "created_at TIMESTAMP NOT NULL, " +
                         "updated_at TIMESTAMP NOT NULL, " +
                         "FOREIGN KEY (user_id) REFERENCES users (user_id))");
            
            // Create appointments table
            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                         "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "patient_id INTEGER NOT NULL, " +
                         "patient_name TEXT NOT NULL, " +
                         "doctor_id INTEGER NOT NULL, " +
                         "doctor_name TEXT NOT NULL, " +
                         "date DATE NOT NULL, " +
                         "start_time TIME NOT NULL, " +
                         "end_time TIME NOT NULL, " +
                         "reason TEXT NOT NULL, " +
                         "notes TEXT, " +
                         "status TEXT NOT NULL, " +
                         "created_at TIMESTAMP NOT NULL, " +
                         "updated_at TIMESTAMP NOT NULL, " +
                         "FOREIGN KEY (patient_id) REFERENCES patients (patient_id), " +
                         "FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id))");
            
            // Create medical_records table
            stmt.execute("CREATE TABLE IF NOT EXISTS medical_records (" +
                         "record_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "patient_id INTEGER NOT NULL, " +
                         "patient_name TEXT NOT NULL, " +
                         "doctor_id INTEGER NOT NULL, " +
                         "doctor_name TEXT NOT NULL, " +
                         "appointment_id INTEGER, " +
                         "appointment_date DATE NOT NULL, " +
                         "diagnosis TEXT NOT NULL, " +
                         "treatment TEXT NOT NULL, " +
                         "prescription TEXT, " +
                         "notes TEXT, " +
                         "created_at TIMESTAMP NOT NULL, " +
                         "updated_at TIMESTAMP NOT NULL, " +
                         "FOREIGN KEY (patient_id) REFERENCES patients (patient_id), " +
                         "FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id), " +
                         "FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id))");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test the database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
