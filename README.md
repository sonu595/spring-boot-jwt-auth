# Spring Boot User Authentication API with JWT

This is a backend project built with Spring Boot that implements a secure REST API for user registration and login using Spring Security and JSON Web Tokens (JWT).

## Features

* **User Registration:** Allows new users to register an account.
* **User Login:** Authenticates existing users and returns a JWT upon successful login.
* **Password Encryption:** Automatically hashes user passwords using `BCryptPasswordEncoder` before saving them to the database.
* **JWT Validation:** All incoming requests (except for public routes) are intercepted by a `JwtFilter` to validate the `Authorization` header.
* **Secured Endpoints:** Configured Spring Security to protect most endpoints, requiring a valid JWT for access.
* **Public Endpoints:** Allows public access to the registration (`/api/auth/register`) and login (`/api/auth/login`) endpoints.
* **Input Validation:** Uses `jakarta.validation` annotations in DTOs (like `Userdto`) to validate incoming request data.

## Technologies Used

* **Java**
* **Spring Boot**
    * Spring Web
    * Spring Security
    * Spring Data JPA
* **JJWT (Java JWT):** Used in `JwtService` to create and parse JSON Web Tokens.
* **Lombok:** Used to reduce boilerplate code (getters, setters, constructors) in data models and DTOs.
* **JPA (Hibernate):** Manages the database schema and queries via the `User` entity and `UserRepository`.
* **Any JPA-supported Database:** (e.g., H2, MySQL, PostgreSQL)

## Core Components

* **`AuthController.java`**: A REST Controller that exposes the authentication endpoints (`/register`, `/login`, `/user/{id}`).
* **`AuthService.java`**: The service layer containing the core business logic for registering a user, checking for existing users, verifying login credentials, and generating a response.
* **`JwtService.java`**: A utility service responsible for generating a new JWT after login/registration and validating incoming tokens.
* **`SecurityConfig.java`**: Configures Spring Security. This is where the `PasswordEncoder` bean is defined, CSRF is disabled, and HTTP request authorization rules (which paths are public vs. private) are set.
* **`JwtFilter.java`**: A filter that extends `OncePerRequestFilter`. It intercepts every request, extracts the JWT from the "Authorization" header, validates it, and sets the `SecurityContextHolder` with the user's authentication details.
* **`User.java`**: The JPA Entity class that represents the `users` table in the database.
* **`UserRepository.java`**: The Spring Data JPA repository interface for performing CRUD operations on the `User` entity.
* **`Userdto.java`**: A Data Transfer Object (DTO) used to receive user registration data from the client. It includes validation rules.
* **`LoginDto.java`**: A DTO used to receive user login credentials (username and password).
* **`AuthResponse.java`**: A DTO used to structure the JSON response sent back to the client after successful registration or login.

## API Endpoints

All endpoints are prefixed with `/api/auth`.

### 1. Register User

* **URL:** `POST /api/auth/register`
* **Description:** Creates a new user account.
* **Request Body:** (`Userdto`)
    ```json
    {
      "username": "newuser",
      "password": "password123",
      "email": "user@example.com",
      "firstName": "Test",
      "lastName": "User"
    }
    ```
* **Success Response:** (`AuthResponse`)
    ```json
    {
      "message": "User registered successfully",
      "userId": 1,
      "username": "newuser",
      "email": "user@example.com",
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIi..."
    }
    ```

### 2. Login User

* **URL:** `POST /api/auth/login`
* **Description:** Authenticates a user and provides a JWT.
* **Request Body:** (`LoginDto`)
    ```json
    {
      "username": "newuser",
      "password": "password123"
    }
    ```
* **Success Response:** (`AuthResponse`)
    ```json
    {
      "message": "Login successfully",
      "userId": 1,
      "username": "newuser",
      "email": "user@example.com",
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIi..."
    }
    ```

### 3. Get User By ID (Secured)

* **URL:** `GET /api/auth/user/{id}`
* **Description:** Retrieves the details of a user by their ID. Requires a valid JWT.
* **Headers:**
    * `Authorization: Bearer <your-jwt-token>`
* **Success Response:** (`User`)
    ```json
    {
      "id": 1,
      "username": "newuser",
      "password": "$2a$10$...",
      "email": "user@example.com",
      "firstname": "Test",
      "lastname": "User"
    }
    ```
* **Error Response (if unauthorized):**
    `HTTP 401 Unauthorized` (Handled by Spring Security)

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone <your-repo-url>
    cd <project-directory>
    ```

2.  **Configure your database:**
    Open `src/main/resources/application.properties` and add your database configuration (URL, username, password, driver).

    *Example for H2 (in-memory database):*
    ```properties
    spring.h2.console.enabled=true
    spring.datasource.url=jdbc:h2:mem:testdb
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=password
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` by default.
