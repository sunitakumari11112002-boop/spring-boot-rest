package br.com.ukbank.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for money transfer operations
 * Implements data transfer patterns for transaction results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String transactionReference;
    private String status;
    private Long fromAccountId;
    private BigDecimal amount;
    private String payeeName;
    private String reference;
    private LocalDateTime processedAt;

    public static TransferResponse success(String transactionReference, Long fromAccountId,
                                         BigDecimal amount, String payeeName, String reference) {
        return TransferResponse.builder()
            .transactionReference(transactionReference)
            .status("COMPLETED")
            .fromAccountId(fromAccountId)
            .amount(amount)
            .payeeName(payeeName)
            .reference(reference)
            .processedAt(LocalDateTime.now())
            .build();
    }
}
