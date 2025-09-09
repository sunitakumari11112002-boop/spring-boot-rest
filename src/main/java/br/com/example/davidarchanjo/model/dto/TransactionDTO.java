package br.com.example.davidarchanjo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import br.com.example.davidarchanjo.model.domain.Transaction;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransactionDTO {

    @NotNull(message = "Transaction type is required")
    @JsonProperty("type")
    private Transaction.TransactionType type;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @NotNull(message = "Amount is required")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @JsonProperty("description")
    private String description;

    @Size(max = 100, message = "Reference must not exceed 100 characters")
    @JsonProperty("reference")
    private String reference;

    @Pattern(regexp = "^[0-9]{8}$", message = "Account number must be 8 digits")
    @JsonProperty("toAccountNumber")
    private String toAccountNumber;

    @Pattern(regexp = "^[0-9]{6}$", message = "Sort code must be 6 digits")
    @JsonProperty("toSortCode")
    private String toSortCode;

    @Size(max = 100, message = "Payee name must not exceed 100 characters")
    @JsonProperty("payeeName")
    private String payeeName;

    @NotNull(message = "Account ID is required")
    @JsonProperty("accountId")
    private Long accountId;

    @Builder
    public TransactionDTO(Transaction.TransactionType type, BigDecimal amount, String description,
                         String reference, String toAccountNumber, String toSortCode,
                         String payeeName, Long accountId) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.reference = reference;
        this.toAccountNumber = toAccountNumber;
        this.toSortCode = toSortCode;
        this.payeeName = payeeName;
        this.accountId = accountId;
    }
}
