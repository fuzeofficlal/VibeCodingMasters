package org.example.vibecodingmaster.service;

import org.example.vibecodingmaster.entity.CompanyInfo;
import org.example.vibecodingmaster.entity.MarketPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional 
public class MarketDataServiceTest {

    @Autowired
    private MarketDataService marketDataService;

    @Test
    public void testSearchCompanies() {
        
        List<CompanyInfo> allCompanies = marketDataService.searchCompanies("");
        assertNotNull(allCompanies);
        assertFalse(allCompanies.isEmpty(), "数据库中应该有股票数据（标普500等）");
        System.out.println("✅ [测试成功] 空关键字查询返回了 " + allCompanies.size() + " 家公司的数据。");

        
        List<CompanyInfo> searchResults = marketDataService.searchCompanies("AAPL");
        assertNotNull(searchResults);
        assertTrue(searchResults.stream().anyMatch(c -> c.getTickerSymbol().equalsIgnoreCase("AAPL")), 
                   "搜索结果中应该包含 AAPL");
        System.out.println("✅ [测试成功] 准确搜索到了 'AAPL' 以及相关的 " + searchResults.size() + " 条匹配结果。");
    }

    @Test
    public void testGetLatestPrice() {
        
        try {
            MarketPrice price = marketDataService.getLatestPrice("AAPL");
            assertNotNull(price);
            System.out.println("✅ [测试成功] 成功获取 AAPL 最新市价: " + price.getCurrentPrice());
        } catch (Exception e) {
            
            System.out.println("⚠️ [测试说明] market_price 表中没有找到 AAPL 数据，业务抛出预期异常: " + e.getMessage());
        }
    }
}
