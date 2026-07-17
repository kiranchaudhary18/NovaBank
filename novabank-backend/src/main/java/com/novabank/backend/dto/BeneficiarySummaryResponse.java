package com.novabank.backend.dto;

import com.novabank.backend.enums.BeneficiaryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object representing lightweight saved beneficiary summary info.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiarySummaryResponse {

    private UUID id;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankName;
    private String nickname;
    private boolean isFavorite;
    private BeneficiaryStatus status;
}
