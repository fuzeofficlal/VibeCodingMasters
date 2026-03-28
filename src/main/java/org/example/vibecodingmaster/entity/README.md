# Entity 层 (Domain Models)

本目录包含 JPA 实体类，直接映射到数据库表结构。

## 模型说明

- **[Portfolio](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/entity/Portfolio.java)**: 投资组合主表。包含用户关联和多条持仓记录（一对多）。
- **[PortfolioItem](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/entity/PortfolioItem.java)**: 资产明细（多对一关联到 Portfolio）。
- **[MarketPrice](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/entity/MarketPrice.java)**: 实时价格。
- **[HistoricalPrice](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/entity/HistoricalPrice.java)**: 历史价格数据。
- **[AssetType](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/entity/AssetType.java)**: 资产类型枚举（STOCK, BOND, CASH）。
