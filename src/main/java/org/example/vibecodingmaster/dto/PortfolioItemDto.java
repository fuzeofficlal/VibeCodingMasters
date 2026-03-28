package org.example.vibecodingmaster.dto;

import org.example.vibecodingmaster.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItemDto {
    private Long id;
    private Long portfolioId;
    private AssetType assetType;
    private String tickerSymbol;
    private BigDecimal volume;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private LocalDateTime createdAt;
}
