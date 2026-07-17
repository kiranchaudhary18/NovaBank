package com.novabank.backend.event;

import com.novabank.backend.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Spring Event representing approved credit loan structures.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class LoanApprovedEvent {
    private final User user;
    private final BigDecimal amount;
}
