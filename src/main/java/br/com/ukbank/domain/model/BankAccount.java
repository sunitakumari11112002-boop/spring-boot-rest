package br.com.ukbank.domain.model;

import br.com.ukbank.domain.valueobjects.*;
import br.com.ukbank.domain.events.TransactionProcessedEvent;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * BankAccount aggregate root following DDD principles
 * Encapsulates account business logic and transaction processing
 */
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Embedded
    private AccountIdentifier identifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "balance"))
    private Money balance;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "overdraft_limit"))
    private Money overdraftLimit;

    @Column(precision = 5, scale = 4)
    private java.math.BigDecimal interestRate;

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
    private List<Transaction> transactions = new ArrayList<>();

    // Default constructor for JPA
    protected BankAccount() {}

    // Private constructor - use factory methods
    private BankAccount(Customer customer, AccountType accountType, Money initialDeposit, Money overdraftLimit) {
        this.customer = Objects.requireNonNull(customer, "Customer is required");
        this.accountType = Objects.requireNonNull(accountType, "Account type is required");
        this.identifier = AccountIdentifier.ukBankAccount(generateAccountNumber());
        this.balance = initialDeposit != null ? initialDeposit : Money.zero();
        this.overdraftLimit = overdraftLimit;
        this.interestRate = getDefaultInterestRate(accountType);
        this.status = AccountStatus.ACTIVE;
        this.openedAt = LocalDateTime.now();

        validateBusinessRules();
    }

    /**
     * Factory method for opening new bank accounts
     */
    public static BankAccount openAccount(Customer customer, AccountType accountType,
                                        Money initialDeposit, Money overdraftLimit) {
        if (customer.getStatus() != Customer.CustomerStatus.ACTIVE) {
            throw new IllegalStateException("Cannot open account for inactive customer");
        }

        return new BankAccount(customer, accountType, initialDeposit, overdraftLimit);
    }

    /**
     * Business method to process a debit transaction
     */
    public TransactionResult processDebit(Money amount, String description, String reference) {
        validateAccountActive();

        Money availableBalance = calculateAvailableBalance();
        if (amount.isGreaterThan(availableBalance)) {
            return TransactionResult.failure("Insufficient funds");
        }

        this.balance = this.balance.subtract(amount);
        String transactionRef = generateTransactionReference();

        Transaction transaction = Transaction.createDebit(this, amount, this.balance, description, reference, transactionRef);
        this.transactions.add(transaction);

        // Domain event for audit trail
        TransactionProcessedEvent event = new TransactionProcessedEvent(
            this.accountId, transactionRef, "DEBIT", amount, this.balance);

        return TransactionResult.success(transactionRef, event);
    }

    /**
     * Business method to process a credit transaction
     */
    public TransactionResult processCredit(Money amount, String description, String reference) {
        validateAccountActive();

        this.balance = this.balance.add(amount);
        String transactionRef = generateTransactionReference();

        Transaction transaction = Transaction.createCredit(this, amount, this.balance, description, reference, transactionRef);
        this.transactions.add(transaction);

        // Domain event for audit trail
        TransactionProcessedEvent event = new TransactionProcessedEvent(
            this.accountId, transactionRef, "CREDIT", amount, this.balance);

        return TransactionResult.success(transactionRef, event);
    }

    /**
     * Business method to freeze the account
     */
    public void freeze(String reason) {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot freeze a closed account");
        }
        this.status = AccountStatus.FROZEN;
    }

    /**
     * Business method to unfreeze the account
     */
    public void unfreeze() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot unfreeze a closed account");
        }
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * Business method to close the account
     */
    public void close() {
        if (!this.balance.isZero()) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    // Helper methods
    private Money calculateAvailableBalance() {
        Money available = this.balance;
        if (this.overdraftLimit != null && this.overdraftLimit.isPositive()) {
            available = available.add(this.overdraftLimit);
        }
        return available;
    }

    private void validateAccountActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active for transactions");
        }
    }

    private void validateBusinessRules() {
        if (this.balance != null && !this.balance.isGreaterThanOrEqual(Money.zero())) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }

    private String generateAccountNumber() {
        return String.format("%08d", ThreadLocalRandom.current().nextInt(10000000, 99999999));
    }

    private String generateTransactionReference() {
        return "TXN" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private java.math.BigDecimal getDefaultInterestRate(AccountType accountType) {
        switch (accountType) {
            case SAVINGS:
                return new java.math.BigDecimal("0.0125"); // 1.25%
            case ISA:
                return new java.math.BigDecimal("0.015"); // 1.5%
            default:
                return java.math.BigDecimal.ZERO;
        }
    }

    // Getters
    public Long getAccountId() { return accountId; }
    public AccountIdentifier getIdentifier() { return identifier; }
    public AccountType getAccountType() { return accountType; }
    public Money getBalance() { return balance; }
    public Money getOverdraftLimit() { return overdraftLimit; }
    public java.math.BigDecimal getInterestRate() { return interestRate; }
    public AccountStatus getStatus() { return status; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public Customer getCustomer() { return customer; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public enum AccountType {
        CURRENT("Current Account"),
        SAVINGS("Savings Account"),
        ISA("Individual Savings Account"),
        BUSINESS("Business Account"),
        JOINT("Joint Account");

        private final String displayName;

        AccountType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    public enum AccountStatus {
        ACTIVE, FROZEN, CLOSED
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankAccount that = (BankAccount) obj;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}

/**
 * Result object for transaction operations
 * Follows the Result pattern for handling success/failure scenarios
 */
class TransactionResult {
    private final boolean success;
    private final String transactionReference;
    private final String errorMessage;
    private final TransactionProcessedEvent event;

    private TransactionResult(boolean success, String transactionReference, String errorMessage, TransactionProcessedEvent event) {
        this.success = success;
        this.transactionReference = transactionReference;
        this.errorMessage = errorMessage;
        this.event = event;
    }

    public static TransactionResult success(String transactionReference, TransactionProcessedEvent event) {
        return new TransactionResult(true, transactionReference, null, event);
    }

    public static TransactionResult failure(String errorMessage) {
        return new TransactionResult(false, null, errorMessage, null);
    }

    public boolean isSuccess() { return success; }
    public String getTransactionReference() { return transactionReference; }
    public String getErrorMessage() { return errorMessage; }
    public TransactionProcessedEvent getEvent() { return event; }
}
