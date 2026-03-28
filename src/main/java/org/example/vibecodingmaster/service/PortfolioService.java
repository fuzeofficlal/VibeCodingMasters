package org.example.vibecodingmaster.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.vibecodingmaster.dto.*;
import org.example.vibecodingmaster.entity.HistoricalPrice;
import org.example.vibecodingmaster.entity.MarketPrice;
import org.example.vibecodingmaster.entity.Portfolio;
import org.example.vibecodingmaster.entity.PortfolioItem;
import org.example.vibecodingmaster.repository.HistoricalPriceRepository;
import org.example.vibecodingmaster.repository.MarketPriceRepository;
import org.example.vibecodingmaster.repository.PortfolioItemRepository;
import org.example.vibecodingmaster.repository.PortfolioRepository;
import org.example.vibecodingmaster.repository.TransactionHistoryRepository;
import org.example.vibecodingmaster.entity.TransactionHistory;
import org.example.vibecodingmaster.entity.TransactionStatus;
import org.example.vibecodingmaster.entity.TransactionType;
import org.example.vibecodingmaster.entity.AssetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final MarketPriceRepository marketPriceRepository;
    private final HistoricalPriceRepository historicalPriceRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            PortfolioItemRepository portfolioItemRepository,
                            MarketPriceRepository marketPriceRepository,
                            HistoricalPriceRepository historicalPriceRepository,
                            TransactionHistoryRepository transactionHistoryRepository) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.marketPriceRepository = marketPriceRepository;
        this.historicalPriceRepository = historicalPriceRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    // ========================================
    // Portfolio Core Operations
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
    // Transaction Engine (Phase 3)
    // ========================================

    public void depositCash(Long portfolioId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
        
        portfolio.setCashBalance(portfolio.getCashBalance().add(amount));
        portfolioRepository.save(portfolio);
        
        TransactionHistory tx = TransactionHistory.builder()
                .portfolio(portfolio).transactionType(TransactionType.DEPOSIT)
                .volume(0).price(amount).status(TransactionStatus.COMPLETED)
                .build();
        transactionHistoryRepository.save(tx);
    }

    public void withdrawCash(Long portfolioId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
        
        if (portfolio.getCashBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient cash balance");
        }
        
        portfolio.setCashBalance(portfolio.getCashBalance().subtract(amount));
        portfolioRepository.save(portfolio);
        
        TransactionHistory tx = TransactionHistory.builder()
                .portfolio(portfolio).transactionType(TransactionType.WITHDRAW)
                .volume(0).price(amount).status(TransactionStatus.COMPLETED)
                .build();
        transactionHistoryRepository.save(tx);
    }

    public void buyAsset(Long portfolioId, OrderRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
        
        BigDecimal cost = request.getPrice().multiply(new BigDecimal(request.getVolume()));
        if (portfolio.getCashBalance().compareTo(cost) < 0) {
            throw new IllegalArgumentException("Insufficient cash balance to buy the asset");
        }
        
        // 1. Generate Transaction Ledger
        TransactionHistory tx = TransactionHistory.builder()
                .portfolio(portfolio).transactionType(TransactionType.BUY)
                .tickerSymbol(request.getTickerSymbol())
                .volume(request.getVolume()).price(request.getPrice())
                .status(TransactionStatus.COMPLETED).build();
        transactionHistoryRepository.save(tx);
        
        // 2. Deduct Cash Balance
        portfolio.setCashBalance(portfolio.getCashBalance().subtract(cost));
        portfolioRepository.save(portfolio);
        
        // 3. Update Portfolio Holding
        PortfolioItem item = portfolioItemRepository.findByPortfolioId(portfolioId).stream()
                .filter(i -> request.getTickerSymbol().equals(i.getTickerSymbol()))
                .findFirst().orElse(null);
                
        if (item != null) {
            BigDecimal oldTotal = item.getVolume().multiply(item.getPurchasePrice() == null ? BigDecimal.ZERO : item.getPurchasePrice());
            BigDecimal newTotal = oldTotal.add(cost);
            BigDecimal newVolume = item.getVolume().add(new BigDecimal(request.getVolume()));
            item.setVolume(newVolume);
            item.setPurchasePrice(newTotal.divide(newVolume, 4, RoundingMode.HALF_UP));
            portfolioItemRepository.save(item);
        } else {
            PortfolioItem newItem = PortfolioItem.builder()
                    .portfolio(portfolio).assetType(AssetType.STOCK)
                    .tickerSymbol(request.getTickerSymbol())
                    .volume(new BigDecimal(request.getVolume()))
                    .purchasePrice(request.getPrice())
                    .purchaseDate(LocalDate.now())
                    .build();
            portfolioItemRepository.save(newItem);
        }
    }

    public void sellAsset(Long portfolioId, OrderRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));
                
        PortfolioItem item = portfolioItemRepository.findByPortfolioId(portfolioId).stream()
                .filter(i -> request.getTickerSymbol().equals(i.getTickerSymbol()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Asset not found in portfolio"));
                
        if (item.getVolume().compareTo(new BigDecimal(request.getVolume())) < 0) {
            throw new IllegalArgumentException("Insufficient volume to sell");
        }
        
        BigDecimal proceed = request.getPrice().multiply(new BigDecimal(request.getVolume()));
        
        // 1. Generate Transaction Ledger
        TransactionHistory tx = TransactionHistory.builder()
                .portfolio(portfolio).transactionType(TransactionType.SELL)
                .tickerSymbol(request.getTickerSymbol())
                .volume(request.getVolume()).price(request.getPrice())
                .status(TransactionStatus.COMPLETED).build();
        transactionHistoryRepository.save(tx);
        
        // 2. Add Proceeds to Cash Balance
        portfolio.setCashBalance(portfolio.getCashBalance().add(proceed));
        portfolioRepository.save(portfolio);
        
        // 3. Deduct Volume from Holding
        item.setVolume(item.getVolume().subtract(new BigDecimal(request.getVolume())));
        if (item.getVolume().compareTo(BigDecimal.ZERO) == 0) {
            portfolioItemRepository.delete(item); // JPA handles soft deletion automatically
        } else {
            portfolioItemRepository.save(item);
        }
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

        // Map of item volume by ticker for historical calculation
        Map<String, BigDecimal> volumeByTicker = new java.util.HashMap<>();

        for (PortfolioItem item : items) {
            BigDecimal vol = item.getVolume() == null ? BigDecimal.ZERO : item.getVolume();
            BigDecimal costPrice = item.getPurchasePrice() == null ? BigDecimal.ZERO : item.getPurchasePrice();

            totalCost = totalCost.add(vol.multiply(costPrice));

            BigDecimal currentPrice;
            if (item.getAssetType() != null && item.getAssetType().name().equals("CASH")) {
                currentPrice = BigDecimal.ONE;
            } else {
                currentPrice = currentPrices.getOrDefault(item.getTickerSymbol(), costPrice);
                if (item.getTickerSymbol() != null && !item.getTickerSymbol().isBlank()) {
                    volumeByTicker.merge(item.getTickerSymbol(), vol, BigDecimal::add);
                }
            }
            currentTotalValue = currentTotalValue.add(vol.multiply(currentPrice));
        }

        BigDecimal roiPercentage = BigDecimal.ZERO;
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            roiPercentage = currentTotalValue.subtract(totalCost)
                    .divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }

        // Build historical trend: past 90 days
        List<PerformanceResponseDto.HistoricalTrend> trends = new ArrayList<>();
        if (!tickers.isEmpty()) {
            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(90);

            List<HistoricalPrice> historicalPrices =
                    historicalPriceRepository.findByTickerSymbolInAndTradeDateBetweenOrderByTradeDate(
                            tickers, from, to);

            // Group by date, sum each ticker's value on that day
            TreeMap<LocalDate, BigDecimal> dailyValues = new TreeMap<>();
            for (HistoricalPrice hp : historicalPrices) {
                BigDecimal vol = volumeByTicker.getOrDefault(hp.getTickerSymbol(), BigDecimal.ZERO);
                BigDecimal dayValue = vol.multiply(hp.getClosePrice());
                dailyValues.merge(hp.getTradeDate(), dayValue, BigDecimal::add);
            }

            dailyValues.forEach((date, value) ->
                    trends.add(PerformanceResponseDto.HistoricalTrend.builder()
                            .date(date)
                            .portfolioValue(value)
                            .build()));
        }

        return PerformanceResponseDto.builder()
                .totalCost(totalCost)
                .currentTotalValue(currentTotalValue)
                .roiPercentage(roiPercentage)
                .historicalTrendList(trends)
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
                .cashBalance(portfolio.getCashBalance())
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
