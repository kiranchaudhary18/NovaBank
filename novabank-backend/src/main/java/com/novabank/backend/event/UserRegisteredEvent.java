package com.novabank.backend.event;

import com.novabank.backend.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Event representing successful user profile registrations.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class UserRegisteredEvent {
    private final User user;
}
