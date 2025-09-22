# JWT Student Library Management System

## 📋 Project Overview

The JWT Student Library Management System is a comprehensive microservices-based application designed to manage students, books, loans, and reservations in a library environment. The system implements JWT-based authentication and role-based access control to ensure secure operations.

### 🏗️ Architecture

This project consists of three main microservices:

1. **Authentication Service** (Port 8080) - Handles user authentication and JWT token management
2. **Student Management Service** (Port 8081) - Manages student information and profiles
3. **Library Management Service** (Port 8082) - Manages books, loans, and reservations

### 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.4.4
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security 6
- **Database**: MySQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17
- **Additional Libraries**: 
  - Lombok for boilerplate code reduction
  - MapStruct for object mapping
  - Jackson for JSON processing
  - Validation API for request validation

### 🎯 Key Features

- **Multi-service Architecture**: Modular design with separate services for different domains
- **JWT Authentication**: Secure token-based authentication system
- **Role-based Access Control**: Support for ADMIN, LIBRARIAN, STUDENT, and USER roles
- **Book Management**: Complete CRUD operations for library books
- **Student Management**: Comprehensive student profile management
- **Loan System**: Book checkout and return functionality
- **Reservation System**: Book reservation with approval workflow
- **Inter-service Communication**: RESTful APIs for service-to-service communication

### 🗄️ Database Configuration

Each service uses its own MySQL database:
- Authentication Service: `stu_jwt`
- Student Management: `student_management`
- Library Management: `library`

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/TechnicalJin/JWT-STUDENT-LIBRARY.git
   cd JWT-STUDENT-LIBRARY
   ```

2. **Database Setup**
   Create the required databases in MySQL:
   ```sql
   CREATE DATABASE stu_jwt;
   CREATE DATABASE student_management;
   CREATE DATABASE library;
   ```

3. **Configure Database Connection**
   Update the database credentials in the `application.properties` files for each service:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and Run Services**
   
   Start each service in separate terminals:
   
   **Authentication Service (Port 8080):**
   ```bash
   cd authentication
   mvn spring-boot:run
   ```
   
   **Student Management Service (Port 8081):**
   ```bash
   cd management
   mvn spring-boot:run
   ```
   
   **Library Management Service (Port 8082):**
   ```bash
   cd library
   mvn spring-boot:run
   ```

### 🔐 Authentication Flow

1. Register a new user or student
2. Login to receive JWT tokens (access token and refresh token)
3. Include the access token in the Authorization header for subsequent requests
4. Use the refresh token to obtain new access tokens when they expire

## 📚 API Documentation

### 🔑 Authentication Service (Port 8080)

Base URL: `http://localhost:8080`

#### Authentication Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/auth/login` | User login | Public |
| POST | `/api/auth/register` | User registration | Public |
| POST | `/api/auth/register-student` | Student registration | Public |
| POST | `/api/auth/refresh` | Refresh JWT token | Public |
| PUT | `/api/auth/change-password` | Change user password | Authenticated |
| PUT | `/api/auth/update-profile` | Update user profile | Authenticated |

#### Role-based Access Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/admin/dashboard` | Admin dashboard | ADMIN |
| GET | `/api/user/profile` | User profile | ADMIN, USER |
| GET | `/api/public` | Public endpoint | Public |

### 👥 Student Management Service (Port 8081)

Base URL: `http://localhost:8081`

#### Authentication Endpoints (Student Service)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/auth/login` | Student login | Public |
| POST | `/api/auth/register` | Student registration | Public |

