package com.novabank.backend.util;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Utility class generating unique, non-overlapping transaction IDs.
 *
 * @author Senior Java Backend Architect
 */
public final class TransactionIdGenerator {

    private static final String PREFIX = "TXN";
    private static final SecureRandom RANDOM = new SecureRandom();

    private TransactionIdGenerator() {
        throw new UnsupportedOperationException("Utility class and cannot be instantiated");
    }

    /**
     * Generates a unique transaction identifier.
     * Format: TXN + EpochSecond + 4-digit random number.
     *
     * @return unique transaction ID string
     */
    public static String generateId() {
        long epochSecond = Instant.now().getEpochSecond();
        int randomPart = 1000 + RANDOM.nextInt(9000); // 4 digit random
        return PREFIX + epochSecond + randomPart;
    }
}
