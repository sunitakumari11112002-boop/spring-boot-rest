package br.com.ukbank.infrastructure.repositories;

import br.com.ukbank.domain.model.BankAccount;
import br.com.ukbank.domain.model.Customer;
import br.com.ukbank.domain.valueobjects.AccountIdentifier;
import br.com.ukbank.domain.valueobjects.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BankAccount aggregate following DDD principles
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query("SELECT a FROM BankAccount a WHERE a.identifier.accountNumber = :accountNumber AND a.identifier.sortCode = :sortCode")
    Optional<BankAccount> findByAccountNumberAndSortCode(@Param("accountNumber") String accountNumber, @Param("sortCode") String sortCode);

    List<BankAccount> findByCustomer(Customer customer);

    List<BankAccount> findByCustomerCustomerId(Long customerId);

    List<BankAccount> findByAccountType(BankAccount.AccountType accountType);

    List<BankAccount> findByStatus(BankAccount.AccountStatus status);

    @Query("SELECT a FROM BankAccount a WHERE a.balance.amount < :threshold")
    List<BankAccount> findAccountsWithBalanceBelow(@Param("threshold") Money threshold);

    @Query("SELECT a FROM BankAccount a WHERE a.customer.customerId = :customerId AND a.status = :status")
    List<BankAccount> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") BankAccount.AccountStatus status);

    @Query("SELECT COUNT(a) FROM BankAccount a WHERE a.customer.customerId = :customerId AND a.status = 'ACTIVE'")
    long countActiveAccountsByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT a FROM BankAccount a WHERE a.accountType = 'SAVINGS' AND a.balance.amount > :minBalance")
    List<BankAccount> findHighValueSavingsAccounts(@Param("minBalance") BigDecimal minBalance);

    @Query("SELECT SUM(a.balance.amount) FROM BankAccount a WHERE a.customer.customerId = :customerId AND a.status = 'ACTIVE'")
    BigDecimal getTotalBalanceByCustomer(@Param("customerId") Long customerId);
}
