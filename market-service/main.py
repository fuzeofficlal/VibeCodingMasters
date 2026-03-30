from fastapi import FastAPI, Depends, HTTPException, Query, BackgroundTasks
from sqlalchemy.orm import Session
from typing import List

from database import engine, get_db
import models
from scheduler import start_scheduler, run_sync_task
from services import quant_engine

app = FastAPI(
    title="Market Data Microservice",
    description="ETL Provider managing historical syncs and real-time polling from Yahoo Finance",
    version="1.0.0"
)


@app.on_event("startup")
async def startup_event():
    print("[STARTUP] [FastAPI] Initializing Market Data Service...")
    
    
    start_scheduler()
    
    
    
    print("[STARTUP] [FastAPI] Awaiting manual Sync trigger from User Interface...")
from services.polling_engine import poll_realtime_prices
from database import SessionLocal

def execute_full_override_sync():
    """Execute both historical catch-up and real-time polling bypassing time gates."""
    
    run_sync_task()
    
    
    print("[MANUAL] Executing Forced Real-Time Polling...")
    db = SessionLocal()
    try:
        poll_realtime_prices(db)
    finally:
        db.close()

@app.post("/api/v1/market/sync", summary="Manually trigger ETL High-Watermark Sync")
async def manual_sync(background_tasks: BackgroundTasks):
    background_tasks.add_task(execute_full_override_sync)
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

@app.get("/api/v1/market/indicators/sma/{ticker}", summary="[Task 4] Get Simple Moving Average (SMA)")
async def get_sma_indicator(ticker: str, days: int = Query(50, description="Rolling window days (e.g. 20, 50, 200)")):
    return quant_engine.calculate_sma(ticker, days)
