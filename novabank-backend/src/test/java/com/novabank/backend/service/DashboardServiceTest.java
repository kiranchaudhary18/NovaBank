package com.novabank.backend.service;

import com.lowagie.text.pdf.PdfReader;
import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.service.impl.AnalyticsServiceImpl;
import com.novabank.backend.service.impl.DashboardServiceImpl;
import com.novabank.backend.service.impl.ReportServiceImpl;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service Layer Unit tests for administrative analytics, dashboards, and reports.
 * Uses Mockito to mock JPA EntityManager queries.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private TypedQuery<Long> longQuery;
    private TypedQuery<BigDecimal> bigDecimalQuery;
    private TypedQuery<Customer> customerQuery;
    private TypedQuery<Account> accountQuery;
    private TypedQuery<Transaction> transactionQuery;

    @BeforeEach
    void setUp() {
        longQuery = Mockito.mock(TypedQuery.class);
        bigDecimalQuery = Mockito.mock(TypedQuery.class);
        customerQuery = Mockito.mock(TypedQuery.class);
        accountQuery = Mockito.mock(TypedQuery.class);
        transactionQuery = Mockito.mock(TypedQuery.class);
    }

    @Test
    void getDashboardStats_Success() {
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(BigDecimal.class))).thenReturn(bigDecimalQuery);

        Mockito.when(longQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(longQuery);
        Mockito.when(bigDecimalQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(bigDecimalQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(150L, 100L, 5L, 200L, 10L);
        Mockito.when(bigDecimalQuery.getSingleResult()).thenReturn(new BigDecimal("250000.00"), new BigDecimal("15000.00"));

        DashboardResponse stats = dashboardService.getDashboardStats();

        Assertions.assertNotNull(stats);
        Assertions.assertEquals(150L, stats.getTotalCustomers());
        Assertions.assertEquals(100L, stats.getActiveCustomers());
        Assertions.assertEquals(5L, stats.getPendingKycCount());
        Assertions.assertEquals(200L, stats.getTotalAccounts());
        Assertions.assertEquals(new BigDecimal("250000.00"), stats.getTotalBalance());
        Assertions.assertEquals(new BigDecimal("75000.00"), stats.getTotalOutstandingLoans());
    }

    @Test
    void getCustomerAnalytics_Success() {
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(50L, 40L, 5L, 2L, 42L, 4L);

        CustomerAnalyticsResponse response = analyticsService.getCustomerAnalytics();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(50L, response.getTotalCustomers());
        Assertions.assertEquals(40L, response.getActiveCustomers());
        Assertions.assertEquals(4L, response.getPendingKyc());
    }

    @Test
    void getCardAnalytics_Success() {
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(30L, 20L, 2L, 0L);

        CardAnalyticsResponse response = analyticsService.getCardAnalytics();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(30L, response.getPhysicalCards());
        Assertions.assertEquals(20L, response.getVirtualCards());
        Assertions.assertEquals(2L, response.getBlockedCards());
    }

    @Test
    void getRevenueAnalytics_Success() {
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(longQuery);
        Mockito.when(longQuery.getSingleResult()).thenReturn(100L); // 100 transfers

        RevenueAnalyticsResponse response = analyticsService.getRevenueAnalytics();

        Assertions.assertNotNull(response);
        // 100 transfers * $1.50 = $150.00
        Assertions.assertEquals(new BigDecimal("150.00"), response.getTransactionCharges());
        Assertions.assertEquals(new BigDecimal("6200.00"), response.getTotalRevenue());
    }

    @Test
    void generateCustomerReport_Csv_Success() {
        Customer c1 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phoneNumber("+123456")
                .status(CustomerStatus.ACTIVE)
                .build();
        c1.setCustomerId("CUST-001");

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Customer.class))).thenReturn(customerQuery);
        Mockito.when(customerQuery.getResultList()).thenReturn(List.of(c1));

        byte[] csvData = reportService.generateCustomerReport("CSV", null, null);
        Assertions.assertNotNull(csvData);
        
        String csvContent = new String(csvData, StandardCharsets.UTF_8);
        Assertions.assertTrue(csvContent.contains("Customer ID"));
        Assertions.assertTrue(csvContent.contains("CUST-001"));
        Assertions.assertTrue(csvContent.contains("Jane"));
    }

    @Test
    void generateCustomerReport_Pdf_Success() throws Exception {
        Customer c1 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phoneNumber("+123456")
                .status(CustomerStatus.ACTIVE)
                .build();
        c1.setCustomerId("CUST-001");

        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(Customer.class))).thenReturn(customerQuery);
        Mockito.when(customerQuery.getResultList()).thenReturn(List.of(c1));

        byte[] pdfData = reportService.generateCustomerReport("PDF", null, null);
        Assertions.assertNotNull(pdfData);

        // OpenPDF outputs standard PDF document stream bytes, parsing using PdfReader to check compliance
        PdfReader reader = new PdfReader(pdfData);
        Assertions.assertTrue(reader.getNumberOfPages() > 0);
        reader.close();
    }
}
