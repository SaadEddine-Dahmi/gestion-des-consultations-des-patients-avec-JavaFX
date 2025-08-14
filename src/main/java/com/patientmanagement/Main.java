package com.patientmanagement;

import com.patientmanagement.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Patient Consultation Management System
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database
        DatabaseUtil.initializeDatabase();
        
        // Load the login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/login.fxml"));
        Parent root = loader.load();
        
        // Set up the scene
        Scene scene = new Scene(root, 600, 400);
        
        // Set up the stage
        primaryStage.setTitle("Patient Consultation Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Main method to launch the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
