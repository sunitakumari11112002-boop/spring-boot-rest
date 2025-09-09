package br.com.ukbank.domain.model;

import br.com.ukbank.domain.events.TransactionProcessedEvent;

/**
 * Result object for transaction operations
 * Follows the Result pattern for handling success/failure scenarios
 */
public class TransactionResult {
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
