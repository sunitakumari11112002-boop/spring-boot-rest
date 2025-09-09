package br.com.example.davidarchanjo.controller;

import br.com.example.davidarchanjo.model.dto.CustomerDTO;
import br.com.example.davidarchanjo.model.domain.Customer;
import br.com.example.davidarchanjo.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        Long customerId = service.createCustomer(dto);
        UriComponents uriComponents = uriComponentsBuilder
            .path("/api/v1/customers/{id}")
            .buildAndExpand(customerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(service.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String name) {
        return ResponseEntity.ok(service.searchCustomersByName(name));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getCustomerByEmail(email));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerDTO>> getCustomersByStatus(@PathVariable Customer.CustomerStatus status) {
        return ResponseEntity.ok(service.getCustomersByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        service.updateCustomer(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomerById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/populate")
    public ResponseEntity<?> populateTestCustomers() {
        service.populateTestCustomers();
        return ResponseEntity.ok("Test customers created successfully");
    }
}
