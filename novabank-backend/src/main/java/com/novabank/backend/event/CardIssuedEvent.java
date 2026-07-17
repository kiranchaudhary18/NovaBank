package com.novabank.backend.event;

import com.novabank.backend.entity.Card;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Event representing debit or virtual card issuance actions.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class CardIssuedEvent {
    private final Card card;
}
