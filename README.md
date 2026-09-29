# Expense Tracker — Spring Boot Backend

A secure REST API backend for a full-stack Expense Tracker application.

The backend provides authentication, transaction management, categories, monthly budgets, user isolation, and dashboard data for both the Flutter mobile application and the web dashboard.

## 🚀 Live Backend

Backend API:

https://expense-tracker-abe2.onrender.com

> The API is protected by Spring Security. Some endpoints require an authenticated session.

---

## ✨ Features

### Authentication & Security

- User registration
- BCrypt password hashing
- Email-based authentication
- Spring Security authentication
- Session-based authentication
- CSRF protection
- Secure logout
- Protected API endpoints
- Server-side authorization
- Per-user transaction isolation
- Per-user budget isolation
- Input validation
- Safe response DTOs
- Password hashes are never returned through API responses

### Transaction Management

Users can:

- Add transactions
- View their transactions
- Update transactions
- Delete transactions
- Create income transactions
- Create expense transactions
- Assign categories
- Add descriptions
- Specify transaction dates

Transactions belong to the authenticated user and cannot be accessed or modified by another user.

### Categories

The backend provides predefined categories such as:

- Food
- Transport
- Shopping
- Salary

Categories are associated with either:

- `INCOME`
- `EXPENSE`

The backend validates that a transaction's type matches its category type.

### Monthly Budgets

Users can:

- Create a monthly budget
- Update a monthly budget
- View the current month's budget
- Delete their budget
- Track monthly spending against the budget

Budgets are unique per:

```text
User + Month + Year
                         ┌─────────────────────┐
                         │    Flutter App      │
                         │     Android/iOS     │
                         └──────────┬──────────┘
                                    │
                                    │ REST API
                                    │
                         ┌──────────▼──────────┐
                         │                     │
                         │    Spring Boot      │
                         │       API           │
                         │                     │
                         │      Render         │
                         │                     │
                         └──────────┬──────────┘
                                    │
                           JDBC / Hibernate
                                    │
                         ┌──────────▼──────────┐
                         │                     │
                         │     Aiven MySQL     │
                         │      Database       │
                         │                     │
                         └─────────────────────┘


                         ┌─────────────────────┐
                         │   Web Dashboard     │
                         │      Vercel         │
                         └──────────┬──────────┘
                                    │
                              API Proxy
                                    │
                         ┌──────────▼──────────┐
                         │    Spring Boot      │
                         │       API           │
                         └─────────────────────┘
The same backend is shared by both the Flutter application and the web dashboard.

## 🏗️ Architecture

![Expense Tracker Architecture](BACKEND%20ARCHITECTURE.png)

The application uses a shared Spring Boot backend for both the Flutter mobile application and the web dashboard.

##Tech Stack

Backend
-Java
-Spring Boot
-Spring Security
-Spring Data JPA
-Hibernate
-Jakarta Persistence
-Jakarta Validation

Database
-MySQL
-Aiven

Deployment
-Render
-GitHub

Clients
-Flutter Android/iOS application
-HTML/CSS/JavaScript web dashboard
-Vercel

Authentication is handled using Spring Security.

Password Security

Passwords are never stored in plain text.

Passwords are hashed using BCrypt before being stored in the database.
User Password
      │
      ▼
 BCryptPasswordEncoder
      │
      ▼
Hashed Password
      │
      ▼
   MySQL

Session Authentication

After successful authentication, the backend creates an authenticated HTTP session.

Protected API requests require a valid authenticated session.

CSRF Protection

CSRF protection is enabled for state-changing requests.

The frontend obtains a CSRF token before making protected requests and sends it using:
X-XSRF-TOKEN

Authorization

Transactions and budgets are always associated with the authenticated user.

For example:

User A
 ├── Transactions
 └── Budget

User B
 ├── Transactions
 └── Budget

User A cannot access User B's transaction or budget data.

API Structure

--Authentication
POST /api/users/register
POST /api/auth/login
GET  /api/auth/me
GET  /api/auth/csrf
PUT  /api/auth/profile
POST /api/auth/logout

--Transactions
GET    /api/transactions
POST   /api/transactions
PUT    /api/transactions/{id}
DELETE /api/transactions/{id}

--Categories
GET /api/categories

--Budgets
GET    /api/budgets
POST   /api/budgets
DELETE /api/budgets

Database Structure

The application uses the following main tables:

users
 │
 ├───────────────┐
 │               │
 ▼               ▼
transactions    budgets
 │
 ▼
categories
Users

Stores:

User ID
Name
Email
BCrypt password hash
Categories

Stores:

Category ID
Category name
Category type
Transactions

Stores:

Transaction ID
User
Category
Amount
Type
Description
Transaction date
Budgets

Stores:

Budget ID
User
Month
Year
Amount
🔄 Transaction Flow
Client
  │
  │ POST /api/transactions
  ▼
Spring Security
  │
  ├── Authentication check
  │
  ├── CSRF validation
  │
  ▼
TransactionController
  │
  ├── Identify authenticated user
  │
  ├── Validate transaction
  │
  ├── Validate category
  │
  └── Verify category/type compatibility
  │
  ▼
TransactionService
  │
  ▼
TransactionRepository
  │
  ▼
MySQL

Budget Calculation

Monthly spending is calculated using expense transactions belonging to:

Current User
+
Current Month
+
Current Year
+
Type = EXPENSE

This prevents transactions from other users or previous months from affecting the current budget.

Deployment
Backend

The backend is deployed on Render.

GitHub
   │
   ▼
Render
   │
   ▼
Spring Boot
   │
   ▼
Aiven MySQL

Environment Variables

Production database credentials are provided through environment variables.

The application does not store production database credentials in source control.

Example configuration:

DB_URL=...
DB_USERNAME=...
DB_PASSWORD=...
SPRING_PROFILES_ACTIVE=prod

esting

The application has been tested for:

User registration
Login/logout
Session persistence
Protected routes
CSRF protection
User isolation
Transaction CRUD
Category validation
Budget isolation
Monthly budget calculation
Production API connectivity
Web dashboard authentication
Flutter application authentication

Clients

The backend currently serves two clients:

Flutter Mobile App

Cross-platform application targeting:

Android
iOS

The mobile application communicates with the Spring Boot REST API.

Web Dashboard

A separate web dashboard built using:

HTML
CSS
JavaScript
Chart.js

The web application is deployed through Vercel.

Related Project

Web Dashboard:

https://expense-tracker-web-eight-alpha.vercel.app/

Backend:

https://expense-tracker-abe2.onrender.com

Project Status

Version: v1.0.0

Status: Stable production release

The application is currently deployed and operational.

Author

Nishant Dhangar

GitHub:

https://github.com/Nishant-Dhangar



