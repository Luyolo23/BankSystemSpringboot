# Bank System API

A comprehensive backend system for managing banking operations including customers, accounts, and secure user authentication. Built with Java 21, Spring Boot 3, and containerized with Docker for seamless deployment.

## Features

* **Authentication & Authorization**: Secure login and registration using Spring Security.
* **Customer Management**: Register, update, retrieve, and delete customer profiles.
* **Account Operations**: Open accounts, manage balances, and handle basic account operations linked to specific customers.
* **Database & Persistence**: Leveraging Spring Data JPA for data mapping with MySQL for the primary datastore.
* **Containerization**: Fully dockerized application and database environments using `docker-compose`.

## Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.5
* **Database**: MySQL 8.0
* **Build Tool**: Maven
* **Containerization**: Docker & Docker Compose

## Prerequisites

Before you begin, ensure you have the following installed on your machine:
* Java 21 JDK
* Maven
* Docker

## Getting things started

### Running with Docker

The easiest way to get the system up and running is via Docker Compose. It will spin up both the MySQL database and the Spring Boot application container.

1. Navigate to the project directory:
   ```bash
   cd BankSystem
   ```
2. Start the services using Docker Compose:
   ```bash
   docker-compose up --build
   ```
3. The application will be accessible at: `http://localhost:8080`
   The database runs on port `3307` locally to avoid conflicts.

   **Note**: run `docker-compose down` to shut down.



## API Endpoints

Here is a high-level overview of the available controllers. Ensure you test these via tools like **Postman**.

### Authentication (`/auth` or `/api/auth`)
* Handling user registration and login to receive authentication tokens.

### Customers (`/api/customers`)
* `GET /api/customers` - Retrieve all customers.
* `GET /api/customers/{id}` - Retrieve a specific customer.
* `POST /api/customers` - Add a new customer.
* `PUT /api/customers/{id}` - Update a customer.
* `DELETE /api/customers/{id}` - Remove a customer.

### Accounts (`/api/accounts`)
* `GET /api/accounts` - Retrieve all accounts.
* `POST /api/accounts` - Open a new account (linked to a customer).
* Specific operations to manage balances, deposits, and status.



## Architecture

- **Controller Layer**: Handles incoming HTTP requests and delegates to services (`AuthController`, `CustomerController`, `AccountController`).
- **Service Layer**: Contains core business logic (`CustomerService`, `AccountService`).
- **Repository Layer**: Maps Java objects to database records using Spring Data JPA (`CustomerRepository`, etc.).
- **Security**: Handled using `SecurityConfig`.

## Docker Configuration

* `Dockerfile`: Uses a multi-stage build. First stage utilizes `maven:3.9.6-eclipse-temurin-21` to build the application. The second stage uses a lightweight `eclipse-temurin:21-jre-jammy` image to run the compiled JAR.
* `docker-compose.yml`: Defines the backend `app` service and the `mysql` database service, linking them via an internal network. Data persistence is managed via the `mysql_data` volume.
