package com.patientmanagement.controller;

import com.patientmanagement.model.Patient;
import com.patientmanagement.model.User;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.AlertUtil;
import com.patientmanagement.util.DateTimeUtil;

import javafx.beans.property.SimpleIntegerProperty;
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

public class PatientsController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Patient> patientsTable;
    
    @FXML
    private TableColumn<Patient, Integer> idColumn;
    
    @FXML
    private TableColumn<Patient, String> nameColumn;
    
    @FXML
    private TableColumn<Patient, String> dobColumn;
    
    @FXML
    private TableColumn<Patient, String> genderColumn;
    
    @FXML
    private TableColumn<Patient, String> phoneColumn;
    
    @FXML
    private TableColumn<Patient, String> emailColumn;
    
    @FXML
    private TableColumn<Patient, Void> actionsColumn;
    
    @FXML
    private Label totalPatientsLabel;
    
    private User currentUser;
    private PatientDAO patientDAO;
    private ObservableList<Patient> patientsList;
    
    @FXML
    private void initialize() {
        patientDAO = new PatientDAO();
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));
        
        dobColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DateTimeUtil.formatDate(cellData.getValue().getDateOfBirth())));
        
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Set up the actions column with buttons
        setupActionsColumn();
        
        // Load all patients
        loadPatients();
    }
    
    /**
     * Initialize the controller with user data
     * @param user The logged-in user
     */
    public void initData(User user) {
        this.currentUser = user;
    }
    
    /**
     * Load all patients from the database
     */
    private void loadPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        patientsList = FXCollections.observableArrayList(patients);
        patientsTable.setItems(patientsList);
        totalPatientsLabel.setText("Total Patients: " + patients.size());
    }
    
    /**
     * Set up the actions column with view, edit, and delete buttons
     */
    private void setupActionsColumn() {
        Callback<TableColumn<Patient, Void>, TableCell<Patient, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Patient, Void> call(final TableColumn<Patient, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox pane = new HBox(5, viewBtn, editBtn, deleteBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            handleViewPatient(patient);
                        });
                        
                        editBtn.setOnAction(event -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            handleEditPatient(patient);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            handleDeletePatient(patient);
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
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadPatients();
        } else {
            List<Patient> searchResults = patientDAO.searchPatientsByName(searchTerm);
            patientsList = FXCollections.observableArrayList(searchResults);
            patientsTable.setItems(patientsList);
            totalPatientsLabel.setText("Search Results: " + searchResults.size());
        }
    }
    
    @FXML
    private void handleAddPatient(ActionEvent event) {
        try {
            // Load the patient dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patientDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for adding a new patient
            PatientDialogController dialogController = loader.getController();
            dialogController.initData(null);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Patient");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh the patients list
            loadPatients();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the patient dialog. Please try again.");
        }
    }
    
    private void handleViewPatient(Patient patient) {
        try {
            // Load the patient details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patientDetails.fxml"));
            Parent detailsRoot = loader.load();
            
            // Get the controller and pass the patient
            PatientDetailsController detailsController = loader.getController();
            detailsController.initData(patient, currentUser);
            
            // Create a new stage for the details view
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Patient Details - " + patient.getFullName());
            detailsStage.initModality(Modality.WINDOW_MODAL);
            detailsStage.initOwner(patientsTable.getScene().getWindow());
            detailsStage.setScene(new Scene(detailsRoot));
            
            // Show the details view
            detailsStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "View Error", 
                "Could not open the patient details. Please try again.");
        }
    }
    
    private void handleEditPatient(Patient patient) {
        try {
            // Load the patient dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/patientmanagement/view/patientDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            // Get the controller and set it up for editing an existing patient
            PatientDialogController dialogController = loader.getController();
            dialogController.initData(patient);
            
            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Patient - " + patient.getFullName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(patientsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh the patients list
            loadPatients();
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Dialog Error", 
                "Could not open the patient dialog. Please try again.");
        }
    }
    
    private void handleDeletePatient(Patient patient) {
        if (AlertUtil.showConfirmation("Delete Patient", "Confirm Deletion", 
                "Are you sure you want to delete patient " + patient.getFullName() + "?\n" +
                "This will also delete all appointments and medical records for this patient.")) {
            
            boolean deleted = patientDAO.deletePatient(patient.getPatientId());
            
            if (deleted) {
                AlertUtil.showInformation("Success", "Patient Deleted", 
                    "Patient " + patient.getFullName() + " has been deleted successfully.");
                loadPatients();
            } else {
                AlertUtil.showError("Error", "Deletion Failed", 
                    "Could not delete the patient. Please try again.");
            }
        }
    }
}
