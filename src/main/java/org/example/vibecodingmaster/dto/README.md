# Data Transfer Object (DTO) Layer

Contains POJOs used for data exchange between the client (or Gateway) and the REST Controller. DTOs ensure that the internal database schema (`entity`) is decoupled from the external API representation.

## Key Features
- **Immutability Focus**: Many DTOs leverage `@Builder` and `record` patterns for thread-safe data immutability.
- **Request Payload Models**: Structured inputs for complex financial actions:
  - `OrderRequest`: For submitting `BUY` and `SELL` instructions (includes volume and target price).
  - `CashTransactionRequest`: For dispatching `DEPOSIT` and `WITHDRAW` events.
  - `CreatePortfolioRequest`: For initializing new accounts.
- **Response Models**: 
  - `PerformanceResponseDto`: A specialized aggregated view of a portfolio's ROI, total cost, and current valuation.
  - `TransactionHistoryDto`: A read-only projection of the immutable ledger for end-users.
