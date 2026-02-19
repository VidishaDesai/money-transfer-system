-- Drop and Recreate Tables
-- Run this script to reset your database

USE money_transfer_db;

-- Drop tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS transaction_logs;
DROP TABLE IF EXISTS accounts;

-- Recreate accounts table
    CREATE TABLE accounts (
           id BIGINT AUTO_INCREMENT PRIMARY KEY,
           holder_name VARCHAR(100) NOT NULL,
           email VARCHAR(255) NOT NULL UNIQUE,
           phone VARCHAR(20),
           address VARCHAR(255),
           date_of_birth DATE,
           password_hash VARCHAR(255) NOT NULL,
           balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
           status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
           approved BOOLEAN NOT NULL DEFAULT FALSE,
           role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
           account_type VARCHAR(20) NOT NULL,
           version INT NOT NULL DEFAULT 0,
           last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
           INDEX idx_email (email),
           INDEX idx_status (status),
           INDEX idx_approved (approved)
       ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Recreate transaction_logs table
    CREATE TABLE transaction_logs (
        transaction_id VARCHAR(36) PRIMARY KEY,
        from_account_id BIGINT,
        to_account_id BIGINT,
        amount DECIMAL(15, 2) NOT NULL,
        transaction_type ENUM('DEBIT', 'CREDIT', 'DEPOSIT', 'TRANSFER', 'REVERSAL') NOT NULL,
        status ENUM('SUCCESS', 'FAILED', 'ROLLBACK_REQUESTED', 'ROLLBACK_REJECTED', 'ROLLED_BACK') NOT NULL DEFAULT 'SUCCESS',
        failure_reason VARCHAR(500),
        idempotency_key VARCHAR(255) UNIQUE,
        created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        rollback_requested_at DATETIME NULL,
        rollback_processed_at DATETIME NULL,
        rollback_processed_by BIGINT NULL,
        original_transaction_id VARCHAR(36) NULL,
        INDEX idx_from_account (from_account_id),
        INDEX idx_to_account (to_account_id),
        INDEX idx_idempotency (idempotency_key),
        INDEX idx_created_on (created_on),
        FOREIGN KEY (from_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
        FOREIGN KEY (to_account_id) REFERENCES accounts(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Tables are now empty and ready for the application to create the admin user automatically
SELECT 'Database reset complete. Start the application to auto-create admin user.' AS message;





--If the application fails to run but the build is success, run the following query
ALTER TABLE accounts
  ADD COLUMN min_balance DECIMAL(19,2) NOT NULL DEFAULT 0;