package com.novabank.backend.event;

import com.novabank.backend.entity.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Event representing completed core banking transactions.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class TransactionCompletedEvent {
    private final Transaction transaction;
}
