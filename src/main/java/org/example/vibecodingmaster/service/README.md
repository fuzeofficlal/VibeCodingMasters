# Service 层 (Business Logic)

本目录包含应用程序的核心业务逻辑实现。它是 Controller 层与 Repository 层之间的桥梁，负责复杂的计算、事务管理和数据转换。

## 主要类说明

### [PortfolioService](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/service/PortfolioService.java)
- **功能**: 投资组合的全生命周期管理及资产表现计算。
- **核心逻辑**:
  - `calculatePerformance`: 聚合持仓，调用市场数据，计算 ROI 和资产分布情况。
  - 提供 DTO 与 Entity 之间的转换逻辑。

### [MarketDataService](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/service/MarketDataService.java)
- **功能**: 封装对市场数据和公司信息的访问逻辑。
- **核心逻辑**:
  - 处理模糊搜索匹配算法。
  - 封装价格查询的异常处理。
