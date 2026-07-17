package com.novabank.backend.dto;

import com.novabank.backend.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object representing serialized address details in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private UUID id;
    private AddressType addressType;
    private String houseNumber;
    private String street;
    private String city;
    private String district;
    private String state;
    private String country;
    private String postalCode;
}
