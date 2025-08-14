package com.patientmanagement.model;

import java.time.LocalDate;

public class Doctor {
    private int doctorId;
    private int userId;
    private String specialty;
    private String licenseNumber;
    private String availabilitySchedule;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    
    // User information (from User table)
    private String firstName;
    private String lastName;
    private String email;
    
    // Default constructor
    public Doctor() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    // Constructor with fields
    public Doctor(int doctorId, int userId, String specialty, String licenseNumber, 
                 String availabilitySchedule, String firstName, String lastName, String email) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.availabilitySchedule = availabilitySchedule;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }
    
    public void setAvailabilitySchedule(String availabilitySchedule) {
        this.availabilitySchedule = availabilitySchedule;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void updateTimestamp() {
        this.updatedAt = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}
