# Money Transfer System — Full Stack Banking Application

## Overview

Money Transfer System is a full stack financial transaction platform built with **Spring Boot (backend)** and **Angular (frontend)**. The system supports secure user account management, fund transfers, transaction tracking, administrative workflows, analytics, and rollback management for financial safety.

The application follows a role based architecture:

* **User**

  * Account registration
  * Money transfer
  * Transaction history
  * Rollback request initiation
  * Profile management

* **Admin**

  * Account approval and rejection
  * Deposit management
  * System-wide transaction monitoring
  * Rollback approval or rejection
  * Analytics dashboard

The system is designed around clean separation of concerns, modular Angular components, and REST driven backend services.

---

## Technology Stack

### Frontend

* Angular (Standalone Components)
* TypeScript
* HTML5 + CSS3
* RxJS
* Angular HttpClient
* Custom reusable UI components

  * Pagination
  * Confirm Dialog
  * Date Picker

### Backend

* Spring Boot
* REST APIs
* JWT Authentication
* Role Based Authorization
* Transaction Services
* Rollback Workflow

### Database

* Relational database (JPA based persistence)

---

## Core Features

### Authentication and Authorization

* User registration
* Login with JWT token
* Role based access:

  * ROLE_USER
  * ROLE_ADMIN
* Route protection using Angular guards

---

### User Features

#### 1. Dashboard

* Welcome header
* Account balance display
* Action cards navigation
* Profile popup with:

  * Name
  * Email
  * Phone
  * Address
  * Date of Birth

#### 2. Transfer Money

* Secure transfer between accounts
* Idempotency key support
* Real time balance refresh

#### 3. Transaction History

* Paginated table
* Filtering:

  * Date range
  * Transaction type
  * Status
* Rollback request action for eligible transactions

#### 4. Rollback Request Flow

* Available only for:

  * DEBIT transactions
  * SUCCESS status
* Confirmation dialog before request
* Status updated to:

```
ROLLBACK_REQUESTED
```

* Action button removed immediately after request

---

### Admin Features

#### Admin Dashboard Cards

* Pending Approvals
* Pending Rollback Requests
* Deposit Money
* All Transactions
* All Accounts
* Analytics

Grid layout optimized for desktop view.

---

#### Pending Approvals

* Approve new accounts
* Reject accounts
* Pagination support
* Message feedback system

---

#### Rollback Management

* View pending rollback requests
* Approve rollback
* Reject rollback
* Confirmation dialog before action
* Auto refresh list after action

---

#### Deposit Management

* Credit money to any account
* Input validation
* Transaction refresh after deposit

---

#### System Transactions

* View all transactions
* Status visibility
* Transaction metadata

---

#### Account Management

* View all registered users
* Balance and approval status

---

#### Analytics Dashboard

* KPI metrics
* Daily trends
* Status breakdown charts

---

## Project Workflow

### User Lifecycle

1. User registers
2. Account remains pending
3. Admin approves account
4. User logs in
5. User performs transfers
6. Transactions recorded
7. User may request rollback
8. Admin reviews rollback request
9. Transaction updated accordingly

---

### Rollback Workflow

```
SUCCESS (DEBIT)
        |
        v
ROLLBACK_REQUESTED
        |
   Admin Decision
   /            \
APPROVED       REJECTED
   |
ROLLED_BACK
```

---

## Frontend Architecture

### Structure (Simplified)

```
src/
 ├── core/
 │   ├── api.ts
 │   ├── guards/
 │   └── services/
 │
 ├── shared/
 │   ├── confirm-dialog/
 │   ├── pagination/
 │
 ├── user/
 │   ├── user-dashboard/
 │   ├── transfer/
 │   └── transactions/
 │
 ├── admin/
 │   ├── admin-dashboard/
 │   ├── approvals/
 │   ├── rollbacks/
 │   ├── deposit/
 │   ├── accounts/
 │   └── transactions/
```

---

### UI Design Principles

* Glassmorphism inspired cards
* Rounded modern UI
* Dark mode support
* Reusable modal system
* Responsive grids
* Consistent typography

