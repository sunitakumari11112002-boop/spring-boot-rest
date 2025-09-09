package br.com.example.davidarchanjo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.example.davidarchanjo.model.dto.TransactionDTO;
import br.com.example.davidarchanjo.model.domain.Transaction;

public interface TransactionService {

    String processTransaction(TransactionDTO dto);

    List<TransactionDTO> getAllTransactions();

    Optional<TransactionDTO> getTransactionById(Long transactionId);

    Optional<TransactionDTO> getTransactionByReference(String reference);

    List<TransactionDTO> getTransactionsByAccountId(Long accountId);

    List<TransactionDTO> getTransactionsByCustomerId(Long customerId);

    List<TransactionDTO> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getAccountBalance(Long accountId);

    String transfer(Long fromAccountId, String toAccountNumber, String toSortCode,
                   BigDecimal amount, String reference, String payeeName);

    String deposit(Long accountId, BigDecimal amount, String description);

    String withdraw(Long accountId, BigDecimal amount, String description);

    void cancelTransaction(Long transactionId);

    List<TransactionDTO> getLargeTransactions(Long accountId, BigDecimal minAmount);

    void populateTestTransactions();
}
