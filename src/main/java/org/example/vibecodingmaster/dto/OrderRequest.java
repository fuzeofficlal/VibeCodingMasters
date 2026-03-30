package org.example.vibecodingmaster.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private String action; // BUY or SELL
    private String tickerSymbol;
    private Integer volume;
    private BigDecimal price;
}
