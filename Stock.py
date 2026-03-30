import pymysql
import yfinance as yf
import pandas as pd
from datetime import datetime
import time
import requests
import io
import random
import cryptography
import os
import json

# 1. 数据库配置 (请确保密码和你的本地环境一致)
DB_CONFIG = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'root',
    'password': 'Azhe114514',  # 替换为你的 MySQL 密码
    'database': 'portfolio_db',
    'charset': 'utf8mb4',
    'autocommit': True
}


# 2. 初始化数据库和双表结构
def init_db():
    temp_conn = pymysql.connect(
        host=DB_CONFIG['host'], port=DB_CONFIG['port'],
        user=DB_CONFIG['user'], password=DB_CONFIG['password']
    )
    with temp_conn.cursor() as cursor:
        cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_CONFIG['database']} DEFAULT CHARSET utf8mb4")
    temp_conn.close()

    conn = pymysql.connect(**DB_CONFIG)
    with conn.cursor() as cursor:
        # 表1: 公司基本信息表
        cursor.execute('''
                       CREATE TABLE IF NOT EXISTS company_info
                       (
                           ticker_symbol
                           VARCHAR
                       (
                           20
                       ) PRIMARY KEY,
                           company_name VARCHAR
                       (
                           255
                       )
                           )
                       ''')
        # 表2: 历史价格流水表
        cursor.execute('''
                       CREATE TABLE IF NOT EXISTS historical_price
                       (
                           ticker_symbol
                           VARCHAR
                       (
                           20
                       ),
                           trade_date DATE,
                           close_price DECIMAL
                       (
                           15,
                           4
                       ),
                           PRIMARY KEY
                       (
                           ticker_symbol,
                           trade_date
                       )
                           )
                       ''')
    return conn


# 3. 获取标普 500 列表 (已修复 403 Forbidden 和 Pandas 文件未找到错误)
def get_sp500_tickers():
    cache_file = 'sp500_cache.json'

    # 檢查本地是否已經有緩存文件
    if os.path.exists(cache_file):
        print("📦 發現本地緩存！正在從 sp500_cache.json 讀取股票列表...")
        with open(cache_file, 'r', encoding='utf-8') as f:
            return json.load(f)

    # 如果沒有緩存，則從維基百科抓取
    print("🌐 緩存不存在，正在從維基百科獲取 S&P 500 股票列表...")
    url = 'https://en.wikipedia.org/wiki/List_of_S%26P_500_companies'
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    response = requests.get(url, headers=headers)
    response.raise_for_status()

    tables = pd.read_html(io.StringIO(response.text))
    df = tables[0]
    tickers = df['Symbol'].str.replace('.', '-').tolist()
    names = df['Security'].tolist()

    stock_list = list(zip(tickers, names))

    # 將抓取到的名單寫入本地 JSON 緩存文件
    print("💾 正在將股票名單保存到本地緩存 (sp500_cache.json)...")
    with open(cache_file, 'w', encoding='utf-8') as f:
        json.dump(stock_list, f, ensure_ascii=False, indent=4)

    return stock_list


# 4. 抓取历史数据 (加入企业级“指数退避”重试机制)
def fetch_and_save_historical_data(conn, stock_list):
    total_stocks = len(stock_list)
    print(f"\n准备抓取 {total_stocks} 只股票从 2021-01-01 至今的历史数据...")

    with conn.cursor() as cursor:
        for index, (ticker, name) in enumerate(stock_list, 1):
            max_retries = 3  # 最大重试次数

            for attempt in range(max_retries):
                try:
                    # 1. 插入公司信息
                    cursor.execute('''
                                   INSERT INTO company_info (ticker_symbol, company_name)
                                   VALUES (%s, %s) ON DUPLICATE KEY
                                   UPDATE company_name=
                                   VALUES (company_name)
                                   ''', (ticker, name))

                    # 2. 获取历史数据
                    stock = yf.Ticker(ticker)
                    hist = stock.history(start="2021-01-01")

                    if not hist.empty:
                        records = []
                        for date, row in hist.iterrows():
                            trade_date_str = date.strftime('%Y-%m-%d')
                            close_price = float(row['Close'])
                            records.append((ticker, trade_date_str, close_price))

                        # 批量写入
                        sql = '''
                              INSERT INTO historical_price (ticker_symbol, trade_date, close_price)
                              VALUES (%s, %s, %s) ON DUPLICATE KEY \
                              UPDATE close_price = \
                              VALUES (close_price) \
                              '''
                        cursor.executemany(sql, records)
                        print(f"[{index}/{total_stocks}] 成功: {ticker} ({name}) - 写入 {len(records)} 条")
                    else:
                        print(f"[{index}/{total_stocks}] 警告: {ticker} 没有返回历史数据")

                    # 成功后，跳出重试循环
                    break

                except Exception as e:
                    error_msg = str(e)
                    # 拦截 429 频率限制错误
                    if "Too Many Requests" in error_msg or "Rate limited" in error_msg:
                        if attempt < max_retries - 1:
                            # 指数退避休眠：10秒, 20秒... 加上一点随机抖动
                            sleep_time = 10 * (attempt + 1) + random.uniform(1.0, 3.0)
                            print(
                                f"  ⚠️ 触发限流，进入伪装潜水模式... 休眠 {sleep_time:.1f} 秒后进行第 {attempt + 1} 次重试")
                            time.sleep(sleep_time)
                        else:
                            print(f"[{index}/{total_stocks}] ❌ 彻底失败: {ticker} 被坚决拦截。")
                    else:
                        print(f"[{index}/{total_stocks}] 错误: 抓取 {ticker} 失败。原因: {e}")
                        break  # 如果是其他未知错误，直接跳过这只股票

            # 正常情况下的基础休眠，稍微调大一点点保护 IP
            time.sleep(random.uniform(2.0, 4.0))

    print("\n✅ 所有历史数据写入流程结束！")

if __name__ == "__main__":
    db_connection = init_db()
    sp500_stocks = get_sp500_tickers()

    fetch_and_save_historical_data(db_connection, sp500_stocks[:500])

    db_connection.close()