---

## API Endpoints

### Authentication

| Method | Endpoint                | Description       |
| ------ | ----------------------- | ----------------- |
| POST   | `/api/v1/auth/login`    | User login        |
| POST   | `/api/v1/auth/register` | User registration |

---

### Transfers

| Method | Endpoint                              | Description                    |
| ------ | ------------------------------------- | ------------------------------ |
| POST   | `/api/v1/transfers`                   | Create transfer                |
| GET    | `/api/v1/transfers/history`           | User transaction history       |
| POST   | `/api/v1/transfers/{id}/rollback`     | Request rollback               |
| GET    | `/api/v1/transfers/rollback-requests` | List pending rollback requests |

---

### Accounts

| Method | Endpoint                        | Description     |
| ------ | ------------------------------- | --------------- |
| GET    | `/api/v1/accounts/{id}`         | Account details |
| GET    | `/api/v1/accounts/{id}/balance` | Account balance |
| GET    | `/api/v1/accounts/search`       | Account lookup  |

---

### Admin — Accounts

| Method | Endpoint                              | Description       |
| ------ | ------------------------------------- | ----------------- |
| GET    | `/api/v1/admin/accounts/pending`      | Pending approvals |
| POST   | `/api/v1/admin/accounts/{id}/approve` | Approve account   |
| POST   | `/api/v1/admin/accounts/{id}/reject`  | Reject account    |
| POST   | `/api/v1/admin/accounts/deposit`      | Deposit money     |
| GET    | `/api/v1/admin/accounts`              | All accounts      |

---

### Admin — Transactions

| Method | Endpoint                     | Description         |
| ------ | ---------------------------- | ------------------- |
| GET    | `/api/v1/admin/transactions` | System transactions |

---

### Admin — Rollbacks

| Method | Endpoint                                          | Description      |
| ------ | ------------------------------------------------- | ---------------- |
| POST   | `/api/v1/admin/rollbacks/{transactionId}/approve` | Approve rollback |
| POST   | `/api/v1/admin/rollbacks/{transactionId}/reject`  | Reject rollback  |

---

### Analytics

| Method | Endpoint                 | Description |
| ------ | ------------------------ | ----------- |
| GET    | `/api/v1/analytics/kpis` | KPI metrics |

---

## Shared Components

### Confirm Dialog

Reusable modal used for:

* Logout confirmation
* Rollback request confirmation
* Admin rollback approval or rejection

Configurable via inputs:

* title
* message
* confirmText
* cancelText

---

### Pagination Component

* Generic reusable paginator
* Used across:

  * Transactions
  * Approvals
  * Rollbacks

---

### Date Picker

* Custom calendar component
* Controlled opening
* Click outside to close
* Theme aware

---

## Important Implementation Notes

### Angular Response Parsing

Some endpoints return plain text instead of JSON.

Example:

```
Rollback request submitted successfully
```

Angular requires:

```ts
{ responseType: 'text' }
```

to avoid parsing errors.

---

### Change Detection Strategy

Manual change detection is used where necessary:

```ts
this.cdr.detectChanges();
```

Applied after async state updates and modal state transitions.

---

## Security Considerations

* JWT based authentication
* Guard protected routes
* Role based access control
* Backend authorization enforcement
* Idempotency key for transfer safety

---

## Running the Project

### Backend

```
mvnw spring-boot:run
```

or

```
mvn spring-boot:run
```

Backend runs on:

```
http://localhost:8080
```

---

### Frontend

```
npm install
ng serve
```

Frontend runs on:

```
http://localhost:4200
```

---

## Future Improvements

* WebSocket based live updates
* Advanced analytics charts
* Transaction search and export
* Audit logs
* Notification system
* Optimistic state management with signals
* Improved state store architecture

---

## Summary

Money Transfer System demonstrates a production style architecture with:

* Clean role separation
* Modular Angular design
* REST driven backend
* Financial workflow safety via rollback system
* Reusable UI infrastructure
* Scalable component structure

The project is structured to allow incremental feature growth while maintaining readability and maintainability.
