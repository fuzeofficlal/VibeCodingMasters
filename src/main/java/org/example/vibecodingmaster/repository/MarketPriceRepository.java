package org.example.vibecodingmaster.repository;

import org.example.vibecodingmaster.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, String> {
    List<MarketPrice> findByTickerSymbolIn(List<String> tickerSymbols);
}
