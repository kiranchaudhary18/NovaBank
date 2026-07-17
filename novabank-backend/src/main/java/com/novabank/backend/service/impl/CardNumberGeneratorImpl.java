package com.novabank.backend.service.impl;

import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.service.CardNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service implementation for generating Luhn-compliant card numbers, CVVs, and masking patterns.
 *
 * @author Senior Java Backend Architect
 */
@Service
@Slf4j
public class CardNumberGeneratorImpl implements CardNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generateCardNumber(CardNetwork network) {
        log.debug("Generating realistic 16-digit card number for network: {}", network);
        String prefix;
        if (network == CardNetwork.VISA) {
            prefix = "4539";
        } else if (network == CardNetwork.MASTERCARD) {
            prefix = "5398";
        } else {
            prefix = "6071"; // RuPay BIN prefix
        }

        StringBuilder builder = new StringBuilder(prefix);
        // Generate next 11 random digits
        for (int i = 0; i < 11; i++) {
            builder.append(RANDOM.nextInt(10));
        }

        String first15 = builder.toString();
        int checkDigit = calculateLuhnCheckDigit(first15);
        return first15 + checkDigit;
    }

    @Override
    public String generateMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return "**** **** **** ****";
        }
        return cardNumber.substring(0, 4) + " **** **** " + cardNumber.substring(12);
    }

    @Override
    public String generateCvv() {
        int code = RANDOM.nextInt(1000);
        return String.format("%03d", code);
    }

    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        int remainder = sum % 10;
        return (remainder == 0) ? 0 : 10 - remainder;
    }
}
