package br.com.ukbank.application.services;

import br.com.ukbank.domain.model.BankAccount;
import br.com.ukbank.domain.model.Customer;
import br.com.ukbank.domain.valueobjects.Money;
import br.com.ukbank.infrastructure.repositories.BankAccountRepository;
import br.com.ukbank.infrastructure.repositories.CustomerRepository;
import br.com.ukbank.application.dto.*;
import br.com.ukbank.application.exceptions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service for Banking Account operations
 * Implements Command Query Responsibility Segregation (CQRS) pattern
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BankingAccountService {

    private final BankAccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Open a new bank account for customer
     */
    public BankAccountResponse openAccount(AccountOpeningRequest request) {
        log.info("Opening new {} account for customer: {}",
                request.getAccountType(), request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + request.getCustomerId()));

        Money initialDeposit = request.getInitialDeposit() != null ?
            Money.pounds(request.getInitialDeposit()) : Money.zero();
        Money overdraftLimit = request.getOverdraftLimit() != null ?
            Money.pounds(request.getOverdraftLimit()) : null;

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
     * Process money transfer between accounts
     */
    public TransferResponse processTransfer(MoneyTransferRequest request) {
        log.info("Processing transfer from account {} to {}-{}",
                request.getFromAccountId(), request.getToSortCode(), request.getToAccountNumber());

        BankAccount fromAccount = accountRepository.findById(request.getFromAccountId())
            .orElseThrow(() -> new BankAccountNotFoundException("Source account not found"));

        Money transferAmount = Money.pounds(request.getAmount());

        // Use domain method for business logic
        TransactionResult result = fromAccount.processDebit(
            transferAmount,
            "Transfer to " + request.getPayeeName(),
            request.getReference()
        );

        if (!result.isSuccess()) {
            throw new InsufficientFundsException(result.getErrorMessage());
        }

        accountRepository.save(fromAccount);

        // Publish domain event
        eventPublisher.publish(result.getEvent());

        log.info("Successfully processed transfer with reference: {}", result.getTransactionReference());
        return TransferResponse.builder()
            .transactionReference(result.getTransactionReference())
            .status("COMPLETED")
            .fromAccountBalance(fromAccount.getBalance().getAmount())
            .build();
    }

    /**
     * Freeze account for security purposes
     */
    public void freezeAccount(Long accountId, String reason) {
        log.info("Freezing account: {} for reason: {}", accountId, reason);

        BankAccount account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        account.freeze(reason);
        accountRepository.save(account);

        log.info("Successfully froze account: {}", accountId);
    }

    /**
     * Get account details by ID
     */
    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(Long accountId) {
        BankAccount account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        return BankAccountResponse.from(account);
    }

    /**
     * Get all accounts for a customer
     */
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getCustomerAccounts(Long customerId) {
        List<BankAccount> accounts = accountRepository.findByCustomerCustomerId(customerId);

        return accounts.stream()
            .map(BankAccountResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * Get accounts with low balance
     */
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getLowBalanceAccounts(double thresholdAmount) {
        Money threshold = Money.pounds(thresholdAmount);
        List<BankAccount> accounts = accountRepository.findAccountsWithBalanceBelow(threshold);

        return accounts.stream()
            .map(BankAccountResponse::from)
            .collect(Collectors.toList());
    }
}
