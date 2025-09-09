package br.com.ukbank.application.services;

import br.com.ukbank.domain.model.Customer;
import br.com.ukbank.domain.events.CustomerRegisteredEvent;
import br.com.ukbank.infrastructure.repositories.CustomerRepository;
import br.com.ukbank.application.dto.CustomerRegistrationRequest;
import br.com.ukbank.application.dto.CustomerResponse;
import br.com.ukbank.application.dto.CustomerUpdateRequest;
import br.com.ukbank.application.exceptions.CustomerNotFoundException;
import br.com.ukbank.application.exceptions.DuplicateCustomerException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service for Customer operations
 * Orchestrates domain objects and handles cross-cutting concerns
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerApplicationService {

    private final CustomerRepository customerRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Register a new customer with validation and event publishing
     */
    public CustomerResponse registerCustomer(CustomerRegistrationRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());

        // Business rule validation
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Customer with email already exists: " + request.getEmail());
        }

        if (customerRepository.existsByNationalInsuranceNumber(request.getNationalInsuranceNumber())) {
            throw new DuplicateCustomerException("Customer with NI number already exists");
        }

        // Create domain object using factory method
        Customer customer = Customer.registerNewCustomer(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getDateOfBirth(),
            request.getAddressLine(),
            request.getPostcode(),
            request.getNationalInsuranceNumber()
        );

        // Persist and publish event
        Customer savedCustomer = customerRepository.save(customer);

        CustomerRegisteredEvent event = new CustomerRegisteredEvent(
            savedCustomer.getCustomerId(),
            savedCustomer.getPersonalName().getFirstName(),
            savedCustomer.getPersonalName().getLastName(),
            savedCustomer.getEmail()
        );

        eventPublisher.publish(event);

        log.info("Successfully registered customer with ID: {}", savedCustomer.getCustomerId());
        return CustomerResponse.from(savedCustomer);
    }

    /**
     * Retrieve customer by ID
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        return CustomerResponse.from(customer);
    }

    /**
     * Update customer information
     */
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        // Use domain method for business logic
        customer.updatePersonalInformation(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getAddressLine(),
            request.getPostcode()
        );

        Customer updatedCustomer = customerRepository.save(customer);

        log.info("Successfully updated customer with ID: {}", customerId);
        return CustomerResponse.from(updatedCustomer);
    }

    /**
     * Suspend customer account
     */
    public void suspendCustomer(Long customerId) {
        log.info("Suspending customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        customer.suspend();
        customerRepository.save(customer);

        log.info("Successfully suspended customer with ID: {}", customerId);
    }

    /**
     * Activate suspended customer
     */
    public void activateCustomer(Long customerId) {
        log.info("Activating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        customer.activate();
        customerRepository.save(customer);

        log.info("Successfully activated customer with ID: {}", customerId);
    }

    /**
     * Search customers by name
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomersByName(String name) {
        List<Customer> customers = customerRepository.findByNameContaining(name);

        return customers.stream()
            .map(CustomerResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * Get all active customers
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllActiveCustomers() {
        List<Customer> customers = customerRepository.findByStatus(Customer.CustomerStatus.ACTIVE);

        return customers.stream()
            .map(CustomerResponse::from)
            .collect(Collectors.toList());
    }
}
