-- Money Transfer System - Database Schema
-- MySQL 8.x compatible

CREATE DATABASE IF NOT EXISTS money_transfer_db;
USE money_transfer_db;

DROP TABLE IF EXISTS transaction_logs;
DROP TABLE IF EXISTS accounts;

-- Accounts Table
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    date_of_birth DATE,
    password_hash VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'LOCKED', 'CLOSED') NOT NULL DEFAULT 'LOCKED',
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    account_type ENUM('SAVINGS','STUDENT','CURRENT','SALARY','NRE','NRO','BUSINESS','PREMIUM','JOINT','SENIOR', 'ADMIN') NOT NULL,
    min_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    version INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_approved (approved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Transaction Logs Table
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

