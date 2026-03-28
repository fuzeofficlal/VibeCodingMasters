package org.example.vibecodingmaster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalPriceId implements Serializable {
    private String tickerSymbol;
    private LocalDate tradeDate;
}
