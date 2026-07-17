package com.novabank.backend.dto;

import com.novabank.backend.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing address creation or modification requests.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    @NotBlank(message = "House number is required")
    private String houseNumber;

    @NotBlank(message = "Street name is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    private String district;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;
}
