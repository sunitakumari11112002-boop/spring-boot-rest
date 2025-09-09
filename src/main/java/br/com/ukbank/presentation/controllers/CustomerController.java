package br.com.ukbank.presentation.controllers;

import br.com.ukbank.application.dto.*;
import br.com.ukbank.application.services.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Controller for customer operations
 * Implements proper HTTP semantics and error handling
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerApplicationService customerService;

    /**
     * Register a new customer
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("Received customer registration request for email: {}", request.getEmail());

        CustomerResponse response = customerService.registerCustomer(request);

        log.info("Successfully registered customer with ID: {}", response.getCustomerId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId) {
        log.info("Retrieving customer with ID: {}", customerId);

        CustomerResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer information
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {

        log.info("Updating customer with ID: {}", customerId);

        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Suspend customer account
     */
    @PatchMapping("/{customerId}/suspend")
    public ResponseEntity<Void> suspendCustomer(@PathVariable Long customerId) {
        log.info("Suspending customer with ID: {}", customerId);

        customerService.suspendCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate suspended customer
     */
    @PatchMapping("/{customerId}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long customerId) {
        log.info("Activating customer with ID: {}", customerId);

        customerService.activateCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search customers by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String name) {
        log.info("Searching customers by name: {}", name);

        List<CustomerResponse> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get all active customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllActiveCustomers() {
        log.info("Retrieving all active customers");

        List<CustomerResponse> customers = customerService.getAllActiveCustomers();
        return ResponseEntity.ok(customers);
    }
}
