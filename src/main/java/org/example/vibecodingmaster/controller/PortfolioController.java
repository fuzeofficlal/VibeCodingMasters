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
@Tag(name = "Portfolio API", description = "Endpoints for managing user investment portfolios and calculate performance")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a portfolio")
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

    // =====================================
    // Portfolio Items Endpoints
    // =====================================

    @PostMapping("/{portfolioId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an item to portfolio")
    public PortfolioItemDto addPortfolioItem(@PathVariable Long portfolioId, @RequestBody AddPortfolioItemRequest request) {
        return portfolioService.addPortfolioItem(portfolioId, request);
    }

    @PutMapping("/{portfolioId}/items/{itemId}")
    @Operation(summary = "Update a portfolio item")
    public PortfolioItemDto updatePortfolioItem(@PathVariable Long portfolioId, @PathVariable Long itemId, @RequestBody UpdatePortfolioItemRequest request) {
        return portfolioService.updatePortfolioItem(portfolioId, itemId, request);
    }

    @DeleteMapping("/{portfolioId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a portfolio item")
    public void deletePortfolioItem(@PathVariable Long portfolioId, @PathVariable Long itemId) {
        portfolioService.removePortfolioItem(portfolioId, itemId);
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
}
