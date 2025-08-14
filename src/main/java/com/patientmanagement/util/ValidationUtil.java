package com.patientmanagement.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    /**
     * Validate that a required field is not empty
     * @param control The control to validate
     * @param fieldName The name of the field for error messages
     * @return true if valid, false otherwise
     */
    public static boolean validateRequired(TextInputControl control, String fieldName) {
        if (control.getText() == null || control.getText().trim().isEmpty()) {
            AlertUtil.showError("Validation Error", fieldName + " Required", 
                fieldName + " is required and cannot be empty.");
            control.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Validate that a required combo box has a selection
     * @param control The combo box to validate
     * @param fieldName The name of the field for error messages
     * @return true if valid, false otherwise
     */
    public static boolean validateRequired(ComboBoxBase<?> control, String fieldName) {
        if (control.getValue() == null) {
            AlertUtil.showError("Validation Error", fieldName + " Required", 
                "Please select a " + fieldName.toLowerCase() + ".");
            control.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Validate that a required date picker has a date selected
     * @param control The date picker to validate
     * @param fieldName The name of the field for error messages
     * @return true if valid, false otherwise
     */
    public static boolean validateRequired(DatePicker control, String fieldName) {
        if (control.getValue() == null) {
            AlertUtil.showError("Validation Error", fieldName + " Required", 
                "Please select a " + fieldName.toLowerCase() + ".");
            control.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Validate that an email address is in a valid format
     * @param email The email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Empty email is allowed (not required)
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validate that a phone number is in a valid format
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Empty phone is allowed (not required)
        }
        
        // Allow digits, spaces, dashes, parentheses, and plus sign
        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$";
        return phone.matches(phoneRegex);
    }
    
    /**
     * Validate that a password meets minimum requirements
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            AlertUtil.showError("Validation Error", "Invalid Password", 
                "Password must be at least 6 characters long.");
            return false;
        }
        return true;
    }
    
    /**
     * Validate that two passwords match
     * @param password The password
     * @param confirmPassword The confirmation password
     * @return true if they match, false otherwise
     */
    public static boolean validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            AlertUtil.showError("Validation Error", "Password Mismatch", 
                "Passwords do not match.");
            return false;
        }
        return true;
    }
}
