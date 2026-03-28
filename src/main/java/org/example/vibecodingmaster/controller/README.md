# Controller 层 (Web API)

本目录包含应用程序的 REST 接口定义。负责处理 HTTP 请求、验证输入并调用 Service 层逻辑。

## 主要类说明

### [PortfolioController](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/controller/PortfolioController.java)
- **功能**: 管理投资组合（Portfolio）及其持仓明细（PortfolioItem）。
- **核心接口**:
  - `POST /api/v1/portfolios`: 创建新的投资组合。
  - `GET /api/v1/portfolios/{id}`: 获取组合详情。
  - `GET /api/v1/portfolios/{id}/performance`: **核心计算接口**，返回组合的实时总价值、成本及 ROI。
  - `POST /api/v1/portfolios/{id}/items`: 向组合添加资产。

### [MarketDataController](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/controller/MarketDataController.java)
- **功能**: 提供市场基础数据查询。
- **核心接口**:
  - `GET /api/v1/market/companies`: 搜索上市公司信息（支持模糊查询）。
  - `GET /api/v1/market/prices/{ticker}`: 获取特定股票的实时价格。
