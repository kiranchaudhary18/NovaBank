package com.novabank.backend.service;

import com.novabank.backend.dto.DepositRequest;
import com.novabank.backend.dto.TransactionReceiptResponse;
import com.novabank.backend.dto.TransferRequest;
import com.novabank.backend.dto.WithdrawRequest;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.InsufficientBalanceException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.impl.ReceiptServiceImpl;
import com.novabank.backend.service.impl.TransactionServiceImpl;
import com.novabank.backend.service.impl.TransferServiceImpl;
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
 * Service Layer Unit tests for {@link TransactionServiceImpl} and {@link TransferServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private EventPublisherService eventPublisherService;

    @Spy
    private ReceiptServiceImpl receiptService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User sampleUser;
    private Customer sampleCustomer;
    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        Role customerRole = Role.builder()
                .roleName(RoleType.ROLE_CUSTOMER)
                .build();

        sampleUser = User.builder()
                .fullName("Jane Doe")
                .email("jane.doe@novabank.com")
                .role(customerRole) // Fix: set customer role to avoid NPE in controllers/services security checks
                .build();
        sampleUser.setId(UUID.randomUUID());

        sampleCustomer = Customer.builder()
                .user(sampleUser)
                .customerId("CUST000001")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        sampleCustomer.setId(UUID.randomUUID());

        account1 = Account.builder()
                .customer(sampleCustomer)
                .accountNumber("NB100000001")
                .balance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .openedDate(LocalDate.now())
                .build();
        account1.setId(UUID.randomUUID());

        account2 = Account.builder()
                .customer(sampleCustomer)
                .accountNumber("NB100000002")
                .balance(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("500.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .openedDate(LocalDate.now())
                .build();
        account2.setId(UUID.randomUUID());
    }

    @Test
    void deposit_Success() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("NB100000001")
                .amount(new BigDecimal("500.00"))
                .remarks("Deposit test")
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Stub balanceService.deposit to simulate balance increment
        Mockito.doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            BigDecimal amt = invocation.getArgument(1);
            acc.setBalance(acc.getBalance().add(amt));
            acc.setAvailableBalance(acc.getAvailableBalance().add(amt));
            return null;
        }).when(balanceService).deposit(Mockito.any(Account.class), Mockito.any(BigDecimal.class));

        TransactionReceiptResponse receipt = transactionService.deposit(sampleUser, request);

        Assertions.assertNotNull(receipt);
        Assertions.assertEquals("NB100000001", receipt.getReceiverAccountNumber());
        Assertions.assertEquals(new BigDecimal("500.00"), receipt.getAmount());
        Assertions.assertEquals(TransactionStatus.SUCCESS, receipt.getStatus());
        Mockito.verify(transactionRepository).save(Mockito.any(Transaction.class));
    }

    @Test
    void withdraw_Success() {
        WithdrawRequest request = WithdrawRequest.builder()
                .accountNumber("NB100000001")
                .amount(new BigDecimal("200.00"))
                .remarks("Withdrawal test")
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Stub balanceService.withdraw to simulate balance decrement
        Mockito.doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            BigDecimal amt = invocation.getArgument(1);
            acc.setBalance(acc.getBalance().subtract(amt));
            acc.setAvailableBalance(acc.getAvailableBalance().subtract(amt));
            return null;
        }).when(balanceService).withdraw(Mockito.any(Account.class), Mockito.any(BigDecimal.class));

        TransactionReceiptResponse receipt = transactionService.withdraw(sampleUser, request);

        Assertions.assertNotNull(receipt);
        Assertions.assertEquals("NB100000001", receipt.getSenderAccountNumber());
        Assertions.assertEquals(new BigDecimal("200.00"), receipt.getAmount());
        Assertions.assertEquals(TransactionStatus.SUCCESS, receipt.getStatus());
        Mockito.verify(transactionRepository).save(Mockito.any(Transaction.class));
    }

    @Test
    void withdraw_InsufficientBalance_ThrowsException() {
        WithdrawRequest request = WithdrawRequest.builder()
                .accountNumber("NB100000001")
                .amount(new BigDecimal("1200.00"))
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(account1));
        Mockito.doThrow(new InsufficientBalanceException("Insufficient available funds for transaction."))
                .when(balanceService).withdraw(Mockito.any(Account.class), Mockito.any(BigDecimal.class));

        Assertions.assertThrows(InsufficientBalanceException.class, () -> transactionService.withdraw(sampleUser, request));
    }

    @Test
    void transfer_Success() {
        TransferRequest request = TransferRequest.builder()
                .senderAccountNumber("NB100000001")
                .receiverAccountNumber("NB100000002")
                .amount(new BigDecimal("300.00"))
                .remarks("Transfer test")
                .build();

        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber("NB100000002")).thenReturn(Optional.of(account2));
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Stub withdrawals/deposits
        Mockito.doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            BigDecimal amt = invocation.getArgument(1);
            acc.setBalance(acc.getBalance().subtract(amt));
            acc.setAvailableBalance(acc.getAvailableBalance().subtract(amt));
            return null;
        }).when(balanceService).withdraw(Mockito.any(Account.class), Mockito.any(BigDecimal.class));

        Mockito.doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            BigDecimal amt = invocation.getArgument(1);
            acc.setBalance(acc.getBalance().add(amt));
            acc.setAvailableBalance(acc.getAvailableBalance().add(amt));
            return null;
        }).when(balanceService).deposit(Mockito.any(Account.class), Mockito.any(BigDecimal.class));

        TransactionReceiptResponse receipt = transferService.transferFunds(sampleUser, request);

        Assertions.assertNotNull(receipt);
        Assertions.assertEquals("NB100000001", receipt.getSenderAccountNumber());
        Assertions.assertEquals("NB100000002", receipt.getReceiverAccountNumber());
        Assertions.assertEquals(new BigDecimal("300.00"), receipt.getAmount());
        Assertions.assertEquals(TransactionStatus.SUCCESS, receipt.getStatus());
        Mockito.verify(transactionRepository, Mockito.times(2)).save(Mockito.any(Transaction.class));
    }
}
