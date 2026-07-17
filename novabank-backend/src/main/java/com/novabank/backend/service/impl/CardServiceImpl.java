package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import com.novabank.backend.exception.*;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CardRepository;
import com.novabank.backend.service.CardNumberGenerator;
import com.novabank.backend.service.CardService;
import com.novabank.backend.service.PinEncryptionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing physical and virtual debit cards lifecycles.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final PinEncryptionService pinEncryptionService;
    private final com.novabank.backend.service.EventPublisherService eventPublisherService;

    @Override
    @Transactional
    public CardResponse issuePhysicalCard(User user, IssueCardRequest request) {
        log.info("Issuing physical debit card for account {}", request.getAccountNumber());
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + request.getAccountNumber()));

        // Validation 1: Check account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Card issuance declined: Account is not ACTIVE.");
        }

        // Validation 2: Ensure only one physical card per account
        boolean duplicate = cardRepository.existsByAccountAndCardTypeAndStatusIn(
                account, CardType.PHYSICAL, List.of(CardStatus.PENDING, CardStatus.ACTIVE, CardStatus.FROZEN)
        );
        if (duplicate) {
            throw new DuplicateCardException("A physical debit card is already active or pending for this account.");
        }

        String cardNumber;
        do {
            cardNumber = cardNumberGenerator.generateCardNumber(request.getCardNetwork());
        } while (cardRepository.existsByCardNumber(cardNumber));

        String cardHolderName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .maskedCardNumber(cardNumberGenerator.generateMaskedCardNumber(cardNumber))
                .customer(account.getCustomer())
                .account(account)
                .cardHolderName(cardHolderName.toUpperCase())
                .cardType(CardType.PHYSICAL)
                .cardNetwork(request.getCardNetwork())
                .expiryDate(LocalDate.now().plusYears(5)) // 5 years expiry
                .cvv(cardNumberGenerator.generateCvv())
                .encryptedPin(pinEncryptionService.encryptPin(request.getPin()))
                .atmLimit(new BigDecimal("1000.00"))     // Default Limits
                .onlineLimit(new BigDecimal("2000.00"))
                .dailyLimit(new BigDecimal("3000.00"))
                .status(CardStatus.ACTIVE) // Activated immediately upon tellers issue
                .issuedDate(LocalDate.now())
                .activatedDate(LocalDate.now())
                .replacementCount(0)
                .build();

        Card saved = cardRepository.save(card);
        log.info("Physical card issued successfully. ID: {}", saved.getId());
        eventPublisherService.publishCardIssuedEvent(saved);
        return convertToCardResponse(saved);
    }

    @Override
    @Transactional
    public CardResponse issueVirtualCard(User user, VirtualCardRequest request) {
        log.info("Issuing virtual debit card for account {}", request.getAccountNumber());
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + request.getAccountNumber()));

        // Validation 1: Check account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Card issuance declined: Account is not ACTIVE.");
        }

        // Security check: Customer can only request virtual cards for their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!account.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the requested account.");
            }
        }

        // Validation 2: Ensure only one virtual card per account
        boolean duplicate = cardRepository.existsByAccountAndCardTypeAndStatusIn(
                account, CardType.VIRTUAL, List.of(CardStatus.PENDING, CardStatus.ACTIVE, CardStatus.FROZEN)
        );
        if (duplicate) {
            throw new DuplicateCardException("A virtual debit card is already active or pending for this account.");
        }

        String cardNumber;
        do {
            cardNumber = cardNumberGenerator.generateCardNumber(request.getCardNetwork());
        } while (cardRepository.existsByCardNumber(cardNumber));

        String cardHolderName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .maskedCardNumber(cardNumberGenerator.generateMaskedCardNumber(cardNumber))
                .customer(account.getCustomer())
                .account(account)
                .cardHolderName(cardHolderName.toUpperCase())
                .cardType(CardType.VIRTUAL)
                .cardNetwork(request.getCardNetwork())
                .expiryDate(LocalDate.now().plusYears(5))
                .cvv(cardNumberGenerator.generateCvv())
                .encryptedPin(pinEncryptionService.encryptPin(request.getPin()))
                .atmLimit(BigDecimal.ZERO) // ATM withdrawal disabled on virtual cards
                .onlineLimit(new BigDecimal("1500.00"))
                .dailyLimit(new BigDecimal("1500.00"))
                .status(CardStatus.ACTIVE)
                .issuedDate(LocalDate.now())
                .activatedDate(LocalDate.now())
                .replacementCount(0)
                .build();

        Card saved = cardRepository.save(card);
        log.info("Virtual card issued successfully. ID: {}", saved.getId());
        eventPublisherService.publishCardIssuedEvent(saved);
        return convertToCardResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + id));
        return convertToCardResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByAccountNumber(User user, String accountNumber) {
        log.info("Request for cards linked to account: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        // Security validation
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!account.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the requested account.");
            }
        }

        List<Card> cards = cardRepository.findByAccountAccountNumber(accountNumber);
        return cards.stream().map(this::convertToCardResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CardResponse> searchCards(
            int page, int size, String sortBy, String sortDir,
            String cardNumber, String accountNumber, UUID customerId,
            CardType cardType, CardStatus status, CardNetwork network
    ) {
        log.info("Searching cards ledger dynamically");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Card> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (cardNumber != null && !cardNumber.isBlank()) {
                predicates.add(cb.equal(root.get("cardNumber"), cardNumber));
            }
            if (accountNumber != null && !accountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("account").get("accountNumber"), accountNumber));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (cardType != null) {
                predicates.add(cb.equal(root.get("cardType"), cardType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (network != null) {
                predicates.add(cb.equal(root.get("cardNetwork"), network));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Card> pageResult = cardRepository.findAll(spec, pageable);
        return new PagedResponse<>(pageResult.map(this::convertToCardResponse));
    }

    @Override
    @Transactional
    public CardResponse freezeCard(User user, UUID id) {
        log.info("Freezing card ID: {}", id);
        Card card = loadAndVerifyCard(user, id);

        if (card.getStatus() == CardStatus.FROZEN) {
            throw new CardAlreadyFrozenException("Card is already FROZEN.");
        }
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Cannot freeze a permanently BLOCKED card.");
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new CardExpiredException("Cannot freeze an EXPIRED card.");
        }

        card.setStatus(CardStatus.FROZEN);
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse unfreezeCard(User user, UUID id) {
        log.info("Unfreezing card ID: {}", id);
        Card card = loadAndVerifyCard(user, id);

        if (card.getStatus() != CardStatus.FROZEN) {
            throw new BadRequestException("Card is not FROZEN. Current status: " + card.getStatus());
        }

        card.setStatus(CardStatus.ACTIVE);
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse blockCard(User user, UUID id, String reason) {
        log.info("Blocking card ID: {} permanently due to: {}", id, reason);
        Card card = loadAndVerifyCard(user, id);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Card is already BLOCKED.");
        }

        card.setStatus(CardStatus.BLOCKED);
        card.setBlockedReason(reason);
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse replaceCard(User user, UUID id, String newPin) {
        log.info("Replacing card ID: {}", id);
        Card oldCard = loadAndVerifyCard(user, id);

        if (oldCard.getStatus() != CardStatus.BLOCKED && oldCard.getStatus() != CardStatus.EXPIRED) {
            throw new BadRequestException("Declined: Replacement is only allowed for BLOCKED or EXPIRED cards.");
        }

        oldCard.setStatus(CardStatus.REPLACED);
        cardRepository.save(oldCard);

        String cardNumber;
        do {
            cardNumber = cardNumberGenerator.generateCardNumber(oldCard.getCardNetwork());
        } while (cardRepository.existsByCardNumber(cardNumber));

        Card newCard = Card.builder()
                .cardNumber(cardNumber)
                .maskedCardNumber(cardNumberGenerator.generateMaskedCardNumber(cardNumber))
                .customer(oldCard.getCustomer())
                .account(oldCard.getAccount())
                .cardHolderName(oldCard.getCardHolderName())
                .cardType(oldCard.getCardType())
                .cardNetwork(oldCard.getCardNetwork())
                .expiryDate(LocalDate.now().plusYears(5))
                .cvv(cardNumberGenerator.generateCvv())
                .encryptedPin(pinEncryptionService.encryptPin(newPin))
                .atmLimit(oldCard.getAtmLimit())
                .onlineLimit(oldCard.getOnlineLimit())
                .dailyLimit(oldCard.getDailyLimit())
                .contactlessEnabled(oldCard.isContactlessEnabled())
                .internationalEnabled(oldCard.isInternationalEnabled())
                .status(CardStatus.ACTIVE)
                .issuedDate(LocalDate.now())
                .activatedDate(LocalDate.now())
                .replacementCount(oldCard.getReplacementCount() + 1)
                .build();

        Card saved = cardRepository.save(newCard);
        log.info("New replacement card issued. ID: {}", saved.getId());
        return convertToCardResponse(saved);
    }

    @Override
    @Transactional
    public CardResponse changePin(User user, PinChangeRequest request) {
        log.info("Changing security PIN for card ID: {}", request.getCardId());
        Card card = loadAndVerifyCard(user, request.getCardId());

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Declined: PIN change is only allowed for ACTIVE cards.");
        }

        if (!pinEncryptionService.matches(request.getOldPin(), card.getEncryptedPin())) {
            throw new BadRequestException("Declined: Current PIN mismatch.");
        }

        card.setEncryptedPin(pinEncryptionService.encryptPin(request.getNewPin()));
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse updateLimits(User user, UpdateCardLimitRequest request) {
        log.info("Updating spending limits for card ID: {}", request.getCardId());
        Card card = loadAndVerifyCard(user, request.getCardId());

        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException("Declined: Cannot modify limits on blocked or expired cards.");
        }

        card.setDailyLimit(request.getDailyLimit());
        card.setOnlineLimit(request.getOnlineLimit());
        card.setAtmLimit(request.getAtmLimit());

        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse toggleContactless(User user, UUID id, boolean enabled) {
        log.info("Toggling contactless to {} on card ID: {}", enabled, id);
        Card card = loadAndVerifyCard(user, id);

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Declined: Status is not ACTIVE.");
        }

        card.setContactlessEnabled(enabled);
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    @Transactional
    public CardResponse toggleInternational(User user, UUID id, boolean enabled) {
        log.info("Toggling international usage to {} on card ID: {}", enabled, id);
        Card card = loadAndVerifyCard(user, id);

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Declined: Status is not ACTIVE.");
        }

        card.setInternationalEnabled(enabled);
        Card updated = cardRepository.save(card);
        return convertToCardResponse(updated);
    }

    @Override
    public CardResponse convertToCardResponse(Card card) {
        if (card == null) {
            return null;
        }

        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .maskedCardNumber(card.getMaskedCardNumber())
                .customerId(card.getCustomer().getId())
                .accountNumber(card.getAccount().getAccountNumber())
                .cardHolderName(card.getCardHolderName())
                .cardType(card.getCardType())
                .cardNetwork(card.getCardNetwork())
                .expiryDate(card.getExpiryDate())
                .dailyLimit(card.getDailyLimit())
                .onlineLimit(card.getOnlineLimit())
                .atmLimit(card.getAtmLimit())
                .contactlessEnabled(card.isContactlessEnabled())
                .internationalEnabled(card.isInternationalEnabled())
                .status(card.getStatus())
                .issuedDate(card.getIssuedDate())
                .activatedDate(card.getActivatedDate())
                .replacementCount(card.getReplacementCount())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    private Card loadAndVerifyCard(User user, UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

        // Security check: Customer can only modify cards that they own
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!card.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own this card.");
            }
        }

        return card;
    }
}
