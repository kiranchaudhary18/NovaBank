package com.novabank.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

/**
 * Validator implementation for {@link ValidAge} annotation.
 * Calculates age based on the current date and birthdate.
 *
 * @author Senior Java Backend Architect
 */
public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        // Initialization if needed
    }

    /**
     * Checks if the date of birth corresponds to an age of at least 18 years.
     * Null values are considered valid (enforce non-null separately using @NotNull).
     *
     * @param value date of birth to validate
     * @param context validator context
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Period.between(value, LocalDate.now()).getYears() >= 18;
    }
}
