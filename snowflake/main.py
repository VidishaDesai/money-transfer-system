import mysql.connector
import snowflake.connector
import pandas as pd
import json
import os
import traceback
import numpy as np
from config import MYSQL_CONFIG, SNOWFLAKE_CONFIG

STATE_FILE = "state.json"

print("==== ETL SCRIPT STARTED ====")


def load_state():
    print("Loading state file...")
    if not os.path.exists(STATE_FILE):
        print("State file not found. Creating default state.")
        return {
            "accounts_last_loaded": "1970-01-01 00:00:00",
            "transactions_last_loaded": "1970-01-01 00:00:00",
            "rewards_last_loaded": "1970-01-01 00:00:00"
        }
    with open(STATE_FILE, "r") as f:
        state = json.load(f)
        print("Loaded state:", state)
        return state


def save_state(state):
    print("Saving state:", state)
    with open(STATE_FILE, "w") as f:
        json.dump(state, f, indent=4)

def test_mysql_connection():
    print("Testing MySQL connection...")

    try:
        print("MYSQL CONFIG:", MYSQL_CONFIG)

        conn = mysql.connector.connect(
            **MYSQL_CONFIG
        )

        print("MySQL connection established")

        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM accounts")
        count = cursor.fetchone()[0]
        print("MySQL accounts count:", count)

        cursor.close()
        conn.close()
        print("MySQL connection closed")

    except Exception as e:
        print("MYSQL CONNECTION FAILED")
        print("Error:", e)
        traceback.print_exc()

# def test_mysql_connection():
    print("Testing MySQL connection...")
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM accounts")
    count = cursor.fetchone()[0]
    print("MySQL accounts count:", count)
    cursor.close()
    conn.close()


def test_snowflake_connection():
    print("Testing Snowflake connection...")
    conn = snowflake.connector.connect(**SNOWFLAKE_CONFIG)
    cursor = conn.cursor()

    cursor.execute("SELECT CURRENT_DATABASE(), CURRENT_SCHEMA()")
    print("Snowflake context:", cursor.fetchone())

    cursor.execute("SELECT COUNT(*) FROM MONEY_TRANSFER_OLTP.CORE.accounts")
    count = cursor.fetchone()[0]
    print("Snowflake accounts count:", count)

    cursor.close()
    conn.close()


def extract_mysql(query):
    print("Running MySQL query:")
    print(query)
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    df = pd.read_sql(query, conn)
    conn.close()
    print("Rows extracted:", len(df))
    return df

def load_to_snowflake(df, table_name):
    print(f"Loading into Snowflake table: {table_name}")

    if df.empty:
        print(f"No new data for {table_name}")
        return 0

    # Convert datetime columns to string
    for col in df.columns:
        if "datetime" in str(df[col].dtype) or "timestamp" in str(df[col].dtype):
            df[col] = df[col].astype(str)

    # Replace NaN with None
    df = df.replace({np.nan: None})

    conn = snowflake.connector.connect(**SNOWFLAKE_CONFIG)
    cursor = conn.cursor()

    columns = ", ".join(df.columns)
    placeholders = ", ".join(["%s"] * len(df.columns))
    sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders})"

    data = [tuple(row) for row in df.to_numpy()]

    cursor.executemany(sql, data)

    conn.commit()
    cursor.close()
    conn.close()

    print(f"SUCCESS: Loaded {len(df)} rows into {table_name}")
    return len(df)


