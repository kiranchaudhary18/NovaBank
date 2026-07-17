package com.novabank.backend.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for programmatic validation of beans.
 * Allows manually triggering validation of entities or DTOs outside of controller endpoints.
 *
 * @author Senior Java Backend Architect
 */
public final class ValidationUtil {

    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    private ValidationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Programmatically validates an object against its JSR-303 constraints.
     * Throws an {@link IllegalArgumentException} if violations are found.
     *
     * @param object the object to validate
     * @param groups the validation groups to apply
     * @param <T> the type of object being validated
     * @throws IllegalArgumentException if the object is invalid
     */
    public static <T> void validate(T object, Class<?>... groups) {
        if (object == null) {
            throw new IllegalArgumentException("Object to validate must not be null");
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errorMsg);
        }
    }

    /**
     * Programmatically validates an object against its JSR-303 constraints and returns the violation set.
     *
     * @param object the object to validate
     * @param groups the validation groups to apply
     * @param <T> the type of object being validated
     * @return set of constraint violations (empty if valid)
     */
    public static <T> Set<ConstraintViolation<T>> getViolations(T object, Class<?>... groups) {
        if (object == null) {
            throw new IllegalArgumentException("Object to validate must not be null");
        }
        return VALIDATOR.validate(object, groups);
    }
}
