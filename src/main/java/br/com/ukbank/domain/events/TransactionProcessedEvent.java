package br.com.ukbank.domain.events;

import br.com.ukbank.domain.valueobjects.Money;

/**
 * Domain event fired when a transaction is processed
 * Used for audit trails and event sourcing
 */
public class TransactionProcessedEvent extends DomainEvent {

    private final Long accountId;
    private final String transactionReference;
    private final String transactionType;
    private final Money amount;
    private final Money balanceAfter;

    public TransactionProcessedEvent(Long accountId, String transactionReference,
                                   String transactionType, Money amount, Money balanceAfter) {
        super("TRANSACTION_PROCESSED");
        this.accountId = accountId;
        this.transactionReference = transactionReference;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getBalanceAfter() {
        return balanceAfter;
    }
}
