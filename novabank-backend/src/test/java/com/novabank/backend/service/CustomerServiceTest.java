package com.novabank.backend.service;

import com.novabank.backend.dto.AddressRequest;
import com.novabank.backend.dto.CustomerRequest;
import com.novabank.backend.dto.CustomerResponse;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AddressType;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.Gender;
import com.novabank.backend.enums.MaritalStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AddressRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.impl.CustomerServiceImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link CustomerServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User sampleUser;
    private Customer sampleCustomer;
    private CustomerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        sampleUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@novabank.com")
                .build();
        sampleUser.setId(userId);

        AddressRequest addrReq = AddressRequest.builder()
                .addressType(AddressType.RESIDENTIAL)
                .houseNumber("10A")
                .street("Main Street")
                .city("Chicago")
                .state("IL")
                .country("USA")
                .postalCode("60601")
                .build();

        sampleRequest = CustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 5, 20))
                .phoneNumber("+12025550143")
                .email("john.doe@novabank.com")
                .maritalStatus(MaritalStatus.SINGLE)
                .occupation("Analyst")
                .annualIncome(new BigDecimal("75000.00"))
                .nationality("American")
                .addresses(List.of(addrReq))
                .build();

        sampleCustomer = Customer.builder()
                .user(sampleUser)
                .customerId("CUST000001")
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 5, 20))
                .phoneNumber("+12025550143")
                .email("john.doe@novabank.com")
                .maritalStatus(MaritalStatus.SINGLE)
                .occupation("Analyst")
                .annualIncome(new BigDecimal("75000.00"))
                .nationality("American")
                .status(CustomerStatus.INACTIVE)
                .addresses(new ArrayList<>())
                .build();
        sampleCustomer.setId(UUID.randomUUID());
    }

    @Test
    void createProfile_Success() {
        Mockito.when(customerRepository.existsByUser(sampleUser)).thenReturn(false);
        Mockito.when(customerRepository.findByEmail(sampleRequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(customerRepository.findByPhoneNumber(sampleRequest.getPhoneNumber())).thenReturn(Optional.empty());
        Mockito.when(customerRepository.count()).thenReturn(0L);
        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.createProfile(sampleUser, sampleRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST000001", response.getCustomerId());
        Assertions.assertEquals(CustomerStatus.INACTIVE, response.getStatus());
        Assertions.assertEquals("John", response.getFirstName());
    }

    @Test
    void createProfile_AlreadyExists_ThrowsException() {
        Mockito.when(customerRepository.existsByUser(sampleUser)).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> customerService.createProfile(sampleUser, sampleRequest));
    }

    @Test
    void createProfile_DuplicateEmail_ThrowsException() {
        Mockito.when(customerRepository.existsByUser(sampleUser)).thenReturn(false);
        Mockito.when(customerRepository.findByEmail(sampleRequest.getEmail())).thenReturn(Optional.of(sampleCustomer));

        Assertions.assertThrows(BadRequestException.class, () -> customerService.createProfile(sampleUser, sampleRequest));
    }

    @Test
    void getMyProfile_Success() {
        Mockito.when(customerRepository.findByUser(sampleUser)).thenReturn(Optional.of(sampleCustomer));

        CustomerResponse response = customerService.getMyProfile(sampleUser);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST000001", response.getCustomerId());
    }

    @Test
    void getMyProfile_NotFound_ThrowsException() {
        Mockito.when(customerRepository.findByUser(sampleUser)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.getMyProfile(sampleUser));
    }

    @Test
    void searchCustomer_ById_Success() {
        Mockito.when(customerRepository.findByCustomerId("CUST000001")).thenReturn(Optional.of(sampleCustomer));

        CustomerResponse response = customerService.searchCustomer("customerId", "CUST000001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST000001", response.getCustomerId());
    }
}
