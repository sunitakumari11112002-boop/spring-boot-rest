package com.ukbank.presentation.controllers;

import com.ukbank.application.services.CustomerApplicationService;
import com.ukbank.application.dto.CustomerRegistrationRequest;
import com.ukbank.application.dto.CustomerResponse;
import com.ukbank.application.dto.CustomerUpdateRequest;
import com.ukbank.domain.model.Customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Customer operations following Clean Architecture
 * Handles HTTP concerns and delegates to Application Services
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerApplicationService customerService;

    /**
     * Register a new customer
     * POST /api/v1/customers
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request,
            UriComponentsBuilder uriBuilder) {

        log.info("Received customer registration request for email: {}", request.getEmail());

        CustomerResponse response = customerService.registerCustomer(request);

        URI location = uriBuilder
            .path("/api/v1/customers/{id}")
            .buildAndExpand(response.getCustomerId())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Get customer by ID
     * GET /api/v1/customers/{id}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long customerId) {
        log.debug("Fetching customer with ID: {}", customerId);

        CustomerResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer information
     * PUT /api/v1/customers/{id}
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
     * Search customers by name
     * GET /api/v1/customers/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String name) {
        log.debug("Searching customers by name: {}", name);

        List<CustomerResponse> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get all active customers
     * GET /api/v1/customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllActiveCustomers() {
        log.debug("Fetching all active customers");

        List<CustomerResponse> customers = customerService.getAllActiveCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Suspend customer account
     * PATCH /api/v1/customers/{id}/suspend
     */
    @PatchMapping("/{customerId}/suspend")
    public ResponseEntity<Void> suspendCustomer(@PathVariable Long customerId) {
        log.info("Suspending customer with ID: {}", customerId);

        customerService.suspendCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Activate suspended customer
     * PATCH /api/v1/customers/{id}/activate
     */
    @PatchMapping("/{customerId}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long customerId) {
        log.info("Activating customer with ID: {}", customerId);

        customerService.activateCustomer(customerId);
        return ResponseEntity.ok().build();
    }
}
