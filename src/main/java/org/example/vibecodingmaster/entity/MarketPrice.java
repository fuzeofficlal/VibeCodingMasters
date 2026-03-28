package org.example.vibecodingmaster.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPrice {

    @Id
    @Column(name = "ticker_symbol", length = 20)
    private String tickerSymbol;
    
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "current_price", precision = 15, scale = 4)
    private BigDecimal currentPrice;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
