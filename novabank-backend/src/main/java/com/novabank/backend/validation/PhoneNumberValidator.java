package com.novabank.backend.validation;

import com.novabank.backend.constants.AppConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for {@link PhoneNumber} annotation.
 * Matches string inputs against the E.164 phone number pattern.
 *
 * @author Senior Java Backend Architect
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile(AppConstants.PHONE_REGEX);

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // Initialization logic if required
    }

    /**
     * Checks if the phone number string matches the configured regular expression pattern.
     * Null values are considered valid; use {@code jakarta.validation.constraints.NotNull}
     * explicitly on the field to enforce non-null values.
     *
     * @param value the phone number to validate
     * @param context validation context
     * @return true if valid or null, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return PHONE_PATTERN.matcher(value).matches();
    }
}
