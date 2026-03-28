# Repository Layer

A Spring Data JPA abstraction layer for database querying and persistence logic.

## Key Features
- **Auto-generated Query Strategies**: Avoids native SQL string-building. Provides standard JpaRepository interfaces (`save`, `findById`, `findAll`).
- **Derived Queries**: Uses Spring Data's naming conventions (e.g., `findByPortfolioIdOrderByCreatedAtDesc`) for automatic pagination and filtering.
- **Transaction Ledger Awareness**: Implements `TransactionHistoryRepository` to query and reconstruct historic financial flows without manually writing SQL queries.
- **Support for Optimistic Locks**: Leverages Spring Data JPA capabilities to seamlessly detect version stamp updates and rollback modifications if needed.
