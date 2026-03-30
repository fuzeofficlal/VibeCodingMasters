from typing import List, Dict
import pandas as pd
from sqlalchemy import select
from database import engine
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import HistoricalPrice

def calculate_sma(ticker: str, days: int) -> List[Dict]:
    """
    Calculate Simple Moving Average for a given ticker over N days.
    - Fetches closing prices from historical_price table using SQLAlchemy/Pandas.
    - Applies df['close_price'].rolling(window=days).mean()
    """
    with engine.connect() as conn:
        # Fetch enough days to provide the 'days' rolling window + latest 100 points
        query = select(HistoricalPrice.trade_date, HistoricalPrice.close_price)\
            .where(HistoricalPrice.ticker_symbol == ticker)\
            .order_by(HistoricalPrice.trade_date.desc())\
            .limit(days + 200)
            
        df = pd.read_sql(query, conn)

    if df.empty:
        return []

    # Sort chronologically for rolling calculation
    df = df.sort_values('trade_date').reset_index(drop=True)

    # Calculate rolling SMA
    df['sma'] = df['close_price'].astype(float).rolling(window=days).mean()

    # Drop early records missing the SMA baseline
    df = df.dropna(subset=['sma'])
    
    # Optional: We only want to send back the most recent 100 valid SMA points to prevent massive UI payloads
    df = df.tail(100)

    # Format Output
    return [
        {
            "date": row['trade_date'].strftime('%Y-%m-%d'),
            "sma": round(float(row['sma']), 4)
        }
        for _, row in df.iterrows()
    ]
