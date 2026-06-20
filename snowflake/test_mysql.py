import mysql.connector

try:
    conn = mysql.connector.connect(
        host="127.0.0.1",
        port=3306,
        user="etl_user",
        password="etl123",
        database="money_transfer_db"
    )

    print("CONNECTED SUCCESSFULLY")
    conn.close()

except Exception as e:
    print("FAILED")
    print(e)