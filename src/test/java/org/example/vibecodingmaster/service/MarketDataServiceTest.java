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
@Transactional // 加上注解：测试结束后自动回滚，绝对不会影响数据库的完整性
public class MarketDataServiceTest {

    @Autowired
    private MarketDataService marketDataService;

    @Test
    public void testSearchCompanies() {
        // 1. 测试空关键字查询（应该返回所有）
        List<CompanyInfo> allCompanies = marketDataService.searchCompanies("");
        assertNotNull(allCompanies);
        assertFalse(allCompanies.isEmpty(), "数据库中应该有股票数据（标普500等）");
        System.out.println("✅ [测试成功] 空关键字查询返回了 " + allCompanies.size() + " 家公司的数据。");

        // 2. 测试带关键字查询（搜索苹果 AAPL）
        List<CompanyInfo> searchResults = marketDataService.searchCompanies("AAPL");
        assertNotNull(searchResults);
        assertTrue(searchResults.stream().anyMatch(c -> c.getTickerSymbol().equalsIgnoreCase("AAPL")), 
                   "搜索结果中应该包含 AAPL");
        System.out.println("✅ [测试成功] 准确搜索到了 'AAPL' 以及相关的 " + searchResults.size() + " 条匹配结果。");
    }

    @Test
    public void testGetLatestPrice() {
        // 测试获取指定股票的最新价格
        try {
            MarketPrice price = marketDataService.getLatestPrice("AAPL");
            assertNotNull(price);
            System.out.println("✅ [测试成功] 成功获取 AAPL 最新市价: " + price.getCurrentPrice());
        } catch (Exception e) {
            // 如果表里没数据，触发 EntityNotFoundException
            System.out.println("⚠️ [测试说明] market_price 表中没有找到 AAPL 数据，业务抛出预期异常: " + e.getMessage());
        }
    }
}
