package com.patientmanagement.test;

import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.UserDAO;
import com.patientmanagement.util.DatabaseUtil;
import com.patientmanagement.util.SecurityUtil;

/**
 * Test class for the authentication system
 */
public class AuthenticationTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Authentication System Test");
        System.out.println("==================================");
        
        // Initialize database
        DatabaseUtil.initializeDatabase();
        
        // Test database connection
        boolean connectionSuccess = DatabaseUtil.testConnection();
        System.out.println("Database Connection Test: " + (connectionSuccess ? "PASSED" : "FAILED"));
        
        if (connectionSuccess) {
            // Test user creation and authentication
            testUserAuthentication();
        }
    }
    
    private static void testUserAuthentication() {
        UserDAO userDAO = new UserDAO();
        
        // Initialize default admin
        userDAO.initializeDefaultAdmin();
        
        // Test admin authentication
        User admin = userDAO.authenticateUser("admin", "admin123");
        System.out.println("Admin Authentication Test: " + (admin != null ? "PASSED" : "FAILED"));
        
        if (admin != null) {
            System.out.println("Admin User Details:");
            System.out.println("  ID: " + admin.getUserId());
            System.out.println("  Username: " + admin.getUsername());
            System.out.println("  Name: " + admin.getFullName());
            System.out.println("  Role: " + admin.getRole());
        }
        
        // Test invalid authentication
        User invalidUser = userDAO.authenticateUser("admin", "wrongpassword");
        System.out.println("Invalid Authentication Test: " + (invalidUser == null ? "PASSED" : "FAILED"));
        
        // Test password hashing
        String password = "testpassword";
        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(password, salt);
        
        boolean verificationResult = SecurityUtil.verifyPassword(password, hash, salt);
        System.out.println("Password Hashing and Verification Test: " + (verificationResult ? "PASSED" : "FAILED"));
        
        // Test creating a new user
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("testpassword");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setEmail("test@example.com");
        newUser.setPhone("1234567890");
        newUser.setRole("Staff");
        
        boolean userCreated = userDAO.createUser(newUser);
        System.out.println("User Creation Test: " + (userCreated ? "PASSED" : "FAILED"));
        
        if (userCreated) {
            // Test authenticating the new user
            User authenticatedUser = userDAO.authenticateUser("testuser", "testpassword");
            System.out.println("New User Authentication Test: " + (authenticatedUser != null ? "PASSED" : "FAILED"));
            
            // Test changing password
            boolean passwordChanged = userDAO.changePassword(newUser.getUserId(), "newpassword");
            System.out.println("Password Change Test: " + (passwordChanged ? "PASSED" : "FAILED"));
            
            if (passwordChanged) {
                // Test authenticating with new password
                User userWithNewPassword = userDAO.authenticateUser("testuser", "newpassword");
                System.out.println("Authentication After Password Change Test: " + 
                                  (userWithNewPassword != null ? "PASSED" : "FAILED"));
            }
            
            // Clean up - delete test user
            boolean userDeleted = userDAO.deleteUser(newUser.getUserId());
            System.out.println("User Deletion Test: " + (userDeleted ? "PASSED" : "FAILED"));
        }
        
        System.out.println("Authentication System Test Completed");
    }
}
