package br.com.ukbank.application.dto;

import br.com.ukbank.domain.model.BankAccount;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * Request DTO for opening new bank accounts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningRequest {

    private Long customerId;
    private BankAccount.AccountType accountType;
    private BigDecimal initialDeposit;
    private BigDecimal overdraftLimit;
}
