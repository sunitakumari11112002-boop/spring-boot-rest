package br.com.example.davidarchanjo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.example.davidarchanjo.model.dto.BankAccountDTO;
import br.com.example.davidarchanjo.model.domain.BankAccount;

public interface BankAccountService {

    Long createAccount(BankAccountDTO dto);

    List<BankAccountDTO> getAllAccounts();

    Optional<BankAccountDTO> getAccountById(Long accountId);

    List<BankAccountDTO> getAccountsByCustomerId(Long customerId);

    Optional<BankAccountDTO> getAccountByAccountNumber(String accountNumber, String sortCode);

    Optional<BankAccountDTO> updateAccount(Long accountId, BankAccountDTO dto);

    void closeAccount(Long accountId);

    void freezeAccount(Long accountId);

    void unfreezeAccount(Long accountId);

    BigDecimal getAccountBalance(Long accountId);

    List<BankAccountDTO> getAccountsWithLowBalance(BigDecimal threshold);

    String generateAccountNumber();

    void populateTestAccounts();
}
