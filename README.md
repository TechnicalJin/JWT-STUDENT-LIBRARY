# JWT Student Library Management System

A comprehensive library management system built with Spring Boot microservices architecture, featuring JWT authentication, student management, and book lending functionality.

## 🚀 Features

- **JWT Authentication**: Secure token-based authentication system
- **Student Management**: Complete CRUD operations for student records
- **Library Management**: Books, reservations, and loan tracking
- **Role-based Access Control**: Different permissions for students, librarians, and admins
- **Microservices Architecture**: Scalable and maintainable service design
- **RESTful APIs**: Well-documented REST endpoints

## 🏗️ Architecture

The system consists of three main microservices:

1. **Authentication Service** (Port 8080)
   - JWT token generation and validation
   - User authentication and authorization
   - Role-based access control

2. **Student Management Service** (Port 8081)
   - Student registration and profile management
   - CRUD operations for student records
   - Student validation and lookup

3. **Library Service** (Port 8082)
   - Book catalog management
   - Reservation system
   - Loan tracking and management

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.4.4
- **Security**: Spring Security with JWT
- **Database**: MySQL
- **Java Version**: 17
- **Build Tool**: Maven
- **Additional Libraries**: 
  - Lombok for boilerplate code reduction
  - MapStruct for object mapping
  - Jackson for JSON processing

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/TechnicalJin/JWT-STUDENT-LIBRARY.git
cd JWT-STUDENT-LIBRARY
```

### 2. Database Setup
Create three MySQL databases:
```sql
CREATE DATABASE jwt_auth_db;
CREATE DATABASE student_management_db;
CREATE DATABASE library_management_db;
```

### 3. Configure Database Connections
Update the `application.properties` or `application.yml` files in each service:

**Authentication Service** (`authentication/src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_auth_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**Management Service** (`management/src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_management_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**Library Service** (`library/src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build and Run Services

#### Option 1: Run each service separately
```bash
# Terminal 1 - Authentication Service
cd authentication
./mvnw spring-boot:run

# Terminal 2 - Student Management Service
cd management
./mvnw spring-boot:run

# Terminal 3 - Library Service
cd library
./mvnw spring-boot:run
```

#### Option 2: Build all services
```bash
# Build all services
mvn clean install

# Run each service
java -jar authentication/target/authentication-0.0.1-SNAPSHOT.jar
java -jar management/target/management-0.0.1-SNAPSHOT.jar
java -jar library/target/library-0.0.1-SNAPSHOT.jar
```

### 5. Verify Services
- Authentication Service: http://localhost:8080
- Student Management Service: http://localhost:8081
- Library Service: http://localhost:8082

## 📚 API Documentation

### Authentication Endpoints

#### 1. Login & Get JWT Token
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "usernameOrEmail": "student",
    "password": "student"
}
```

### Student Management Endpoints

All student endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer YOUR_JWT_TOKEN
```
#### 1. Create Student
```http
POST http://localhost:8081/api/students
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "firstname": "Emily",
    "lastname": "Chen",
    "email": "emily.chen@university.edu",
    "department": "Business Administration",
    "gender": "Non-Binary",
    "dateOfBirth": "2003-07-19",
    "phoneNumber": "+15558889999",
    "address": "234 Entrepreneurship Plaza",
    "city": "New York",
    "state": "New York",
    "country": "USA",
    "studentId": "BA20230102",
    "enrollmentDate": "2023-09-01",
    "enrollmentStatus": "ACTIVE"
}
```

#### 2. Update Student
```http
PUT http://localhost:8081/api/students/2
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "firstname": "James",
    "lastname": "Wilson",
    "email": "james.wilson.updated@university.edu", 
    "department": "Computer Science",
    "gender": "Male",
    "dateOfBirth": "2000-05-15",
    "phoneNumber": "+15551239999",  
    "address": "123 Updated Street",  
    "city": "Cambridge",  
    "state": "Massachusetts",
    "country": "USA",
    "studentId": "CS20230015",  
    "enrollmentDate": "2023-09-05",
    "enrollmentStatus": "GRADUATED"  
}
```
#### 3. Delete Student
```http
DELETE http://localhost:8081/api/students/2
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```
#### 4. Get All Students
```http
GET http://localhost:8081/api/students
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

#### 5. Check if Student Exists
```http
GET http://localhost:8081/api/students/exists/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

