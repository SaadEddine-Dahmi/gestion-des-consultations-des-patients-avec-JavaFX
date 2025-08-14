package com.patientmanagement.controller;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.AppointmentDAO;
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
import java.time.LocalDate;
import java.util.List;

public class AppointmentsController {
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<String> statusComboBox;
    
    @FXML
    private TableView<Appointment> appointmentsTable;
    
    @FXML
    private TableColumn<Appointment, Integer> idColumn;
    
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
    
    @FXML
    private TableColumn<Appointment, Void> actionsColumn;
    
    @FXML
    private Label totalAppointmentsLabel;
    
    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private ObservableList<Appointment> appointmentsList;
    
    @FXML
    private void initialize() {
        appointmentDAO = new AppointmentDAO();
        
        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
            "All", "Scheduled", "Completed", "Cancelled"));
        statusComboBox.setValue("All");
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        
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
        
        // Set up the actions column with buttons
        setupActionsColumn();
        
        // Load all appointments
        loadAppointments();
    }
    
    /**
     * Initialize the controller with user data
     * @param user The logged-in user
     */
    public void initData(User user) {
        this.currentUser = user;
        
        // If user is a doctor, filter appointments by doctor
        if (user.isDoctor()) {
            // This would require getting the doctor ID from the user ID
            // For simplicity, we'll just show all appointments for now
        }
    }
    
    /**
     * Load all appointments from the database
     */
    private void loadAppointments() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        appointmentsList = FXCollections.observableArrayList(appointments);
        appointmentsTable.setItems(appointmentsList);
        totalAppointmentsLabel.setText("Total Appointments: " + appointments.size());
    }
    
    /**
     * Set up the actions column with view, edit, and cancel buttons
     */
    private void setupActionsColumn() {
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
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    @FXML
    private void handleFilter(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        String status = statusComboBox.getValue();
        
        List<Appointment> filteredAppointments;
        
        if (date != null && !"All".equals(status)) {
            // Filter by both date and status
            filteredAppointments = appointmentDAO.getAppointmentsByDate(date);
            filteredAppointments.removeIf(appointment -> !appointment.getStatus().equalsIgnoreCase(status));
        } else if (date != null) {
            // Filter by date only
            filteredAppointments = appointmentDAO.getAppointmentsByDate(date);
        } else if (!"All".equals(status)) {
            // Filter by status only
            filteredAppointments = appointmentDAO.getAllAppointments();
            filteredAppointments.removeIf(appointment -> !appointment.getStatus().equalsIgnoreCase(status));
        } else {
            // No filters, show all
            filteredAppointments = appointmentDAO.getAllAppointments();
        }
        
        appointmentsList = FXCollections.observableArrayList(filteredAppointments);
        appointmentsTable.setItems(appointmentsList);
        totalAppointmentsLabel.setText("Filtered Appointments: " + filteredAppointments.size());
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        datePicker.setValue(null);
        statusComboBox.setValue("All");
        loadAppointments();
    }
    
    @FXML
    private void handleAddAppointment(ActionEvent event) {
        try {
            // Load the appointment dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/appointmentDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for adding a new appointment
            AppointmentDialogController dialogController = loader.getController();
            dialogController.initData(currentUser, null);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Appointment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh the appointments list
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
            detailsStage.initOwner(appointmentsTable.getScene().getWindow());
            detailsStage.setScene(new Scene(detailsRoot));
            
            // Show the details view
            detailsStage.showAndWait();
            
            // Refresh data after viewing (in case changes were made)
            loadAppointments();
            
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
            dialogController.initData(currentUser, null, appointment);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Appointment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentsTable.getScene().getWindow());
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
}
