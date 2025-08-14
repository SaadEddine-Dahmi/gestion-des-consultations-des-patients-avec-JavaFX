package com.patientmanagement.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status; // scheduled, completed, cancelled
    private String reason;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    
    // Additional fields for display purposes
    private String patientName;
    private String doctorName;
    
    // Default constructor
    public Appointment() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.status = "scheduled";
    }
    
    // Constructor with fields
    public Appointment(int appointmentId, int patientId, int doctorId, LocalDate date, 
                      LocalTime startTime, LocalTime endTime, String status, 
                      String reason, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.reason = reason;
        this.notes = notes;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    // Helper methods
    public void updateTimestamp() {
        this.updatedAt = LocalDate.now();
    }
    
    public void markAsCompleted() {
        this.status = "completed";
        updateTimestamp();
    }
    
    public void markAsCancelled() {
        this.status = "cancelled";
        updateTimestamp();
    }
    
    public boolean isScheduled() {
        return "scheduled".equals(this.status);
    }
    
    public boolean isCompleted() {
        return "completed".equals(this.status);
    }
    
    public boolean isCancelled() {
        return "cancelled".equals(this.status);
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", date=" + date +
                ", startTime=" + startTime +
                ", status='" + status + '\'' +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                '}';
    }
}
