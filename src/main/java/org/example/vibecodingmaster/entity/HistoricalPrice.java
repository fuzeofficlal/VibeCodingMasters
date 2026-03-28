package org.example.vibecodingmaster.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historical_price")
@IdClass(HistoricalPriceId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricalPrice {

    @Id
    @Column(name = "ticker_symbol", length = 20)
    private String tickerSymbol;
    
    @Id
    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "close_price", precision = 15, scale = 4)
    private BigDecimal closePrice;
}
