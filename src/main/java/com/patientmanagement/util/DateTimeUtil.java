package com.patientmanagement.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Format a LocalDate to string
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Format a LocalTime to string
     * @param time The time to format
     * @return Formatted time string
     */
    public static String formatTime(LocalTime time) {
        if (time == null) return "";
        return TIME_FORMATTER.format(time);
    }
    
    /**
     * Format a LocalDateTime to string
     * @param dateTime The date time to format
     * @return Formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return DATETIME_FORMATTER.format(dateTime);
    }
    
    /**
     * Parse a string to LocalDate
     * @param dateStr The date string to parse
     * @return Parsed LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    /**
     * Parse a string to LocalTime
     * @param timeStr The time string to parse
     * @return Parsed LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }
    
    /**
     * Parse a string to LocalDateTime
     * @param dateTimeStr The date time string to parse
     * @return Parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    /**
     * Get the current date
     * @return Current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Get the current time
     * @return Current time
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }
    
    /**
     * Get the current date and time
     * @return Current date and time
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