#### Student Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/students` | Create new student | Public |
| GET | `/api/students/{id}` | Get student by ID | ADMIN, STUDENT |
| GET | `/api/students` | Get all students | ADMIN, USER |
| PUT | `/api/students/{id}` | Update student | ADMIN |
| DELETE | `/api/students/{id}` | Delete student | ADMIN |
| GET | `/api/students/validate/{studentId}` | Validate student ID | Public |
| GET | `/api/students/exists/{studentId}` | Check if student exists | Public |
| GET | `/api/students/by-email/{email}` | Get student by email | Public |
| GET | `/api/students/studentId/{studentId}` | Get student by student ID | ADMIN, USER |
| GET | `/api/students/status/{status}` | Get students by enrollment status | ADMIN, USER |
| GET | `/api/students/enrollment-range` | Get students by enrollment date range | ADMIN, USER |
| GET | `/api/students/me` | Get current student info | STUDENT |
| PUT | `/api/students/{id}/deactivate` | Deactivate student | ADMIN |
| GET | `/api/students/search` | Search students by name/email | ADMIN, STUDENT |
| GET | `/api/students/department/{department}` | Get students by department | ADMIN, STUDENT |

### 📖 Library Management Service (Port 8082)

Base URL: `http://localhost:8082`

#### Book Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/books` | Create new book | ADMIN, LIBRARIAN |
| GET | `/api/books` | Get all books | ADMIN, LIBRARIAN, STUDENT, USER |
| GET | `/api/books/{id}` | Get book by ID | ADMIN, LIBRARIAN, STUDENT, USER |
| PUT | `/api/books/{id}` | Update book | ADMIN, LIBRARIAN |
| DELETE | `/api/books/{id}` | Delete book | ADMIN, LIBRARIAN |
| GET | `/api/books/search` | Search books by title | ADMIN, LIBRARIAN, STUDENT, USER |
| GET | `/api/books/{id}/availability` | Check book availability | ADMIN, LIBRARIAN, STUDENT, USER |

#### Loan Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/loans/checkout` | Check out a book | Authenticated |
| PUT | `/api/loans/return/{loanId}` | Return a book | Authenticated |
| GET | `/api/loans/active/student/{studentId}` | Get active loans for student | Authenticated |
| GET | `/api/loans/active/me` | Get my active loans | Authenticated |

#### Reservation Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/reservations` | Create book reservation | STUDENT, USER |
| PUT | `/api/reservations/approve/{reservationId}` | Approve reservation | LIBRARIAN, ADMIN |
| PUT | `/api/reservations/reject/{reservationId}` | Reject reservation | LIBRARIAN, ADMIN |
| PUT | `/api/reservations/approve-and-loan/{reservationId}` | Approve reservation and create loan | LIBRARIAN, ADMIN |
| GET | `/api/reservations/pending` | Get pending reservations | LIBRARIAN, ADMIN |
| DELETE | `/api/reservations/{id}` | Cancel reservation | STUDENT, USER |
| GET | `/api/reservations/me` | Get my reservations | STUDENT, USER |
| GET | `/api/reservations/history/student/{studentId}` | Get reservation history | LIBRARIAN, ADMIN, STUDENT, USER |

## 🎯 Usage Examples

### Authentication

1. **Register a new student:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register-student \
     -H "Content-Type: application/json" \
     -d '{
       "email": "student@example.com",
       "password": "password123",
       "firstName": "John",
       "lastName": "Doe"
     }'
   ```

2. **Login:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "usernameOrEmail": "student@example.com",
       "password": "password123"
     }'
   ```

3. **Access protected endpoint:**
   ```bash
   curl -X GET http://localhost:8081/api/students/me \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

### Book Operations

1. **Search for books:**
   ```bash
   curl -X GET "http://localhost:8082/api/books/search?title=Programming" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

2. **Check out a book:**
   ```bash
   curl -X POST http://localhost:8082/api/loans/checkout \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{
       "bookId": 1,
       "studentId": 123
     }'
   ```

## 🔧 Configuration

### JWT Configuration

The JWT configuration is shared across all services with the following settings:
- **Secret Key**: Configured in `application.properties`
- **Access Token Expiration**: 7 days (604800000 ms)
- **Refresh Token Expiration**: 7 days (604800000 ms)

### Roles and Permissions

The system supports four main roles:
- **ADMIN**: Full system access
- **LIBRARIAN**: Library management operations
- **STUDENT**: Student-specific operations
- **USER**: Basic user operations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 📞 Support

For questions or support, please contact the development team or create an issue in the repository.