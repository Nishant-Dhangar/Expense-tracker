# 💰 Expense Tracker

A full-stack personal finance application for tracking income, expenses, transactions, and monthly budgets across **Android, iOS, and Web**.

The project uses a **Flutter mobile application** and a **web dashboard** connected to a shared **Spring Boot REST API** with **MySQL** as the database.

---

## 🌐 Live Demo

### Web Dashboard

**https://expense-tracker-web-eight-alpha.vercel.app/**

### Backend API

**https://expense-tracker-abe2.onrender.com**

> The backend API requires authentication for protected endpoints.

---

## ✨ Features

### 🔐 Authentication & Security

- User registration and login
- BCrypt password hashing
- Spring Security authentication
- Session-based authentication
- CSRF protection
- Secure logout
- Protected API endpoints
- User-specific data isolation
- Server-side authorization
- Request validation
- Safe response DTOs
- Password hashes never exposed through API responses

### 💸 Transaction Management

- Add income and expenses
- View transaction history
- Edit transactions
- Delete transactions
- Assign categories
- Add descriptions
- Select transaction dates
- Automatic income/expense classification

The backend verifies that a transaction's category matches its transaction type.

### 📊 Dashboard & Analytics

- Total balance
- Total income
- Total expenses
- Income vs. expense analysis
- Monthly spending trends
- Month-to-month comparison
- Savings rate
- Expense breakdown
- Recent transactions

### 🎯 Monthly Budgets

- Create monthly budgets
- Update budgets
- Delete budgets
- Track monthly spending
- Calculate remaining budget
- Budget progress indicators
- Budget warnings when spending approaches the limit
- User-specific budget isolation

Budget calculations only include the authenticated user's expenses for the selected month and year.

### ⚡ Quick Expense

The Flutter application includes a **shake-to-open Quick Expense feature**.

Shake the phone → Quick Expense interface opens → enter the expense → save → interface closes.

This allows users to record an expense without navigating through the entire application.

### 🔔 Notifications

The mobile application supports local budget notifications, including alerts when spending reaches important budget thresholds.

### 🎨 User Experience

- Responsive web dashboard
- Flutter Android/iOS application
- Dark mode
- Persistent login session
- Profile management
- Notification settings
- Clean dashboard interface

---

## 🏗️ Architecture

![Expense Tracker Architecture](BACKEND%20ARCHITECTURE.png)

The application uses a shared Spring Boot backend for both the Flutter mobile application and the web dashboard.

