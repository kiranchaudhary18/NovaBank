package com.novabank.backend.service;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Beneficiary;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;

import java.util.UUID;

/**
 * Service interface defining operations related to Beneficiary management.
 *
 * @author Senior Java Backend Architect
 */
public interface BeneficiaryService {

    /**
     * Registers a new saved money transfer beneficiary.
     * Enforces own-account checking, duplicate block checks, and IFSC formatting.
     *
     * @param user currently authenticated user (customer owner)
     * @param request creation parameters
     * @return BeneficiaryResponse details DTO
     */
    BeneficiaryResponse addBeneficiary(User user, CreateBeneficiaryRequest request);

    /**
     * Retrieves details of a saved beneficiary by ID.
     *
     * @param id beneficiary UUID
     * @return BeneficiaryResponse details DTO
     */
    BeneficiaryResponse getBeneficiaryById(UUID id);

    /**
     * Lists, searches, and filters beneficiaries dynamically.
     * Support paging and sorting.
     *
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy parameter key to sort by
     * @param sortDir sort direction (asc/desc)
     * @param status filter by status (optional)
     * @param isFavorite filter by favorite flag (optional)
     * @param relationship filter by RelationshipType (optional)
     * @param name search query partial match on beneficiary name (optional)
     * @param accountNumber search query match on account number (optional)
     * @param nickname search query partial match on nickname (optional)
     * @param customerId filter by customer owner ID (optional, mandatory for CUSTOMER roles)
     * @return PagedResponse containing matching BeneficiaryResponse list
     */
    PagedResponse<BeneficiaryResponse> searchBeneficiaries(
            int page, int size, String sortBy, String sortDir,
            BeneficiaryStatus status, Boolean isFavorite, RelationshipType relationship,
            String name, String accountNumber, String nickname, UUID customerId
    );

    /**
     * Modifies beneficiary properties (name, nickname, relationship, etc.).
     *
     * @param id beneficiary UUID to update
     * @param request modification parameters
     * @return updated BeneficiaryResponse details DTO
     */
    BeneficiaryResponse updateBeneficiary(UUID id, UpdateBeneficiaryRequest request);

    /**
     * Toggles the favorite flag of a saved beneficiary.
     *
     * @param id beneficiary UUID to update
     * @param isFavorite favorite state toggle
     * @return updated BeneficiaryResponse details DTO
     */
    BeneficiaryResponse toggleFavorite(UUID id, boolean isFavorite);

    /**
     * Modifies the operational state status of a saved beneficiary (e.g. block or activate).
     *
     * @param id beneficiary UUID to update
     * @param status target status (ACTIVE, BLOCKED, etc.)
     * @return updated BeneficiaryResponse details DTO
     */
    BeneficiaryResponse updateStatus(UUID id, BeneficiaryStatus status);

    /**
     * Soft deletes a saved beneficiary by changing status to DELETED.
     *
     * @param id beneficiary UUID to delete
     */
    void deleteBeneficiary(UUID id);

    /**
     * Helper to map Beneficiary entity details to BeneficiaryResponse DTO.
     *
     * @param beneficiary Beneficiary persistence entity
     * @return BeneficiaryResponse DTO representation
     */
    BeneficiaryResponse convertToBeneficiaryResponse(Beneficiary beneficiary);
}
