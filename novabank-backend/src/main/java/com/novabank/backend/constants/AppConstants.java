package com.novabank.backend.constants;

/**
 * Global application constants.
 * This class is designed to prevent instantiation and hold core static configuration keys.
 *
 * @author Senior Java Backend Architect
 */
public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /** The official name of the application. */
    public static final String APPLICATION_NAME = "NovaBank Digital Banking Platform";

    /** Default API version prefixed for resources. */
    public static final String API_VERSION = "v1";

    /** Standard Date & Time formats across the application. */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Default pagination properties. */
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    /** Validation Regex Constants. */
    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$"; // E.164 phone number format standard
}
