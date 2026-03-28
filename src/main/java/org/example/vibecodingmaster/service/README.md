# Service Layer

The core financial engine of VibeCodingMaster, housing all business logic and trade validation schemas.

## Key Features
- **Transactional Atomic Operations**: Every operation (BUY, SELL, DEPOSIT, WITHDRAW) is wrapped in `@Transactional` blocks, preventing partial updates during failures.
- **Financial Validation Rules**: 
  - Prevents buying assets with insufficient cash (`depositCash` requirement).
  - Throws custom assertions if `volume` or `price` inputs are mathematically invalid or malicious.
- **Portfolio Analytics**: `PortfolioService.calculatePerformance` computes ROE, absolute gains/losses, and retrieves aggregated historical metrics for the front-end dashboard.
- **Dependency Inversion**: Relies exclusively on `Repository` and maps properties down to DTOs before transferring payloads to the `Controller`.
