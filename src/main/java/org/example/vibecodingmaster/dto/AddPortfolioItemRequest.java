package org.example.vibecodingmaster.dto;

import org.example.vibecodingmaster.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPortfolioItemRequest {
    private AssetType assetType;
    private String tickerSymbol;
    private BigDecimal volume;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
}
