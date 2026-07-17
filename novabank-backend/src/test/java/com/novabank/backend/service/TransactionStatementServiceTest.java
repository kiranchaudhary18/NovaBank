package com.novabank.backend.service;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.impl.CsvStatementServiceImpl;
import com.lowagie.text.pdf.PdfReader; // Checked that OpenPDF imports are correct! Let's just verify CSV string output
import com.novabank.backend.service.impl.StatementGeneratorServiceImpl;
import com.novabank.backend.service.impl.TransactionStatementServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link TransactionStatementServiceImpl} and {@link StatementGeneratorServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class TransactionStatementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy
    private CsvStatementServiceImpl csvStatementService;

    @InjectMocks
    private StatementGeneratorServiceImpl statementGeneratorService;

    private TransactionStatementServiceImpl transactionStatementService;

    private User sampleUser;
    private Customer sampleCustomer;
    private Account sampleAccount;
    private Transaction sampleTxn;

    @BeforeEach
    void setUp() {
        // Instantiate the wrapper service passing the generators
        transactionStatementService = new TransactionStatementServiceImpl(
                accountRepository,
                statementGeneratorService,
                null, // Mock PDF service can be null for CSV tests
                csvStatementService
        );

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
                .balance(new BigDecimal("1200.00"))
                .availableBalance(new BigDecimal("1200.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .openedDate(LocalDate.now())
                .build();
        sampleAccount.setId(UUID.randomUUID());

        sampleTxn = Transaction.builder()
                .transactionId("TXN100000001D")
                .referenceNumber("REF100000001")
                .senderAccount(sampleAccount)
                .transactionType(TransactionType.WITHDRAW)
                .amount(new BigDecimal("100.00"))
                .openingBalance(new BigDecimal("1300.00"))
                .closingBalance(new BigDecimal("1200.00"))
                .currency("USD")
                .remarks("Cash withdrawal test")
                .status(TransactionStatus.SUCCESS)
                .initiatedBy(sampleUser.getEmail())
                .transactionDate(LocalDateTime.now())
                .build();
        sampleTxn.setId(UUID.randomUUID());
    }

    @Test
    void getMiniStatement_Success() {
        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(sampleTxn));
        Mockito.when(transactionRepository.findBySenderAccountOrReceiverAccount(
                Mockito.any(Account.class), Mockito.any(Account.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        MiniStatementResponse response = transactionStatementService.getMiniStatement(sampleUser, "NB100000001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("NB100000001", response.getAccountNumber());
        Assertions.assertEquals(1, response.getTransactions().size());
        Assertions.assertEquals(new BigDecimal("100.00"), response.getTransactions().get(0).getDebitAmount());
    }

    @Test
    void getCustomStatement_Success() {
        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class)))
                .thenReturn(Collections.singletonList(sampleTxn));

        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        StatementResponse response = transactionStatementService.getCustomStatement(sampleUser, "NB100000001", start, end);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(new BigDecimal("1300.00"), response.getSummary().getOpeningBalance());
        Assertions.assertEquals(new BigDecimal("1200.00"), response.getSummary().getClosingBalance());
        Assertions.assertEquals(new BigDecimal("100.00"), response.getSummary().getTotalDebits());
    }

    @Test
    void getCustomStatement_FutureDate_ThrowsException() {
        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2); // Future date

        Assertions.assertThrows(BadRequestException.class, () ->
                transactionStatementService.getCustomStatement(sampleUser, "NB100000001", start, end));
    }

    @Test
    void getCustomStatement_InvalidRange_ThrowsException() {
        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        LocalDate start = LocalDate.now().minusDays(370); // Exceeds 1 year limit
        LocalDate end = LocalDate.now();

        Assertions.assertThrows(BadRequestException.class, () ->
                transactionStatementService.getCustomStatement(sampleUser, "NB100000001", start, end));
    }

    @Test
    void exportCsvStatement_Success() {
        Mockito.when(accountRepository.findByAccountNumber("NB100000001")).thenReturn(Optional.of(sampleAccount));
        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Sort.class)))
                .thenReturn(Collections.singletonList(sampleTxn));

        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        byte[] csvBytes = transactionStatementService.exportCsvStatement(sampleUser, "NB100000001", start, end);

        Assertions.assertNotNull(csvBytes);
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);
        Assertions.assertTrue(csvContent.contains("NovaBank Account Statement"));
        Assertions.assertTrue(csvContent.contains("NB100000001"));
        Assertions.assertTrue(csvContent.contains("Cash withdrawal test"));
    }
}
