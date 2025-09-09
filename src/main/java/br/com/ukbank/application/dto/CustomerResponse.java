package br.com.ukbank.application.dto;

import br.com.ukbank.domain.model.Customer;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Customer information
 * Implements data transfer patterns with proper encapsulation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String addressLine;
    private String postcode;
    private String nationalInsuranceNumber;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime lastUpdatedAt;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
            .customerId(customer.getCustomerId())
            .firstName(customer.getPersonalName().getFirstName())
            .lastName(customer.getPersonalName().getLastName())
            .fullName(customer.getPersonalName().getFullName())
            .email(customer.getEmail())
            .phoneNumber(customer.getPhoneNumber().getNumber())
            .dateOfBirth(customer.getDateOfBirth())
            .addressLine(customer.getAddress().getAddressLine())
            .postcode(customer.getAddress().getPostcode())
            .nationalInsuranceNumber(customer.getNationalInsuranceNumber().getValue())
            .status(customer.getStatus().name())
            .registeredAt(customer.getRegisteredAt())
            .lastUpdatedAt(customer.getLastUpdatedAt())
            .build();
    }
}
