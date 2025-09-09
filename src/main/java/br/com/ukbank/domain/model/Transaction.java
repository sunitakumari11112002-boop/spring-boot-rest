package br.com.ukbank.domain.model;

import br.com.ukbank.domain.valueobjects.Money;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction entity representing financial transactions
 * Following DDD principles for transaction modeling
 */
@Entity
@Table(name = "transactions")
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount"))
    private Money amount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "balance_after"))
    private Money balanceAfter;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false, unique = true)
    private String transactionReference;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    public enum TransactionType {
        DEBIT, CREDIT
    }

    // Default constructor for JPA
    protected Transaction() {}

    private Transaction(BankAccount account, TransactionType type, Money amount,
                       Money balanceAfter, String description, String reference, String transactionRef) {
        this.account = Objects.requireNonNull(account);
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
        this.balanceAfter = Objects.requireNonNull(balanceAfter);
        this.description = Objects.requireNonNull(description);
        this.reference = Objects.requireNonNull(reference);
        this.transactionReference = Objects.requireNonNull(transactionRef);
        this.processedAt = LocalDateTime.now();
    }

    public static Transaction createDebit(BankAccount account, Money amount, Money balanceAfter,
                                        String description, String reference, String transactionRef) {
        return new Transaction(account, TransactionType.DEBIT, amount, balanceAfter,
                             description, reference, transactionRef);
    }

    public static Transaction createCredit(BankAccount account, Money amount, Money balanceAfter,
                                         String description, String reference, String transactionRef) {
        return new Transaction(account, TransactionType.CREDIT, amount, balanceAfter,
                             description, reference, transactionRef);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction transaction = (Transaction) obj;
        return Objects.equals(transactionId, transaction.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
