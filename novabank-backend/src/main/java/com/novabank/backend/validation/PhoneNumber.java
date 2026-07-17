package com.novabank.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for validating phone numbers.
 * Validates that the input follows international E.164 phone format standards.
 *
 * @author Senior Java Backend Architect
 */
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    /**
     * Default error message if the phone number is invalid.
     *
     * @return error message string
     */
    String message() default "Invalid phone number format. Must comply with E.164 international format (e.g., +1234567890).";

    /**
     * Standard JSR-303 validation groups.
     *
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Standard JSR-303 validation payload.
     *
     * @return validation payload
     */
    Class<? extends Payload>[] payload() default {};
}
