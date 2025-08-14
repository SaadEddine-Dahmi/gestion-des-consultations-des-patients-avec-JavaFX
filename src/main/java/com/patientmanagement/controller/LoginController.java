package com.patientmanagement.controller;

import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.UserDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.ValidationUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    private UserDAO userDAO;
    
    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        errorLabel.setText("");
        
        // Initialize default admin user if no users exist
        userDAO.initializeDefaultAdmin();
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate inputs
        if (username.isEmpty()) {
            errorLabel.setText("Please enter a username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            errorLabel.setText("Please enter a password");
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticateUser(username, password);
        
        if (user != null) {
            try {
                // Load the dashboard view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                
                // Get the controller and pass the user
                DashboardController dashboardController = loader.getController();
                dashboardController.initData(user);
                
                // Set the new scene
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(dashboardRoot);
                stage.setScene(scene);
                stage.setTitle("Patient Management System - Dashboard");
                stage.setMaximized(true);
                stage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtil.showError("Error", "Navigation Error", 
                    "Could not load the dashboard. Please try again.");
            }
        } else {
            errorLabel.setText("Invalid username or password");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
}
