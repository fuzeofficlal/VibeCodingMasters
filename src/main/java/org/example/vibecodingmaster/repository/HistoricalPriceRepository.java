package org.example.vibecodingmaster.repository;

import org.example.vibecodingmaster.entity.HistoricalPrice;
import org.example.vibecodingmaster.entity.HistoricalPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, HistoricalPriceId> {
}
