# SMART-QR Pay Backend

This is the backend service for the **SMART-QR Pay** application, built using **Spring Boot 4.x** and **Java 21**. It provides APIs for user authentication (JWT-based), transaction history, payments, and account management.

## Prerequisites

Before running the application, ensure you have the following installed on your system:

- **Java Development Kit (JDK) 21** or later
- **Maven** (Optional, as the project includes Maven Wrapper)
- An active internet connection (to connect to the remote PostgreSQL database on Azure)

## Getting Started

Follow the steps below to clone, build, and run the project locally.

### 1. Clone the project

Open your terminal or command prompt and path to the cloned repository:

```bash
cd smart_qr_backend
```

### 2. Build the application

Use the included Maven wrapper to install dependencies and build the application. 

**On Windows:**
```cmd
mvnw.cmd clean install
```

**On Linux/macOS:**
```bash
./mvnw clean install
```

*(Note: This step downloads all necessary dependencies from Maven Central and compiles the project.)*

### 3. Application Configuration

The application is pre-configured to connect to its remote Azure PostgreSQL database (`azure-smartqrdb.postgres.database.azure.com`). If you need to make changes to database credentials, port bindings, or JWT secrets, you can modify the properties in:
`src/main/resources/application.yaml`

The server runs under the base context path: `/api` (default port 8080).

### 4. Run the application

To start the Spring Boot server running, execute:

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**On Linux/macOS:**
```bash
./mvnw spring-boot:run
```

Alternatively, you can run the assembled jar file from the `target` directory:

```bash
java -jar target/smart_qr_backend-0.0.1-SNAPSHOT.jar
```

The application should start, and you will see the Spring Boot banner along with log output indicating Tomcat initialized. The API endpoints will be accessible at `http://localhost:8080/api/`.

## API Endpoints

Once the application is running, you can interact with the various REST API controllers established under `/api/`, for example:
- `AppUserController` - Handles user-related operations, register, login, top-up, and verifying payments.

Make sure to include your Bearer token in the `Authorization` header for protected endpoints!

