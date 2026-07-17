package com.novabank.backend.service;

import com.novabank.backend.dto.CardResponse;
import com.novabank.backend.dto.IssueCardRequest;
import com.novabank.backend.dto.PinChangeRequest;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.exception.CardAlreadyFrozenException;
import com.novabank.backend.exception.DuplicateCardException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CardRepository;
import com.novabank.backend.service.impl.CardNumberGeneratorImpl;
import com.novabank.backend.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link CardServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PinEncryptionService pinEncryptionService;

    @Mock
    private EventPublisherService eventPublisherService;

    @Spy
    private CardNumberGeneratorImpl cardNumberGenerator;

    @InjectMocks
    private CardServiceImpl cardService;

    private User sampleUser;
    private Customer sampleCustomer;
    private Account sampleAccount;
    private Card sampleCard;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();

        Role customerRole = Role.builder()
                .roleName(RoleType.ROLE_CUSTOMER)
                .build();

        sampleUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@novabank.com")
                .role(customerRole)
                .build();
        sampleUser.setId(UUID.randomUUID());

        sampleCustomer = Customer.builder()
                .user(sampleUser)
                .customerId("CUST000001")
                .firstName("John")
                .lastName("Doe")
                .build();
        sampleCustomer.setId(UUID.randomUUID());

        sampleAccount = Account.builder()
                .customer(sampleCustomer)
                .accountNumber("NB100000001")
                .balance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        sampleAccount.setId(UUID.randomUUID());

        sampleCard = Card.builder()
                .cardNumber("4539123456789012")
                .maskedCardNumber("4539 **** **** 9012")
                .customer(sampleCustomer)
                .account(sampleAccount)
                .cardHolderName("JOHN DOE")
                .cardType(CardType.PHYSICAL)
                .cardNetwork(CardNetwork.VISA)
                .expiryDate(LocalDate.now().plusYears(5))
                .cvv("123")
                .encryptedPin("hashed_pin_1234")
                .status(CardStatus.ACTIVE)
                .build();
        sampleCard.setId(cardId);
    }

    @Test
    void issuePhysicalCard_Success() {
        IssueCardRequest request = IssueCardRequest.builder()
                .accountNumber("NB100000001")
                .cardNetwork(CardNetwork.VISA)
                .pin("1234")
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        Mockito.when(cardRepository.existsByAccountAndCardTypeAndStatusIn(
                Mockito.any(Account.class), Mockito.any(CardType.class), Mockito.anyCollection()))
                .thenReturn(false); // No duplicates
        Mockito.when(cardRepository.existsByCardNumber(Mockito.anyString())).thenReturn(false);
        Mockito.when(pinEncryptionService.encryptPin("1234")).thenReturn("hashed_pin_1234");
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(invocation -> {
            Card saved = invocation.getArgument(0);
            saved.setId(cardId);
            return saved;
        });

        CardResponse response = cardService.issuePhysicalCard(sampleUser, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("JOHN DOE", response.getCardHolderName());
        Assertions.assertEquals(CardType.PHYSICAL, response.getCardType());
        Assertions.assertEquals(CardStatus.ACTIVE, response.getStatus());
        Mockito.verify(cardRepository).save(Mockito.any(Card.class));
    }

    @Test
    void issuePhysicalCard_Duplicate_ThrowsException() {
        IssueCardRequest request = IssueCardRequest.builder()
                .accountNumber("NB100000001")
                .cardNetwork(CardNetwork.VISA)
                .pin("1234")
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        Mockito.when(cardRepository.existsByAccountAndCardTypeAndStatusIn(
                Mockito.any(Account.class), Mockito.any(CardType.class), Mockito.anyCollection()))
                .thenReturn(true); // Duplicate physical card exists

        Assertions.assertThrows(DuplicateCardException.class, () -> cardService.issuePhysicalCard(sampleUser, request));
    }

    @Test
    void freezeCard_Success() {
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(sampleCard));
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponse response = cardService.freezeCard(sampleUser, cardId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(CardStatus.FROZEN, response.getStatus());
        Mockito.verify(cardRepository).save(sampleCard);
    }

    @Test
    void freezeCard_AlreadyFrozen_ThrowsException() {
        sampleCard.setStatus(CardStatus.FROZEN);
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(sampleCard));

        Assertions.assertThrows(CardAlreadyFrozenException.class, () -> cardService.freezeCard(sampleUser, cardId));
    }

    @Test
    void changePin_Success() {
        PinChangeRequest request = PinChangeRequest.builder()
                .cardId(cardId)
                .oldPin("1234")
                .newPin("5678")
                .build();

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(sampleCard));
        Mockito.when(pinEncryptionService.matches("1234", "hashed_pin_1234")).thenReturn(true);
        Mockito.when(pinEncryptionService.encryptPin("5678")).thenReturn("hashed_pin_5678");
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponse response = cardService.changePin(sampleUser, request);

        Assertions.assertNotNull(response);
        Mockito.verify(cardRepository).save(sampleCard);
        Assertions.assertEquals("hashed_pin_5678", sampleCard.getEncryptedPin());
    }
}
