package org.example.vibecodingmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDto {
    private Long id;
    private String userId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal cashBalance;
    private List<PortfolioItemDto> items;
}
