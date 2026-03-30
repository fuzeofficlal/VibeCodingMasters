package org.example.vibecodingmaster.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.vibecodingmaster.dto.*;
import org.example.vibecodingmaster.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@Tag(name = "Portfolio API", description = "Endpoints for managing portfolios and calculations")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create portfolio")
    public PortfolioDto createPortfolio(@RequestBody CreatePortfolioRequest request) {
        return portfolioService.createPortfolio(request);
    }

    @GetMapping
    @Operation(summary = "Get all portfolios")
    public List<PortfolioDto> getAllPortfolios() {
        return portfolioService.getAllPortfolios();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get portfolio details")
    public PortfolioDto getPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolio(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update portfolio name")
    public PortfolioDto updatePortfolio(@PathVariable Long id, @RequestBody CreatePortfolioRequest request) {
        return portfolioService.updatePortfolio(id, request.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete portfolio")
    public void deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
    }

    
    
    

    @PostMapping("/{portfolioId}/transactions")
    @Operation(summary = "Submit a trade order")
    public void submitOrder(@PathVariable Long portfolioId, @RequestBody OrderRequest request) {
        if ("BUY".equalsIgnoreCase(request.getAction())) {
            portfolioService.buyAsset(portfolioId, request);
        } else if ("SELL".equalsIgnoreCase(request.getAction())) {
            portfolioService.sellAsset(portfolioId, request);
        } else {
            throw new IllegalArgumentException("Invalid action: " + request.getAction());
        }
    }

    @PostMapping("/{portfolioId}/cash")
    @Operation(summary = "Deposit or Withdraw cash")
    public void handleCash(@PathVariable Long portfolioId, @RequestBody CashTransactionRequest request) {
        if ("DEPOSIT".equalsIgnoreCase(request.getAction())) {
            portfolioService.depositCash(portfolioId, request.getAmount());
        } else if ("WITHDRAW".equalsIgnoreCase(request.getAction())) {
            portfolioService.withdrawCash(portfolioId, request.getAmount());
        } else {
            throw new IllegalArgumentException("Invalid action: " + request.getAction());
        }
    }

    @GetMapping("/{portfolioId}/items")
    @Operation(summary = "Get all items in a portfolio")
    public List<PortfolioItemDto> getPortfolioItems(@PathVariable Long portfolioId) {
        return portfolioService.getPortfolioItems(portfolioId);
    }

    @GetMapping("/{portfolioId}/performance")
    @Operation(summary = "Calculate performance", description = "Get ROE, total cost, and current valuation based on real-time market data")
    public PerformanceResponseDto getPerformance(@PathVariable Long portfolioId) {
        return portfolioService.calculatePerformance(portfolioId);
    }

    
    
    

    @GetMapping("/{portfolioId}/allocation")
    @Operation(summary = "[Task 2] Get Asset Allocation Breakdown")
    public java.util.Map<String, java.math.BigDecimal> getAssetAllocation(@PathVariable Long portfolioId) {
        return portfolioService.getAssetAllocation(portfolioId);
    }

    @PutMapping("/{portfolioId}/items/{itemId}/alerts")
    @Operation(summary = "[Task 3] Set Price Alerts for a Holding")
    public void setPriceAlerts(@PathVariable Long portfolioId, @PathVariable Long itemId, @RequestBody java.util.Map<String, java.math.BigDecimal> payload) {
        portfolioService.setPriceAlerts(portfolioId, itemId, payload.get("targetPrice"), payload.get("stopLossPrice"));
    }

    @GetMapping("/{portfolioId}/alerts")
    @Operation(summary = "[Task 3] Check active Price Alerts")
    public List<String> checkPriceAlerts(@PathVariable Long portfolioId) {
        return portfolioService.checkPriceAlerts(portfolioId);
    }
}
