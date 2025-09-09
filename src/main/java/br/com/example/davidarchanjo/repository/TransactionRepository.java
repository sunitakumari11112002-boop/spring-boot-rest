package br.com.example.davidarchanjo.repository;

import br.com.example.davidarchanjo.model.domain.Transaction;
import br.com.example.davidarchanjo.model.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);

    List<Transaction> findByAccount(BankAccount account);

    List<Transaction> findByAccountAccountId(Long accountId);

    List<Transaction> findByType(Transaction.TransactionType type);

    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountAndDateRange(@Param("accountId") Long accountId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.amount >= :minAmount ORDER BY t.createdAt DESC")
    List<Transaction> findLargeTransactions(@Param("accountId") Long accountId, @Param("minAmount") BigDecimal minAmount);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.accountId = :accountId AND t.type = :type AND t.status = 'COMPLETED'")
    BigDecimal getTotalAmountByAccountAndType(@Param("accountId") Long accountId, @Param("type") Transaction.TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.account.customer.customerId = :customerId ORDER BY t.createdAt DESC")
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);
}
