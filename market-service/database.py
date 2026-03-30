from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import sessionmaker

# Database connection URL (matches Stock.py configuration)
SQLALCHEMY_DATABASE_URL = "mysql+pymysql://root:Azhe114514@127.0.0.1:3306/portfolio_db?charset=utf8mb4"

# Configure connection pool as requested: pool_size=5, max_overflow=10
engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    pool_size=5,
    max_overflow=10,
    pool_pre_ping=True,  # Proactively verify connections
    echo=False
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base strictly used for mapping existing tables. We will NOT call Base.metadata.create_all()
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