def refresh_data_warehouse():
    print("Refreshing DW tables in Snowflake...")
    conn = snowflake.connector.connect(**SNOWFLAKE_CONFIG)
    cursor = conn.cursor()

    statements = [
        # Merge accounts into DIM_ACCOUNT
        """
        MERGE INTO MONEY_TRANSFER_DW.ANALYTICS.DIM_ACCOUNT tgt
        USING (
            SELECT id account_id,
                   holder_name,
                   status
            FROM MONEY_TRANSFER_OLTP.CORE.accounts
        ) src
        ON tgt.account_id = src.account_id
        WHEN MATCHED THEN
        UPDATE SET holder_name = src.holder_name, status = src.status
        WHEN NOT MATCHED THEN
        INSERT(account_id, holder_name, status)
        VALUES(src.account_id, src.holder_name, src.status)
        """,

        # Rebuild FACT_TRANSACTIONS
        "TRUNCATE TABLE MONEY_TRANSFER_DW.ANALYTICS.FACT_TRANSACTIONS",

        """
        INSERT INTO MONEY_TRANSFER_DW.ANALYTICS.FACT_TRANSACTIONS
        (account_from_key, account_to_key, date_key, amount, status)
        SELECT
            da_from.account_key,
            da_to.account_key,
            TO_NUMBER(TO_CHAR(CAST(t.created_on AS DATE),'YYYYMMDD')),
            t.amount,
            t.status
        FROM MONEY_TRANSFER_OLTP.CORE.transaction_logs t
        LEFT JOIN MONEY_TRANSFER_DW.ANALYTICS.DIM_ACCOUNT da_from
            ON da_from.account_id = t.from_account_id
        LEFT JOIN MONEY_TRANSFER_DW.ANALYTICS.DIM_ACCOUNT da_to
            ON da_to.account_id = t.to_account_id
        """,

        # Ensure FACT_REWARDS exists, then rebuild it from OLTP rewards
        """
        CREATE TABLE IF NOT EXISTS MONEY_TRANSFER_DW.ANALYTICS.FACT_REWARDS (
            reward_key NUMBER(38,0) IDENTITY START 1 INCREMENT 1,
            account_key NUMBER(38,0),
            date_key NUMBER(8,0),
            transaction_id VARCHAR(36),
            points NUMBER(10,0),
            amount NUMBER(15,2)
        )
        """,

        "TRUNCATE TABLE MONEY_TRANSFER_DW.ANALYTICS.FACT_REWARDS",

        """
        INSERT INTO MONEY_TRANSFER_DW.ANALYTICS.FACT_REWARDS
        (account_key, date_key, transaction_id, points, amount)
        SELECT
            da.account_key,
            TO_NUMBER(TO_CHAR(CAST(r.created_on AS DATE),'YYYYMMDD')),
            r.transaction_id,
            r.points,
            r.amount
        FROM MONEY_TRANSFER_OLTP.CORE.rewards r
        LEFT JOIN MONEY_TRANSFER_DW.ANALYTICS.DIM_ACCOUNT da
            ON da.account_id = r.account_id
        """
    ]

    try:
        for stmt in statements:
            print("Executing DW statement...")
            cursor.execute(stmt)

        conn.commit()
        print("DW refresh completed.")

    except Exception as e:
        print("Failed to refresh DW:", e)
        traceback.print_exc()

    finally:
        cursor.close()
        conn.close()

def main():
    print("==== ENTERED MAIN FUNCTION ====")

    state = load_state()

    test_mysql_connection()
    test_snowflake_connection()

    accounts_query = f"""
        SELECT *
        FROM accounts
        WHERE last_updated > '{state['accounts_last_loaded']}'
    """

    transactions_query = f"""
        SELECT *
        FROM transaction_logs
        WHERE created_on > '{state['transactions_last_loaded']}'
    """

    rewards_query = f"""
        SELECT *
        FROM rewards
        WHERE created_on > '{state.get('rewards_last_loaded', '1970-01-01 00:00:00')}'
    """

    accounts_df = extract_mysql(accounts_query)
    transactions_df = extract_mysql(transactions_query)
    rewards_df = extract_mysql(rewards_query)

    accounts_loaded = load_to_snowflake(
        accounts_df,
        "MONEY_TRANSFER_OLTP.CORE.accounts"
    )

    transactions_loaded = load_to_snowflake(
        transactions_df,
        "MONEY_TRANSFER_OLTP.CORE.transaction_logs"
    )

    rewards_loaded = load_to_snowflake(
        rewards_df,
        "MONEY_TRANSFER_OLTP.CORE.rewards"
    )

    if accounts_loaded > 0:
        state["accounts_last_loaded"] = str(accounts_df["last_updated"].max())

    if transactions_loaded > 0:
        state["transactions_last_loaded"] = str(transactions_df["created_on"].max())

    if rewards_loaded > 0:
        # rewards table uses `created_on` as the timestamp column
        state["rewards_last_loaded"] = str(rewards_df["created_on"].max())

    save_state(state)

    # Refresh the analytics/data-warehouse tables from OLTP data
    try:
        refresh_data_warehouse()
    except Exception as e:
        print("Warning: DW refresh failed:", e)

    print("==== ETL COMPLETED SUCCESSFULLY ====")


if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print("==== ETL CRASHED ====")
        print("Error:", e)
        traceback.print_exc()
