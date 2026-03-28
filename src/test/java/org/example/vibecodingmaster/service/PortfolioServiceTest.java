package org.example.vibecodingmaster.service;

import org.example.vibecodingmaster.dto.*;
import org.example.vibecodingmaster.entity.AssetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 保证测试数据必定回滚，绝不污染真实数据库
public class PortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;

    @Test
    public void testPortfolioCrudAndPerformance() {
        // 1. 创建投资组合 (Create)
        CreatePortfolioRequest createReq = new CreatePortfolioRequest("My Test Portfolio", "test_user");
        PortfolioDto created = portfolioService.createPortfolio(createReq);
        assertNotNull(created.getId());
        assertEquals("My Test Portfolio", created.getName());
        System.out.println("✅ [测试] 成功创建投资组合，ID=" + created.getId());
        
        // 2. 增加测试股票资产 (Add Item)
        AddPortfolioItemRequest stockReq = new AddPortfolioItemRequest(
                AssetType.STOCK, "AAPL", new BigDecimal("10"), new BigDecimal("150.0"), LocalDate.now());
        PortfolioItemDto stockItem = portfolioService.addPortfolioItem(created.getId(), stockReq);
        assertNotNull(stockItem.getId());
        System.out.println("✅ [测试] 成功添加股票资产 AAPL，数量 10，成本价 150");

        // 3. 增加现金资产
        AddPortfolioItemRequest cashReq = new AddPortfolioItemRequest(
                AssetType.CASH, null, new BigDecimal("5000"), new BigDecimal("1"), LocalDate.now());
        portfolioService.addPortfolioItem(created.getId(), cashReq);
        System.out.println("✅ [测试] 成功添加现金 $5000");

        // 4. 更新资产数量 (Update Item)
        UpdatePortfolioItemRequest updateReq = new UpdatePortfolioItemRequest(new BigDecimal("20"), null);
        PortfolioItemDto updatedStock = portfolioService.updatePortfolioItem(created.getId(), stockItem.getId(), updateReq);
        assertEquals(new BigDecimal("20"), updatedStock.getVolume());
        System.out.println("✅ [测试] 成功修正 AAPL 的持仓数量为 20");

        // 5. 查询验证列表读取 (Read Items)
        List<PortfolioItemDto> items = portfolioService.getPortfolioItems(created.getId());
        assertEquals(2, items.size());
        System.out.println("✅ [测试] 成功读取投资明细列表，共计 2 项");

        // 6. 核心表现计算引擎验证 (Calculate Performance)
        PerformanceResponseDto performance = portfolioService.calculatePerformance(created.getId());
        assertNotNull(performance);
        assertTrue(performance.getTotalCost().compareTo(BigDecimal.ZERO) > 0);
        System.out.println("✅ [测试] 表现引擎计算成功！总成本: " + performance.getTotalCost() + 
                           ", 总现值: " + performance.getCurrentTotalValue() + 
                           ", ROI: " + performance.getRoiPercentage() + "%");

        // 7. 删除单项资产 (Delete Item)
        portfolioService.removePortfolioItem(created.getId(), stockItem.getId());
        items = portfolioService.getPortfolioItems(created.getId());
        assertEquals(1, items.size());
        System.out.println("✅ [测试] 成功将 AAPL 资产移出组合");

        // 8. 删除整体组合 (Delete Portfolio)
        portfolioService.deletePortfolio(created.getId());
        assertThrows(Exception.class, () -> portfolioService.getPortfolio(created.getId()));
        System.out.println("✅ [测试] 成功级联删除整个投资组合！");
    }
}
