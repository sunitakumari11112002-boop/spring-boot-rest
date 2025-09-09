package br.com.ukbank.application.services;

import br.com.ukbank.application.dto.*;
import br.com.ukbank.application.exceptions.*;
import br.com.ukbank.domain.model.*;
import br.com.ukbank.domain.valueobjects.Money;
import br.com.ukbank.infrastructure.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for banking account operations
 * Coordinates domain logic and manages transactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankingAccountService {

    private final BankAccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Opens a new bank account
     */
    public BankAccountResponse openAccount(AccountOpeningRequest request) {
        log.info("Opening {} account for customer ID: {}", request.getAccountType(), request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + request.getCustomerId()));

        Money initialDeposit = request.getInitialDeposit() != null
            ? Money.of(request.getInitialDeposit()) : null;
        Money overdraftLimit = request.getOverdraftLimit() != null
            ? Money.of(request.getOverdraftLimit()) : null;

        // Use domain factory method
        BankAccount account = BankAccount.openAccount(
            customer,
            request.getAccountType(),
            initialDeposit,
            overdraftLimit
        );

        BankAccount savedAccount = accountRepository.save(account);

        log.info("Successfully opened account with ID: {}", savedAccount.getAccountId());
        return BankAccountResponse.from(savedAccount);
    }

    /**
     * Processes money transfer between accounts
     */
    public TransferResponse transferMoney(MoneyTransferRequest request) {
        log.info("Processing transfer from account ID {} to {}-{}",
            request.getFromAccountId(), request.getToSortCode(), request.getToAccountNumber());

        BankAccount fromAccount = accountRepository.findById(request.getFromAccountId())
            .orElseThrow(() -> new BankAccountNotFoundException("From account not found"));

        Money transferAmount = Money.of(request.getAmount());

        // Process debit on source account
        String description = "Transfer to " + request.getPayeeName();
        String reference = request.getReference();

        TransactionResult debitResult = fromAccount.processDebit(transferAmount, description, reference);

        if (!debitResult.isSuccess()) {
            throw new InsufficientFundsException(debitResult.getErrorMessage());
        }

        // Save the account state
        accountRepository.save(fromAccount);

        // Publish domain event
        if (debitResult.getDomainEvent() != null) {
            eventPublisher.publish(debitResult.getDomainEvent());
        }

        log.info("Successfully processed transfer with reference: {}", debitResult.getTransactionReference());

        return TransferResponse.builder()
            .transactionReference(debitResult.getTransactionReference())
            .status("COMPLETED")
            .fromAccountId(request.getFromAccountId())
            .amount(request.getAmount())
            .payeeName(request.getPayeeName())
            .reference(request.getReference())
            .build();
    }

    /**
     * Retrieves account details by ID
     */
    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(Long accountId) {
        log.info("Retrieving account details for ID: {}", accountId);

        BankAccount account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        return BankAccountResponse.from(account);
    }

    /**
     * Gets all accounts for a customer
     */
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAccountsByCustomerId(Long customerId) {
        log.info("Retrieving accounts for customer ID: {}", customerId);

        List<BankAccount> accounts = accountRepository.findByCustomerId(customerId);

        return accounts.stream()
            .map(BankAccountResponse::from)
            .collect(Collectors.toList());
    }
}
