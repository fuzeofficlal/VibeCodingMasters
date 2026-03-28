from fastapi import FastAPI, Depends, HTTPException, Query, BackgroundTasks
from sqlalchemy.orm import Session
from typing import List

from database import engine, get_db
import models
from scheduler import start_scheduler, run_sync_task

app = FastAPI(
    title="Market Data Microservice",
    description="ETL Provider managing historical syncs and real-time polling from Yahoo Finance",
    version="1.0.0"
)

# Application Lifecycle Hook
@app.on_event("startup")
async def startup_event():
    print("[STARTUP] [FastAPI] Initializing Market Data Service...")
    
    # 1. Start APScheduler
    start_scheduler()
    
    # 2. Trigger an immediate catch-up sync (Cold Start) has been DISABLED.
    # The server will now peacefully wait for the user to trigger it from the frontend UI.
    print("[STARTUP] [FastAPI] Awaiting manual Sync trigger from User Interface...")
@app.post("/api/v1/market/sync", summary="Manually trigger ETL High-Watermark Sync")
async def manual_sync(background_tasks: BackgroundTasks):
    background_tasks.add_task(run_sync_task)
    return {"status": "accepted", "message": "Background ETL Sync Started."}

@app.get("/api/v1/market/prices", summary="Get all current real-time market prices")
async def get_market_prices(
    tickers: str = Query(None, description="Comma-separated ticker symbols (e.g. AAPL,MSFT)"),
    db: Session = Depends(get_db)
):
    query = db.query(models.MarketPrice)
    if tickers:
        ticker_list = [t.strip().upper() for t in tickers.split(',')]
        query = query.filter(models.MarketPrice.ticker_symbol.in_(ticker_list))
    
    results = query.all()
    return [{"ticker_symbol": r.ticker_symbol, "current_price": r.current_price, "last_updated": r.last_updated} for r in results]


@app.get("/api/v1/market/history/{ticker}", summary="Get historical EOD close prices for a ticker")
async def get_historical_prices(
    ticker: str,
    start_date: str = Query(None, description="Start date (YYYY-MM-DD)"),
    end_date: str = Query(None, description="End date (YYYY-MM-DD)"),
    db: Session = Depends(get_db)
):
    query = db.query(models.HistoricalPrice)\
              .filter(models.HistoricalPrice.ticker_symbol == ticker.upper())\
              .order_by(models.HistoricalPrice.trade_date.asc())
              
    if start_date:
        query = query.filter(models.HistoricalPrice.trade_date >= start_date)
    if end_date:
        query = query.filter(models.HistoricalPrice.trade_date <= end_date)
        
    results = query.all()
    return [{"trade_date": r.trade_date, "close_price": r.close_price} for r in results]
