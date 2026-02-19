# Quick Setup Guide for VM

This guide is for setting up the Money Transfer System in a VM environment where you're pulling code from GitHub.

## Prerequisites
- MySQL 8.x installed and running
- Java 17+ installed
- Maven installed (or use the included Maven wrapper)

## Setup Steps

### 1. Clone/Pull the Repository
```bash
git pull origin main
# or
git clone <your-repo-url>
cd Fidelity-leap-springboot
```

### 2. Setup MySQL Database

**Open MySQL Workbench or MySQL Command Line:**

```sql
CREATE DATABASE money_transfer_db;
```

### 3. Run Schema Script

**In MySQL Workbench:**
1. File → Open SQL Script
2. Select `database/schema.sql`
3. Click Execute (⚡ icon)

**OR via Command Line:**
```bash
mysql -u root -p money_transfer_db < database/schema.sql
```

### 4. Run Seed Data Script

**In MySQL Workbench:**
1. File → Open SQL Script
2. Select `database/seed-data.sql`
3. Click Execute (⚡ icon)

**OR via Command Line:**
```bash
mysql -u root -p money_transfer_db < database/seed-data.sql
```

✅ **No password hash generation needed!** The BCrypt hashes are already included in the SQL file.

### 5. Configure Application

Update the `.env` file in the project root with your actual values:

The application reads sensitive values (DB password, JWT secret, admin password) from environment variables.
See `.env` for the full list of required variables.

### 6. Build and Run

**Using Maven:**
```bash
mvn clean install
mvn spring-boot:run
```

**Using Maven Wrapper (if Maven not installed):**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 7. Access the Application

Open your browser and go to:
```
http://localhost:8080
```

## Default Login Credentials

Credentials are configured via environment variables in `.env`.
The admin account is auto-created on first startup using `ADMIN_EMAIL` and `ADMIN_PASSWORD`.
Test user passwords correspond to the BCrypt hashes in `seed-data.sql`.

## Testing the Application

### 1. Login as Admin
1. Go to http://localhost:8080/login.html
2. Login with the admin credentials from your `.env`
3. You'll see the admin dashboard

### 2. Approve Pending User
1. In admin dashboard, see "Bob Johnson" in pending approvals
2. Click "Approve"
3. Bob can now login

### 3. Login as User
1. Logout from admin
2. Login with a test user email and the password you set for the seed data
3. You'll see the user dashboard with balance ₹5000

### 4. Transfer Money
1. In user dashboard, enter:
   - To Account ID: 3 (Jane's account)
   - Amount: 500
2. Click "Transfer"
3. Check transaction history

### 5. Admin Deposit
1. Login as admin
2. Go to "Deposit Money" section
3. Enter Account ID: 2, Amount: 1000
4. Click "Deposit"

## Troubleshooting

### Build Fails - JAVA_HOME not set
```bash
# Check Java version
java -version

# If not installed, install Java 17
# Then set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH
```

### MySQL Connection Failed
- Check MySQL is running: `sudo systemctl status mysql`
- Verify credentials in `application.yaml`
- Ensure database `money_transfer_db` exists

### Port 8080 Already in Use
Change port in `application.yaml`:
```yaml
server:
  port: 8081  # or any other available port
```

## Security Notes

All sensitive values (DB password, JWT secret, admin password) are loaded from environment variables.
See `.env` for the required variables. **Never commit real secrets to version control.**

The BCrypt hashes in the seed data use strength 10 (standard security). For production, regenerate all hashes and rotate all secrets.
