package br.com.ukbank.application.dto;

import br.com.ukbank.domain.model.Customer;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for customer information
 * Implements data transfer and mapping patterns
 */
@Data
@Builder
public class CustomerResponse {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String postcode;
    private String nationalInsuranceNumber;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime lastUpdatedAt;
    private int numberOfAccounts;

    /**
     * Factory method to create response from domain entity
     */
    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
            .customerId(customer.getCustomerId())
            .firstName(customer.getPersonalName().getFirstName())
            .lastName(customer.getPersonalName().getLastName())
            .fullName(customer.getPersonalName().getFullName())
            .email(customer.getEmail())
            .phoneNumber(customer.getPhoneNumber().getDisplayFormat())
            .dateOfBirth(customer.getDateOfBirth())
            .address(customer.getAddress().getAddressLine())
            .postcode(customer.getAddress().getPostcode())
            .nationalInsuranceNumber(customer.getNationalInsuranceNumber().getFormattedValue())
            .status(customer.getStatus().name())
            .registeredAt(customer.getRegisteredAt())
            .lastUpdatedAt(customer.getLastUpdatedAt())
            .numberOfAccounts(customer.getBankAccounts().size())
            .build();
    }
}

/**
 * Request DTO for customer updates
 */
@Data
@Builder
public class CustomerUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String addressLine;
    private String postcode;
}
