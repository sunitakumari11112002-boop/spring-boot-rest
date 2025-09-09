package br.com.ukbank.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * Request DTO for money transfer operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequest {

    private Long fromAccountId;
    private String toAccountNumber;
    private String toSortCode;
    private BigDecimal amount;
    private String payeeName;
    private String reference;
}
