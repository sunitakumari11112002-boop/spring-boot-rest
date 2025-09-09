package br.com.ukbank.domain.model;

import br.com.ukbank.domain.valueobjects.*;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Customer aggregate root following DDD principles
 * Encapsulates customer business logic and maintains invariants
 */
@Entity
@Table(name = "customers")
public class Customer {

    // Getters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
        @AttributeOverride(name = "lastName", column = @Column(name = "last_name"))
    })
    private PersonalName personalName;

    @Getter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter
    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    private UKPhoneNumber phoneNumber;

    @Getter
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine", column = @Column(name = "address_line")),
        @AttributeOverride(name = "postcode", column = @Column(name = "postcode"))
    })
    private UKAddress address;

    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "national_insurance_number"))
    private NationalInsuranceNumber nationalInsuranceNumber;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Getter
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Getter
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    // Default constructor for JPA
    protected Customer() {}

    // Private constructor - use factory methods
    private Customer(PersonalName personalName, String email, UKPhoneNumber phoneNumber,
                    LocalDate dateOfBirth, UKAddress address, NationalInsuranceNumber nin) {
        this.personalName = Objects.requireNonNull(personalName, "Personal name is required");
        this.email = validateEmail(email);
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number is required");
        this.dateOfBirth = validateDateOfBirth(dateOfBirth);
        this.address = Objects.requireNonNull(address, "Address is required");
        this.nationalInsuranceNumber = Objects.requireNonNull(nin, "National Insurance Number is required");
        this.status = CustomerStatus.ACTIVE;
        this.registeredAt = LocalDateTime.now();
    }

    /**
     * Factory method for creating new customers
     */
    public static Customer registerNewCustomer(String firstName, String lastName, String email,
                                             String phoneNumber, LocalDate dateOfBirth,
                                             String addressLine, String postcode, String nin) {

        PersonalName name = PersonalName.of(firstName, lastName);
        UKPhoneNumber phone = UKPhoneNumber.of(phoneNumber);
        UKAddress addr = UKAddress.of(addressLine, postcode);
        NationalInsuranceNumber niNumber = NationalInsuranceNumber.of(nin);

        return new Customer(name, email, phone, dateOfBirth, addr, niNumber);
    }

    /**
     * Business method to update customer information
     */
    public void updatePersonalInformation(String firstName, String lastName, String email,
                                        String phoneNumber, String addressLine, String postcode) {
        this.personalName = PersonalName.of(firstName, lastName);
        this.email = validateEmail(email);
        this.phoneNumber = UKPhoneNumber.of(phoneNumber);
        this.address = UKAddress.of(addressLine, postcode);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Business method to suspend customer account
     */
    public void suspend() {
        if (this.status == CustomerStatus.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed customer account");
        }
        this.status = CustomerStatus.SUSPENDED;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Business method to activate suspended customer
     */
    public void activate() {
        if (this.status == CustomerStatus.CLOSED) {
            throw new IllegalStateException("Cannot activate a closed customer account");
        }
        this.status = CustomerStatus.ACTIVE;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Business method to add a bank account
     */
    public void addBankAccount(BankAccount account) {
        if (this.status != CustomerStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add account to inactive customer");
        }
        this.bankAccounts.add(account);
    }

    // Validation methods
    private String validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.toLowerCase();
    }

    private LocalDate validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Customer must be at least 18 years old");
        }
        return dateOfBirth;
    }

    public List<BankAccount> getBankAccounts() { return Collections.unmodifiableList(bankAccounts); }

    public enum CustomerStatus {
        ACTIVE, SUSPENDED, CLOSED
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
