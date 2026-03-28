# Controller Layer

Exposes the core business capabilities to the frontend or Go API Gateway using RESTful, semantic mappings.

## Scope of Responsibilities
- **API Versioning**: Enforces strict URL structures under `/api/v1/portfolios`.
- **Payload Unpacking**: Translates HTTP JSON objects into mapped `xyzRequest` models.
- **Data Transfer Isolation**: Responds using strongly-typed `Dto` response elements (e.g. `PortfolioItemDto`, `TransactionHistoryDto`).
- **Endpoint Design**: Promotes specific functional behavior paths:
  - `POST /{id}/transactions` (Trading system).
  - `POST /{id}/cash` (Deposit/Withdraw module).
  - `GET /{id}/performance` (Valuation metrics).
- **Swagger Documentation**: Utilizes `@Tag` and `@Operation` OpenAPI metadata.
