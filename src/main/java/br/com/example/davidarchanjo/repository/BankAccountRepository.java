package br.com.example.davidarchanjo.repository;

import br.com.example.davidarchanjo.model.domain.BankAccount;
import br.com.example.davidarchanjo.model.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumberAndSortCode(String accountNumber, String sortCode);

    List<BankAccount> findByCustomer(Customer customer);

    List<BankAccount> findByCustomerCustomerId(Long customerId);

    List<BankAccount> findByAccountType(BankAccount.AccountType accountType);

    List<BankAccount> findByStatus(BankAccount.AccountStatus status);

    @Query("SELECT a FROM BankAccount a WHERE a.balance < :threshold")
    List<BankAccount> findAccountsWithBalanceBelow(@Param("threshold") BigDecimal threshold);

    @Query("SELECT a FROM BankAccount a WHERE a.customer.customerId = :customerId AND a.status = :status")
    List<BankAccount> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") BankAccount.AccountStatus status);

    boolean existsByAccountNumberAndSortCode(String accountNumber, String sortCode);
}
