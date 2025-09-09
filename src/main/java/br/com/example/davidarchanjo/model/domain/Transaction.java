package br.com.example.davidarchanjo.model.domain;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
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

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @NotBlank
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

    @Builder
    public Transaction(Long transactionId, String transactionReference, TransactionType type,
                      BigDecimal amount, BigDecimal balanceAfter, String description, String reference,
                      String toAccountNumber, String toSortCode, String payeeName,
                      TransactionStatus status, LocalDateTime createdAt, LocalDateTime processedAt,
                      BankAccount account) {
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.reference = reference;
        this.toAccountNumber = toAccountNumber;
        this.toSortCode = toSortCode;
        this.payeeName = payeeName;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.account = account;
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT,
        DIRECT_DEBIT, STANDING_ORDER, CARD_PAYMENT,
        INTEREST_CREDIT, FEE_DEBIT, ATM_WITHDRAWAL
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
}
