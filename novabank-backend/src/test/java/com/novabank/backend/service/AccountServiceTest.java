package com.novabank.backend.service;

import com.novabank.backend.dto.AccountResponse;
import com.novabank.backend.dto.CreateAccountRequest;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.CustomerNotVerifiedException;
import com.novabank.backend.exception.InvalidAccountStatusException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link AccountServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGeneratorService generatorService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User sampleUser;
    private Customer sampleCustomer;
    private CreateAccountRequest sampleRequest;
    private Account sampleAccount;
    private UUID customerId;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        sampleUser = User.builder()
                .fullName("Jane Doe")
                .email("jane.doe@novabank.com")
                .build();

        sampleCustomer = Customer.builder()
                .user(sampleUser)
                .customerId("CUST000001")
                .firstName("Jane")
                .lastName("Doe")
                .status(CustomerStatus.ACTIVE) // ACTIVE/Verified
                .build();
        sampleCustomer.setId(customerId);

        sampleRequest = CreateAccountRequest.builder()
                .customerId(customerId)
                .accountType(AccountType.SAVINGS)
                .currency("USD")
                .branchCode("1001")
                .isPrimary(true)
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        sampleAccount = Account.builder()
                .customer(sampleCustomer)
                .accountNumber("NB100000001")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .currency("USD")
                .branchCode("1001")
                .ifscCode("NOVA0001001")
                .status(AccountStatus.ACTIVE)
                .openedDate(LocalDate.now())
                .isPrimary(true)
                .build();
        sampleAccount.setId(accountId);
    }

    @Test
    void createAccount_Success() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(accountRepository.findByCustomerAndAccountTypeAndIsPrimary(sampleCustomer, AccountType.SAVINGS, true))
                .thenReturn(Optional.empty());
        Mockito.when(generatorService.generateAccountNumber()).thenReturn("NB100000001");
        Mockito.when(generatorService.generateIfscCode("1001")).thenReturn("NOVA0001001");
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(invocation -> {
            Account saved = invocation.getArgument(0);
            saved.setId(accountId);
            return saved;
        });

        AccountResponse response = accountService.createAccount(sampleRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("NB100000001", response.getAccountNumber());
        Assertions.assertEquals("NOVA0001001", response.getIfscCode());
        Assertions.assertEquals(accountId, response.getId());
    }

    @Test
    void createAccount_UnverifiedCustomer_ThrowsException() {
        sampleCustomer.setStatus(CustomerStatus.INACTIVE); // Unverified customer
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));

        Assertions.assertThrows(CustomerNotVerifiedException.class, () -> accountService.createAccount(sampleRequest));
    }

    @Test
    void createAccount_DuplicatePrimarySavings_ThrowsException() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(accountRepository.findByCustomerAndAccountTypeAndIsPrimary(sampleCustomer, AccountType.SAVINGS, true))
                .thenReturn(Optional.of(sampleAccount)); // Primary already exists

        Assertions.assertThrows(BadRequestException.class, () -> accountService.createAccount(sampleRequest));
    }

    @Test
    void getAccountById_Success() {
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(sampleAccount));

        AccountResponse response = accountService.getAccountById(accountId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("NB100000001", response.getAccountNumber());
    }

    @Test
    void getAccountById_NotFound_ThrowsException() {
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    void freezeAccount_Success() {
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(sampleAccount));
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.freezeAccount(accountId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountStatus.FROZEN, response.getStatus());
    }

    @Test
    void freezeAccount_ClosedAccount_ThrowsException() {
        sampleAccount.setStatus(AccountStatus.CLOSED);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(sampleAccount));

        Assertions.assertThrows(InvalidAccountStatusException.class, () -> accountService.freezeAccount(accountId));
    }
}
