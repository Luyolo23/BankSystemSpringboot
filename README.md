# Bank System API

A comprehensive backend system for managing banking operations including customers, accounts, fund transfers, and transaction history. Built with Java 21, Spring Boot 3, and containerized with Docker for seamless deployment.

## Features

* **Authentication & Authorization**: User registration and authentication with BCrypt password hashing.
* **Customer Management**: Register, retrieve, and delete customer profiles.
* **Account Operations**: Open savings/checking accounts linked to customers, check balances, deposit, withdraw, and transfer funds.
* **Transaction Tracking**: Audit trail for deposits, withdrawals, and transfers with date-range query support.
* **Database & Persistence**: Spring Data JPA with MySQL 8.0 datastore.
* **Containerization**: Multi-stage Docker build and multi-container orchestration with Docker Compose.

## Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.5
* **Database**: MySQL 8.0
* **Build Tool**: Maven
* **Containerization**: Docker & Docker Compose

## Prerequisites

Before running the application, ensure you have installed:
* Java 21 JDK
* Maven 3.8+
* Docker & Docker Compose

---

## Quick Start (Docker)

1. Navigate to the project root directory:
   ```bash
   cd BankSystem
   ```

2. Start the application and database containers:
   ```bash
   docker compose up --build
   ```

3. The app will run at `http://localhost:8080` (MySQL running on port `3307`).

4. To stop the containers:
   ```bash
   docker compose down
   ```

---

## API Endpoints

Below is the complete list of REST endpoints available in the current version of the application.

---

### 1. Authentication (`/api/auth`)

| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new customer | **JSON Body**: Customer object |
| `POST` | `/api/auth/login` | Authenticate customer | **JSON Body**: Login credentials |

#### Request Examples:

* **Register Customer (`POST /api/auth/register`)**
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "username": "johndoe",
    "password": "securepassword123"
  }
  ```

* **Login (`POST /api/auth/login`)**
  ```json
  {
    "username": "johndoe",
    "password": "securepassword123"
  }
  ```

---

### 2. Customer Management (`/customers`)

| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `POST` | `/customers` | Register/Create a new customer | **JSON Body**: Customer object |
| `GET` | `/customers` | Get all customers | None |
| `GET` | `/customers/{id}` | Get customer by ID | Path variable: `id` |
| `DELETE` | `/customers/{id}` | Delete customer by ID | Path variable: `id` |

#### Request Examples:

* **Create Customer (`POST /customers`)**
  ```json
  {
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "0987654321",
    "username": "janesmith",
    "password": "mypassword123"
  }
  ```

---

### 3. Account Management & Operations (`/accounts`)

| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `POST` | `/accounts/create/{customerId}` | Create an account for customer | Path variable: `customerId`<br>**JSON Body**: Account object |
| `GET` | `/accounts` | Get all accounts | None |
| `GET` | `/accounts/customer/{customerId}` | Get accounts by customer ID | Path variable: `customerId` |
| `POST` | `/accounts/{accountNumber}/deposit` | Deposit funds | Path variable: `accountNumber`<br>**Query Param**: `amount` (e.g. `?amount=150.00`) |
| `POST` | `/accounts/{accountNumber}/withdraw` | Withdraw funds | Path variable: `accountNumber`<br>**Query Param**: `amount` (e.g. `?amount=50.00`) |
| `POST` | `/accounts/transfer` | Transfer funds between accounts | **JSON Body**: Transfer details |

#### Request Examples:

* **Create Account (`POST /accounts/create/1`)**
  ```json
  {
    "accountNumber": "ACC1001",
    "type": "SAVINGS",
    "balance": 500.00
  }
  ```

* **Deposit (`POST /accounts/ACC1001/deposit?amount=200.50`)**

* **Withdraw (`POST /accounts/ACC1001/withdraw?amount=50.00`)**

* **Transfer Funds (`POST /accounts/transfer`)**
  ```json
  {
    "fromAccountNumber": "ACC1001",
    "toAccountNumber": "ACC1002",
    "amount": 100.00,
    "description": "Rent payment"
  }
  ```

---

### 4. Transactions & History (`/api/transactions`)

| Method | Endpoint | Description | Query Parameters |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/transactions` | Get account transaction history | `accountNumber` (e.g. `?accountNumber=ACC1001`) |
| `GET` | `/api/transactions/range` | Get transactions by date range | `accountNumber`, `start`, `end`<br>*(ISO format: `YYYY-MM-DDTHH:MM:SS`)* |

#### Request Examples:

* **Get Account Transactions (`GET /api/transactions?accountNumber=ACC1001`)**

* **Get Transactions in Date Range (`GET /api/transactions/range?accountNumber=ACC1001&start=2026-01-01T00:00:00&end=2026-12-31T23:59:59`)**

---

## Architecture Overview

- **Controller Layer**: REST API endpoints (`AuthController`, `CustomerController`, `AccountController`, `TransactionController`).
- **Service Layer**: Business logic, transfers, deposit/withdrawal calculations, and password encoding (`AuthService`, `CustomerService`, `AccountService`, `TransactionService`).
- **Repository Layer**: Database access using Spring Data JPA repositories.
- **Security Config**: Configured via `SecurityConfig` (Spring Security).

## Docker Configuration

* **`Dockerfile`**: Multi-stage build (`maven:3.9.6-eclipse-temurin-21` -> `eclipse-temurin:21-jre-jammy`).
* **`docker-compose.yml`**: Configures `app` service and `mysql` database container (port 3307 mapped to container 3306) with volume persistence.
