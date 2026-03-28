package org.example.vibecodingmaster.repository;

import org.example.vibecodingmaster.entity.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, String> {
    List<CompanyInfo> findByCompanyNameContainingIgnoreCaseOrTickerSymbolContainingIgnoreCase(String name, String ticker);
}
