package com.novabank.backend.service;

import com.novabank.backend.dto.BeneficiaryResponse;
import com.novabank.backend.dto.CreateBeneficiaryRequest;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Beneficiary;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;
import com.novabank.backend.exception.DuplicateBeneficiaryException;
import com.novabank.backend.exception.OwnAccountNotAllowedException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.BeneficiaryRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.impl.BeneficiaryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link BeneficiaryServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BeneficiaryRepository beneficiaryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BeneficiaryServiceImpl beneficiaryService;

    private User sampleUser;
    private Customer sampleCustomer;
    private CreateBeneficiaryRequest sampleRequest;
    private Beneficiary sampleBeneficiary;
    private Account sampleOwnAccount;
    private UUID customerId;
    private UUID beneficiaryId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        beneficiaryId = UUID.randomUUID();

        sampleUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@novabank.com")
                .build();

        sampleCustomer = Customer.builder()
                .user(sampleUser)
                .customerId("CUST000001")
                .firstName("John")
                .lastName("Doe")
                .build();
        sampleCustomer.setId(customerId);

        sampleRequest = CreateBeneficiaryRequest.builder()
                .beneficiaryName("Jane Smith")
                .beneficiaryAccountNumber("NB100000002")
                .beneficiaryBankName("NovaBank")
                .beneficiaryIfscCode("NOVA0001001")
                .nickname("Jane")
                .relationship(RelationshipType.FRIEND)
                .isFavorite(true)
                .build();

        sampleBeneficiary = Beneficiary.builder()
                .customer(sampleCustomer)
                .beneficiaryName("Jane Smith")
                .beneficiaryAccountNumber("NB100000002")
                .beneficiaryBankName("NovaBank")
                .beneficiaryIfscCode("NOVA0001001")
                .nickname("Jane")
                .relationship(RelationshipType.FRIEND)
                .isFavorite(true)
                .status(BeneficiaryStatus.ACTIVE)
                .build();
        sampleBeneficiary.setId(beneficiaryId);

        sampleOwnAccount = Account.builder()
                .customer(sampleCustomer)
                .accountNumber("NB100000002")
                .build();
    }

    @Test
    void addBeneficiary_Success() {
        Mockito.when(customerRepository.findByUser(sampleUser)).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(accountRepository.findByAccountNumber(sampleRequest.getBeneficiaryAccountNumber()))
                .thenReturn(Optional.empty()); // Not own account
        Mockito.when(beneficiaryRepository.existsByCustomerAndBeneficiaryAccountNumberAndStatusNot(
                sampleCustomer, sampleRequest.getBeneficiaryAccountNumber(), BeneficiaryStatus.DELETED))
                .thenReturn(false); // No duplicates
        Mockito.when(beneficiaryRepository.save(Mockito.any(Beneficiary.class))).thenAnswer(invocation -> {
            Beneficiary saved = invocation.getArgument(0);
            saved.setId(beneficiaryId);
            return saved;
        });

        BeneficiaryResponse response = beneficiaryService.addBeneficiary(sampleUser, sampleRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Jane Smith", response.getBeneficiaryName());
        Assertions.assertEquals(BeneficiaryStatus.ACTIVE, response.getStatus());
    }

    @Test
    void addBeneficiary_OwnAccount_ThrowsException() {
        Mockito.when(customerRepository.findByUser(sampleUser)).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(accountRepository.findByAccountNumber(sampleRequest.getBeneficiaryAccountNumber()))
                .thenReturn(Optional.of(sampleOwnAccount)); // Is own account

        Assertions.assertThrows(OwnAccountNotAllowedException.class, () -> beneficiaryService.addBeneficiary(sampleUser, sampleRequest));
    }

    @Test
    void addBeneficiary_DuplicateAccountNumber_ThrowsException() {
        Mockito.when(customerRepository.findByUser(sampleUser)).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(accountRepository.findByAccountNumber(sampleRequest.getBeneficiaryAccountNumber()))
                .thenReturn(Optional.empty());
        Mockito.when(beneficiaryRepository.existsByCustomerAndBeneficiaryAccountNumberAndStatusNot(
                sampleCustomer, sampleRequest.getBeneficiaryAccountNumber(), BeneficiaryStatus.DELETED))
                .thenReturn(true); // Duplicate exists

        Assertions.assertThrows(DuplicateBeneficiaryException.class, () -> beneficiaryService.addBeneficiary(sampleUser, sampleRequest));
    }

    @Test
    void getBeneficiaryById_Success() {
        Mockito.when(beneficiaryRepository.findById(beneficiaryId)).thenReturn(Optional.of(sampleBeneficiary));

        BeneficiaryResponse response = beneficiaryService.getBeneficiaryById(beneficiaryId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Jane Smith", response.getBeneficiaryName());
    }

    @Test
    void getBeneficiaryById_NotFound_ThrowsException() {
        Mockito.when(beneficiaryRepository.findById(beneficiaryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> beneficiaryService.getBeneficiaryById(beneficiaryId));
    }

    @Test
    void deleteBeneficiary_Success() {
        Mockito.when(beneficiaryRepository.findById(beneficiaryId)).thenReturn(Optional.of(sampleBeneficiary));
        Mockito.when(beneficiaryRepository.save(Mockito.any(Beneficiary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        beneficiaryService.deleteBeneficiary(beneficiaryId);

        Assertions.assertEquals(BeneficiaryStatus.DELETED, sampleBeneficiary.getStatus());
        Mockito.verify(beneficiaryRepository).save(sampleBeneficiary);
    }
}
