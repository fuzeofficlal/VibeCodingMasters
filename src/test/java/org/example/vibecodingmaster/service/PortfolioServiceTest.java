package org.example.vibecodingmaster.service;

import org.example.vibecodingmaster.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional 
public class PortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private org.example.vibecodingmaster.repository.MarketPriceRepository marketPriceRepository;

    @BeforeEach
    public void setupMarketUniverse() {
        
        if (!marketPriceRepository.existsById("AAPL")) {
            org.example.vibecodingmaster.entity.MarketPrice aapl = new org.example.vibecodingmaster.entity.MarketPrice();
            aapl.setTickerSymbol("AAPL");
            aapl.setCurrentPrice(new BigDecimal("150.00"));
            marketPriceRepository.save(aapl);
        }
        
        if (!marketPriceRepository.existsById("MSFT")) {
            org.example.vibecodingmaster.entity.MarketPrice msft = new org.example.vibecodingmaster.entity.MarketPrice();
            msft.setTickerSymbol("MSFT");
            msft.setCurrentPrice(new BigDecimal("300.00"));
            marketPriceRepository.save(msft);
        }
    }

    @Test
    public void testFinancialGradeTransactions() {
        
        CreatePortfolioRequest createReq = new CreatePortfolioRequest("Financial Portfolio", "test_user");
        PortfolioDto created = portfolioService.createPortfolio(createReq);
        Long pId = created.getId();
        assertNotNull(pId);
        assertEquals(BigDecimal.ZERO, created.getCashBalance());
        System.out.println("✅ [Test] Created portfolio, ID=" + pId);

        
        portfolioService.depositCash(pId, new BigDecimal("10000"));
        PortfolioDto withCash = portfolioService.getPortfolio(pId);
        assertEquals(0, new BigDecimal("10000").compareTo(withCash.getCashBalance()));
        System.out.println("✅ [Test] Deposited 10000, Balance: " + withCash.getCashBalance());

        portfolioService.withdrawCash(pId, new BigDecimal("2000"));
        PortfolioDto afterWithdraw = portfolioService.getPortfolio(pId);
        assertEquals(0, new BigDecimal("8000").compareTo(afterWithdraw.getCashBalance()));
        System.out.println("✅ [Test] Withdrew 2000, Balance: " + afterWithdraw.getCashBalance());

        
        OrderRequest buyOrder = new OrderRequest();
        buyOrder.setTickerSymbol("AAPL");
        buyOrder.setVolume(10);
        buyOrder.setPrice(new BigDecimal("150.0000"));
        portfolioService.buyAsset(pId, buyOrder);

        PortfolioDto afterBuy = portfolioService.getPortfolio(pId);
        assertEquals(0, new BigDecimal("6500").compareTo(afterBuy.getCashBalance()));
        assertEquals(1, portfolioService.getPortfolioItems(pId).size());
        assertEquals("AAPL", portfolioService.getPortfolioItems(pId).get(0).getTickerSymbol());
        System.out.println("✅ [Test] Bought 10 AAPL @ $150, Cash deducted correctly: " + afterBuy.getCashBalance());

        
        OrderRequest buyMore = new OrderRequest();
        buyMore.setTickerSymbol("AAPL");
        buyMore.setVolume(10);
        buyMore.setPrice(new BigDecimal("200.0000"));
        portfolioService.buyAsset(pId, buyMore);

        List<PortfolioItemDto> items = portfolioService.getPortfolioItems(pId);
        assertEquals(1, items.size());
        assertEquals(0, new BigDecimal("20").compareTo(items.get(0).getVolume()));
        assertEquals(0, new BigDecimal("175.0000").compareTo(items.get(0).getPurchasePrice()));
        OrderRequest buyInvalid = new OrderRequest();
        buyInvalid.setTickerSymbol("XYZ-FAKE");
        buyInvalid.setVolume(1);
        buyInvalid.setPrice(new BigDecimal("100"));
        Exception noItemException = assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.buyAsset(pId, buyInvalid);
        });
        System.out.println("✅ [Test] Negative scenario passed: Prevented buying unlisted asset - " + noItemException.getMessage());
        System.out.println("✅ [Test] Averaged down successfully. Holding 20 AAPL, Avg Cost: " + items.get(0).getPurchasePrice());

        
        OrderRequest sellOrder = new OrderRequest();
        sellOrder.setTickerSymbol("AAPL");
        sellOrder.setVolume(5);
        sellOrder.setPrice(new BigDecimal("250.0000"));
        portfolioService.sellAsset(pId, sellOrder);

        PortfolioDto afterSell = portfolioService.getPortfolio(pId);
        assertEquals(0, new BigDecimal("5750").compareTo(afterSell.getCashBalance())); 
        assertEquals(0, new BigDecimal("15").compareTo(portfolioService.getPortfolioItems(pId).get(0).getVolume()));
        System.out.println("✅ [Test] Sold 5 AAPL for profit. Cash reimbursed.");

        
        PerformanceResponseDto perf = portfolioService.calculatePerformance(pId);
        System.out.println("✅ [Test] Performance retrieved. Total Cost: " + perf.getTotalCost() + ", Current Value: " + perf.getCurrentTotalValue());

        
        OrderRequest sellAll = new OrderRequest();
        sellAll.setTickerSymbol("AAPL");
        sellAll.setVolume(15);
        sellAll.setPrice(new BigDecimal("250.0000"));
        portfolioService.sellAsset(pId, sellAll);

        assertEquals(0, portfolioService.getPortfolioItems(pId).size());
        System.out.println("✅ [Test] Position fully liquidated. Soft delete triggered.");
    }

    @Test
    public void testNegativeScenariosAndValidation() {
        CreatePortfolioRequest createReq = new CreatePortfolioRequest("Validation Test", "test_user");
        PortfolioDto created = portfolioService.createPortfolio(createReq);
        Long pId = created.getId();

        
        Exception withdrawEx = assertThrows(IllegalArgumentException.class, () -> 
            portfolioService.withdrawCash(pId, new BigDecimal("5000"))
        );
        System.out.println("✅ [Validation] Expected Exception: " + withdrawEx.getMessage());

        
        OrderRequest buyOrder = new OrderRequest();
        buyOrder.setTickerSymbol("MSFT");
        buyOrder.setVolume(10);
        buyOrder.setPrice(new BigDecimal("200.00")); 
        
        Exception buyEx = assertThrows(IllegalArgumentException.class, () -> 
            portfolioService.buyAsset(pId, buyOrder)
        );
        System.out.println("✅ [Validation] Expected Exception: " + buyEx.getMessage());

        
        portfolioService.depositCash(pId, new BigDecimal("3000"));
        portfolioService.buyAsset(pId, buyOrder); 
        
        
        OrderRequest overSell = new OrderRequest();
        overSell.setTickerSymbol("MSFT");
        overSell.setVolume(15);
        overSell.setPrice(new BigDecimal("210.00"));
        
        Exception sellEx = assertThrows(IllegalArgumentException.class, () -> 
            portfolioService.sellAsset(pId, overSell)
        );
        System.out.println("✅ [Validation] Expected Exception: " + sellEx.getMessage());

        
        OrderRequest sellGhost = new OrderRequest();
        sellGhost.setTickerSymbol("TSLA");
        sellGhost.setVolume(5);
        sellGhost.setPrice(new BigDecimal("100.00"));

        Exception ghostEx = assertThrows(IllegalArgumentException.class, () -> 
            portfolioService.sellAsset(pId, sellGhost)
        );
        System.out.println("✅ [Validation] Expected Exception: " + ghostEx.getMessage());
    }
}
