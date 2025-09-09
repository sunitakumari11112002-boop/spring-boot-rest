package br.com.example.davidarchanjo.service.impl;

import br.com.example.davidarchanjo.builder.BankAccountBuilder;
import br.com.example.davidarchanjo.exception.BankAccountNotFoundException;
import br.com.example.davidarchanjo.exception.CustomerNotFoundException;
import br.com.example.davidarchanjo.model.domain.BankAccount;
import br.com.example.davidarchanjo.model.domain.Customer;
import br.com.example.davidarchanjo.model.dto.BankAccountDTO;
import br.com.example.davidarchanjo.repository.BankAccountRepository;
import br.com.example.davidarchanjo.repository.CustomerRepository;
import br.com.example.davidarchanjo.service.BankAccountService;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repository;
    private final CustomerRepository customerRepository;
    private final BankAccountBuilder builder;

    @Override
    @Transactional
    public Long createAccount(BankAccountDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        BankAccount account = BankAccount.builder()
            .accountNumber(generateAccountNumber())
            .sortCode("40-00-01") // Standard UK sort code for our bank
            .accountType(dto.getAccountType())
            .balance(dto.getInitialDeposit() != null ? dto.getInitialDeposit() : BigDecimal.ZERO)
            .overdraftLimit(dto.getOverdraftLimit())
            .interestRate(getDefaultInterestRate(dto.getAccountType()))
            .status(BankAccount.AccountStatus.ACTIVE)
            .openedAt(LocalDateTime.now())
            .customer(customer)
            .build();

        return repository.save(account).getAccountId();
    }

    @Override
    public List<BankAccountDTO> getAllAccounts() {
        return repository.findAll()
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<BankAccountDTO> getAccountById(Long accountId) {
        return repository.findById(accountId)
            .map(builder::build);
    }

    @Override
    public List<BankAccountDTO> getAccountsByCustomerId(Long customerId) {
        return repository.findByCustomerCustomerId(customerId)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<BankAccountDTO> getAccountByAccountNumber(String accountNumber, String sortCode) {
        return repository.findByAccountNumberAndSortCode(accountNumber, sortCode)
            .map(builder::build);
    }

    @Override
    @Transactional
    public Optional<BankAccountDTO> updateAccount(Long accountId, BankAccountDTO dto) {
        return Optional.of(repository.findById(accountId)
            .map(account -> {
                if (dto.getOverdraftLimit() != null) {
                    account.setOverdraftLimit(dto.getOverdraftLimit());
                }
                return repository.save(account);
            })
            .map(builder::build)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found")));
    }

    @Override
    @Transactional
    public void closeAccount(Long accountId) {
        BankAccount account = repository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }

        account.setStatus(BankAccount.AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        repository.save(account);
    }

    @Override
    @Transactional
    public void freezeAccount(Long accountId) {
        BankAccount account = repository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        account.setStatus(BankAccount.AccountStatus.FROZEN);
        repository.save(account);
    }

    @Override
    @Transactional
    public void unfreezeAccount(Long accountId) {
        BankAccount account = repository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        account.setStatus(BankAccount.AccountStatus.ACTIVE);
        repository.save(account);
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        return repository.findById(accountId)
            .map(BankAccount::getBalance)
            .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));
    }

    @Override
    public List<BankAccountDTO> getAccountsWithLowBalance(BigDecimal threshold) {
        return repository.findAccountsWithBalanceBelow(threshold)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%08d", ThreadLocalRandom.current().nextInt(10000000, 99999999));
        } while (repository.existsByAccountNumberAndSortCode(accountNumber, "40-00-01"));

        return accountNumber;
    }

    @Override
    public void populateTestAccounts() {
        val faker = new Faker();
        List<Customer> customers = customerRepository.findAll();

        customers.forEach(customer -> {
            // Create a current account for each customer
            BankAccount currentAccount = BankAccount.builder()
                .accountNumber(generateAccountNumber())
                .sortCode("40-00-01")
                .accountType(BankAccount.AccountType.CURRENT)
                .balance(new BigDecimal(faker.number().numberBetween(100, 10000)))
                .overdraftLimit(new BigDecimal(faker.number().numberBetween(500, 2000)))
                .interestRate(BigDecimal.ZERO)
                .status(BankAccount.AccountStatus.ACTIVE)
                .openedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 365)))
                .customer(customer)
                .build();

            repository.save(currentAccount);

            // 30% chance to create a savings account
            if (faker.random().nextDouble() < 0.3) {
                BankAccount savingsAccount = BankAccount.builder()
                    .accountNumber(generateAccountNumber())
                    .sortCode("40-00-01")
                    .accountType(BankAccount.AccountType.SAVINGS)
                    .balance(new BigDecimal(faker.number().numberBetween(1000, 50000)))
                    .interestRate(new BigDecimal("0.0125"))
                    .status(BankAccount.AccountStatus.ACTIVE)
                    .openedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 365)))
                    .customer(customer)
                    .build();

                repository.save(savingsAccount);
            }
        });
    }

    private BigDecimal getDefaultInterestRate(BankAccount.AccountType accountType) {
        switch (accountType) {
            case SAVINGS:
                return new BigDecimal("0.0125"); // 1.25%
            case ISA:
                return new BigDecimal("0.015"); // 1.5%
            default:
                return BigDecimal.ZERO;
        }
    }
}
