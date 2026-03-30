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
    
    nyc_tz = pytz.timezone('America/New_York')
    now_nyc = datetime.now(nyc_tz)
    
    if now_nyc.hour == 9 and now_nyc.minute < 30:
        return 
    if now_nyc.hour == 16 and now_nyc.minute > 0:
        return 
        
    print("[SCHEDULER] Executing Intra-day Real-Time Polling...")
    db = SessionLocal()
    try:
        poll_realtime_prices(db)
    finally:
        db.close()


def start_scheduler():
    scheduler = BackgroundScheduler(timezone=pytz.timezone('America/New_York'))
    
    
    scheduler.add_job(
        run_sync_task,
        CronTrigger(day_of_week='mon-fri', hour=18, minute=0, timezone='America/New_York'),
        id="catch_up_eod",
        replace_existing=True
    )
    
    
    
    scheduler.add_job(
        run_polling_task,
        CronTrigger(day_of_week='mon-fri', hour='9-16', minute='*/5', timezone='America/New_York'),
        id="realtime_polling",
        replace_existing=True
    )

    scheduler.start()
    print("[SCHEDULER] APScheduler started. Tracking America/New_York timezone.")
    return scheduler
