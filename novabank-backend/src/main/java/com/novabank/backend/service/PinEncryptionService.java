package com.novabank.backend.service;

/**
 * Service interface defining operations related to security hashing and verification of card PINs.
 *
 * @author Senior Java Backend Architect
 */
public interface PinEncryptionService {

    /**
     * Hashes a raw PIN code.
     *
     * @param pin 4-digit raw PIN code
     * @return BCrypt encrypted hash
     */
    String encryptPin(String pin);

    /**
     * Checks if a raw input PIN matches the stored encrypted hash.
     *
     * @param rawPin input PIN
     * @param encryptedPin stored hash
     * @return true if matches, false otherwise
     */
    boolean matches(String rawPin, String encryptedPin);
}
