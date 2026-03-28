package org.example.vibecodingmaster.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CashTransactionRequest {
    private String action; // DEPOSIT or WITHDRAW
    private BigDecimal amount;
}
