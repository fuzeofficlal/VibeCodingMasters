import time
import yfinance as yf
from datetime import datetime
import pandas as pd
from sqlalchemy.dialects.mysql import insert
from models import CompanyInfo, MarketPrice

def poll_realtime_prices(db):
    """
    Batches all tickers into a single string for a low-frequency 
    network call to yFinance requesting a '1d' snapshot.
    Extracts the latest close price per ticker and bulk UPSERTs.
    """
    print(f"[POLL] Triggered Real-time Snapshot at {datetime.now()} EST")

    # 1. Fetch all tickers
    companies = db.query(CompanyInfo.ticker_symbol).all()
    if not companies:
        print("[WARN] No companies found in portfolio_db.")
        return

    tickers = [c[0] for c in companies]
    tickers_string = " ".join(tickers)

    # 2. Batch Download Data
    try:
        # download returns a MultiIndex DataFrame if there are multiple tickers
        # Data format: columns level 0 consists of price fields ('Close', 'Open', etc.)
        # columns level 1 consists of tickers ('AAPL', 'MSFT', etc.)
        print(f"[FETCH] Real-time 1d snapshot for {len(tickers)} tickers...")
        snapshot = yf.download(tickers_string, period="1d", threads=True, progress=False)

        if snapshot.empty:
            print("[WARN] yFinance snapshot returned empty.")
            return

        records = []
        # Support both single-ticker and multi-ticker return types
        if len(tickers) == 1:
            ticker = tickers[0]
            # Series or DataFrame without MultiIndex
            if 'Close' in snapshot.columns:
                close_price = snapshot['Close'].iloc[-1]
                records.append({
                    "ticker_symbol": ticker,
                    "current_price": float(close_price)
                })
        else:
            # MultiIndex columns. e.g. snapshot['Close']['AAPL']
            if 'Close' in snapshot.columns.levels[0]:
                close_df = snapshot['Close']
                for ticker in tickers:
                    if ticker in close_df.columns:
                        latest_price = close_df[ticker].iloc[-1]
                        if not pd.isna(latest_price):
                            records.append({
                                "ticker_symbol": ticker,
                                "current_price": float(latest_price)
                            })
                            
        if not records:
            print("[ERROR] Found no processable close prices.")
            return

        # 3. UPSERT into MySQL table `market_price`
        stmt = insert(MarketPrice).values(records)
        upsert_stmt = stmt.on_duplicate_key_update(
            current_price=stmt.inserted.current_price
            # last_updated will auto-update based on ON UPDATE CURRENT_TIMESTAMP mapping
        )

        db.execute(upsert_stmt)
        db.commit()

        print(f"[OK] [Polling Engine] Successfully flushed {len(records)} updated market prices to DB.")

    except Exception as e:
        db.rollback()
        err_str = str(e)
        if "Too Many Requests" in err_str or "Rate limit" in err_str:
            print("[RATE LIMIT HIT] Polling Engine paused due to 429 restriction.")
        else:
            print(f"[ERROR] during batched yf.download: {e}")
