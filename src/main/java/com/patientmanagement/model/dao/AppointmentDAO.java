package com.patientmanagement.model.dao;

import com.patientmanagement.model.Appointment;
import com.patientmanagement.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    // Create a new appointment
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, start_time, end_time, " +
                     "status, reason, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setDate(3, java.sql.Date.valueOf(appointment.getDate()));
            pstmt.setTime(4, java.sql.Time.valueOf(appointment.getStartTime()));
            pstmt.setTime(5, java.sql.Time.valueOf(appointment.getEndTime()));
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setDate(9, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setDate(10, java.sql.Date.valueOf(LocalDate.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setAppointmentId(generatedKeys.getInt(1));
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
    
    // Get an appointment by ID
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE a.appointment_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "ORDER BY a.date, a.start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by date
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE a.date = ? " +
                     "ORDER BY a.start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by patient
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.date DESC, a.start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by doctor
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name || ' ' || p.last_name as patient_name, " +
                     "u.first_name || ' ' || u.last_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE a.doctor_id = ? " +
                     "ORDER BY a.date, a.start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Update an appointment
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, date = ?, " +
                     "start_time = ?, end_time = ?, status = ?, reason = ?, notes = ?, " +
                     "updated_at = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setDate(3, java.sql.Date.valueOf(appointment.getDate()));
            pstmt.setTime(4, java.sql.Time.valueOf(appointment.getStartTime()));
            pstmt.setTime(5, java.sql.Time.valueOf(appointment.getEndTime()));
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setDate(9, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setInt(10, appointment.getAppointmentId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete an appointment
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check for scheduling conflicts
    public boolean hasSchedulingConflict(int doctorId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer excludeAppointmentId) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                     "WHERE doctor_id = ? AND date = ? AND status != 'cancelled' " +
                     "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?) OR (start_time >= ? AND end_time <= ?))";
        
        if (excludeAppointmentId != null) {
            sql += " AND appointment_id != ?";
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setTime(3, java.sql.Time.valueOf(startTime));
            pstmt.setTime(4, java.sql.Time.valueOf(startTime));
            pstmt.setTime(5, java.sql.Time.valueOf(endTime));
            pstmt.setTime(6, java.sql.Time.valueOf(endTime));
            pstmt.setTime(7, java.sql.Time.valueOf(startTime));
            pstmt.setTime(8, java.sql.Time.valueOf(endTime));
            
            if (excludeAppointmentId != null) {
                pstmt.setInt(9, excludeAppointmentId);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Helper method to extract an Appointment from a ResultSet
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setDate(rs.getDate("date").toLocalDate());
        appointment.setStartTime(rs.getTime("start_time").toLocalTime());
        appointment.setEndTime(rs.getTime("end_time").toLocalTime());
        appointment.setStatus(rs.getString("status"));
        appointment.setReason(rs.getString("reason"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedAt(rs.getDate("created_at").toLocalDate());
        appointment.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        return appointment;
    }
}
