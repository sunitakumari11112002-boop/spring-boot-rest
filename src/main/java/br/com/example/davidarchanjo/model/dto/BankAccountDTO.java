package br.com.example.davidarchanjo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import br.com.example.davidarchanjo.model.domain.BankAccount;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BankAccountDTO {

    @NotNull(message = "Account type is required")
    @JsonProperty("accountType")
    private BankAccount.AccountType accountType;

    @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
    @JsonProperty("initialDeposit")
    private BigDecimal initialDeposit;

    @DecimalMin(value = "0.00", message = "Overdraft limit cannot be negative")
    @JsonProperty("overdraftLimit")
    private BigDecimal overdraftLimit;

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customerId")
    private Long customerId;

    @Builder
    public BankAccountDTO(BankAccount.AccountType accountType, BigDecimal initialDeposit,
                         BigDecimal overdraftLimit, Long customerId) {
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
        this.overdraftLimit = overdraftLimit;
        this.customerId = customerId;
    }
}
