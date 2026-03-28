# 📅 VibeCodingMaster Team Development TODO

### 🎯 任务 1：自选股清单 (Watchlist) CRUD
**业务描述**：实现最经典的“我的自选股”逻辑，让用户可以收藏暂时还没购买的股票。

**代码落地位置 (Where to code)**:
- 📌 `src/main/java/org/example/vibecodingmaster/service/WatchlistService.java:Line 15` (填充 `getWatchlist` 等三个方法的 `TODO` 桩子)
- `repository/WatchlistRepository.java` (需主动新建底层 JpaRepository)
- `controller/WatchlistController.java` (已搭建完毕)

**🗄️ 数据库设计说明 (MySQL Schema)**:
表结构名叫 `watchlist`，对应实体在 `entity/Watchlist.java`，包含了 `id`, `portfolio_id`, `ticker_symbol`, `created_at`。你只需直接创建 Repository 使用即可。

> 💡 **提示**: 在 `Watchlist.java` 实体类中，我已经使用 `@ManyToOne` 关联了已有的 `Portfolio.java`。

**方法与 API 要求**:
1. **添加自选**
   - **API**: `POST /api/v1/portfolios/{id}/watchlist`
   - **Body (JSON)**: `{"tickerSymbol": "AAPL"}`
2. **移除自选**
   - **API**: `DELETE /api/v1/portfolios/{id}/watchlist/{ticker}`
3. **获取自选 (带当前市价)**
   - **API**: `GET /api/v1/portfolios/{id}/watchlist`
   - **Return**: `[{"tickerSymbol": "AAPL", "currentPrice": 150.50}, ...]`
   - **亮点实现**: 在 Service 中不仅要拿到该 ID 对应的清单，还要用依赖注入进来的 `marketPriceRepository` 去 `market_price` 快表里把最新价连带查出来返回给前端。

---

### 🎯 任务 2：资产配置饼图统计 (Asset Allocation)
**业务描述**：聚合用户的持仓，提供数据给前端画 Dashboard 饼图。

**代码落地位置 (Where to code)**:
- 📌 `src/main/java/org/example/vibecodingmaster/service/PortfolioService.java:Line 346` (填充 `getAssetAllocation` 方法中的 `TODO` 桩子)

**🗄️ 数据库交互说明 (Queries)**:
你需要查询已有的 `portfolio_item` 表和 `portfolio` 表。
- `portfolio_item` 包含字段：`asset_type` (枚举: STOCK/BOND/ETF)、`volume`、`purchase_price`。
- `portfolio` 包含字段：`cash_balance`。

**方法与 API 要求**:
- **API**: `GET /api/v1/portfolios/{id}/allocation`
- **Return (JSON)**: `{"STOCK": 50000.0, "CASH": 12000.0, "BOND": 8000.0}`


---

### 🎯 任务 3：止盈止损提醒策略 (Price Alerts)
**业务描述**：高阶玩家的量化交易风控基础，允许为单一持仓仓位设置两个阈值。

**代码落地位置 (Where to code)**:
- 📌 `src/main/java/org/example/vibecodingmaster/service/PortfolioService.java:Line 358` (填充 `setPriceAlerts` 和 `checkPriceAlerts` 方法里的 `TODO` 桩子)

**🗄️ 数据库设计说明 (MySQL Schema)**:
现有的 `portfolio_item` 表结构上
- `target_price`: 目标止盈价 (DECIMAL(19,4))
- `stop_loss_price`: 止损价 (DECIMAL(19,4))

**方法与 API 要求**:
1. **设置/更新阈值接口**:
   - **API**: `PUT /api/v1/portfolios/{id}/items/{itemId}/alerts`
   - **Body**: `{"targetPrice": 200.0, "stopLossPrice": 120.0}`
2. **嗅探报警接口 (核心)**:
   - **API**: `GET /api/v1/portfolios/{id}/alerts`
   - **Return**: `["AAPL 已突破止盈目标 $200.0!", "TSLA 严重跌破止损阈值 $180.0!"]`
   - **工作流**: 读取该账户所有的 `portfolio_item` -> 提取 `target_price` -> 去 `market_price` 表拉取现价 -> 用 Java `BigDecimal.compareTo()` 判断越界。