### Library Management Endpoints
#### Books

##### 1. Create Book
```http
POST http://localhost:8082/api/books
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "title": "Introduction to Java Programming",
    "author": "John Smith",
    "isbn": "978-1234567890",
    "genre": "Programming",
    "totalQuantity": 5,
    "availableQuantity": 5
}
```
##### 2. Get All Books
```http
GET http://localhost:8082/api/books
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 3. Get Book by ID
```http
GET http://localhost:8082/api/books/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 4. Update Book
```http
PUT http://localhost:8082/api/books/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "title": "Advanced Java Programming",
    "author": "John Smith",
    "isbn": "978-1234567890",
    "genre": "Programming",
    "totalQuantity": 10,
    "availableQuantity": 8
}
```

##### 5. Delete Book
```http
DELETE http://localhost:8082/api/books/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Reservations
##### 1. Create Reservation
```http
POST http://localhost:8082/api/reservations
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "studentId": 1,
    "bookId": 1,
    "reservationDate": "2025-09-20"
}
```

##### 2. Get Pending Reservations (LIBRARIAN/ADMIN only)
```http
GET http://localhost:8082/api/reservations/pending
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 3. Approve Reservation (LIBRARIAN/ADMIN only)
```http
PUT http://localhost:8082/api/reservations/approve/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 4. Reject Reservation (LIBRARIAN/ADMIN only)
```http
PUT http://localhost:8082/api/reservations/reject/1?action=REJECT
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 5. Approve and Create Loan (LIBRARIAN/ADMIN only)
```http
PUT http://localhost:8082/api/reservations/approve-and-loan/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

#### Loans
##### 1. Checkout Book
```http
POST http://localhost:8082/api/loans/checkout
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
    "studentId": 1,
    "bookId": 1,
    "checkOutDate": "2023-09-20",
    "dueDate": "2023-10-04"
}
```

##### 2. Return Book
```http
PUT http://localhost:8082/api/loans/return/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 3. Get Active Loans by Student
```http
GET http://localhost:8082/api/loans/active/student/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

##### 4. Get My Active Loans
```http
GET http://localhost:8082/api/loans/active/me
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

## 🔧 Configuration

### JWT Configuration
Configure JWT settings in your `application.properties`:

```properties
# JWT Configuration
app.jwt-secret=mySecretKey
app.jwt-expiration-milliseconds=604800000

# Role Configuration
app.roles.admin=ADMIN
app.roles.user=USER
```

### Database Configuration
Each service requires its own database configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 🧪 Testing

### Running Tests
```bash
# Run tests for all services
mvn test

# Run tests for specific service
cd authentication && mvn test
cd management && mvn test
cd library && mvn test
```

### Testing with Postman
1. Import the API endpoints into Postman
2. First, authenticate using the login endpoint to get a JWT token
3. Use the token in the Authorization header for subsequent requests
4. Test the various endpoints according to your user role

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL is running
   - Check database credentials in application.properties
   - Ensure databases exist

2. **JWT Token Invalid**
   - Check if token has expired
   - Verify JWT secret configuration
   - Ensure proper Authorization header format: `Bearer <token>`

3. **Port Already in Use**
   - Check if services are already running
   - Change port configuration in application.properties
   - Kill existing processes using the ports

4. **Service Not Responding**
   - Check service logs for errors
   - Verify service dependencies are running
   - Check network connectivity between services

### Logs
Check application logs for detailed error information:
```bash
# View logs for each service
tail -f authentication/logs/application.log
tail -f management/logs/application.log
tail -f library/logs/application.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write unit tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **TechnicalJin** - *Initial work* - [TechnicalJin](https://github.com/TechnicalJin)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- JWT.io for JWT implementation guides
- All contributors who helped improve this project