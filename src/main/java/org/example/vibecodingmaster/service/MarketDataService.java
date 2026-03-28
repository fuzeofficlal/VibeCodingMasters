package org.example.vibecodingmaster.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.vibecodingmaster.entity.CompanyInfo;
import org.example.vibecodingmaster.entity.MarketPrice;
import org.example.vibecodingmaster.repository.CompanyInfoRepository;
import org.example.vibecodingmaster.repository.MarketPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarketDataService {

    private final CompanyInfoRepository companyInfoRepository;
    private final MarketPriceRepository marketPriceRepository;

    public MarketDataService(CompanyInfoRepository companyInfoRepository, MarketPriceRepository marketPriceRepository) {
        this.companyInfoRepository = companyInfoRepository;
        this.marketPriceRepository = marketPriceRepository;
    }

    public List<CompanyInfo> searchCompanies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return companyInfoRepository.findAll();
        }
        String trimmed = keyword.trim();
        return companyInfoRepository.findByCompanyNameContainingIgnoreCaseOrTickerSymbolContainingIgnoreCase(trimmed, trimmed);
    }

    public MarketPrice getLatestPrice(String tickerSymbol) {
        return marketPriceRepository.findById(tickerSymbol)
                .orElseThrow(() -> new EntityNotFoundException("Market price not found for ticker: " + tickerSymbol));
    }
}
