package org.example.vibecodingmaster.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfo {
    
    @Id
    @Column(name = "ticker_symbol", length = 20)
    private String tickerSymbol;
    
    @Column(name = "company_name")
    private String companyName;
}
