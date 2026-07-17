package com.novabank.backend.event;

import com.novabank.backend.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Event representing user credential modifications.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@RequiredArgsConstructor
public class PasswordChangedEvent {
    private final User user;
}
