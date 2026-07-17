package com.novabank.backend.service;

import com.novabank.backend.enums.CardNetwork;

/**
 * Service interface generating unique card numbers, CVVs, and masks.
 *
 * @author Senior Java Backend Architect
 */
public interface CardNumberGenerator {

    /**
     * Generates a realistic, unique 16-digit card number matching network prefix prefixes and Luhn check algorithms.
     *
     * @param network CardNetwork (VISA, MASTERCARD, RUPAY)
     * @return 16-digit card number string
     */
    String generateCardNumber(CardNetwork network);

    /**
     * Generates a masked representation of the card number.
     * Example: 5398 **** **** 4589
     *
     * @param cardNumber 16-digit card number
     * @return masked card number string
     */
    String generateMaskedCardNumber(String cardNumber);

    /**
     * Generates a random 3-digit CVV code.
     *
     * @return 3-digit CVV string
     */
    String generateCvv();
}
