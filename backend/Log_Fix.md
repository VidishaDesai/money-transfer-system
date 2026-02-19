# 📝 Log File Setup & Schema Issue Fix

This document explains how the logging issue and the `min_balance` schema validation error were fixed in the project.

***

## ✅ 1. Log File Setup

### **Create `logs/` folder**

Go to the **project root directory**:

    C:\Users\labuser\Capstone_project\Fidelity-leap-springboot

Create a folder named:

    logs

> ⚠️ **Will the folder be created automatically?**  
> No — Spring Boot **does NOT create the folder automatically**.  
> The folder must exist beforehand, otherwise the app may fail to write the log file.

***

## ✅ 2. YAML Logging Configuration

Add the following to your `application.yml`:

```yaml
logging:
  file:
    name: money-transfer.log
    path: logs/
  level:
    root: INFO
```

This configuration will create the log file:

    logs/money-transfer.log

inside your project root.

***

## ✅ 3. Logging Behavior

With the above setup:

*   All logs from controllers, services, and aspects (e.g., `LoggingAspect`)  
    will be written to **both the console and the log file**.
*   The log file rotates only when manually deleted (no rolling policy configured).

If you want rolling logs later, you can add a `logback-spring.xml`.

***

## ✅ 4. Fix for the `min_balance` Schema Validation Error

Spring Boot failed to start due to:

    Schema-validation: missing column [min_balance] in table [accounts]

### ✔ Why it happened

Your `Account` entity contains a field mapped to a column named `min_balance`, but this column did **not** exist in the database table.

### ✔ How it was fixed

**Option used**: Add the missing column in MySQL.

Example SQL fix:

```sql
ALTER TABLE accounts
ADD COLUMN min_balance DECIMAL(19,2) NOT NULL DEFAULT 0;
```

## ✅ Final Status

✔ Logging now writes to a persistent file.  
✔ Project root contains a `logs/` folder.  
✔ A `money-transfer.log` file is generated automatically at runtime.  
✔ The database schema now includes the required `min_balance` column.  
✔ The Spring Boot application starts successfully.
