package org.example.vibecodingmaster.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WatchlistService {

    
    public List<Map<String, Object>> getWatchlist(Long portfolioId) {
        
        return List.of(
            Map.of("tickerSymbol", "NVDA", "currentPrice", new BigDecimal("850.20")),
            Map.of("tickerSymbol", "TSLA", "currentPrice", new BigDecimal("175.50"))
        );
    }

    
    public void addWatchlist(Long portfolioId, String tickerSymbol) {
        
        System.out.println("Dummy: Added " + tickerSymbol + " to watchlist for portfolio " + portfolioId);
    }

    
    public void removeWatchlist(Long portfolioId, String tickerSymbol) {
        
        System.out.println("Dummy: Removed " + tickerSymbol + " from watchlist for portfolio " + portfolioId);
    }
}
