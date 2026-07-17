package com.novabank.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to verify that a date (typically dateOfBirth)
 * corresponds to an age of at least 18 years.
 *
 * @author Senior Java Backend Architect
 */
@Documented
@Constraint(validatedBy = ValidAgeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {

    /**
     * Default error message when age is less than 18.
     *
     * @return error message string
     */
    String message() default "Customer must be at least 18 years of age.";

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
