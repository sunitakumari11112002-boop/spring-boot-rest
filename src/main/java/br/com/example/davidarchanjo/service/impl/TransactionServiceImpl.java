package br.com.example.davidarchanjo.service.impl;

import br.com.example.davidarchanjo.builder.TransactionBuilder;
import br.com.example.davidarchanjo.exception.BankAccountNotFoundException;
import br.com.example.davidarchanjo.exception.InsufficientFundsException;
import br.com.example.davidarchanjo.exception.TransactionNotFoundException;
import br.com.example.davidarchanjo.model.domain.BankAccount;
import br.com.example.davidarchanjo.model.domain.Transaction;
import br.com.example.davidarchanjo.model.dto.TransactionDTO;
import br.com.example.davidarchanjo.repository.BankAccountRepository;
import br.com.example.davidarchanjo.repository.TransactionRepository;
import br.com.example.davidarchanjo.service.TransactionService;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final BankAccountRepository accountRepository;
    private final TransactionBuilder builder;

    @Override
    @Transactional
    public String processTransaction(TransactionDTO dto) {
        BankAccount account = accountRepository.findById(dto.getAccountId())
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (account.getStatus() != BankAccount.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

        BigDecimal newBalance;
        if (isDebitTransaction(dto.getType())) {
            BigDecimal availableBalance = account.getBalance().add(account.getOverdraftLimit() != null ? account.getOverdraftLimit() : BigDecimal.ZERO);
            if (availableBalance.compareTo(dto.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds for transaction");
            }
            newBalance = account.getBalance().subtract(dto.getAmount());
        } else {
            newBalance = account.getBalance().add(dto.getAmount());
        }

        String transactionRef = generateTransactionReference();

        Transaction transaction = Transaction.builder()
            .transactionReference(transactionRef)
            .type(dto.getType())
            .amount(dto.getAmount())
            .balanceAfter(newBalance)
            .description(dto.getDescription())
            .reference(dto.getReference())
            .toAccountNumber(dto.getToAccountNumber())
            .toSortCode(dto.getToSortCode())
            .payeeName(dto.getPayeeName())
            .status(Transaction.TransactionStatus.COMPLETED)
            .createdAt(LocalDateTime.now())
            .processedAt(LocalDateTime.now())
            .account(account)
            .build();

        account.setBalance(newBalance);
        accountRepository.save(account);
        repository.save(transaction);

        return transactionRef;
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return repository.findAll()
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<TransactionDTO> getTransactionById(Long transactionId) {
        return repository.findById(transactionId)
            .map(builder::build);
    }

    @Override
    public Optional<TransactionDTO> getTransactionByReference(String reference) {
        return repository.findByTransactionReference(reference)
            .map(builder::build);
    }

    @Override
    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
        return repository.findByAccountAccountId(accountId)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByAccountAndDateRange(accountId, startDate, endDate)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        return accountRepository.findById(accountId)
            .map(BankAccount::getBalance)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));
    }

    @Override
    @Transactional
    public String transfer(Long fromAccountId, String toAccountNumber, String toSortCode,
                          BigDecimal amount, String reference, String payeeName) {
        TransactionDTO transferOut = TransactionDTO.builder()
            .type(Transaction.TransactionType.TRANSFER_OUT)
            .amount(amount)
            .description("Transfer to " + payeeName)
            .reference(reference)
            .toAccountNumber(toAccountNumber)
            .toSortCode(toSortCode)
            .payeeName(payeeName)
            .accountId(fromAccountId)
            .build();

        return processTransaction(transferOut);
    }

    @Override
    @Transactional
    public String deposit(Long accountId, BigDecimal amount, String description) {
        TransactionDTO deposit = TransactionDTO.builder()
            .type(Transaction.TransactionType.DEPOSIT)
            .amount(amount)
            .description(description)
            .accountId(accountId)
            .build();

        return processTransaction(deposit);
    }

    @Override
    @Transactional
    public String withdraw(Long accountId, BigDecimal amount, String description) {
        TransactionDTO withdrawal = TransactionDTO.builder()
            .type(Transaction.TransactionType.WITHDRAWAL)
            .amount(amount)
            .description(description)
            .accountId(accountId)
            .build();

        return processTransaction(withdrawal);
    }

    @Override
    @Transactional
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = repository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed transaction");
        }

        transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
        repository.save(transaction);
    }

    @Override
    public List<TransactionDTO> getLargeTransactions(Long accountId, BigDecimal minAmount) {
        return repository.findLargeTransactions(accountId, minAmount)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public void populateTestTransactions() {
        val faker = new Faker();
        List<BankAccount> accounts = accountRepository.findAll();

        accounts.forEach(account -> {
            IntStream.range(0, faker.number().numberBetween(5, 20)).forEach(i -> {
                Transaction.TransactionType[] types = {
                    Transaction.TransactionType.DEPOSIT,
                    Transaction.TransactionType.WITHDRAWAL,
                    Transaction.TransactionType.CARD_PAYMENT,
                    Transaction.TransactionType.DIRECT_DEBIT
                };

                Transaction.TransactionType type = types[faker.random().nextInt(types.length)];
                BigDecimal amount = new BigDecimal(faker.number().numberBetween(10, 500));

                Transaction transaction = Transaction.builder()
                    .transactionReference(generateTransactionReference())
                    .type(type)
                    .amount(amount)
                    .balanceAfter(account.getBalance())
                    .description(generateDescription(type, faker))
                    .reference(faker.lorem().word())
                    .status(Transaction.TransactionStatus.COMPLETED)
                    .createdAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)))
                    .processedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)))
                    .account(account)
                    .build();

                repository.save(transaction);
            });
        });
    }

    private boolean isDebitTransaction(Transaction.TransactionType type) {
        return type == Transaction.TransactionType.WITHDRAWAL ||
               type == Transaction.TransactionType.TRANSFER_OUT ||
               type == Transaction.TransactionType.CARD_PAYMENT ||
               type == Transaction.TransactionType.DIRECT_DEBIT ||
               type == Transaction.TransactionType.FEE_DEBIT ||
               type == Transaction.TransactionType.ATM_WITHDRAWAL;
    }

    private String generateTransactionReference() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String generateDescription(Transaction.TransactionType type, Faker faker) {
        switch (type) {
            case DEPOSIT:
                return "Cash deposit at branch";
            case WITHDRAWAL:
                return "Cash withdrawal";
            case CARD_PAYMENT:
                return "Card payment to " + faker.company().name();
            case DIRECT_DEBIT:
                return "Direct debit - " + faker.company().name();
            default:
                return "Transaction";
        }
    }
}
