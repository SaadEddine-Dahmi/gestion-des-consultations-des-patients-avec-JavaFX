package com.patientmanagement.model.dao;

import com.patientmanagement.model.MedicalRecord;
import com.patientmanagement.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    
    // Create a new medical record
    public boolean createMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, appointment_id, symptoms, diagnosis, " +
                     "treatment, prescription, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, record.getPatientId());
            pstmt.setInt(2, record.getAppointmentId());
            pstmt.setString(3, record.getSymptoms());
            pstmt.setString(4, record.getDiagnosis());
            pstmt.setString(5, record.getTreatment());
            pstmt.setString(6, record.getPrescription());
            pstmt.setString(7, record.getNotes());
            pstmt.setDate(8, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setDate(9, java.sql.Date.valueOf(LocalDate.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setRecordId(generatedKeys.getInt(1));
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
    
    // Get a medical record by ID
    public MedicalRecord getMedicalRecordById(int recordId) {
        String sql = "SELECT mr.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name, a.date as appointment_date " +
                     "FROM medical_records mr " +
                     "JOIN patients p ON mr.patient_id = p.patient_id " +
                     "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE mr.record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMedicalRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get medical records by patient ID
    public List<MedicalRecord> getMedicalRecordsByPatient(int patientId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name, a.date as appointment_date " +
                     "FROM medical_records mr " +
                     "JOIN patients p ON mr.patient_id = p.patient_id " +
                     "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE mr.patient_id = ? " +
                     "ORDER BY a.date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(extractMedicalRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    // Get medical record by appointment ID
    public MedicalRecord getMedicalRecordByAppointment(int appointmentId) {
        String sql = "SELECT mr.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name, a.date as appointment_date " +
                     "FROM medical_records mr " +
                     "JOIN patients p ON mr.patient_id = p.patient_id " +
                     "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE mr.appointment_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMedicalRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Update a medical record
    public boolean updateMedicalRecord(MedicalRecord record) {
        String sql = "UPDATE medical_records SET symptoms = ?, diagnosis = ?, treatment = ?, " +
                     "prescription = ?, notes = ?, updated_at = ? WHERE record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, record.getSymptoms());
            pstmt.setString(2, record.getDiagnosis());
            pstmt.setString(3, record.getTreatment());
            pstmt.setString(4, record.getPrescription());
            pstmt.setString(5, record.getNotes());
            pstmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setInt(7, record.getRecordId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a medical record
    public boolean deleteMedicalRecord(int recordId) {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, recordId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to extract a MedicalRecord from a ResultSet
    private MedicalRecord extractMedicalRecordFromResultSet(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setAppointmentId(rs.getInt("appointment_id"));
        record.setSymptoms(rs.getString("symptoms"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setTreatment(rs.getString("treatment"));
        record.setPrescription(rs.getString("prescription"));
        record.setNotes(rs.getString("notes"));
        record.setCreatedAt(rs.getDate("created_at").toLocalDate());
        record.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        record.setPatientName(rs.getString("patient_name"));
        record.setDoctorName(rs.getString("doctor_name"));
        record.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        return record;
    }
}
