package com.patientmanagement.test;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.model.Doctor;
import com.patientmanagement.model.Patient;
import com.patientmanagement.model.dao.AppointmentDAO;
import com.patientmanagement.model.dao.DoctorDAO;
import com.patientmanagement.model.dao.PatientDAO;
import com.patientmanagement.util.DatabaseUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Test class for the appointment scheduling system
 */
public class AppointmentSchedulingTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Appointment Scheduling System Test");
        System.out.println("===========================================");
        
        // Initialize database
        DatabaseUtil.initializeDatabase();
        
        // Test database connection
        boolean connectionSuccess = DatabaseUtil.testConnection();
        System.out.println("Database Connection Test: " + (connectionSuccess ? "PASSED" : "FAILED"));
        
        if (connectionSuccess) {
            // Test appointment scheduling operations
            testAppointmentScheduling();
        }
    }
    
    private static void testAppointmentScheduling() {
        PatientDAO patientDAO = new PatientDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        
        // Create test patient
        Patient patient = new Patient();
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setDateOfBirth(LocalDate.of(1985, 5, 20));
        patient.setGender("Female");
        patient.setPhone("555-111-2222");
        patientDAO.createPatient(patient);
        System.out.println("Test Patient Created - ID: " + patient.getPatientId());
        
        // Create test doctor
        Doctor doctor = new Doctor();
        doctor.setFirstName("Dr. Robert");
        doctor.setLastName("Johnson");
        doctor.setSpecialization("General Medicine");
        doctor.setPhone("555-333-4444");
        doctor.setEmail("dr.johnson@example.com");
        doctorDAO.createDoctor(doctor);
        System.out.println("Test Doctor Created - ID: " + doctor.getDoctorId());
        
        // Test creating a new appointment
        Appointment newAppointment = new Appointment();
        newAppointment.setPatientId(patient.getPatientId());
        newAppointment.setPatientName(patient.getFullName());
        newAppointment.setDoctorId(doctor.getDoctorId());
        newAppointment.setDoctorName(doctor.getFullName());
        newAppointment.setDate(LocalDate.now().plusDays(1)); // Tomorrow
        newAppointment.setStartTime(LocalTime.of(9, 0)); // 9:00 AM
        newAppointment.setEndTime(LocalTime.of(9, 30)); // 9:30 AM
        newAppointment.setReason("Regular checkup");
        newAppointment.setNotes("First visit");
        newAppointment.setStatus("Scheduled");
        
        boolean appointmentCreated = appointmentDAO.createAppointment(newAppointment);
        System.out.println("Appointment Creation Test: " + (appointmentCreated ? "PASSED" : "FAILED"));
        
        if (appointmentCreated && newAppointment.getAppointmentId() > 0) {
            int appointmentId = newAppointment.getAppointmentId();
            System.out.println("Created Appointment ID: " + appointmentId);
            
            // Test retrieving appointment by ID
            Appointment retrievedAppointment = appointmentDAO.getAppointmentById(appointmentId);
            boolean retrieveTest = retrievedAppointment != null && 
                                  retrievedAppointment.getPatientId() == patient.getPatientId() &&
                                  retrievedAppointment.getDoctorId() == doctor.getDoctorId();
            System.out.println("Appointment Retrieval Test: " + (retrieveTest ? "PASSED" : "FAILED"));
            
            if (retrieveTest) {
                System.out.println("Retrieved Appointment Details:");
                System.out.println("  ID: " + retrievedAppointment.getAppointmentId());
                System.out.println("  Patient: " + retrievedAppointment.getPatientName());
                System.out.println("  Doctor: " + retrievedAppointment.getDoctorName());
                System.out.println("  Date: " + retrievedAppointment.getDate());
                System.out.println("  Time: " + retrievedAppointment.getStartTime() + " - " + retrievedAppointment.getEndTime());
                System.out.println("  Status: " + retrievedAppointment.getStatus());
            }
            
            // Test updating appointment
            retrievedAppointment.setReason("Follow-up checkup");
            retrievedAppointment.setNotes("Updated notes");
            boolean updateTest = appointmentDAO.updateAppointment(retrievedAppointment);
            System.out.println("Appointment Update Test: " + (updateTest ? "PASSED" : "FAILED"));
            
            if (updateTest) {
                // Verify update
                Appointment updatedAppointment = appointmentDAO.getAppointmentById(appointmentId);
                boolean verifyUpdate = updatedAppointment != null && 
                                      updatedAppointment.getReason().equals("Follow-up checkup") &&
                                      updatedAppointment.getNotes().equals("Updated notes");
                System.out.println("Update Verification Test: " + (verifyUpdate ? "PASSED" : "FAILED"));
            }
            
            // Test getting appointments by patient
            List<Appointment> patientAppointments = appointmentDAO.getAppointmentsByPatient(patient.getPatientId());
            boolean getByPatientTest = patientAppointments != null && !patientAppointments.isEmpty();
            System.out.println("Get Appointments By Patient Test: " + (getByPatientTest ? "PASSED" : "FAILED"));
            
            if (getByPatientTest) {
                System.out.println("Patient Appointments Count: " + patientAppointments.size());
            }
            
            // Test getting appointments by doctor
            List<Appointment> doctorAppointments = appointmentDAO.getAppointmentsByDoctor(doctor.getDoctorId());
            boolean getByDoctorTest = doctorAppointments != null && !doctorAppointments.isEmpty();
            System.out.println("Get Appointments By Doctor Test: " + (getByDoctorTest ? "PASSED" : "FAILED"));
            
            if (getByDoctorTest) {
                System.out.println("Doctor Appointments Count: " + doctorAppointments.size());
            }
            
            // Test getting appointments by date
            List<Appointment> dateAppointments = appointmentDAO.getAppointmentsByDate(LocalDate.now().plusDays(1));
            boolean getByDateTest = dateAppointments != null && !dateAppointments.isEmpty();
            System.out.println("Get Appointments By Date Test: " + (getByDateTest ? "PASSED" : "FAILED"));
            
            if (getByDateTest) {
                System.out.println("Date Appointments Count: " + dateAppointments.size());
            }
            
            // Test checking for conflicting appointments
            List<Appointment> conflictingAppointments = appointmentDAO.getConflictingAppointments(
                doctor.getDoctorId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                0 // Exclude no appointments
            );
            boolean conflictTest = conflictingAppointments != null && !conflictingAppointments.isEmpty();
            System.out.println("Conflict Detection Test: " + (conflictTest ? "PASSED" : "FAILED"));
            
            // Test marking appointment as completed
            retrievedAppointment.markAsCompleted();
            boolean markCompletedTest = appointmentDAO.updateAppointment(retrievedAppointment);
            System.out.println("Mark Appointment Completed Test: " + (markCompletedTest ? "PASSED" : "FAILED"));
            
            if (markCompletedTest) {
                // Verify status update
                Appointment completedAppointment = appointmentDAO.getAppointmentById(appointmentId);
                boolean verifyStatus = completedAppointment != null && 
                                      completedAppointment.getStatus().equals("Completed");
                System.out.println("Status Update Verification Test: " + (verifyStatus ? "PASSED" : "FAILED"));
            }
            
            // Clean up - delete test appointment
            boolean appointmentDeleted = appointmentDAO.deleteAppointment(appointmentId);
            System.out.println("Appointment Deletion Test: " + (appointmentDeleted ? "PASSED" : "FAILED"));
        }
        
        // Clean up - delete test doctor and patient
        doctorDAO.deleteDoctor(doctor.getDoctorId());
        patientDAO.deletePatient(patient.getPatientId());
        
        System.out.println("Appointment Scheduling System Test Completed");
    }
}
