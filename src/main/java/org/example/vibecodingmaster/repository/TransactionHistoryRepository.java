package org.example.vibecodingmaster.repository;

import org.example.vibecodingmaster.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Page<TransactionHistory> findByPortfolioIdOrderByCreatedAtDesc(Long portfolioId, Pageable pageable);
    List<TransactionHistory> findByPortfolioIdOrderByCreatedAtDesc(Long portfolioId);
}
