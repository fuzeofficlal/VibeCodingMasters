package org.example.vibecodingmaster.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WatchlistService {

    /**
     * [TODO: Task 1] Fetch watchlist for portfolio and merge with current prices from MarketPriceRepository
     */
    public List<Map<String, Object>> getWatchlist(Long portfolioId) {
        // DUMMY RESPONSE FOR UI TESTING
        return List.of(
            Map.of("tickerSymbol", "NVDA", "currentPrice", new BigDecimal("850.20")),
            Map.of("tickerSymbol", "TSLA", "currentPrice", new BigDecimal("175.50"))
        );
    }

    /**
     * [TODO: Task 1] Add ticker to Watchlist database table for this portfolio
     */
    public void addWatchlist(Long portfolioId, String tickerSymbol) {
        // DUMMY IMPLEMENTATION: Do nothing
        System.out.println("Dummy: Added " + tickerSymbol + " to watchlist for portfolio " + portfolioId);
    }

    /**
     * [TODO: Task 1] Remove ticker from Watchlist database table
     */
    public void removeWatchlist(Long portfolioId, String tickerSymbol) {
        // DUMMY IMPLEMENTATION: Do nothing
        System.out.println("Dummy: Removed " + tickerSymbol + " from watchlist for portfolio " + portfolioId);
    }
}
