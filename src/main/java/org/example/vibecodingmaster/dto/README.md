# DTO 层 (Data Transfer Objects)

本目录包含数据传输对象，用于 API 响应和请求参数。

## 主要 DTO 说明

### 响应对象 (Response)
- **[PortfolioDto](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/dto/PortfolioDto.java)**: 投资组合全量数据（包含 items）。
- **[PerformanceResponseDto](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/dto/PerformanceResponseDto.java)**: 收益评测数据，包含总盈亏和 ROI。

### 请求对象 (Request)
- **[CreatePortfolioRequest](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/dto/CreatePortfolioRequest.java)**: 创建或更新组合名称。
- **[AddPortfolioItemRequest](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/dto/AddPortfolioItemRequest.java)**: 录入资产信息的参数包。
