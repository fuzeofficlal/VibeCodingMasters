from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
import pytz
from datetime import datetime

from database import SessionLocal
from services.sync_engine import catch_up_sync
from services.polling_engine import poll_realtime_prices

def run_sync_task():
    """Wrapper to inject a DB session into the catch_up_sync engine."""
    print("[SCHEDULER] Executing Scheduled Catch-Up Sync (EOD)...")
    db = SessionLocal()
    try:
        catch_up_sync(db)
    finally:
        db.close()

def run_polling_task():
    """Wrapper to inject a DB session into the poll_realtime_prices engine."""
    # Strict market hour check (9:30 AM - 4:00 PM EST)
    nyc_tz = pytz.timezone('America/New_York')
    now_nyc = datetime.now(nyc_tz)
    
    if now_nyc.hour == 9 and now_nyc.minute < 30:
        return # Skip pre-market 9:00 - 9:29
    if now_nyc.hour == 16 and now_nyc.minute > 0:
        return # Skip post-market 16:01 - 16:59
        
    print("[SCHEDULER] Executing Intra-day Real-Time Polling...")
    db = SessionLocal()
    try:
        poll_realtime_prices(db)
    finally:
        db.close()


def start_scheduler():
    scheduler = BackgroundScheduler(timezone=pytz.timezone('America/New_York'))
    
    # 1. Catch-up Sync: Runs at 6:00 PM EST every weekday
    scheduler.add_job(
        run_sync_task,
        CronTrigger(day_of_week='mon-fri', hour=18, minute=0, timezone='America/New_York'),
        id="catch_up_eod",
        replace_existing=True
    )
    
    # 2. Real-time Polling: Runs every 5 minutes from 9:00 AM to 4:00 PM EST 
    # (Pre/Post market strictly validated inside run_polling_task wrapper)
    scheduler.add_job(
        run_polling_task,
        CronTrigger(day_of_week='mon-fri', hour='9-16', minute='*/5', timezone='America/New_York'),
        id="realtime_polling",
        replace_existing=True
    )

    scheduler.start()
    print("[SCHEDULER] APScheduler started. Tracking America/New_York timezone.")
    return scheduler
