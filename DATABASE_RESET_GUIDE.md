# Database Reset and Setup Guide

## Problem
The hardcoded BCrypt hashes in seed-data.sql are not working properly.

## Solution
The application now **automatically creates the admin user** when it starts for the first time!

## Steps to Reset and Start Fresh

### 1. Reset the Database

Run this script in MySQL Workbench to drop and recreate the tables:

**File:** `database/reset-database.sql`

```sql
-- Copy and paste the entire reset-database.sql file into MySQL Workbench and execute
```

This will:
- Drop the existing `accounts` and `transaction_logs` tables
- Recreate them fresh and empty

### 2. Start the Application

```bash
mvn spring-boot:run
```

OR

```bash
./mvnw spring-boot:run
```

### 3. Admin User Auto-Created!

When the application starts, it will **automatically create** the admin user:

```
==============================================
DEFAULT ADMIN USER CREATED
Email: admin@system.com
Password: admin123
==============================================
```

You'll see this message in the console logs.

### 4. Login as Admin

Go to: http://localhost:8080/login.html

**Credentials:**
- Email: `admin@system.com`
- Password: `admin123`

### 5. Add Users via Frontend

Now you can register new users through the frontend:

1. Go to http://localhost:8080/register.html
2. Fill in the registration form
3. Login as admin
4. Approve the new user
5. The user can now login!

## Configuration

The admin credentials are stored in `application.yaml`:

```yaml
admin:
  default:
    email: admin@system.com
    password: admin123
    name: System Administrator
```

You can change these values before starting the application if you want different credentials.

## How It Works

1. On application startup, `AdminUserInitializer` runs
2. It checks if an admin user exists in the database
3. If not, it creates one with the credentials from `application.yaml`
4. The password is automatically BCrypt-encoded by the application
5. The admin user is saved to the database

## Benefits

✅ No need to manually generate BCrypt hashes
✅ No need to run separate SQL scripts for seed data
✅ Works every time, guaranteed
✅ Password is properly BCrypt-encoded by the application
✅ Easy to change admin credentials in config file

## Troubleshooting

**Admin user not created?**
- Check the console logs for errors
- Verify MySQL is running and accessible
- Check `application.yaml` database credentials

**Can't login with admin credentials?**
- Make sure you see the "DEFAULT ADMIN USER CREATED" message in logs
- Check the database: `SELECT * FROM accounts WHERE email='admin@system.com';`
- Verify the password in `application.yaml` matches what you're typing

**Want to recreate the admin user?**
1. Run `reset-database.sql` again
2. Restart the application
3. Admin user will be recreated
