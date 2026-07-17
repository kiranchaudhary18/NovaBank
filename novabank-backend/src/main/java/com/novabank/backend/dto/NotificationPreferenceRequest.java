package com.novabank.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing parameters to update user notification preferences.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceRequest {

    @NotNull(message = "Email enabled switch status is required")
    private Boolean emailEnabled;

    @NotNull(message = "In-App enabled switch status is required")
    private Boolean inAppEnabled;
}
