package com.novabank.backend.util;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Utility class generating unique reference numbers for bank receipt correlation.
 *
 * @author Senior Java Backend Architect
 */
public final class ReferenceNumberGenerator {

    private static final String PREFIX = "REF";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceNumberGenerator() {
        throw new UnsupportedOperationException("Utility class and cannot be instantiated");
    }

    /**
     * Generates a unique transaction reference number.
     * Format: REF + EpochMillis + 4-digit random number.
     *
     * @return unique reference number string
     */
    public static String generateReference() {
        long epochMillis = Instant.now().toEpochMilli();
        int randomPart = 1000 + RANDOM.nextInt(9000); // 4 digit random
        return PREFIX + epochMillis + randomPart;
    }
}
