package br.com.ukbank.domain.model;

import br.com.ukbank.domain.events.DomainEvent;
import lombok.Getter;

/**
 * Result object for transaction operations
 * Encapsulates success/failure state and domain events
 */
@Getter
public class TransactionResult {

    private final boolean success;
    private final String transactionReference;
    private final String errorMessage;
    private final DomainEvent domainEvent;

    private TransactionResult(boolean success, String transactionReference,
                             String errorMessage, DomainEvent domainEvent) {
        this.success = success;
        this.transactionReference = transactionReference;
        this.errorMessage = errorMessage;
        this.domainEvent = domainEvent;
    }

    public static TransactionResult success(String transactionReference, DomainEvent domainEvent) {
        return new TransactionResult(true, transactionReference, null, domainEvent);
    }

    public static TransactionResult failure(String errorMessage) {
        return new TransactionResult(false, null, errorMessage, null);
    }
}
