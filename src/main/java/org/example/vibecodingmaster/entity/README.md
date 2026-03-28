# Entity Layer

Contains the JPA entity definitions for the application, representing the core domain model mapped to the database.

## Key Features
- **Auditing**: Implements `@EntityListeners(AuditingEntityListener.class)` to automatically manage `createdAt` and `updatedAt` timestamps.
- **Optimistic Locking**: Uses `@Version` fields to prevent concurrent modification anomalies (e.g., race conditions during trades).
- **Soft Delete**: Adopts `@SQLDelete` and `@SQLRestriction("is_deleted=false")` to ensure records (like fully liquidated holdings or deleted portfolios) are logically archived rather than physically removed.
- **Immutability**: The `TransactionHistory` entity is designed as an append-only ledger component, preventing updates or physical deletes.

## Core Entities
- `Portfolio`: The root entity managing a user's cash balance and holding references.
- `PortfolioItem`: Represents an individual asset holding within a portfolio (Stock, Bond, etc.).
- `TransactionHistory`: Financial-grade ledger recording every DEPOSIT, WITHDRAW, BUY, and SELL event.
