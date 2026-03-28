# VibeCodingMasters


For JIRA workspace
https://vibecodingmaster.atlassian.net/jira/software/projects/SCRUM/code?atlOrigin=eyJpIjoiNzBjNmU0YTM2OWQ5NDUzMGFmMTIzNjMxNmE0M2Q3ODMiLCJwIjoiaiJ9



数据表名称,所属领域,核心功能与作用,数据更新频率
portfolio,投资组合管理,定义和管理用户的投资账户主体。,低（由用户操作触发）
portfolio_item,投资组合管理,记录用户具体的资产购买、持有明细与成本基准。,中（由用户交易触发）
company_info,市场数据,提供标准化、结构化的公司基础信息字典。,极低（定期维护）
market_price,市场数据,缓存全市场最新单日收盘价，提供低延迟的资产估值计算基础。,高（每日/实时更新）
historical_price,市场数据,存储大体量时间序列价格数据，支撑性能走势图与趋势分析。,高（每日批量写入）



1. 投资组合管理业务模块 (Portfolio Management)
   该模块负责系统核心业务逻辑，承载所有的用户状态与交易行为。

portfolio (投资组合主表)
定义：账户层级的实体，作为一切资产配置的最高层级容器。

核心字段：

id (PK): 唯一标识符。

user_id: 用户标识，用于区分不同所有者。

name: 组合名称（如“养老基金”、“激进成长”）。

架构意义：为系统未来从单用户向多用户、多账户体系扩展建立逻辑边界。

portfolio_item (持仓明细表)
定义：记录投资组合内各项资产（股票、债券、现金）的具体持有状态。

核心字段：

id (PK): 唯一标识符。

portfolio_id (FK): 关联 portfolio.id。

asset_type: 资产类型。

ticker_symbol: 交易代码。

volume: 持有份额（建议采用高精度小数，如 DECIMAL(18, 8)）。

purchase_price: 成本均价。

purchase_date: 建仓日期。

架构意义：通过记录成本价与持有份额，为系统计算 投资回报率 (ROI) 提供绝对基准数据。

2. 金融市场数据模块 (Market Data)
   该模块为系统提供客观的第三方金融数据支撑，与用户行为完全解耦。

company_info (公司基本信息表)
定义：标普 500 等成分股的基础数据字典。

核心字段：

ticker_symbol (PK): 交易代码。

company_name: 公司全称。

架构意义：实现数据规范化，保证前端展示与后端校验的名称一致性。

market_price (最新行情快照表)
定义：存储各资产的最新已知市场价格。

核心字段：

ticker_symbol (PK): 交易代码。

current_price: 最新现价。

last_updated: 更新时间戳。

架构意义：采用快慢表分离设计。计算用户资产规模（AUM）时直接查询此表，避免对海量历史数据进行全表扫描（Full Table Scan），显著降低数据库 I/O 压力并提升 API 响应速度。

historical_price (历史价格流水表)
定义：存储长周期的时间序列数据集 (Time-Series Data)。

核心字段：

ticker_symbol & trade_date (Composite PK): 组合主键。

close_price: 每日收盘价。

架构意义：支持复杂的数据分析，直接服务于前端的“查看表现 (View the performance)”折线图功能，并为未来的 AI 预测或蒙特卡洛模拟（Monte Carlo simulation）提供底层原始数据。

设计笔记：
在处理 volume 和 price 等金融字段时，务必避免使用浮点数（float/double），推荐使用 DECIMAL 类型以防止精度丢失。