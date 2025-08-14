package com.patientmanagement.controller;

import com.patientmanagement.model.Patient;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.ValidationUtil;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PatientDialogController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private DatePicker dobPicker;
    
    @FXML
    private ComboBox<String> genderComboBox;
    
    @FXML
    private TextArea addressArea;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField emergencyContactField;
    
    @FXML
    private TextArea insuranceInfoArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private PatientDAO patientDAO;
    private Patient patient;
    private boolean isEditMode = false;
    
    @FXML
    private void initialize() {
        patientDAO = new PatientDAO();
        
        // Initialize gender combo box
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        
        // Set max date to today (can't be born in the future)
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }
    
    /**
     * Initialize the dialog with patient data for editing or with empty fields for adding
     * @param patient The patient to edit, or null for a new patient
     */
    public void initData(Patient patient) {
        this.patient = patient;
        
        if (patient != null) {
            // Edit mode
            isEditMode = true;
            titleLabel.setText("Edit Patient");
            
            // Populate fields with patient data
            firstNameField.setText(patient.getFirstName());
            lastNameField.setText(patient.getLastName());
            dobPicker.setValue(patient.getDateOfBirth());
            genderComboBox.setValue(patient.getGender());
            addressArea.setText(patient.getAddress());
            phoneField.setText(patient.getPhone());
            emailField.setText(patient.getEmail());
            emergencyContactField.setText(patient.getEmergencyContact());
            insuranceInfoArea.setText(patient.getInsuranceInfo());
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            // Create or update patient
            if (patient == null) {
                patient = new Patient();
            }
            
            // Set patient data from form fields
            patient.setFirstName(firstNameField.getText().trim());
            patient.setLastName(lastNameField.getText().trim());
            patient.setDateOfBirth(dobPicker.getValue());
            patient.setGender(genderComboBox.getValue());
            patient.setAddress(addressArea.getText().trim());
            patient.setPhone(phoneField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setEmergencyContact(emergencyContactField.getText().trim());
            patient.setInsuranceInfo(insuranceInfoArea.getText().trim());
            
            boolean success;
            if (isEditMode) {
                // Update existing patient
                patient.updateTimestamp();
                success = patientDAO.updatePatient(patient);
                if (success) {
                    AlertUtil.showInformation("Success", "Patient Updated", 
                        "Patient information has been updated successfully.");
                }
            } else {
                // Create new patient
                success = patientDAO.createPatient(patient);
                if (success) {
                    AlertUtil.showInformation("Success", "Patient Added", 
                        "New patient has been added successfully.");
                }
            }
            
            if (success) {
                // Close the dialog
                closeDialog();
            } else {
                AlertUtil.showError("Error", "Operation Failed", 
                    "Could not save patient information. Please try again.");
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
        if (!ValidationUtil.validateRequired(firstNameField, "First Name")) return false;
        if (!ValidationUtil.validateRequired(lastNameField, "Last Name")) return false;
        if (!ValidationUtil.validateRequired(dobPicker, "Date of Birth")) return false;
        if (!ValidationUtil.validateRequired(genderComboBox, "Gender")) return false;
        
        // Optional fields with format validation
        if (!ValidationUtil.validatePhone(phoneField)) return false;
        if (!ValidationUtil.validateEmail(emailField)) return false;
        
        return true;
    }
    
    /**
     * Check if there are unsaved changes in the form
     * @return true if there are unsaved changes, false otherwise
     */
    private boolean hasUnsavedChanges() {
        if (isEditMode) {
            // Check if any field is different from the original patient data
            return !firstNameField.getText().equals(patient.getFirstName()) ||
                   !lastNameField.getText().equals(patient.getLastName()) ||
                   !dobPicker.getValue().equals(patient.getDateOfBirth()) ||
                   !genderComboBox.getValue().equals(patient.getGender()) ||
                   !addressArea.getText().equals(patient.getAddress()) ||
                   !phoneField.getText().equals(patient.getPhone()) ||
                   !emailField.getText().equals(patient.getEmail()) ||
                   !emergencyContactField.getText().equals(patient.getEmergencyContact()) ||
                   !insuranceInfoArea.getText().equals(patient.getInsuranceInfo());
        } else {
            // Check if any field has been filled
            return !firstNameField.getText().isEmpty() ||
                   !lastNameField.getText().isEmpty() ||
                   dobPicker.getValue() != null ||
                   genderComboBox.getValue() != null ||
                   !addressArea.getText().isEmpty() ||
                   !phoneField.getText().isEmpty() ||
                   !emailField.getText().isEmpty() ||
                   !emergencyContactField.getText().isEmpty() ||
                   !insuranceInfoArea.getText().isEmpty();
        }
    }
    
    /**
     * Close the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
