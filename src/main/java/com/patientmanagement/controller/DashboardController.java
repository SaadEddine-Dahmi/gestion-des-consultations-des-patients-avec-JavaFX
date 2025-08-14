package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.AppointmentDAO;
import com.patientmanagement.model.dao.DoctorDAO;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.DateTimeUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {
    
    @FXML
    private Label userLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label todayAppointmentsCount;
    
    @FXML
    private Label totalPatientsCount;
    
    @FXML
    private Label totalDoctorsCount;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private VBox dashboardContent;
    
    @FXML
    private TableView<Appointment> upcomingAppointmentsTable;
    
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    
    @FXML
    private TableColumn<Appointment, String> timeColumn;
    
    @FXML
    private TableColumn<Appointment, String> patientColumn;
    
    @FXML
    private TableColumn<Appointment, String> doctorColumn;
    
    @FXML
    private TableColumn<Appointment, String> reasonColumn;
    
    @FXML
    private TableColumn<Appointment, String> statusColumn;
    
    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    
    @FXML
    private void initialize() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        
        // Set current date
        dateLabel.setText(DateTimeUtil.formatDate(LocalDate.now()));
        
        // Initialize table columns
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatDate(cellData.getValue().getDate())));
        
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatTime(cellData.getValue().getStartTime())));
        
        patientColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPatientName()));
        
        doctorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDoctorName()));
        
        reasonColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReason()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        
        // Set status message
        statusLabel.setText("Ready");
    }
    
    /**
     * Initialize the controller with user data
     * @param user The logged-in user
     */
    public void initData(User user) {
        this.currentUser = user;
        userLabel.setText("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
        
        // Load dashboard data
        loadDashboardData();
    }
    
    /**
     * Load data for the dashboard
     */
    private void loadDashboardData() {
        // Count today's appointments
        List<Appointment> todayAppointments = appointmentDAO.getAppointmentsByDate(LocalDate.now());
        todayAppointmentsCount.setText(String.valueOf(todayAppointments.size()));
        
        // Count total patients
        int patientCount = patientDAO.getAllPatients().size();
        totalPatientsCount.setText(String.valueOf(patientCount));
        
        // Count total doctors
        int doctorCount = doctorDAO.getAllDoctors().size();
        totalDoctorsCount.setText(String.valueOf(doctorCount));
        
        // Load upcoming appointments (next 7 days)
        loadUpcomingAppointments();
    }
    
    /**
     * Load upcoming appointments for the next 7 days
     */
    private void loadUpcomingAppointments() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        // Filter for upcoming appointments (scheduled and not in the past)
        ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        
        for (Appointment appointment : appointments) {
            if (appointment.isScheduled() && 
                (appointment.getDate().equals(today) || appointment.getDate().isAfter(today)) && 
                appointment.getDate().isBefore(nextWeek)) {
                upcomingAppointments.add(appointment);
            }
        }
        
        upcomingAppointmentsTable.setItems(upcomingAppointments);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        if (AlertUtil.showConfirmation("Logout", "Confirm Logout", 
                "Are you sure you want to logout?")) {
            try {
                // Load the login view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/login.fxml"));
                Parent loginRoot = loader.load();
                
                // Set the new scene
                Stage stage = (Stage) userLabel.getScene().getWindow();
                Scene scene = new Scene(loginRoot, 600, 400);
                stage.setScene(scene);
                stage.setTitle("Patient Management System - Login");
                stage.setResizable(false);
                stage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtil.showError("Error", "Navigation Error", 
                    "Could not load the login screen. Please try again.");
            }
        }
    }
    
    @FXML
    private void handleDashboardButton(ActionEvent event) {
        // Already on dashboard, just refresh data
        loadDashboardData();
        statusLabel.setText("Dashboard refreshed");
    }
    
    @FXML
    private void handlePatientsButton(ActionEvent event) {
        try {
            // Load the patients view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patients.fxml"));
            Parent patientsRoot = loader.load();
            
            // Get the controller and pass the user
            PatientsController patientsController = loader.getController();
            patientsController.initData(currentUser);
            
            // Replace the content area with patients view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(patientsRoot);
            
            statusLabel.setText("Patients module loaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Navigation Error", 
                "Could not load the patients screen. Please try again.");
        }
    }
    
    @FXML
    private void handleAppointmentsButton(ActionEvent event) {
        try {
            // Load the appointments view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointments.fxml"));
            Parent appointmentsRoot = loader.load();
            
            // Get the controller and pass the user
            AppointmentsController appointmentsController = loader.getController();
            appointmentsController.initData(currentUser);
            
            // Replace the content area with appointments view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(appointmentsRoot);
            
            statusLabel.setText("Appointments module loaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Navigation Error", 
                "Could not load the appointments screen. Please try again.");
        }
    }
    
    @FXML
    private void handleMedicalRecordsButton(ActionEvent event) {
        try {
            // Load the medical records view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/medicalRecords.fxml"));
            Parent medicalRecordsRoot = loader.load();
            
            // Get the controller and pass the user
            MedicalRecordsController medicalRecordsController = loader.getController();
            medicalRecordsController.initData(currentUser);
            
            // Replace the content area with medical records view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(medicalRecordsRoot);
            
            statusLabel.setText("Medical Records module loaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Navigation Error", 
                "Could not load the medical records screen. Please try again.");
        }
    }
    
    @FXML
    private void handleDoctorsButton(ActionEvent event) {
        try {
            // Load the doctors view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/doctors.fxml"));
            Parent doctorsRoot = loader.load();
            
            // Get the controller and pass the user
            DoctorsController doctorsController = loader.getController();
            doctorsController.initData(currentUser);
            
            // Replace the content area with doctors view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(doctorsRoot);
            
            statusLabel.setText("Doctors module loaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Navigation Error", 
                "Could not load the doctors screen. Please try again.");
        }
    }
    
    @FXML
    private void handleSettingsButton(ActionEvent event) {
        try {
            // Load the settings view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/settings.fxml"));
            Parent settingsRoot = loader.load();
            
            // Get the controller and pass the user
            SettingsController settingsController = loader.getController();
            settingsController.initData(currentUser);
            
            // Replace the content area with settings view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(settingsRoot);
            
            statusLabel.setText("Settings module loaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Navigation Error", 
                "Could not load the settings screen. Please try again.");
        }
    }
    
    @FXML
    private void handleViewTodayAppointments(ActionEvent event) {
        handleAppointmentsButton(event);
    }
    
    @FXML
    private void handleViewAllPatients(ActionEvent event) {
        handlePatientsButton(event);
    }
    
    @FXML
    private void handleViewAllDoctors(ActionEvent event) {
        handleDoctorsButton(event);
    }
    
    @FXML
    private void handleNewAppointment(ActionEvent event) {
        try {
            // Load the new appointment dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and pass the user
            AppointmentDialogController dialogController = loader.getController();
            dialogController.initData(currentUser, null);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Appointment");
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh the dashboard data
            loadDashboardData();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the appointment dialog. Please try again.");
        }
    }
}
