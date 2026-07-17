package com.novabank.backend.dto;

import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized saved beneficiary details in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {

    private UUID id;
    private UUID customerId;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankName;
    private String beneficiaryIfscCode;
    private String nickname;
    private RelationshipType relationship;
    private boolean isFavorite;
    private BeneficiaryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
