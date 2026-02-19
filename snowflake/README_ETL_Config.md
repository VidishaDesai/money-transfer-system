# Money Transfer System – ETL Pipeline (MySQL → Snowflake)

This folder contains the Python-based ETL pipeline that transfers data from
MySQL (OLTP) to Snowflake (OLAP / Data Warehouse).

------------------------------------------------------------
ARCHITECTURE
------------------------------------------------------------

MySQL (Primary Database)
↓
Python ETL (main.py)
↓
Snowflake (OLTP + DW)
↓
Spring Boot Analytics API


------------------------------------------------------------
PREREQUISITES
------------------------------------------------------------

1. Python 3.10 or above
2. MySQL running locally
3. Snowflake account with:
    - Warehouse
    - Database
    - Schema
    - Required tables created

------------------------------------------------------------
STEP 1 — Install Required Python Packages
------------------------------------------------------------

Inside the ETL_Pipeline folder run:

pip install mysql-connector-python snowflake-connector-python pandas

These packages are required:
- mysql-connector-python → Connect to MySQL
- snowflake-connector-python → Connect to Snowflake
- pandas → Extract and process SQL results


------------------------------------------------------------
STEP 2 — Create state.json (MANDATORY)
------------------------------------------------------------

If state.json does not exist, create it inside the ETL_Pipeline folder.

Create file:
state.json

Add the following content:

{
"accounts_last_loaded": "1970-01-01 00:00:00",
"transactions_last_loaded": "1970-01-01 00:00:00"
}

WHY THIS IS REQUIRED:

This file enables incremental loading.

The ETL loads only rows:

WHERE last_updated > accounts_last_loaded
WHERE created_on > transactions_last_loaded

After successful load, state.json is automatically updated
with the latest timestamp.


------------------------------------------------------------
STEP 3 — Configure Database Connections
------------------------------------------------------------

Inside config.py, update:

MYSQL_CONFIG:
- host
- port
- user
- password
- database

SNOWFLAKE_CONFIG:
- user
- password
- account
- warehouse
- database
- schema


------------------------------------------------------------
STEP 4 — Run ETL
------------------------------------------------------------

From inside ETL_Pipeline:

python main.py

If successful, you will see:

SUCCESS: Loaded X rows into MONEY_TRANSFER_OLTP.CORE.accounts
SUCCESS: Loaded X rows into MONEY_TRANSFER_OLTP.CORE.transaction_logs
==== ETL COMPLETED SUCCESSFULLY ====


------------------------------------------------------------
WHEN TO RUN ETL
------------------------------------------------------------

ETL must be run manually whenever:

- A new account is created
- A deposit is made
- A transfer occurs
- Any transaction is performed in the backend

After performing transactions via Spring Boot:

1. Stop backend
2. Run python main.py
3. Restart backend

This ensures Snowflake reflects the latest data.


------------------------------------------------------------
IMPORTANT NOTES
------------------------------------------------------------

• state.json prevents duplicate loading.
• If ETL crashes mid-run, you may need to:
- Clear Snowflake tables
- Reset state.json
• Snowflake is used only for analytics.
• MySQL remains the system of record.

------------------------------------------------------------
CAPSTONE STATUS
------------------------------------------------------------

✔ Incremental ETL implemented
✔ Timestamp-based loading
✔ MySQL to Snowflake integration
✔ Debug logging enabled
✔ Production-style architecture

Future Enhancements:
- Scheduled ETL (cron)
- Bulk insert optimization
- Data validation layer
- Error retry mechanism
- Audit logging
