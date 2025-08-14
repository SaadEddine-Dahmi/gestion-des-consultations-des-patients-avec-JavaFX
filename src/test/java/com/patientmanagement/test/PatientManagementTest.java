package com.patientmanagement.test;

import com.patientmanagement.model.Patient;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.DatabaseUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Test class for the patient management system
 */
public class PatientManagementTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Patient Management System Test");
        System.out.println("======================================");
        
        // Initialize database
        DatabaseUtil.initializeDatabase();
        
        // Test database connection
        boolean connectionSuccess = DatabaseUtil.testConnection();
        System.out.println("Database Connection Test: " + (connectionSuccess ? "PASSED" : "FAILED"));
        
        if (connectionSuccess) {
            // Test patient management operations
            testPatientManagement();
        }
    }
    
    private static void testPatientManagement() {
        PatientDAO patientDAO = new PatientDAO();
        
        // Test creating a new patient
        Patient newPatient = new Patient();
        newPatient.setFirstName("John");
        newPatient.setLastName("Doe");
        newPatient.setDateOfBirth(LocalDate.of(1980, 1, 15));
        newPatient.setGender("Male");
        newPatient.setAddress("123 Main St, Anytown, USA");
        newPatient.setPhone("555-123-4567");
        newPatient.setEmail("john.doe@example.com");
        newPatient.setEmergencyContact("Jane Doe: 555-987-6543");
        newPatient.setInsuranceInfo("HealthPlus Insurance #12345678");
        
        boolean patientCreated = patientDAO.createPatient(newPatient);
        System.out.println("Patient Creation Test: " + (patientCreated ? "PASSED" : "FAILED"));
        
        if (patientCreated && newPatient.getPatientId() > 0) {
            int patientId = newPatient.getPatientId();
            System.out.println("Created Patient ID: " + patientId);
            
            // Test retrieving patient by ID
            Patient retrievedPatient = patientDAO.getPatientById(patientId);
            boolean retrieveTest = retrievedPatient != null && 
                                  retrievedPatient.getFirstName().equals("John") &&
                                  retrievedPatient.getLastName().equals("Doe");
            System.out.println("Patient Retrieval Test: " + (retrieveTest ? "PASSED" : "FAILED"));
            
            if (retrieveTest) {
                System.out.println("Retrieved Patient Details:");
                System.out.println("  ID: " + retrievedPatient.getPatientId());
                System.out.println("  Name: " + retrievedPatient.getFullName());
                System.out.println("  DOB: " + retrievedPatient.getDateOfBirth());
                System.out.println("  Gender: " + retrievedPatient.getGender());
            }
            
            // Test updating patient
            retrievedPatient.setPhone("555-999-8888");
            retrievedPatient.setEmail("john.updated@example.com");
            boolean updateTest = patientDAO.updatePatient(retrievedPatient);
            System.out.println("Patient Update Test: " + (updateTest ? "PASSED" : "FAILED"));
            
            if (updateTest) {
                // Verify update
                Patient updatedPatient = patientDAO.getPatientById(patientId);
                boolean verifyUpdate = updatedPatient != null && 
                                      updatedPatient.getPhone().equals("555-999-8888") &&
                                      updatedPatient.getEmail().equals("john.updated@example.com");
                System.out.println("Update Verification Test: " + (verifyUpdate ? "PASSED" : "FAILED"));
            }
            
            // Test getting all patients
            List<Patient> allPatients = patientDAO.getAllPatients();
            boolean getAllTest = allPatients != null && !allPatients.isEmpty();
            System.out.println("Get All Patients Test: " + (getAllTest ? "PASSED" : "FAILED"));
            
            if (getAllTest) {
                System.out.println("Total Patients: " + allPatients.size());
            }
            
            // Test searching patients by name
            List<Patient> searchResults = patientDAO.searchPatientsByName("John");
            boolean searchTest = searchResults != null && !searchResults.isEmpty();
            System.out.println("Patient Search Test: " + (searchTest ? "PASSED" : "FAILED"));
            
            if (searchTest) {
                System.out.println("Search Results Count: " + searchResults.size());
            }
            
            // Clean up - delete test patient
            boolean patientDeleted = patientDAO.deletePatient(patientId);
            System.out.println("Patient Deletion Test: " + (patientDeleted ? "PASSED" : "FAILED"));
            
            // Verify deletion
            Patient deletedPatient = patientDAO.getPatientById(patientId);
            boolean verifyDeletion = deletedPatient == null;
            System.out.println("Deletion Verification Test: " + (verifyDeletion ? "PASSED" : "FAILED"));
        }
        
        System.out.println("Patient Management System Test Completed");
    }
}
