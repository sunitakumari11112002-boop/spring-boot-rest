package br.com.example.davidarchanjo.controller;

import br.com.example.davidarchanjo.model.dto.TransactionDTO;
import br.com.example.davidarchanjo.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> processTransaction(@Valid @RequestBody TransactionDTO dto) {
        String transactionRef = service.processTransaction(dto);
        return ResponseEntity.ok(Map.of("transactionReference", transactionRef, "status", "completed"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam String toAccountNumber,
            @RequestParam String toSortCode,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reference,
            @RequestParam String payeeName) {
        String transactionRef = service.transfer(fromAccountId, toAccountNumber, toSortCode, amount, reference, payeeName);
        return ResponseEntity.ok(Map.of("transactionReference", transactionRef, "status", "completed"));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        String transactionRef = service.deposit(accountId, amount, description);
        return ResponseEntity.ok(Map.of("transactionReference", transactionRef, "status", "completed"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        String transactionRef = service.withdraw(accountId, amount, description);
        return ResponseEntity.ok(Map.of("transactionReference", transactionRef, "status", "completed"));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(service.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTransactionById(id));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<?> getTransactionByReference(@PathVariable String reference) {
        return ResponseEntity.ok(service.getTransactionByReference(reference));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.getTransactionsByAccountId(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getTransactionsByCustomerId(customerId));
    }

    @GetMapping("/account/{accountId}/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(service.getTransactionsByDateRange(accountId, startDate, endDate));
    }

    @GetMapping("/account/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.getAccountBalance(accountId));
    }

    @GetMapping("/account/{accountId}/large")
    public ResponseEntity<List<TransactionDTO>> getLargeTransactions(
            @PathVariable Long accountId,
            @RequestParam BigDecimal minAmount) {
        return ResponseEntity.ok(service.getLargeTransactions(accountId, minAmount));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        service.cancelTransaction(id);
        return ResponseEntity.ok("Transaction cancelled successfully");
    }

    @PostMapping("/populate")
    public ResponseEntity<?> populateTestTransactions() {
        service.populateTestTransactions();
        return ResponseEntity.ok("Test transactions created successfully");
    }
}
