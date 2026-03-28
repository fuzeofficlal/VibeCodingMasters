package org.example.vibecodingmaster.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.vibecodingmaster.dto.*;
import org.example.vibecodingmaster.entity.MarketPrice;
import org.example.vibecodingmaster.entity.Portfolio;
import org.example.vibecodingmaster.entity.PortfolioItem;
import org.example.vibecodingmaster.repository.MarketPriceRepository;
import org.example.vibecodingmaster.repository.PortfolioItemRepository;
import org.example.vibecodingmaster.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final MarketPriceRepository marketPriceRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, 
                            PortfolioItemRepository portfolioItemRepository, 
                            MarketPriceRepository marketPriceRepository) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.marketPriceRepository = marketPriceRepository;
    }

    // ========================================
    // CRUD: Portfolio
    // ========================================

    public PortfolioDto createPortfolio(CreatePortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .build();
        portfolio = portfolioRepository.save(portfolio);
        return mapToDto(portfolio);
    }

    public PortfolioDto updatePortfolio(Long id, String name) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + id));
        portfolio.setName(name);
        portfolio.setUpdatedAt(LocalDateTime.now());
        return mapToDto(portfolioRepository.save(portfolio));
    }

    public void deletePortfolio(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + id);
        }
        portfolioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PortfolioDto getPortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + id));
        return mapToDto(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getAllPortfolios() {
        return portfolioRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // ========================================
    // CRUD: PortfolioItem
    // ========================================

    public PortfolioItemDto addPortfolioItem(Long portfolioId, AddPortfolioItemRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));

        PortfolioItem item = PortfolioItem.builder()
                .portfolio(portfolio)
                .assetType(request.getAssetType())
                .tickerSymbol(request.getTickerSymbol())
                .volume(request.getVolume())
                .purchasePrice(request.getPurchasePrice())
                .purchaseDate(request.getPurchaseDate())
                .createdAt(LocalDateTime.now())
                .build();

        item = portfolioItemRepository.save(item);
        return mapItemToDto(item);
    }

    public PortfolioItemDto updatePortfolioItem(Long portfolioId, Long itemId, UpdatePortfolioItemRequest request) {
        PortfolioItem item = portfolioItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found with id: " + itemId));

        if (!item.getPortfolio().getId().equals(portfolioId)) {
            throw new IllegalArgumentException("Item does not belong to the specified portfolio.");
        }

        if (request.getVolume() != null) {
            item.setVolume(request.getVolume());
        }
        if (request.getPurchasePrice() != null) {
            item.setPurchasePrice(request.getPurchasePrice());
        }

        return mapItemToDto(portfolioItemRepository.save(item));
    }

    public void removePortfolioItem(Long portfolioId, Long itemId) {
        PortfolioItem item = portfolioItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found with id: " + itemId));

        if (!item.getPortfolio().getId().equals(portfolioId)) {
            throw new IllegalArgumentException("Item does not belong to the specified portfolio.");
        }

        portfolioItemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<PortfolioItemDto> getPortfolioItems(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        return portfolioItemRepository.findByPortfolioId(portfolioId).stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList());
    }

    // ========================================
    // Performance Calculation Engine
    // ========================================

    @Transactional(readOnly = true)
    public PerformanceResponseDto calculatePerformance(Long portfolioId) {
        List<PortfolioItem> items = portfolioItemRepository.findByPortfolioId(portfolioId);
        
        List<String> tickers = items.stream()
                .map(PortfolioItem::getTickerSymbol)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .collect(Collectors.toList());

        Map<String, BigDecimal> currentPrices = marketPriceRepository.findByTickerSymbolIn(tickers).stream()
                .collect(Collectors.toMap(MarketPrice::getTickerSymbol, MarketPrice::getCurrentPrice));

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal currentTotalValue = BigDecimal.ZERO;

        for (PortfolioItem item : items) {
            BigDecimal vol = item.getVolume() == null ? BigDecimal.ZERO : item.getVolume();
            BigDecimal costPrice = item.getPurchasePrice() == null ? BigDecimal.ZERO : item.getPurchasePrice();
            
            // accumulate total cost
            totalCost = totalCost.add(vol.multiply(costPrice));

            // accumulate current value
            BigDecimal currentPrice;
            if (item.getAssetType() != null && item.getAssetType().name().equals("CASH")) {
                currentPrice = BigDecimal.ONE; // 1 unit of cash = 1
            } else {
                currentPrice = currentPrices.getOrDefault(item.getTickerSymbol(), costPrice); // fallback to cost if not found
            }
            
            currentTotalValue = currentTotalValue.add(vol.multiply(currentPrice));
        }

        // Calculate ROI = (Current Value - Total Cost) / Total Cost * 100
        BigDecimal roiPercentage = BigDecimal.ZERO;
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            roiPercentage = currentTotalValue.subtract(totalCost)
                    .divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }

        // Return empty historical trend list for MVP
        List<PerformanceResponseDto.HistoricalTrend> emptyTrends = new ArrayList<>();

        return PerformanceResponseDto.builder()
                .totalCost(totalCost)
                .currentTotalValue(currentTotalValue)
                .roiPercentage(roiPercentage)
                .historicalTrendList(emptyTrends)
                .build();
    }

    // ========================================
    // Helper Mappers
    // ========================================

    private PortfolioDto mapToDto(Portfolio portfolio) {
        List<PortfolioItemDto> itemDtos = new ArrayList<>();
        if (portfolio.getItems() != null) {
            itemDtos = portfolio.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList());
        }
        return PortfolioDto.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUserId())
                .name(portfolio.getName())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .items(itemDtos)
                .build();
    }

    private PortfolioItemDto mapItemToDto(PortfolioItem item) {
        return PortfolioItemDto.builder()
                .id(item.getId())
                .portfolioId(item.getPortfolio() != null ? item.getPortfolio().getId() : null)
                .assetType(item.getAssetType())
                .tickerSymbol(item.getTickerSymbol())
                .volume(item.getVolume())
                .purchasePrice(item.getPurchasePrice())
                .purchaseDate(item.getPurchaseDate())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
