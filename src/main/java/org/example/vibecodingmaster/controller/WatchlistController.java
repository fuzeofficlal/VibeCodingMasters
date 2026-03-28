package org.example.vibecodingmaster.controller;

import org.example.vibecodingmaster.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getWatchlist(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(watchlistService.getWatchlist(portfolioId));
    }

    @PostMapping
    public ResponseEntity<Void> addWatchlist(@PathVariable Long portfolioId, @RequestBody Map<String, String> payload) {
        watchlistService.addWatchlist(portfolioId, payload.get("tickerSymbol"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tickerSymbol}")
    public ResponseEntity<Void> removeWatchlist(@PathVariable Long portfolioId, @PathVariable String tickerSymbol) {
        watchlistService.removeWatchlist(portfolioId, tickerSymbol);
        return ResponseEntity.ok().build();
    }
}
