package org.example.vibecodingmaster.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.vibecodingmaster.dto.TransactionHistoryDto;
import org.example.vibecodingmaster.entity.TransactionHistory;
import org.example.vibecodingmaster.repository.TransactionHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/portfolios")
@Tag(name = "Transaction API", description = "Endpoints for retrieving transaction history ledger")
public class TransactionController {

    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionController(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @GetMapping("/{portfolioId}/transactions")
    @Operation(summary = "Get transaction ledger for a portfolio")
    public List<TransactionHistoryDto> getTransactions(@PathVariable Long portfolioId) {
        return transactionHistoryRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TransactionHistoryDto mapToDto(TransactionHistory tx) {
        return TransactionHistoryDto.builder()
                .id(tx.getId())
                .portfolioId(tx.getPortfolio().getId())
                .tickerSymbol(tx.getTickerSymbol())
                .transactionType(tx.getTransactionType())
                .volume(tx.getVolume())
                .price(tx.getPrice())
                .status(tx.getStatus())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
