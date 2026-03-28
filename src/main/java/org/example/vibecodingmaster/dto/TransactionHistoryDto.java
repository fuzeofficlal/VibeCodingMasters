package org.example.vibecodingmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vibecodingmaster.entity.TransactionStatus;
import org.example.vibecodingmaster.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {
    private Long id;
    private Long portfolioId;
    private String tickerSymbol;
    private TransactionType transactionType;
    private Integer volume;
    private BigDecimal price;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
