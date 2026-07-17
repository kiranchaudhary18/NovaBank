package com.novabank.backend.service;

import com.novabank.backend.dto.FraudAlertResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.FraudAlert;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.AlertType;
import com.novabank.backend.enums.RiskLevel;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.repository.FraudAlertRepository;
import com.novabank.backend.service.impl.FraudDetectionServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link FraudDetectionServiceImpl}.
 * Uses Mockito to mock repository and JPA interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private FraudDetectionServiceImpl fraudDetectionService;

    private User sampleUser;
    private Account sampleAccount;
    private Transaction sampleTransaction;
    private FraudAlert sampleAlert;
    private UUID alertId;

    @BeforeEach
    void setUp() {
        alertId = UUID.randomUUID();

        sampleUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@novabank.com")
                .build();
        sampleUser.setId(UUID.randomUUID());

        Customer customer = Customer.builder()
                .user(sampleUser)
                .firstName("John")
                .lastName("Doe")
                .build();

        sampleAccount = Account.builder()
                .customer(customer)
                .accountNumber("NB100000001")
                .balance(new BigDecimal("20000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        sampleTransaction = Transaction.builder()
                .transactionId("TXN1234567")
                .referenceNumber("REF12345")
                .senderAccount(sampleAccount)
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("15000.00")) // Exceeds large limit ($10,000)
                .status(com.novabank.backend.enums.TransactionStatus.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .build();

        sampleAlert = FraudAlert.builder()
                .user(sampleUser)
                .account(sampleAccount)
                .transaction(sampleTransaction)
                .alertType(AlertType.LARGE_TRANSACTION)
                .riskLevel(RiskLevel.HIGH)
                .riskScore(30)
                .reason("Transaction amount exceeds threshold: $15000.00")
                .status(AlertStatus.OPEN)
                .build();
        sampleAlert.setId(alertId);
    }

    @Test
    void evaluateTransactionRules_LargeTransfer_TriggersAlert() {
        // Prepare mock call
        Mockito.when(fraudAlertRepository.save(Mockito.any(FraudAlert.class))).thenReturn(sampleAlert);

        // Under 15 mins count query setup
        TypedQuery<Long> longQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(0L); // No multiple transfers

        fraudDetectionService.evaluateTransactionRules(sampleTransaction);

        // Verify alert saved with LARGE_TRANSACTION type
        Mockito.verify(fraudAlertRepository, Mockito.atLeastOnce()).save(Mockito.argThat(alert -> 
                alert.getAlertType() == AlertType.LARGE_TRANSACTION &&
                alert.getRiskScore() == 30
        ));
    }

    @Test
    void evaluateTransactionRules_FrozenAccount_TriggersAlert() {
        sampleAccount.setStatus(AccountStatus.FROZEN);
        Mockito.when(fraudAlertRepository.save(Mockito.any(FraudAlert.class))).thenReturn(sampleAlert);

        // Under 15 mins count query setup
        TypedQuery<Long> longQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(0L);

        fraudDetectionService.evaluateTransactionRules(sampleTransaction);

        // Verify HIGH_RISK_TRANSFER alert triggered due to FROZEN account
        Mockito.verify(fraudAlertRepository, Mockito.atLeastOnce()).save(Mockito.argThat(alert -> 
                alert.getAlertType() == AlertType.HIGH_RISK_TRANSFER &&
                alert.getRiskScore() == 60
        ));
    }

    @Test
    void evaluateAuthenticationRules_FailedLogin_TriggersAlert() {
        // Mock audit query returns 4 previous failed logs in last 10 minutes
        TypedQuery<Long> longQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(4L);

        fraudDetectionService.evaluateAuthenticationRules(sampleUser, false, "127.0.0.1", "Web Client", "Chrome", "Windows");

        // Verify brute-force trigger saved with FAILED_LOGIN type
        Mockito.verify(fraudAlertRepository).save(Mockito.argThat(alert -> 
                alert.getAlertType() == AlertType.FAILED_LOGIN &&
                alert.getRiskScore() == 10
        ));
    }

    @Test
    void reviewFraudAlert_Success() {
        Mockito.when(fraudAlertRepository.findById(alertId)).thenReturn(Optional.of(sampleAlert));
        Mockito.when(fraudAlertRepository.save(Mockito.any(FraudAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FraudAlertResponse response = fraudDetectionService.reviewFraudAlert(alertId, "admin@novabank.com");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AlertStatus.UNDER_REVIEW, response.getStatus());
        Assertions.assertEquals("admin@novabank.com", response.getReviewedBy());
    }

    @Test
    void resolveFraudAlert_Success() {
        Mockito.when(fraudAlertRepository.findById(alertId)).thenReturn(Optional.of(sampleAlert));
        Mockito.when(fraudAlertRepository.save(Mockito.any(FraudAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FraudAlertResponse response = fraudDetectionService.resolveFraudAlert(alertId, "admin@novabank.com", AlertStatus.RESOLVED);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AlertStatus.RESOLVED, response.getStatus());
    }

    @Test
    void resolveFraudAlert_InvalidStatus_ThrowsException() {
        Mockito.when(fraudAlertRepository.findById(alertId)).thenReturn(Optional.of(sampleAlert));

        Assertions.assertThrows(BadRequestException.class, () -> 
                fraudDetectionService.resolveFraudAlert(alertId, "admin@novabank.com", AlertStatus.OPEN)
        );
    }
}
