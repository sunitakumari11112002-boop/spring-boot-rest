package br.com.ukbank.infrastructure.repositories;

import br.com.ukbank.domain.model.Customer;
import br.com.ukbank.domain.valueobjects.NationalInsuranceNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer aggregate following DDD principles
 * Provides domain-focused query methods with business meaning
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.nationalInsuranceNumber.value = :niNumber")
    Optional<Customer> findByNationalInsuranceNumber(@Param("niNumber") String niNumber);

    List<Customer> findByStatus(Customer.CustomerStatus status);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.personalName.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(c.personalName.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> findByNameContaining(@Param("name") String name);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.nationalInsuranceNumber.value = :niNumber")
    boolean existsByNationalInsuranceNumber(@Param("niNumber") String niNumber);

    @Query("SELECT c FROM Customer c WHERE c.status = 'ACTIVE' AND SIZE(c.bankAccounts) = 0")
    List<Customer> findActiveCustomersWithoutAccounts();

    @Query("SELECT c FROM Customer c WHERE c.registeredAt >= CURRENT_DATE - 30")
    List<Customer> findRecentlyRegisteredCustomers();
}
