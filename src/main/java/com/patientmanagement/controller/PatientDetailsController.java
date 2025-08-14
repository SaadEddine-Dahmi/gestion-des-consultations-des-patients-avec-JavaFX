package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.MedicalRecord;
import com.patientmanagement.model.Patient;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.AppointmentDAO;
import com.patientmanagement.model.dao.MedicalRecordDAO;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class PatientDetailsController {
    
    @FXML
    private Label patientNameLabel;
    
    @FXML
    private Label patientIdLabel;
    
    @FXML
    private Label firstNameLabel;
    
    @FXML
    private Label lastNameLabel;
    
    @FXML
    private Label dobLabel;
    
    @FXML
    private Label ageLabel;
    
    @FXML
    private Label genderLabel;
    
    @FXML
    private Label addressLabel;
    
    @FXML
    private Label phoneLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Label emergencyContactLabel;
    
    @FXML
    private Label insuranceInfoLabel;
    
    @FXML
    private TableView<Appointment> appointmentsTable;
    
    @FXML
    private TableColumn<Appointment, String> appointmentDateColumn;
    
    @FXML
    private TableColumn<Appointment, String> appointmentTimeColumn;
    
    @FXML
    private TableColumn<Appointment, String> doctorColumn;
    
    @FXML
    private TableColumn<Appointment, String> reasonColumn;
    
    @FXML
    private TableColumn<Appointment, String> statusColumn;
    
    @FXML
    private TableColumn<Appointment, Void> appointmentActionsColumn;
    
    @FXML
    private TableView<MedicalRecord> medicalRecordsTable;
    
    @FXML
    private TableColumn<MedicalRecord, String> recordDateColumn;
    
    @FXML
    private TableColumn<MedicalRecord, String> doctorNameColumn;
    
    @FXML
    private TableColumn<MedicalRecord, String> diagnosisColumn;
    
    @FXML
    private TableColumn<MedicalRecord, Void> recordActionsColumn;
    
    private Patient patient;
    private User currentUser;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private MedicalRecordDAO medicalRecordDAO;
    
    @FXML
    private void initialize() {
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        medicalRecordDAO = new MedicalRecordDAO();
        
        // Initialize appointments table columns
        appointmentDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatDate(cellData.getValue().getDate())));
        
        appointmentTimeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatTime(cellData.getValue().getStartTime())));
        
        doctorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDoctorName()));
        
        reasonColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReason()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        
        // Initialize medical records table columns
        recordDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatDate(cellData.getValue().getAppointmentDate())));
        
        doctorNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDoctorName()));
        
        diagnosisColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDiagnosis()));
        
        // Set up action columns
        setupAppointmentActionsColumn();
        setupRecordActionsColumn();
    }
    
    /**
     * Initialize the controller with patient and user data
     * @param patient The patient to display
     * @param user The logged-in user
     */
    public void initData(Patient patient, User user) {
        this.patient = patient;
        this.currentUser = user;
        
        // Set patient information
        patientNameLabel.setText(patient.getFullName());
        patientIdLabel.setText("ID: " + patient.getPatientId());
        firstNameLabel.setText(patient.getFirstName());
        lastNameLabel.setText(patient.getLastName());
        dobLabel.setText(DateTimeUtil.formatDate(patient.getDateOfBirth()));
        ageLabel.setText(String.valueOf(DateTimeUtil.calculateAge(patient.getDateOfBirth())));
        genderLabel.setText(patient.getGender());
        addressLabel.setText(patient.getAddress());
        phoneLabel.setText(patient.getPhone());
        emailLabel.setText(patient.getEmail());
        emergencyContactLabel.setText(patient.getEmergencyContact());
        insuranceInfoLabel.setText(patient.getInsuranceInfo());
        
        // Load appointments and medical records
        loadAppointments();
        loadMedicalRecords();
    }
    
    /**
     * Load appointments for this patient
     */
    private void loadAppointments() {
        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patient.getPatientId());
        appointmentsTable.setItems(FXCollections.observableArrayList(appointments));
    }
    
    /**
     * Load medical records for this patient
     */
    private void loadMedicalRecords() {
        List<MedicalRecord> records = medicalRecordDAO.getMedicalRecordsByPatient(patient.getPatientId());
        medicalRecordsTable.setItems(FXCollections.observableArrayList(records));
    }
    
    /**
     * Set up the actions column in the appointments table
     */
    private void setupAppointmentActionsColumn() {
        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final Button editBtn = new Button("Edit");
                    private final Button cancelBtn = new Button("Cancel");
                    private final HBox pane = new HBox(5, viewBtn, editBtn, cancelBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            handleViewAppointment(appointment);
                        });
                        
                        editBtn.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            handleEditAppointment(appointment);
                        });
                        
                        cancelBtn.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            handleCancelAppointment(appointment);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            
                            // Disable cancel button if appointment is not scheduled
                            cancelBtn.setDisable(!appointment.isScheduled());
                            
                            // Disable edit button if appointment is completed or cancelled
                            editBtn.setDisable(!appointment.isScheduled());
                            
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        
        appointmentActionsColumn.setCellFactory(cellFactory);
    }
    
    /**
     * Set up the actions column in the medical records table
     */
    private void setupRecordActionsColumn() {
        Callback<TableColumn<MedicalRecord, Void>, TableCell<MedicalRecord, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MedicalRecord, Void> call(final TableColumn<MedicalRecord, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final HBox pane = new HBox(5, viewBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            MedicalRecord record = getTableView().getItems().get(getIndex());
                            handleViewMedicalRecord(record);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        
        recordActionsColumn.setCellFactory(cellFactory);
    }
    
    @FXML
    private void handleEditPatient(ActionEvent event) {
        try {
            // Load the patient dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patientDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for editing
            PatientDialogController dialogController = loader.getController();
            dialogController.initData(patient);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Patient - " + patient.getFullName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientNameLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh patient data
            Patient refreshedPatient = patientDAO.getPatientById(patient.getPatientId());
            if (refreshedPatient != null) {
                initData(refreshedPatient, currentUser);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the patient dialog. Please try again.");
        }
    }
    
    @FXML
    private void handleNewAppointment(ActionEvent event) {
        try {
            // Load the appointment dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for a new appointment
            AppointmentDialogController dialogController = loader.getController();
            dialogController.initData(currentUser, patient);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Appointment - " + patient.getFullName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientNameLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh appointments
            loadAppointments();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the appointment dialog. Please try again.");
        }
    }
    
    private void handleViewAppointment(Appointment appointment) {
        try {
            // Load the appointment details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDetails.fxml"));
            Parent detailsRoot = loader.load();
            
            // Get the controller and pass the appointment
            AppointmentDetailsController detailsController = loader.getController();
            detailsController.initData(appointment, currentUser);
            
            // Create a new stage for the details view
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Appointment Details");
            detailsStage.initModality(Modality.WINDOW_MODAL);
            detailsStage.initOwner(patientNameLabel.getScene().getWindow());
            detailsStage.setScene(new Scene(detailsRoot));
            
            // Show the details view
            detailsStage.showAndWait();
            
            // Refresh data after viewing (in case changes were made)
            loadAppointments();
            loadMedicalRecords();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "View Error", 
                "Could not open the appointment details. Please try again.");
        }
    }
    
    private void handleEditAppointment(Appointment appointment) {
        try {
            // Load the appointment dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for editing
            AppointmentDialogController dialogController = loader.getController();
            dialogController.initData(currentUser, patient, appointment);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Appointment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientNameLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh appointments
            loadAppointments();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the appointment dialog. Please try again.");
        }
    }
    
    private void handleCancelAppointment(Appointment appointment) {
        if (AlertUtil.showConfirmation("Cancel Appointment", "Confirm Cancellation", 
                "Are you sure you want to cancel this appointment?")) {
            
            appointment.markAsCancelled();
            boolean updated = appointmentDAO.updateAppointment(appointment);
            
            if (updated) {
                AlertUtil.showInformation("Success", "Appointment Cancelled", 
                    "The appointment has been cancelled successfully.");
                loadAppointments();
            } else {
                AlertUtil.showError("Error", "Cancellation Failed", 
                    "Could not cancel the appointment. Please try again.");
            }
        }
    }
    
    private void handleViewMedicalRecord(MedicalRecord record) {
        try {
            // Load the medical record details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/medicalRecordDetails.fxml"));
            Parent detailsRoot = loader.load();
            
            // Get the controller and pass the record
            MedicalRecordDetailsController detailsController = loader.getController();
            detailsController.initData(record);
            
            // Create a new stage for the details view
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Medical Record Details");
            detailsStage.initModality(Modality.WINDOW_MODAL);
            detailsStage.initOwner(patientNameLabel.getScene().getWindow());
            detailsStage.setScene(new Scene(detailsRoot));
            
            // Show the details view
            detailsStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "View Error", 
                "Could not open the medical record details. Please try again.");
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) patientNameLabel.getScene().getWindow();
        stage.close();
    }
}
