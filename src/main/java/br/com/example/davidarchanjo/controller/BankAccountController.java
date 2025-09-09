package br.com.example.davidarchanjo.controller;

import br.com.example.davidarchanjo.model.dto.BankAccountDTO;
import br.com.example.davidarchanjo.service.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class BankAccountController {

    private final BankAccountService service;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody BankAccountDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        Long accountId = service.createAccount(dto);
        UriComponents uriComponents = uriComponentsBuilder
            .path("/api/v1/accounts/{id}")
            .buildAndExpand(accountId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAccountById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BankAccountDTO>> getAccountsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getAccountsByCustomerId(customerId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getAccountByAccountNumber(@RequestParam String accountNumber, @RequestParam String sortCode) {
        return ResponseEntity.ok(service.getAccountByAccountNumber(accountNumber, sortCode));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAccountBalance(id));
    }

    @GetMapping("/low-balance")
    public ResponseEntity<List<BankAccountDTO>> getAccountsWithLowBalance(@RequestParam BigDecimal threshold) {
        return ResponseEntity.ok(service.getAccountsWithLowBalance(threshold));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @Valid @RequestBody BankAccountDTO dto) {
        service.updateAccount(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/freeze")
    public ResponseEntity<?> freezeAccount(@PathVariable Long id) {
        service.freezeAccount(id);
        return ResponseEntity.ok("Account frozen successfully");
    }

    @PatchMapping("/{id}/unfreeze")
    public ResponseEntity<?> unfreezeAccount(@PathVariable Long id) {
        service.unfreezeAccount(id);
        return ResponseEntity.ok("Account unfrozen successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> closeAccount(@PathVariable Long id) {
        service.closeAccount(id);
        return ResponseEntity.ok("Account closed successfully");
    }

    @PostMapping("/populate")
    public ResponseEntity<?> populateTestAccounts() {
        service.populateTestAccounts();
        return ResponseEntity.ok("Test accounts created successfully");
    }
}
