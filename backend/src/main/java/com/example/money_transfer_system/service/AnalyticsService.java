package com.example.money_transfer_system.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final JdbcTemplate snowflakeJdbcTemplate;

    public AnalyticsService(
            @Qualifier("snowflakeJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.snowflakeJdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getKpis() {

        // =========================
        // BASIC KPIs
        // =========================

        Integer totalTransactions = snowflakeJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM FACT_TRANSACTIONS",
                Integer.class
        );

        Double totalAmount = snowflakeJdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM FACT_TRANSACTIONS",
                Double.class
        );

        Double successRate = snowflakeJdbcTemplate.queryForObject("""
                SELECT COALESCE(
                ROUND(100.0 *
                SUM(CASE WHEN status='SUCCESS' THEN 1 ELSE 0 END)
                / NULLIF(COUNT(*),0),2),0)
                FROM FACT_TRANSACTIONS
                """,
                Double.class
        );

        Double avgTransactionAmount = snowflakeJdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(amount),0) FROM FACT_TRANSACTIONS",
                Double.class
        );

        Double maxTransaction = snowflakeJdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(amount),0) FROM FACT_TRANSACTIONS",
                Double.class
        );

        // =========================
        // DAILY TREND
        // =========================

        List<Map<String, Object>> dailyTrend =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT d.full_date,
                               COUNT(*) AS tx_count,
                               SUM(f.amount) AS total_amount
                        FROM FACT_TRANSACTIONS f
                        JOIN DIM_DATE d ON f.date_key = d.date_key
                        GROUP BY d.full_date
                        ORDER BY d.full_date
                        """);

        // =========================
        // MONTHLY TREND
        // =========================

        List<Map<String, Object>> monthlyTrend =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT d.year,
                               d.month,
                               COUNT(*) AS tx_count,
                               SUM(f.amount) AS total_amount
                        FROM FACT_TRANSACTIONS f
                        JOIN DIM_DATE d ON f.date_key = d.date_key
                        GROUP BY d.year, d.month
                        ORDER BY d.year, d.month
                        """);

        // =========================
        // STATUS DISTRIBUTION
        // =========================

        List<Map<String, Object>> statusBreakdown =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT status,
                               COUNT(*) AS count
                        FROM FACT_TRANSACTIONS
                        GROUP BY status
                        """);

        // =========================
        // TOP SENDERS
        // =========================

        List<Map<String, Object>> topSenders =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT da.holder_name,
                               COUNT(*) AS tx_count,
                               SUM(f.amount) AS total_sent
                        FROM FACT_TRANSACTIONS f
                        JOIN DIM_ACCOUNT da
                          ON f.account_from_key = da.account_key
                        GROUP BY da.holder_name
                        ORDER BY total_sent DESC
                        LIMIT 5
                        """);

        // =========================
        // TOP RECEIVERS
        // =========================

        List<Map<String, Object>> topReceivers =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT da.holder_name,
                               COUNT(*) AS tx_count,
                               SUM(f.amount) AS total_received
                        FROM FACT_TRANSACTIONS f
                        JOIN DIM_ACCOUNT da
                          ON f.account_to_key = da.account_key
                        GROUP BY da.holder_name
                        ORDER BY total_received DESC
                        LIMIT 5
                        """);

        // =========================
        // FAILED TRANSACTIONS
        // =========================

        List<Map<String, Object>> failedAnalysis =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT COUNT(*) AS failed_count,
                               SUM(amount) AS failed_amount
                        FROM FACT_TRANSACTIONS
                        WHERE status != 'SUCCESS'
                        """);

        // =========================
        // ACCOUNT ACTIVITY
        // =========================

        List<Map<String, Object>> accountActivity =
                snowflakeJdbcTemplate.queryForList("""
                        SELECT da.holder_name,
                               COUNT(*) AS total_transactions
                        FROM FACT_TRANSACTIONS f
                        JOIN DIM_ACCOUNT da
                          ON f.account_from_key = da.account_key
                        GROUP BY da.holder_name
                        ORDER BY total_transactions DESC
                        """);

        return Map.of(
                "kpis", Map.of(
                        "totalTransactions", totalTransactions,
                        "totalAmount", totalAmount,
                        "successRate", successRate,
                        "averageTransactionAmount", avgTransactionAmount,
                        "largestTransaction", maxTransaction
                ),
                "dailyTrend", dailyTrend,
                "monthlyTrend", monthlyTrend,
                "statusBreakdown", statusBreakdown,
                "topSenders", topSenders,
                "topReceivers", topReceivers,
                "failedAnalysis", failedAnalysis,
                "accountActivity", accountActivity
        );
    }
}
