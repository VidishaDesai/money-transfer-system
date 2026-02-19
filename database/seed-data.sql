-- Money Transfer System - Seed Data
-- Initial admin account and test users
-- Ready to run directly - BCrypt password hashes are pre-generated
--
-- IMPORTANT: The hashes below are for DEVELOPMENT/TESTING only.
-- Generate new hashes for production using PasswordHashGenerator:
--   java PasswordHashGenerator <your-password>

USE money_transfer_db;

-- Insert Admin Account
-- Email: admin@system.com
-- (password hash must be regenerated for production)
INSERT INTO accounts (holder_name, email, password_hash, balance, status, approved, role, min_balance, version)
VALUES (
    'System Administrator',
    'admin@system.com',
    '$2a$10$8cjz95BCg3xLL95xMeIgAOidoQd0mW9GvVPvb4b6RZ.WaIxPVq/Oi',
    0.00,
    'ACTIVE',
    TRUE,
    'ROLE_ADMIN',
    0.00,
    0
);

-- Insert Test User 1 (Approved, with balance)
-- Email: john@example.com
-- (password hash must be regenerated for production)
INSERT INTO accounts
(holder_name, email, password_hash, balance, status, approved, role, account_type, version)
VALUES (
    'John Doe',
    'john@example.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AXN4dON1lKMy2S',
    5000.00,
    'ACTIVE',
    TRUE,
    'ROLE_USER',
    'SAVINGS',
    0
);


-- Insert Test User 2 (Approved, with balance)
-- Email: jane@example.com
-- (password hash must be regenerated for production)
INSERT INTO accounts (holder_name, email, password_hash, balance, status, approved, role, min_balance, version)
VALUES (
    'Jane Smith',
    'jane@example.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AXN4dON1lKMy2S',
    3000.00,
    'ACTIVE',
    TRUE,
    'ROLE_USER',
    'STUDENT',
    0
);

-- Insert Test User 3 (Pending Approval)
-- Email: bob@example.com
-- (password hash must be regenerated for production)
INSERT INTO accounts (holder_name, email, password_hash, balance, status, approved, role, min_balance, version)
VALUES (
    'Bob Johnson',
    'bob@example.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AXN4dON1lKMy2S',
    0.00,
    'LOCKED',
    FALSE,
    'ROLE_USER',
    'CURRENT',
    0
);

-- ============================================
-- CREDENTIALS FOR TESTING
-- ============================================
-- See .env for required environment
-- variables (DB_PASSWORD, JWT_SECRET, etc.)
--
-- For development, set passwords and generate
-- BCrypt hashes with PasswordHashGenerator.
-- ============================================

