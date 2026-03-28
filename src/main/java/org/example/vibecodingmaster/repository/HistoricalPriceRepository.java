package org.example.vibecodingmaster.repository;

import org.example.vibecodingmaster.entity.HistoricalPrice;
import org.example.vibecodingmaster.entity.HistoricalPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, HistoricalPriceId> {
    List<HistoricalPrice> findByTickerSymbolInAndTradeDateBetweenOrderByTradeDate(
            List<String> tickerSymbols, LocalDate from, LocalDate to);
}
