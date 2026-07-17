package com.novabank.backend.service;

/**
 * Service interface for generating unique bank account numbers and IFSC identifiers.
 *
 * @author Senior Java Backend Architect
 */
public interface AccountNumberGeneratorService {

    /**
     * Generates a unique, sequential bank account number in the format NB1XXXXXXXX.
     * Guaranteed to be thread-safe and non-overlapping.
     *
     * @return unique account number string
     */
    String generateAccountNumber();

    /**
     * Generates the Indian Financial System Code (IFSC) based on branch code.
     *
     * @param branchCode code representing bank branch location
     * @return 11-digit alphanumeric IFSC code
     */
    String generateIfscCode(String branchCode);
}
