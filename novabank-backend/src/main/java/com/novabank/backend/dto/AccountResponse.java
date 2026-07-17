package com.novabank.backend.dto;

import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized bank account details in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private UUID id;
    private String accountNumber;
    private UUID customerId;
    private String customerName;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
    private String branchCode;
    private String ifscCode;
    private AccountStatus status;
    private LocalDate openedDate;
    private LocalDate closedDate;
    private boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
