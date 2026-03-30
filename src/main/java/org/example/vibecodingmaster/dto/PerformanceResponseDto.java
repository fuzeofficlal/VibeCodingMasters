package org.example.vibecodingmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceResponseDto {
    private BigDecimal totalCost;
    private BigDecimal currentTotalValue;
    private BigDecimal roiPercentage; // Return on Investment %
    private List<HistoricalTrend> historicalTrendList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoricalTrend {
        private LocalDate date;
        private BigDecimal portfolioValue;
    }
}
