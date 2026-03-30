import time
import random
import yfinance as yf
from datetime import datetime, date
from sqlalchemy import func
from sqlalchemy.dialects.mysql import insert
from models import CompanyInfo, HistoricalPrice

def catch_up_sync(db):
    """
    Retrieves the High Watermark (MAX trade_date), fetches missing historical
    data using yfinance, and bulk UPSERTs into the MySQL historical_price table.
    """
    print("[SYNC] Starting Catch-up Sync...")

    
    companies = db.query(CompanyInfo.ticker_symbol).all()
    tickers = [c[0] for c in companies]
    
    total_tickers = len(tickers)
    print(f"[SYNC] Found {total_tickers} tickers to process.")

    for idx, ticker in enumerate(tickers, start=1):
        try:
            
            watermark = db.query(func.max(HistoricalPrice.trade_date))\
                          .filter(HistoricalPrice.ticker_symbol == ticker)\
                          .scalar()

            start_date = watermark.strftime('%Y-%m-%d') if watermark else '2021-01-01'

            today_str = datetime.now().strftime('%Y-%m-%d')
            
            if start_date == today_str:
                continue

            
            stock = yf.Ticker(ticker)
            
            
            hist = stock.history(start=start_date)

            if hist.empty:
                print(f"[WARN] [{idx}/{total_tickers}] {ticker} returned no data since {start_date}.")
                time.sleep(random.uniform(0.5, 1.5))
                continue

            
            
            if hist.index.tz is not None:
                hist.index = hist.index.tz_localize(None)

            records = []
            for dt, row in hist.iterrows():
                trade_date_val = dt.date()
                close_price_val = float(row['Close'])
                records.append({
                    "ticker_symbol": ticker,
                    "trade_date": trade_date_val,
                    "close_price": close_price_val
                })

            if not records:
                continue

            
            stmt = insert(HistoricalPrice).values(records)
            upsert_stmt = stmt.on_duplicate_key_update(
                close_price=stmt.inserted.close_price
            )
            
            
            db.execute(upsert_stmt)
            db.commit()

            print(f"[OK] [{idx}/{total_tickers}] {ticker}: Synced {len(records)} records (Watermark: {start_date})")
            
            
            time.sleep(random.uniform(0.5, 1.5))

        except Exception as e:
            db.rollback()
            err_str = str(e)
            if "Too Many Requests" in err_str or "Rate limit" in err_str:
                print(f"[RATE LIMIT HIT] Sleeping for 15s... ({ticker})")
                time.sleep(15)
            else:
                print(f"[ERROR] syncing {ticker}: {e}")
                time.sleep(random.uniform(0.5, 1.5))
        
    print("[SYNC] Catch-up Sync Complete!")
