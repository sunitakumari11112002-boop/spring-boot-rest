package br.com.ukbank.domain.model;

import br.com.ukbank.domain.valueobjects.Money;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction entity following DDD principles
 * Represents immutable transaction records with audit trail
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false, unique = true, length = 20)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount"))
    private Money amount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "balance_after"))
    private Money balanceAfter;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(length = 100)
    private String reference;

    @Column(length = 8)
    private String toAccountNumber;

    @Column(length = 6)
    private String toSortCode;

    @Column(length = 100)
    private String payeeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    // Default constructor for JPA
    protected Transaction() {}

    // Private constructor - use factory methods
    private Transaction(BankAccount account, TransactionType type, Money amount, Money balanceAfter,
                       String description, String reference, String transactionReference) {
        this.account = Objects.requireNonNull(account, "Account is required");
        this.type = Objects.requireNonNull(type, "Transaction type is required");
        this.amount = Objects.requireNonNull(amount, "Amount is required");
        this.balanceAfter = Objects.requireNonNull(balanceAfter, "Balance after is required");
        this.description = validateDescription(description);
        this.reference = reference;
        this.transactionReference = Objects.requireNonNull(transactionReference, "Transaction reference is required");
        this.status = TransactionStatus.COMPLETED;
        this.createdAt = LocalDateTime.now();
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Factory method for creating debit transactions
     */
    public static Transaction createDebit(BankAccount account, Money amount, Money balanceAfter,
                                        String description, String reference, String transactionReference) {
        return new Transaction(account, TransactionType.DEBIT, amount, balanceAfter, description, reference, transactionReference);
    }

    /**
     * Factory method for creating credit transactions
     */
    public static Transaction createCredit(BankAccount account, Money amount, Money balanceAfter,
                                         String description, String reference, String transactionReference) {
        return new Transaction(account, TransactionType.CREDIT, amount, balanceAfter, description, reference, transactionReference);
    }

    /**
     * Factory method for creating transfer transactions
     */
    public static Transaction createTransfer(BankAccount account, Money amount, Money balanceAfter,
                                           String toAccountNumber, String toSortCode, String payeeName,
                                           String reference, String transactionReference) {
        Transaction transaction = new Transaction(account, TransactionType.TRANSFER_OUT, amount, balanceAfter,
                                                "Transfer to " + payeeName, reference, transactionReference);
        transaction.toAccountNumber = toAccountNumber;
        transaction.toSortCode = toSortCode;
        transaction.payeeName = payeeName;
        return transaction;
    }

    /**
     * Business method to cancel a pending transaction
     */
    public void cancel() {
        if (this.status == TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed transaction");
        }
        this.status = TransactionStatus.CANCELLED;
    }

    /**
     * Business method to mark transaction as failed
     */
    public void markAsFailed(String reason) {
        if (this.status == TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot mark completed transaction as failed");
        }
        this.status = TransactionStatus.FAILED;
    }

    // Validation methods
    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description is required");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Description must not exceed 255 characters");
        }
        return description.trim();
    }

    // Getters
    public Long getTransactionId() { return transactionId; }
    public String getTransactionReference() { return transactionReference; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public Money getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public String getReference() { return reference; }
    public String getToAccountNumber() { return toAccountNumber; }
    public String getToSortCode() { return toSortCode; }
    public String getPayeeName() { return payeeName; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public BankAccount getAccount() { return account; }

    public enum TransactionType {
        CREDIT("Credit"),
        DEBIT("Debit"),
        TRANSFER_OUT("Transfer Out"),
        TRANSFER_IN("Transfer In"),
        DIRECT_DEBIT("Direct Debit"),
        STANDING_ORDER("Standing Order"),
        CARD_PAYMENT("Card Payment"),
        INTEREST_CREDIT("Interest Credit"),
        FEE_DEBIT("Fee Debit"),
        ATM_WITHDRAWAL("ATM Withdrawal");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    public enum TransactionStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        CANCELLED("Cancelled");

        private final String displayName;

        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return String.format("Transaction[ref=%s, type=%s, amount=%s, status=%s]",
                transactionReference, type, amount, status);
    }
}
