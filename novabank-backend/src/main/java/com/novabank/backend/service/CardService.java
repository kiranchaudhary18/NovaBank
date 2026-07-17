package com.novabank.backend.service;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining debit card management, status toggles, limits, and lookups.
 *
 * @author Senior Java Backend Architect
 */
public interface CardService {

    /**
     * Issues a physical debit card on a bank account.
     * Enforces the duplicate active card limit.
     *
     * @param user authenticated employee teller or administrator
     * @param request issuance details
     * @return CardResponse payload
     */
    CardResponse issuePhysicalCard(User user, IssueCardRequest request);

    /**
     * Issues a virtual debit card on a bank account.
     *
     * @param user authenticated customer, employee, or administrator
     * @param request virtual card details
     * @return CardResponse payload
     */
    CardResponse issueVirtualCard(User user, VirtualCardRequest request);

    /**
     * Retrieves details of a specific card by its UUID.
     *
     * @param id card UUID
     * @return CardResponse details DTO
     */
    CardResponse getCardById(UUID id);

    /**
     * Retrieves cards linked to an account.
     *
     * @param user authenticated user
     * @param accountNumber bank account number
     * @return list of card responses
     */
    List<CardResponse> getCardsByAccountNumber(User user, String accountNumber);

    /**
     * Dynamically searches and filters issued cards.
     *
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy sorting property key
     * @param sortDir sort direction (asc/desc)
     * @param cardNumber query card number (optional)
     * @param accountNumber query account number (optional)
     * @param customerId query customer owner ID (optional)
     * @param cardType filter by card type (optional)
     * @param status filter by card status (optional)
     * @param network filter by network (optional)
     * @return PagedResponse containing CardResponse details
     */
    PagedResponse<CardResponse> searchCards(
            int page, int size, String sortBy, String sortDir,
            String cardNumber, String accountNumber, UUID customerId,
            CardType cardType, CardStatus status, CardNetwork network
    );

    /**
     * Freezes a card to prevent online and ATM usages.
     *
     * @param user authenticated card owner or employee
     * @param id card UUID
     * @return updated CardResponse details
     */
    CardResponse freezeCard(User user, UUID id);

    /**
     * Unfreezes a frozen card.
     *
     * @param user authenticated card owner or employee
     * @param id card UUID
     * @return updated CardResponse details
     */
    CardResponse unfreezeCard(User user, UUID id);

    /**
     * Permanently blocks a card (e.g. lost, stolen).
     *
     * @param user authenticated employee teller or administrator
     * @param id card UUID
     * @param reason reason string
     * @return updated CardResponse details
     */
    CardResponse blockCard(User user, UUID id, String reason);

    /**
     * Issues a replacement card for a blocked or expired card.
     *
     * @param user authenticated employee teller or administrator
     * @param id card UUID to replace
     * @param newPin new PIN code to configure
     * @return new CardResponse details
     */
    CardResponse replaceCard(User user, UUID id, String newPin);

    /**
     * Changes or resets a card's PIN code.
     *
     * @param user authenticated card owner
     * @param request PIN change parameters
     * @return CardResponse details
     */
    CardResponse changePin(User user, PinChangeRequest request);

    /**
     * Modifies ATM, Online, and Transaction limits.
     *
     * @param user authenticated card owner or employee
     * @param request limit update parameters
     * @return CardResponse details
     */
    CardResponse updateLimits(User user, UpdateCardLimitRequest request);

    /**
     * Toggles contactless payment permission.
     *
     * @param user authenticated card owner or employee
     * @param id card UUID
     * @param enabled contactless enable flag
     * @return CardResponse details
     */
    CardResponse toggleContactless(User user, UUID id, boolean enabled);

    /**
     * Toggles international payment permission.
     *
     * @param user authenticated card owner or employee
     * @param id card UUID
     * @param enabled international enable flag
     * @return CardResponse details
     */
    CardResponse toggleInternational(User user, UUID id, boolean enabled);

    /**
     * Helper to map Card entity details to CardResponse DTO.
     *
     * @param card Card entity
     * @return CardResponse DTO representation
     */
    CardResponse convertToCardResponse(Card card);
}
