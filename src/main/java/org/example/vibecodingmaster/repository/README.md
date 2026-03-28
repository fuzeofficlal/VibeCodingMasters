# Repository 层 (Data Access)

本目录包含基于 Spring Data JPA 的数据访问接口（Repository）。负责通过 Hibernate 与 MySQL 数据库进行交互。

## 接口说明

- **[PortfolioRepository](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/repository/PortfolioRepository.java)**: 管理 `portfolio` 表。
- **[PortfolioItemRepository](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/repository/PortfolioItemRepository.java)**: 管理持仓明细，包含自定义 `findByPortfolioId` 查询。
- **[MarketPriceRepository](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/repository/MarketPriceRepository.java)**: 管理实时价格，提供 `findByTickerSymbolIn` 批量查询优化性能。
- **[CompanyInfoRepository](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/repository/CompanyInfoRepository.java)**: 管理公司基础信息，支持模糊搜索。
- **[HistoricalPriceRepository](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/repository/HistoricalPriceRepository.java)**: 管理历史价格数据（复合主键支持）。
