package com.novabank.backend.service.impl;

import com.novabank.backend.service.PinEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for secure card PIN hashing and matching.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PinEncryptionServiceImpl implements PinEncryptionService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encryptPin(String pin) {
        log.debug("Encrypting card PIN using configured BCryptPasswordEncoder");
        return passwordEncoder.encode(pin);
    }

    @Override
    public boolean matches(String rawPin, String encryptedPin) {
        return passwordEncoder.matches(rawPin, encryptedPin);
    }
}
