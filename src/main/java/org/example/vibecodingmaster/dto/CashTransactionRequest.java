package org.example.vibecodingmaster.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CashTransactionRequest {
    private String action; 
    private BigDecimal amount;
}
