package com.novabank.backend.repository;

import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Card;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

/**
 * Data Repository for performing queries on the {@link Card} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {

    /**
     * Finds active cards whose expiry date is before the specified date.
     */
    Page<Card> findByExpiryDateBeforeAndStatus(LocalDate date, CardStatus status, Pageable pageable);

    /**
     * Finds active cards whose expiry date falls between a given range (e.g. next 30 days).
     */
    Page<Card> findByExpiryDateBetweenAndStatus(LocalDate start, LocalDate end, CardStatus status, Pageable pageable);

    /**
     * Finds a card by its unique 16-digit card number.
     *
     * @param cardNumber card number string
     * @return Optional containing the card, or empty
     */
    Optional<Card> findByCardNumber(String cardNumber);

    /**
     * Lists cards associated with a specific account number.
     *
     * @param accountNumber bank account number
     * @return list of cards
     */
    List<Card> findByAccountAccountNumber(String accountNumber);

    /**
     * Checks if a card number is already registered in the system.
     *
     * @param cardNumber card number string
     * @return true if exists, false otherwise
     */
    boolean existsByCardNumber(String cardNumber);

    /**
     * Checks if an account holds a card of a specific type in any of the specified statuses.
     * Used to enforce "one physical card / one virtual card per account" business limits.
     *
     * @param account target bank account
     * @param cardType PHYSICAL or VIRTUAL
     * @param statuses collection of statuses to match
     * @return true if duplicate exists, false otherwise
     */
    boolean existsByAccountAndCardTypeAndStatusIn(Account account, CardType cardType, Collection<CardStatus> statuses);
}
