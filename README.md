# Money Transfer System

A complete Spring Boot 3.x money transfer application with JWT authentication, admin approval workflow, and comprehensive transaction management.

## Features

- ✅ User registration with admin approval workflow
- ✅ JWT-based authentication (HS256)
- ✅ Role-based access control (ADMIN, USER)
- ✅ Money transfers with idempotency support
- ✅ Transaction logging (DEBIT, CREDIT, DEPOSIT)
- ✅ Comprehensive exception handling with PDF error codes
- ✅ AOP logging for method execution tracking
- ✅ Optimistic locking for concurrent updates
- ✅ Minimum balance enforcement
- ✅ Minimal responsive frontend (HTML/CSS/JS)

## Tech Stack

- **Backend**: Spring Boot 3.5.10, Java 17
- **Database**: MySQL 8.x
- **Security**: Spring Security 6, JWT (jjwt 0.12.3)
- **ORM**: Spring Data JPA, Hibernate
- **Build Tool**: Maven
- **Frontend**: Vanilla HTML/CSS/JavaScript

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.x
- MySQL Workbench (optional, for GUI)

## Database Setup

### 1. Create Database

Open MySQL Workbench or command line and run:

```sql
CREATE DATABASE money_transfer_db;
```

### 2. Run Schema Script

```bash
# From MySQL Workbench: File > Open SQL Script > select database/schema.sql > Execute

# OR from command line:
mysql -u root -p money_transfer_db < database/schema.sql
```

### 3. Run Seed Data Script

The seed data includes pre-generated BCrypt password hashes, so you can run it directly:

```bash
mysql -u root -p money_transfer_db < database/seed-data.sql
```

**Note**: The BCrypt hashes are already included for the default test passwords.

## Application Configuration

The application reads sensitive values from environment variables defined in `.env`.
Update the `.env` file in the project root with your actual values before starting the app.

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/money_transfer_db?...}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
```

## Build and Run

### Build the project

```bash
cd c:\Users\smlak\Desktop\Fidelity-leap-springboot
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

## Default Credentials

After running the seed data script, you can use the test accounts.
Passwords for seed data accounts must be set by generating BCrypt hashes with `PasswordHashGenerator` and inserting them into `seed-data.sql`.

**⚠️ Important**: Never commit real credentials. Use environment variables configured in `.env`.

## API Endpoints

### Authentication (Public)

- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login and get JWT token

### Transfers (Authenticated)

- `POST /api/v1/transfers` - Transfer money
- `GET /api/v1/transfers/history` - Get transaction history

### Accounts (Authenticated)

- `GET /api/v1/accounts/{id}` - Get account details
- `GET /api/v1/accounts/{id}/balance` - Get account balance

### Admin (ADMIN Role Only)

- `GET /api/v1/admin/accounts/pending` - Get pending approvals
- `POST /api/v1/admin/accounts/{id}/approve` - Approve account
- `POST /api/v1/admin/accounts/{id}/reject` - Reject account
- `POST /api/v1/admin/accounts/deposit` - Deposit money
- `GET /api/v1/admin/transactions` - Get all transactions
- `GET /api/v1/admin/accounts` - Get all accounts

## Frontend Pages

- **/** - Landing page
- **/register.html** - User registration
- **/login.html** - User login
- **/dashboard.html** - User dashboard (transfer money, view history)
- **/admin.html** - Admin dashboard (approve users, deposit, view all)

## Testing Flow

### 1. Register a New User

1. Navigate to http://localhost:8080/register.html
2. Fill in: Name, Email, Password
3. Submit → Account created with PENDING status

### 2. Admin Approves User

1. Login as admin (use the admin email/password from your `.env`)
2. Go to admin dashboard
3. See pending user in "Pending Approvals" section
4. Click "Approve" → User account becomes ACTIVE with ₹1000 initial balance

### 3. User Logs In

1. Logout admin
2. Login as the newly approved user
3. Redirected to user dashboard
4. See Account ID and Balance

### 4. Admin Deposits Money

1. Login as admin
2. Go to "Deposit Money" section
3. Enter user's Account ID and amount (e.g., ₹5000)
4. Submit → User balance increases

### 5. User Transfers Money

1. Login as user
2. In "Transfer Money" section:
   - Enter recipient's Account ID
   - Enter amount (e.g., ₹500)
   - Submit
3. View updated balance
4. Check transaction history → See DEBIT entry
5. Recipient sees CREDIT entry in their history

### 6. Test Exception Scenarios

**Insufficient Funds**:
- Try to transfer more than available balance
- Expected: Error "Insufficient funds (TRX-400)"

**Minimum Balance Violation**:
- Try to transfer amount that would leave balance < ₹1000
- Expected: Error "Transfer would violate minimum balance requirement (TRX-400)"

**Invalid Account**:
- Try to transfer to non-existent Account ID
- Expected: Error "Destination account not found (ACC-404)"

**Duplicate Transfer**:
- Submit same idempotency key twice
- Expected: Error "Transfer with this idempotency key already exists (TRX-409)"

**Unapproved Account**:
- Try to login with pending account
- Expected: Error "Account is pending admin approval (ACC-403)"

## Error Codes (PDF Compliance)

| Code    | HTTP Status | Description                        |
|---------|-------------|------------------------------------|
| ACC-404 | 404         | Account not found                  |
| ACC-403 | 403         | Account not active/approved        |
| TRX-400 | 400         | Insufficient funds                 |
| TRX-409 | 409         | Duplicate transfer (idempotency)   |
| VAL-422 | 422         | Validation error                   |
| AUTH-401| 401         | Invalid credentials                |
| AUTH-403| 403         | Access denied                      |
| GEN-500 | 500         | Internal server error              |

## Business Rules

1. ✅ Accounts must be different (sender ≠ receiver)
2. ✅ Source and destination accounts must exist
3. ✅ Both accounts must be ACTIVE and APPROVED
4. ✅ Transfer amount must be > 0
5. ✅ Source balance must be >= transfer amount
6. ✅ Source balance after transfer must be >= minimum balance
7. ✅ Idempotency key must be unique
8. ✅ Optimistic locking prevents concurrent update conflicts

## Project Structure

```
src/
├── main/
│   ├── java/com/example/money_transfer_system/
│   │   ├── aspect/          # AOP logging
│   │   ├── config/          # Security configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── enums/           # Enumerations
│   │   ├── exception/       # Custom exceptions + handler
│   │   ├── repository/      # JPA repositories
│   │   ├── security/        # JWT utilities
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yaml
│       └── static/          # Frontend files
│           ├── css/
│           ├── js/
│           └── *.html
└── test/                    # Unit tests

database/
├── schema.sql               # DDL scripts
└── seed-data.sql           # Initial data
```

## Development Notes

- **JWT Secret**: Provide via `JWT_SECRET` env var — never commit it to source control
- **Min Balance**: Default ₹1000, configurable in `application.yaml` (`app.min-balance`)
- **Token Expiration**: 24 hours (86400000 ms), configurable in `application.yaml`
- **Password Encoding**: BCrypt with strength 10
- **Transaction Management**: Uses Spring's `@Transactional` with rollback on exceptions

## Troubleshooting

**Database Connection Failed**:
- Verify MySQL is running
- Check credentials in `application.yaml`
- Ensure database `money_transfer_db` exists

**JWT Token Invalid**:
- Check token expiration (24 hours)
- Verify secret key matches between token generation and validation

**Frontend Not Loading**:
- Ensure static resources are in `src/main/resources/static/`
- Check browser console for errors
- Verify CORS configuration in `SecurityConfig`

## License

This project is for educational purposes.
