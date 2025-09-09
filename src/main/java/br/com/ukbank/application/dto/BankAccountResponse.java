package br.com.ukbank.application.dto;

import br.com.ukbank.domain.model.BankAccount;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Banking Account information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {

    private Long accountId;
    private String accountNumber;
    private String sortCode;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private BigDecimal overdraftLimit;
    private BigDecimal interestRate;
    private String status;
    private LocalDateTime openedAt;
    private String customerName;
    private Long customerId;

    public static BankAccountResponse from(BankAccount account) {
        return BankAccountResponse.builder()
            .accountId(account.getAccountId())
            .accountNumber(account.getIdentifier().getAccountNumber())
            .sortCode(account.getIdentifier().getSortCode())
            .accountType(account.getAccountType().getDisplayName())
            .balance(account.getBalance().getAmount())
            .currency(account.getBalance().getCurrency())
            .overdraftLimit(account.getOverdraftLimit() != null ? account.getOverdraftLimit().getAmount() : null)
            .interestRate(account.getInterestRate())
            .status(account.getStatus().name())
            .openedAt(account.getOpenedAt())
            .customerName(account.getCustomer().getPersonalName().getFullName())
            .customerId(account.getCustomer().getCustomerId())
            .build();
    }
}