```text
                     ┌──────────────────────┐
                     │   Flutter App        │
                     │    Android / iOS     │
                     └──────────┬───────────┘
                                │
                                │ REST API
                                │
                     ┌──────────▼───────────┐
                     │                      │
                     │   Spring Boot API    │
                     │       Render         │
                     │                      │
                     └──────────┬───────────┘
                                │
                           JDBC / JPA
                                │
                     ┌──────────▼───────────┐
                     │                      │
                     │     Aiven MySQL      │
                     │                      │
                     └──────────────────────┘


                     ┌──────────────────────┐
                     │   Web Dashboard      │
                     │       Vercel         │
                     └──────────┬───────────┘
                                │
                           API Proxy
                                │
                                ▼
                     ┌──────────────────────┐
                     │   Spring Boot API    │
                     │       Render         │
                     └──────────────────────┘


Production Flow
Flutter
   │
   ▼
Render Spring Boot
   │
   ▼
Aiven MySQL


Web Browser
   │
   ▼
Vercel
   │
   ▼
Render Spring Boot
   │
   ▼
Aiven MySQL


🛠️ Tech Stack

Backend
-Technology	Purpose
-Java	Backend programming language
-Spring Boot	REST API framework
-Spring Security	Authentication & authorization
-Spring Data JPA	Database access
-Hibernate	ORM
-Jakarta Validation	Request validation

Database
-Technology	Purpose
-MySQL	Relational database
-Aiven	Production MySQL hosting

Frontend
-Technology	Purpose
-Flutter	Android & iOS application
-HTML	Web structure
-CSS	Web styling
-JavaScript	Web application logic
-Chart.js	Dashboard charts

Deployment
-Platform	Purpose
-Render	Spring Boot backend
-Vercel	Web dashboard
-GitHub	Source control
-Aiven	Production database

🔐 Security Architecture

Security is handled primarily by Spring Security.

Password Security

Passwords are never stored in plain text.

User Password
      │
      ▼
BCryptPasswordEncoder
      │
      ▼
Password Hash
      │
      ▼
MySQL
Session Authentication

After successful login, the backend creates an authenticated HTTP session.

Protected requests require a valid authenticated session.

CSRF Protection

CSRF protection is enabled for state-changing requests.

Clients obtain a CSRF token before making protected requests and send it using:

X-XSRF-TOKEN
User Data Isolation

Transactions and budgets are associated with the authenticated user.

User A
 ├── Transactions
 └── Budget

User B
 ├── Transactions
 └── Budget

A user cannot access another user's transaction or budget data.

Input Validation

The backend validates incoming requests before processing them.

Examples include:

-Required fields
Valid email addresses
Positive transaction amounts
Valid transaction dates
Valid budget amounts
Valid month values
Description length limits

📡 REST API
Authentication
Method	Endpoint	Description
POST	/api/users/register	Register a user
POST	/api/auth/login	Authenticate user
GET	/api/auth/me	Get current user
GET	/api/auth/csrf	Get CSRF token
PUT	/api/auth/profile	Update profile
POST	/api/auth/logout	Logout

-Transactions
Method	Endpoint	Description
GET	/api/transactions	Get user's transactions
POST	/api/transactions	Create transaction
PUT	/api/transactions/{id}	Update transaction
DELETE	/api/transactions/{id}	Delete transaction

-Categories
Method	Endpoint	Description
GET	/api/categories	Get available categories

-Budgets
Method	Endpoint	Description
GET	/api/budgets	Get current user's budget
POST	/api/budgets	Create/update budget
DELETE	/api/budgets	Delete budget

🗄️ Database Design

The main database entities are:

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

ID
Name
Email
BCrypt password hash
Categories

Stores:

ID
Name
Type

Category types:

INCOME
EXPENSE
Transactions

Stores:

ID
User
Category
Amount
Type
Description
Transaction date
Budgets

Stores:

ID
User
Month
Year
Amount

A unique constraint prevents multiple budgets for the same user, month, and year.

🔄 Transaction Flow
Client
   │
   │ POST /api/transactions
   ▼
Spring Security
   │
   ├── Authentication
   ├── CSRF validation
   └── Authorization
   │
   ▼
TransactionController
   │
   ├── Identify authenticated user
   ├── Validate request
   ├── Validate category
   └── Validate category/type compatibility
   │
   ▼
TransactionService
   │
   ▼
TransactionRepository
   │
   ▼
MySQL
🎯 Budget Calculation

Monthly spending is calculated using:

Authenticated User
        +
Current Month
        +
Current Year
        +
EXPENSE transactions

This ensures that transactions belonging to another user or another month do not affect the budget calculation.

🌐 Deployment
Backend
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
Web Application
GitHub
   │
   ▼
Vercel
   │
   ▼
Web Dashboard
   │
   ▼
Vercel API Proxy
   │
   ▼
Render Spring Boot API

Production secrets such as database credentials are stored as environment variables and are not committed to source control.

🧪 Testing

The production application has been tested for:

-User registration
-Login and logout
-Session persistence
-Protected routes
-CSRF protection
-User data isolation
-Transaction CRUD
-Category validation
-Monthly budgets
-Budget isolation
-Monthly budget calculations
-Web authentication
-Flutter authentication
-Production API connectivity

📱 Clients
Flutter Mobile Application

Cross-platform application targeting:

Android
iOS

The Flutter application communicates with the shared Spring Boot REST API.

Web Dashboard

The web client uses:

HTML
CSS
JavaScript
Chart.js

It is deployed through Vercel and communicates with the backend through the Vercel API proxy.

🚀 Project Status

Version: v1.0.0

Status: Stable Production Release

The application is deployed and operational.

🔮 Future Improvements

Potential future improvements include:

-Advanced recurring transactions
-Export transactions to CSV/PDF
-More detailed financial reports
-Custom user categories
-Cloud-based notification preferences
-Advanced search and filtering
-Financial goals
-Improved mobile analytics
-Automated testing and CI/CD improvements

🔗 Links

Web Dashboard:
https://expense-tracker-web-eight-alpha.vercel.app/

Backend API:
https://expense-tracker-abe2.onrender.com

GitHub:
https://github.com/Nishant-Dhangar

👨‍💻 Author
Nishant Dhangar

Computer Science student and developer focused on building full-stack applications using Java, Spring Boot, Flutter, and modern cloud deployment platforms.




