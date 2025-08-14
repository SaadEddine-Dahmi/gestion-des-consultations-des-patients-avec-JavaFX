package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.MedicalRecord;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.AppointmentDAO;
import com.patientmanagement.model.dao.MedicalRecordDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.DateTimeUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AppointmentDetailsController {
    
    @FXML
    private Label appointmentIdLabel;
    
    @FXML
    private Label patientLabel;
    
    @FXML
    private Label doctorLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private Label reasonLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Label createdLabel;
    
    @FXML
    private Label updatedLabel;
    
    @FXML
    private Button completeButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button addRecordButton;
    
    @FXML
    private GridPane medicalRecordGrid;
    
    @FXML
    private Label diagnosisLabel;
    
    @FXML
    private Label treatmentLabel;
    
    @FXML
    private Label prescriptionLabel;
    
    @FXML
    private TextArea medicalNotesArea;
    
    @FXML
    private Label noRecordLabel;
    
    private Appointment appointment;
    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private MedicalRecord medicalRecord;
    
    @FXML
    private void initialize() {
        appointmentDAO = new AppointmentDAO();
        medicalRecordDAO = new MedicalRecordDAO();
    }
    
    /**
     * Initialize the controller with appointment and user data
     * @param appointment The appointment to display
     * @param user The logged-in user
     */
    public void initData(Appointment appointment, User user) {
        this.appointment = appointment;
        this.currentUser = user;
        
        // Set appointment information
        appointmentIdLabel.setText("ID: " + appointment.getAppointmentId());
        patientLabel.setText(appointment.getPatientName());
        doctorLabel.setText(appointment.getDoctorName());
        dateLabel.setText(DateTimeUtil.formatDate(appointment.getDate()));
        
        String timeRange = DateTimeUtil.formatTime(appointment.getStartTime()) + 
                          " - " + 
                          DateTimeUtil.formatTime(appointment.getEndTime());
        timeLabel.setText(timeRange);
        
        reasonLabel.setText(appointment.getReason());
        statusLabel.setText(appointment.getStatus());
        notesArea.setText(appointment.getNotes());
        
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        createdLabel.setText(appointment.getCreatedAt().format(timestampFormatter));
        updatedLabel.setText(appointment.getUpdatedAt().format(timestampFormatter));
        
        // Set button states based on appointment status
        boolean isScheduled = appointment.isScheduled();
        completeButton.setDisable(!isScheduled);
        cancelButton.setDisable(!isScheduled);
        editButton.setDisable(!isScheduled);
        
        // Only doctors can add medical records
        addRecordButton.setVisible(user.isDoctor());
        addRecordButton.setDisable(!isScheduled);
        
        // Load medical record if it exists
        loadMedicalRecord();
    }
    
    /**
     * Load the medical record for this appointment
     */
    private void loadMedicalRecord() {
        medicalRecord = medicalRecordDAO.getMedicalRecordByAppointment(appointment.getAppointmentId());
        
        if (medicalRecord != null) {
            // Show medical record data
            medicalRecordGrid.setVisible(true);
            noRecordLabel.setVisible(false);
            
            diagnosisLabel.setText(medicalRecord.getDiagnosis());
            treatmentLabel.setText(medicalRecord.getTreatment());
            prescriptionLabel.setText(medicalRecord.getPrescription());
            medicalNotesArea.setText(medicalRecord.getNotes());
            
            // Disable add record button if record already exists
            addRecordButton.setDisable(true);
        } else {
            // Show no record message
            medicalRecordGrid.setVisible(false);
            noRecordLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleEditAppointment(ActionEvent event) {
        try {
            // Load the appointment dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for editing
            AppointmentDialogController dialogController = loader.getController();
            dialogController.initData(currentUser, null, appointment);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Appointment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentIdLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh appointment data
            Appointment refreshedAppointment = appointmentDAO.getAppointmentById(appointment.getAppointmentId());
            if (refreshedAppointment != null) {
                initData(refreshedAppointment, currentUser);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the appointment dialog. Please try again.");
        }
    }
    
    @FXML
    private void handleMarkAsCompleted(ActionEvent event) {
        if (AlertUtil.showConfirmation("Complete Appointment", "Confirm Completion", 
                "Are you sure you want to mark this appointment as completed?")) {
            
            appointment.markAsCompleted();
            boolean updated = appointmentDAO.updateAppointment(appointment);
            
            if (updated) {
                AlertUtil.showInformation("Success", "Appointment Completed", 
                    "The appointment has been marked as completed.");
                
                // Refresh appointment data
                initData(appointment, currentUser);
            } else {
                AlertUtil.showError("Error", "Update Failed", 
                    "Could not update the appointment status. Please try again.");
            }
        }
    }
    
    @FXML
    private void handleCancelAppointment(ActionEvent event) {
        if (AlertUtil.showConfirmation("Cancel Appointment", "Confirm Cancellation", 
                "Are you sure you want to cancel this appointment?")) {
            
            appointment.markAsCancelled();
            boolean updated = appointmentDAO.updateAppointment(appointment);
            
            if (updated) {
                AlertUtil.showInformation("Success", "Appointment Cancelled", 
                    "The appointment has been cancelled successfully.");
                
                // Refresh appointment data
                initData(appointment, currentUser);
            } else {
                AlertUtil.showError("Error", "Cancellation Failed", 
                    "Could not cancel the appointment. Please try again.");
            }
        }
    }
    
    @FXML
    private void handleAddMedicalRecord(ActionEvent event) {
        try {
            // Load the medical record dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/medicalRecordDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for a new record
            MedicalRecordDialogController dialogController = loader.getController();
            dialogController.initData(appointment, currentUser);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Medical Record");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentIdLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh medical record data
            loadMedicalRecord();
            
            // If a record was added, mark the appointment as completed
            if (medicalRecord != null) {
                appointment.markAsCompleted();
                appointmentDAO.updateAppointment(appointment);
                
                // Refresh appointment data
                initData(appointment, currentUser);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the medical record dialog. Please try again.");
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) appointmentIdLabel.getScene().getWindow();
        stage.close();
    }
}
