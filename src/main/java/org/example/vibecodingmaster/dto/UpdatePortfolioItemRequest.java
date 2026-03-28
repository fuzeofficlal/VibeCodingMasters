package org.example.vibecodingmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePortfolioItemRequest {
    private BigDecimal volume;
    private BigDecimal purchasePrice;
}
