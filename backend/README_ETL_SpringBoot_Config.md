# Money Transfer System – Spring Boot Backend with Snowflake Analytics

This Spring Boot application uses:

- MySQL → Primary OLTP database
- Snowflake → Analytics database
- Python ETL → Loads data from MySQL to Snowflake
- JWT Security → Protects API routes
- Admin Dashboard → Displays analytics


------------------------------------------------------------
ARCHITECTURE OVERVIEW
------------------------------------------------------------

MySQL (Primary DB - Transactions)
↓
Python ETL
↓
Snowflake (Analytics DB)
↓
Spring Boot Analytics API
↓
Admin Dashboard


------------------------------------------------------------
FILES ADDED FOR SNOWFLAKE ANALYTICS
------------------------------------------------------------

1. AnalyticsController.java
   Path: /controller/AnalyticsController.java

   Exposes:
   GET /api/v1/analytics/kpis

2. AnalyticsService.java
   Path: /service/AnalyticsService.java

   Contains:
    - KPI calculations
    - Daily trend
    - Monthly trend
    - Status breakdown
    - Success rate
    - Largest transaction
    - Fraud indicator logic

   Uses Snowflake JdbcTemplate.

3. SnowflakeConfig.java
   Path: /config/SnowflakeConfig.java

   Defines:
    - Snowflake DataSource
    - Snowflake JdbcTemplate

4. MySqlConfig.java
   Path: /config/MySqlConfig.java

   Defines MySQL as:

   @Primary

   This ensures:
    - JPA repositories use MySQL
    - Snowflake is used only for analytics


------------------------------------------------------------
APPLICATION CONFIGURATION
------------------------------------------------------------

application.yaml contains:

spring.datasource → MySQL (Primary)

snowflake.datasource:
url:
username:
password:
warehouse:
db:
schema:

IMPORTANT:
If credentials are removed for GitHub security,
they must be restored before running the backend.


------------------------------------------------------------
RUNNING THE BACKEND
------------------------------------------------------------

From project root:

mvn spring-boot:run

OR

mvn clean install
java -jar target/money-transfer-system.jar


------------------------------------------------------------
AFTER RUNNING ETL
------------------------------------------------------------

When new data is loaded into Snowflake:

You must restart the backend.

WHY?

• Connection pool may cache old metadata
• Snowflake session context may not refresh
• Ensures clean JDBC connection

Steps:

1. Stop backend
2. Run python main.py
3. Start backend again


------------------------------------------------------------
ANALYTICS ENDPOINT
------------------------------------------------------------

GET /api/v1/analytics/kpis

Protected by JWT.
Admin role required.

Response structure includes:

- KPIs
- Daily trend
- Monthly trend
- Status breakdown
- Top senders
- Top receivers
- Fraud indicator


------------------------------------------------------------
DEVELOPMENT WORKFLOW
------------------------------------------------------------

1. Start MySQL
2. Start Spring Boot
3. Perform transactions
4. Stop backend
5. Run ETL
6. Restart backend
7. View analytics in Admin Dashboard


------------------------------------------------------------
SECURITY
------------------------------------------------------------

- JWT-based authentication
- ROLE_ADMIN required for analytics
- Authorization header required:
  Authorization: Bearer <token>


------------------------------------------------------------
CAPSTONE STATUS
------------------------------------------------------------

✔ Dual datasource configuration
✔ Snowflake analytics integration
✔ JDBC template analytics queries
✔ Admin dashboard visualization
✔ JWT-secured analytics route
✔ Production-style architecture separation

Future Enhancements:
- Caching layer for analytics
- Scheduled ETL automation
- Materialized views
- Fraud scoring engine
- Microservice separation
