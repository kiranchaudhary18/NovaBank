package com.novabank.backend.util;

import com.novabank.backend.constants.AppConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for common date and time operations, including parsing and formatting.
 * Designed with a private constructor to prevent instantiation.
 *
 * @author Senior Java Backend Architect
 */
public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.DATE_TIME_FORMAT);

    private DateUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats a LocalDate into a string using the default date format pattern.
     *
     * @param date the date to format
     * @return the formatted date string, or null if input is null
     */
    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Formats a LocalDateTime into a string using the default date-time format pattern.
     *
     * @param dateTime the date-time to format
     * @return the formatted date-time string, or null if input is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * Parses a date string using the default date format pattern into a LocalDate.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate
     * @throws java.time.format.DateTimeParseException if parsing fails
     */
    public static LocalDate parseDate(String dateStr) {
        return (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    /**
     * Parses a date-time string using the default date-time format pattern into a LocalDateTime.
     *
     * @param dateTimeStr the date-time string to parse
     * @return the parsed LocalDateTime
     * @throws java.time.format.DateTimeParseException if parsing fails
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return (dateTimeStr != null && !dateTimeStr.isBlank()) ? LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER) : null;
    }
}
