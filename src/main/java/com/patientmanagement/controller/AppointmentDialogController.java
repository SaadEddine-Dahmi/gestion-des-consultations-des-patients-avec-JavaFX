package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.Doctor;
import com.patientmanagement.model.Patient;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.AppointmentDAO;
import com.patientmanagement.model.dao.DoctorDAO;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.DateTimeUtil;
import com.patientmanagement.util.ValidationUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDialogController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private ComboBox<Patient> patientComboBox;
    
    @FXML
    private ComboBox<Doctor> doctorComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<LocalTime> startTimeComboBox;
    
    @FXML
    private ComboBox<LocalTime> endTimeComboBox;
    
    @FXML
    private TextField reasonField;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private ComboBox<String> statusComboBox;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private User currentUser;
    private Patient selectedPatient;
    private Appointment appointment;
    private boolean isEditMode = false;
    
    @FXML
    private void initialize() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        
        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Scheduled", "Completed", "Cancelled"));
        statusComboBox.setValue("Scheduled");
        
        // Set up patient combo box
        setupPatientComboBox();
        
        // Set up doctor combo box
        setupDoctorComboBox();
        
        // Set up time combo boxes
        setupTimeComboBoxes();
        
        // Set date picker constraints (no past dates)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        // Set default date to today
        datePicker.setValue(LocalDate.now());
    }
    
    /**
     * Initialize the dialog with user data for a new appointment
     * @param user The logged-in user
     * @param patient The patient for the appointment (can be null)
     */
    public void initData(User user, Patient patient) {
        this.currentUser = user;
        this.selectedPatient = patient;
        
        if (patient != null) {
            // Pre-select the patient
            patientComboBox.getSelectionModel().select(patient);
            patientComboBox.setDisable(true); // Lock the selection
        }
    }
    
    /**
     * Initialize the dialog with user data for editing an existing appointment
     * @param user The logged-in user
     * @param patient The patient for the appointment (can be null)
     * @param appointment The appointment to edit
     */
    public void initData(User user, Patient patient, Appointment appointment) {
        // First initialize as a new appointment
        initData(user, patient);
        
        // Then set up for editing
        this.appointment = appointment;
        isEditMode = true;
        titleLabel.setText("Edit Appointment");
        
        // Populate fields with appointment data
        Patient appointmentPatient = patientDAO.getPatientById(appointment.getPatientId());
        if (appointmentPatient != null) {
            patientComboBox.getSelectionModel().select(appointmentPatient);
            patientComboBox.setDisable(true); // Lock the selection
        }
        
        Doctor appointmentDoctor = doctorDAO.getDoctorById(appointment.getDoctorId());
        if (appointmentDoctor != null) {
            doctorComboBox.getSelectionModel().select(appointmentDoctor);
        }
        
        datePicker.setValue(appointment.getDate());
        
        // Find and select the closest time slots
        selectClosestTime(startTimeComboBox, appointment.getStartTime());
        selectClosestTime(endTimeComboBox, appointment.getEndTime());
        
        reasonField.setText(appointment.getReason());
        notesArea.setText(appointment.getNotes());
        statusComboBox.setValue(appointment.getStatus());
    }
    
    /**
     * Set up the patient combo box with all patients
     */
    private void setupPatientComboBox() {
        List<Patient> patients = patientDAO.getAllPatients();
        patientComboBox.setItems(FXCollections.observableArrayList(patients));
        
        // Set up display format
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient == null ? "" : patient.getFullName();
            }
            
            @Override
            public Patient fromString(String string) {
                return null; // Not needed for combo box
            }
        });
    }
    
    /**
     * Set up the doctor combo box with all doctors
     */
    private void setupDoctorComboBox() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        doctorComboBox.setItems(FXCollections.observableArrayList(doctors));
        
        // Set up display format
        doctorComboBox.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor doctor) {
                return doctor == null ? "" : doctor.getFullName();
            }
            
            @Override
            public Doctor fromString(String string) {
                return null; // Not needed for combo box
            }
        });
    }
    
    /**
     * Set up the time combo boxes with 30-minute intervals
     */
    private void setupTimeComboBoxes() {
        ObservableList<LocalTime> timeSlots = FXCollections.observableArrayList();
        
        // Create time slots from 8:00 to 17:30 (30-minute intervals)
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 30);
        
        while (!startTime.isAfter(endTime)) {
            timeSlots.add(startTime);
            startTime = startTime.plusMinutes(30);
        }
        
        startTimeComboBox.setItems(timeSlots);
        endTimeComboBox.setItems(timeSlots);
        
        // Set default values
        startTimeComboBox.getSelectionModel().select(LocalTime.of(9, 0));
        endTimeComboBox.getSelectionModel().select(LocalTime.of(9, 30));
        
        // Set up display format
        StringConverter<LocalTime> timeConverter = new StringConverter<LocalTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            
            @Override
            public String toString(LocalTime time) {
                return time == null ? "" : formatter.format(time);
            }
            
            @Override
            public LocalTime fromString(String string) {
                return string == null || string.isEmpty() ? null : LocalTime.parse(string, formatter);
            }
        };
        
        startTimeComboBox.setConverter(timeConverter);
        endTimeComboBox.setConverter(timeConverter);
        
        // Add listener to start time to ensure end time is always after start time
        startTimeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LocalTime currentEndTime = endTimeComboBox.getValue();
                if (currentEndTime == null || !currentEndTime.isAfter(newVal)) {
                    // Find the next time slot after the selected start time
                    for (LocalTime time : timeSlots) {
                        if (time.isAfter(newVal)) {
                            endTimeComboBox.setValue(time);
                            break;
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Select the closest time in a combo box to the given time
     * @param comboBox The combo box to select in
     * @param targetTime The target time to find
     */
    private void selectClosestTime(ComboBox<LocalTime> comboBox, LocalTime targetTime) {
        if (targetTime == null) return;
        
        LocalTime closestTime = null;
        int minDifference = Integer.MAX_VALUE;
        
        for (LocalTime time : comboBox.getItems()) {
            int difference = Math.abs(time.toSecondOfDay() - targetTime.toSecondOfDay());
            if (difference < minDifference) {
                minDifference = difference;
                closestTime = time;
            }
        }
        
        if (closestTime != null) {
            comboBox.setValue(closestTime);
        }
    }
    
    @FXML
    private void handleSelectPatient(ActionEvent event) {
        try {
            // Load the patient selection dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patientSelectionDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller
            PatientSelectionDialogController dialogController = loader.getController();
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select Patient");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Get the selected patient
            Patient selectedPatient = dialogController.getSelectedPatient();
            if (selectedPatient != null) {
                patientComboBox.getSelectionModel().select(selectedPatient);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the patient selection dialog. Please try again.");
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            // Create or update appointment
            if (appointment == null) {
                appointment = new Appointment();
            }
            
            // Set appointment data from form fields
            Patient patient = patientComboBox.getValue();
            Doctor doctor = doctorComboBox.getValue();
            
            appointment.setPatientId(patient.getPatientId());
            appointment.setPatientName(patient.getFullName());
            appointment.setDoctorId(doctor.getDoctorId());
            appointment.setDoctorName(doctor.getFullName());
            appointment.setDate(datePicker.getValue());
            appointment.setStartTime(startTimeComboBox.getValue());
            appointment.setEndTime(endTimeComboBox.getValue());
            appointment.setReason(reasonField.getText().trim());
            appointment.setNotes(notesArea.getText().trim());
            appointment.setStatus(statusComboBox.getValue());
            
            boolean success;
            if (isEditMode) {
                // Update existing appointment
                appointment.updateTimestamp();
                success = appointmentDAO.updateAppointment(appointment);
                if (success) {
                    AlertUtil.showInformation("Success", "Appointment Updated", 
                        "Appointment has been updated successfully.");
                }
            } else {
                // Create new appointment
                success = appointmentDAO.createAppointment(appointment);
                if (success) {
                    AlertUtil.showInformation("Success", "Appointment Created", 
                        "New appointment has been created successfully.");
                }
            }
            
            if (success) {
                // Close the dialog
                closeDialog();
            } else {
                AlertUtil.showError("Error", "Operation Failed", 
                    "Could not save appointment information. Please try again.");
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
        if (!ValidationUtil.validateRequired(patientComboBox, "Patient")) return false;
        if (!ValidationUtil.validateRequired(doctorComboBox, "Doctor")) return false;
        if (!ValidationUtil.validateRequired(datePicker, "Date")) return false;
        if (!ValidationUtil.validateRequired(startTimeComboBox, "Start Time")) return false;
        if (!ValidationUtil.validateRequired(endTimeComboBox, "End Time")) return false;
        if (!ValidationUtil.validateRequired(reasonField, "Reason")) return false;
        if (!ValidationUtil.validateRequired(statusComboBox, "Status")) return false;
        
        // Validate that end time is after start time
        LocalTime startTime = startTimeComboBox.getValue();
        LocalTime endTime = endTimeComboBox.getValue();
        
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            AlertUtil.showError("Validation Error", "Invalid Time Range", 
                "End time must be after start time.");
            return false;
        }
        
        // Check for scheduling conflicts
        if (!isEditMode || (isEditMode && !appointment.getStatus().equals(statusComboBox.getValue()))) {
            List<Appointment> conflictingAppointments = appointmentDAO.getConflictingAppointments(
                doctorComboBox.getValue().getDoctorId(),
                datePicker.getValue(),
                startTimeComboBox.getValue(),
                endTimeComboBox.getValue(),
                isEditMode ? appointment.getAppointmentId() : 0
            );
            
            if (!conflictingAppointments.isEmpty()) {
                AlertUtil.showError("Scheduling Conflict", "Time Slot Not Available", 
                    "The selected doctor already has an appointment during this time slot.");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if there are unsaved changes in the form
     * @return true if there are unsaved changes, false otherwise
     */
    private boolean hasUnsavedChanges() {
        if (isEditMode) {
            // Check if any field is different from the original appointment data
            return patientComboBox.getValue().getPatientId() != appointment.getPatientId() ||
                   doctorComboBox.getValue().getDoctorId() != appointment.getDoctorId() ||
                   !datePicker.getValue().equals(appointment.getDate()) ||
                   !startTimeComboBox.getValue().equals(appointment.getStartTime()) ||
                   !endTimeComboBox.getValue().equals(appointment.getEndTime()) ||
                   !reasonField.getText().equals(appointment.getReason()) ||
                   !notesArea.getText().equals(appointment.getNotes()) ||
                   !statusComboBox.getValue().equals(appointment.getStatus());
        } else {
            // Check if any field has been filled
            return patientComboBox.getValue() != null ||
                   doctorComboBox.getValue() != null ||
                   datePicker.getValue() != null ||
                   startTimeComboBox.getValue() != null ||
                   endTimeComboBox.getValue() != null ||
                   !reasonField.getText().isEmpty() ||
                   !notesArea.getText().isEmpty();
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
