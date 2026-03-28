import pymysql

DB_CONFIG = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'root',
    'password': 'Azhe114514', 
    'database': 'portfolio_db',
    'charset': 'utf8mb4',
    'autocommit': True
}

def create_table():
    print("Connecting to database...")
    conn = pymysql.connect(**DB_CONFIG)
    with conn.cursor() as cursor:
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS market_price (
                ticker_symbol VARCHAR(20) PRIMARY KEY,
                current_price DECIMAL(15,4),
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        ''')
        print("Table `market_price` created or already exists.")
    conn.close()

if __name__ == "__main__":
    create_table()
