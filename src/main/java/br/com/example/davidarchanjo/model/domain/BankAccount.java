package br.com.example.davidarchanjo.model.domain;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true, length = 8)
    private String accountNumber;

    @Column(nullable = false, length = 6)
    private String sortCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(precision = 15, scale = 2)
    private BigDecimal overdraftLimit;

    @DecimalMin(value = "0.00")
    @Column(precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @Builder
    public BankAccount(Long accountId, String accountNumber, String sortCode,
                      AccountType accountType, BigDecimal balance, BigDecimal overdraftLimit,
                      BigDecimal interestRate, AccountStatus status,
                      LocalDateTime openedAt, LocalDateTime closedAt, Customer customer) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountType = accountType;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.interestRate = interestRate;
        this.status = status;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.customer = customer;
    }

    public enum AccountType {
        CURRENT, SAVINGS, ISA, BUSINESS, JOINT
    }

    public enum AccountStatus {
        ACTIVE, DORMANT, FROZEN, CLOSED
    }
}
