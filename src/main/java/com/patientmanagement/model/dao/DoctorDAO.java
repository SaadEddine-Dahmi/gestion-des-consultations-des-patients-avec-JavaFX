package com.patientmanagement.model.dao;

import com.patientmanagement.model.Doctor;
import com.patientmanagement.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    // Create a new doctor
    public boolean createDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, specialty, license_number, availability_schedule, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, doctor.getUserId());
            pstmt.setString(2, doctor.getSpecialty());
            pstmt.setString(3, doctor.getLicenseNumber());
            pstmt.setString(4, doctor.getAvailabilitySchedule());
            pstmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setDoctorId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get a doctor by ID
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.doctor_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDoctorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all doctors
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "ORDER BY u.last_name, u.first_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
    
    // Update a doctor
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET specialty = ?, license_number = ?, " +
                     "availability_schedule = ?, updated_at = ? WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getSpecialty());
            pstmt.setString(2, doctor.getLicenseNumber());
            pstmt.setString(3, doctor.getAvailabilitySchedule());
            pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setInt(5, doctor.getDoctorId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a doctor
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get doctors by specialty
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.first_name, u.last_name, u.email " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.specialty = ? " +
                     "ORDER BY u.last_name, u.first_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialty);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
    
    // Helper method to extract a Doctor from a ResultSet
    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setSpecialty(rs.getString("specialty"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setAvailabilitySchedule(rs.getString("availability_schedule"));
        doctor.setFirstName(rs.getString("first_name"));
        doctor.setLastName(rs.getString("last_name"));
        doctor.setEmail(rs.getString("email"));
        doctor.setCreatedAt(rs.getDate("created_at").toLocalDate());
        doctor.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        return doctor;
    }
}
