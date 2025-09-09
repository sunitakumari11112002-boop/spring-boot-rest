package br.com.ukbank.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO for money transfers
 * Implements validation and data transfer patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequest {

    @NotNull(message = "From account ID is required")
    private Long fromAccountId;

    @NotBlank(message = "Destination sort code is required")
    @Pattern(regexp = "^[0-9]{2}-[0-9]{2}-[0-9]{2}$", message = "Invalid sort code format (XX-XX-XX)")
    private String toSortCode;

    @NotBlank(message = "Destination account number is required")
    @Pattern(regexp = "^[0-9]{8}$", message = "Invalid account number format (8 digits)")
    private String toAccountNumber;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be at least £0.01")
    @DecimalMax(value = "999999.99", message = "Transfer amount cannot exceed £999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotBlank(message = "Payee name is required")
    @Size(max = 100, message = "Payee name must not exceed 100 characters")
    private String payeeName;

    @NotBlank(message = "Payment reference is required")
    @Size(max = 50, message = "Reference must not exceed 50 characters")
    private String reference;
}
