package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing debit and virtual cards statistics.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardAnalyticsResponse {

    private long physicalCards;
    private long virtualCards;
    private long blockedCards;
    private long expiredCards;
}
