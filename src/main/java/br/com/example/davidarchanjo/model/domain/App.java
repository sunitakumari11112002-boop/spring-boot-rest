package br.com.example.davidarchanjo.model.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^\\+44[0-9]{10}$", message = "Invalid UK phone number")
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String postcode;

    @Pattern(regexp = "^[A-Z]{2}[0-9]{6}[A-Z]$", message = "Invalid UK National Insurance Number")
    @Column(nullable = false, unique = true, length = 9)
    private String nationalInsuranceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BankAccount> accounts;

    @Builder
    public Customer(Long customerId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth, String address, String postcode,
                   String nationalInsuranceNumber, CustomerStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.postcode = postcode;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum CustomerStatus {
        ACTIVE, INACTIVE, SUSPENDED, CLOSED
    }
}
