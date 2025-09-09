package br.com.ukbank.application.dto;

import br.com.ukbank.domain.model.AccountType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO for opening new bank accounts
 * Implements validation and data transfer patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Initial deposit must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal initialDeposit;

    @DecimalMin(value = "0.0", message = "Overdraft limit cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal overdraftLimit;
}
