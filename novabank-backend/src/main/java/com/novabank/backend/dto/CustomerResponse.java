package com.novabank.backend.dto;

import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.Gender;
import com.novabank.backend.enums.KycStatus;
import com.novabank.backend.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized customer profiles in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private UUID id;
    private String customerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private MaritalStatus maritalStatus;
    private String occupation;
    private BigDecimal annualIncome;
    private String nationality;
    private String profilePhoto;
    private CustomerStatus status;
    private List<AddressResponse> addresses;
    private KycStatus kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
