package com.novabank.backend.event;

import com.novabank.backend.entity.User;
import com.novabank.backend.enums.KycStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Event representing KYC review outcomes.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class KycVerifiedEvent {
    private final User user;
    private final KycStatus status;
}
