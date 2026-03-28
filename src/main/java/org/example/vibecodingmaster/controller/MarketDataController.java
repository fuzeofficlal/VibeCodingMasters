package org.example.vibecodingmaster.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.vibecodingmaster.entity.CompanyInfo;
import org.example.vibecodingmaster.entity.MarketPrice;
import org.example.vibecodingmaster.service.MarketDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@Tag(name = "Market Data API", description = "Endpoints for fetching market pricing and company lists")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/companies")
    @Operation(summary = "Search companies", description = "Get a list of all companies or filter by keyword")
    public List<CompanyInfo> getCompanies(@RequestParam(required = false) String search) {
        return marketDataService.searchCompanies(search);
    }

    @GetMapping("/prices/{tickerSymbol}")
    @Operation(summary = "Get latest price", description = "Fetch the latest current price for a specific ticker")
    public MarketPrice getLatestPrice(@PathVariable String tickerSymbol) {
        return marketDataService.getLatestPrice(tickerSymbol);
    }
}
