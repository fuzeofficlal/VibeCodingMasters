# 📈 VibeCodingMaster - Financial Portfolio System

VibeCodingMaster is a robust, financial-grade investment portfolio management platform. The application is built using a modern **Microservices Architecture**, cleanly separating the responsibilities of API routing, business transaction processing, and ETL market data aggregation.

---

## 🏗️ Architecture & Component Layout

The system consists of three distinct components running in tandem, communicating securely via standard HTTP and a shared MySQL Database:

### 1. The Core Transaction Engine (Java / Spring Boot)
- **Role**: Secure Business Logic & Incorruptible Ledger.
- **Port**: `:8080`
- **Tech Stack**: Java 25, Spring Boot 4.x, Hibernate (JPA).
- **Duties**:
  - Handles strict double-entry bookkeeping for `cash_balance` and `transaction_history`.
  - Enforces Soft Deletions and Optimistic Locking mechanisms.
  - Queries real-time prices computed by the Python engine to generate highly accurate ROI calculations.

### 2. The API Gateway (Go / Gin)
- **Role**: Edge router, Static File Server & Defense layer.
- **Port**: `:8090`
- **Tech Stack**: Go 1.22+, Gin Web Framework.
- **Duties**:
  - Exposes the HTML frontend.
  - Transparently reverse-proxies requests into the internal Java Backend.
  - Enhances security using a strict Rate Limiter (20 Req/Sec), protecting downstream services from traffic spikes.
  - Hardened CORS controls for standardized cross-origin behavior.

### 3. The Market Data Microservice (Python / FastAPI)
- **Role**: Domain-Driven ETL Data feed running strictly on EST parameters.
- **Port**: `:8000`
- **Tech Stack**: Python 3.10+, FastAPI, yFinance, APScheduler, SQLAlchemy.
- **Duties**:
  - **Cold Start Catch-Up**: Scans historical tables for gaps on startup and populates incremental EOD closing history securely without overlapping.
  - **Intraday Snapshot Engine**: Activates precisely between `09:30 AM - 04:00 PM EST` on weekdays to pull ultra-fast parallel snapshots of all tracked tickers.
  - Flushes high-frequency data into the `market_price` MySQL Table utilizing UPSERT optimizations, enabling the Java backend to read the absolute specific current value of assets effortlessly.

---

## 🚀 Execution & Launch Sequence

To initialize all the isolated microservices seamlessly, we have provided an automated batch script.

### Pre-requisites
- **MySQL**: Running on `localhost:3306` with database `portfolio_db` accessible by `root` / `Azhe114514`.
- **Python Specs**: An initialized virtual environment (`market-service/venv`) holding the dependencies.

### Running the Project
Simply execute the global runner from the root of the project:
```bat
start-all.bat
```

This will automatically boot three isolated terminal windows:
1. Initialize Java Context on Port 8080.
2. Boot Go Edge Gateway routing to 8090.
3. Hook Python `APScheduler` and Uvicorn Server to Port 8000.

### Interacting with the UI
Navigate to **`http://localhost:8090`** to access the interactive Trade Desk and Dashboard. 
*(Ensure you perform CRUD flows through `:8090` exclusively to test Gateway capabilities).*

---

## 🗄️ Database Schema (`portfolio_db`)
The schema enforces strict ACID compliance and relational integrity natively tracking users' assets alongside market trends.

1. **`portfolio`**: Core account anchor. Fields: `id`, `name`, `user_id`, `cash_balance`, `created_at`, `updated_at`.
2. **`portfolio_item`**: Active holdings ledger. Fields: `id`, `portfolio_id` (FK), `ticker_symbol`, `asset_type` (STOCK, BOND, CASH), `volume`, `purchase_price`, `target_price` (Alerts), `stop_loss_price` (Alerts), `created_at`, `updated_at`.
3. **`transaction_history`**: Immutable audit log. Fields: `id`, `portfolio_id` (FK), `transaction_type` (BUY, SELL, DEPOSIT, WITHDRAW), `ticker_symbol`, `amount`, `price`, `transaction_date`.
4. **`market_price`**: Real-time price cache maintained by Python. Fields: `ticker_symbol` (PK), `current_price`, `last_updated_at`.
5. **`historical_price`**: EOD pricing table for back-calculation. Fields: `id`, `ticker_symbol`, `close_price`, `trade_date`.
6. **`watchlist`**: Developer feature for tracking user-favorited tickers. Fields: `id`, `portfolio_id` (FK), `ticker_symbol`, `created_at`.

---

## 📜 System Interfaces (API Endpoints)

### 1. Gateway Routing (Go - `localhost:8090`)
- **ANY `/api/v2/*`** -> Rewrites gracefully to `/api/v1/*` and proxies to the Java Backend.
- **ANY `/api/v2/market/*`** -> Bypasses Java, proxies straight into Python Data Service.
- **ANY `/`** -> Retains reverse-proxy connection to serve `index.html`.

### 2. Core Business Engine (Java - `localhost:8080`)
- `POST /api/v1/portfolios` - Bootstraps a fresh user abstract portfolio
- `POST /api/v1/portfolios/{id}/cash` - Liquidity deposit / withdrawal
- `POST /api/v1/portfolios/{id}/transactions` - Asset transaction ledger mutations
- `GET /api/v1/portfolios/{id}/performance` - Master Valuations engine (ROI & 90-day History)
- `GET /api/v1/portfolios/{id}/allocation` - Asset Allocation Pie Chart Aggregation
- `PUT /api/v1/portfolios/{id}/items/{itemId}/alerts` - Sets algorithmic trading boundary conditions
- `POST /api/v1/system/shutdown` - Execute system-wide kill switch across terminal trees

### 3. Quantitative Market Engine (Python - `localhost:8000`)
- `POST /api/v1/market/sync` - Overrides schedulers and triggers manual ETL pipeline dump
- `GET /api/v1/market/indicators/sma/{ticker}` - Performs live algorithmic Numpy/Pandas SMA calculations

---

## 🧠 Business Logic Highlights

**1. Transaction Ledger Integrity**
- **Purchasing**: A user cannot buy an asset with insufficient `cash_balance`. The Java service inherently guards against API spoofing by validating requested buy prices exclusively against the authoritative `market_price` MySQL table run by Python.
- **Idempotency**: All financial footprint modifications enforce strict Spring `@Transactional` persistence. If an asset buy request goes through, it deducts uninvested cash, upserts the new volume into `portfolio_item` via Optimistic Locking, and imprints a `BUY` record seamlessly into `transaction_history` in one single DB commit.

**2. Market Data Pipeline (Python ETL Scheduled Flow)**
- **Cold Boot Recovery Algorithm**: On boot sequence, Python iterates through the `historical_price` database to fetch the last known trade date for each ticker in the S&P 500, immediately requesting only the precise gap days from Yahoo Finance APIs. 
- **Intraday Flow Control**: The `APScheduler` cron job runs strictly between **9:30 AM and 4:00 PM EST**, generating parallel requests using `ThreadPoolExecutor` to upsert thousands of high-frequency price pulses into `market_price`.

**3. Algorithmic Forward-Fill Valuations (Java)**
- The 90-day historical visualization uses pure backend algorithmic calculation over mathematical UI hacks. It traverses backwards dynamically pulling the specific `historical_price` table mapping to the user's holdings at exactly that date. Even if weekends or holidays cause market gaps, the Java simulation employs a Forward-fill logic mechanism anchored upon the static `uninvestedCash` block to guarantee perfectly continuous mathematical visualization on the Dashboard.

---
*Developed with Vibe & Engineering Discipline.*