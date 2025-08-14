package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.MedicalRecord;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.MedicalRecordDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.DateTimeUtil;
import com.patientmanagement.util.ValidationUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MedicalRecordDialogController {
    
    @FXML
    private Label patientLabel;
    
    @FXML
    private Label doctorLabel;
    
    @FXML
    private Label appointmentDateLabel;
    
    @FXML
    private TextArea diagnosisArea;
    
    @FXML
    private TextArea treatmentArea;
    
    @FXML
    private TextArea prescriptionArea;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private MedicalRecordDAO medicalRecordDAO;
    private Appointment appointment;
    private User currentUser;
    
    @FXML
    private void initialize() {
        medicalRecordDAO = new MedicalRecordDAO();
    }
    
    /**
     * Initialize the dialog with appointment and user data
     * @param appointment The appointment to create a medical record for
     * @param user The logged-in user (doctor)
     */
    public void initData(Appointment appointment, User user) {
        this.appointment = appointment;
        this.currentUser = user;
        
        // Set appointment information
        patientLabel.setText(appointment.getPatientName());
        doctorLabel.setText(appointment.getDoctorName());
        appointmentDateLabel.setText(DateTimeUtil.formatDate(appointment.getDate()));
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            // Create new medical record
            MedicalRecord record = new MedicalRecord();
            
            // Set medical record data from form fields
            record.setPatientId(appointment.getPatientId());
            record.setPatientName(appointment.getPatientName());
            record.setDoctorId(appointment.getDoctorId());
            record.setDoctorName(appointment.getDoctorName());
            record.setAppointmentId(appointment.getAppointmentId());
            record.setAppointmentDate(appointment.getDate());
            record.setDiagnosis(diagnosisArea.getText().trim());
            record.setTreatment(treatmentArea.getText().trim());
            record.setPrescription(prescriptionArea.getText().trim());
            record.setNotes(notesArea.getText().trim());
            
            boolean success = medicalRecordDAO.createMedicalRecord(record);
            
            if (success) {
                AlertUtil.showInformation("Success", "Medical Record Created", 
                    "Medical record has been created successfully.");
                closeDialog();
            } else {
                AlertUtil.showError("Error", "Operation Failed", 
                    "Could not save medical record. Please try again.");
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        // Confirm if there are unsaved changes
        if (hasUnsavedChanges()) {
            if (AlertUtil.showConfirmation("Unsaved Changes", "Confirm Cancel", 
                    "You have unsaved changes. Are you sure you want to cancel?")) {
                closeDialog();
            }
        } else {
            closeDialog();
        }
    }
    
    /**
     * Validate all input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        // Required fields
        if (!ValidationUtil.validateRequired(diagnosisArea, "Diagnosis")) return false;
        if (!ValidationUtil.validateRequired(treatmentArea, "Treatment")) return false;
        
        return true;
    }
    
    /**
     * Check if there are unsaved changes in the form
     * @return true if there are unsaved changes, false otherwise
     */
    private boolean hasUnsavedChanges() {
        return !diagnosisArea.getText().isEmpty() ||
               !treatmentArea.getText().isEmpty() ||
               !prescriptionArea.getText().isEmpty() ||
               !notesArea.getText().isEmpty();
    }
    
    /**
     * Close the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
