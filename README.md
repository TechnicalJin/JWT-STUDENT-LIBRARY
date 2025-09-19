// Post-man Urls..

1] JWT TOKEN 
  POST : http://localhost:8080/api/auth/login
  BODY : row -> JSON
          {
              "usernameOrEmail":"student",
              "password":"student"
          }


2] STUDENT
  I) CREATE STUDENT :
    POST : http://localhost:8081/api/students
    HEADERS : Content-Type / application/json
              Authorization / Bearer TOKEN...
    BODY :
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

  II) UPDATE STUDENT :
    PUT : http://localhost:8081/api/students/2
    HEADERS : Content-Type / application/json
                Authorization / Bearer Token..

    BODY :
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
    
  III) DELETE STUDENT :
    DELETE : http://localhost:8081/api/students/2
    HEADERS : Content-Type / application/json
                Authorization / Bearer Token..

    
  IV) VIEW ALL STUDENT :
    GET : http://localhost:8081/api/students
    HEADERS : Content-Type / application/json
                Authorization / Bearer Token..

    
  V) EXISTS STUDENT :
    GET : http://localhost:8081/api/students/exists/1
     HEADERS : Content-Type / application/json
                Authorization / Bearer Token..


3] BOOKS (Library Service)
  I) CREATE BOOK :
    POST : http://localhost:8082/api/books
    HEADERS : Content-Type / application/json
              Authorization / Bearer TOKEN...
    BODY :
            {
                "title": "Introduction to Java Programming",
                "author": "John Smith",
                "isbn": "978-1234567890",
                "genre": "Programming",
                "totalQuantity": 5,
                "availableQuantity": 5
            }

  II) GET ALL BOOKS :
    GET : http://localhost:8082/api/books
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..

  III) GET BOOK BY ID :
    GET : http://localhost:8082/api/books/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..

  IV) UPDATE BOOK :
    PUT : http://localhost:8082/api/books/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..
    BODY :
            {
                "title": "Advanced Java Programming",
                "author": "John Smith",
                "isbn": "978-1234567890",
                "genre": "Programming",
                "totalQuantity": 10,
                "availableQuantity": 8
            }

  V) DELETE BOOK :
    DELETE : http://localhost:8082/api/books/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..


4] RESERVATIONS (Library Service)
  I) CREATE RESERVATION :
    POST : http://localhost:8082/api/reservations
    HEADERS : Content-Type / application/json
              Authorization / Bearer TOKEN... (STUDENT or USER token)
    BODY :
            {
                "studentId": 1,
                "bookId": 1,
                "reservationDate": "2025-09-20"
            }

  II) GET PENDING RESERVATIONS (LIBRARIAN/ADMIN only):
    GET : http://localhost:8082/api/reservations/pending
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token.. (LIBRARIAN or ADMIN token)

  III) APPROVE RESERVATION (LIBRARIAN/ADMIN only):
    PUT : http://localhost:8082/api/reservations/approve/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token.. (LIBRARIAN or ADMIN token)

  IV) REJECT RESERVATION (LIBRARIAN/ADMIN only):
    PUT : http://localhost:8082/api/reservations/reject/1?action=REJECT
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token.. (LIBRARIAN or ADMIN token)

  V) APPROVE AND CREATE LOAN (LIBRARIAN/ADMIN only):
    PUT : http://localhost:8082/api/reservations/approve-and-loan/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token.. (LIBRARIAN or ADMIN token)


5] LOANS (Library Service)
  I) CHECKOUT BOOK :
    POST : http://localhost:8082/api/loans/checkout
    HEADERS : Content-Type / application/json
              Authorization / Bearer TOKEN...
    BODY :
            {
                "studentId": 1,
                "bookId": 1,
                "checkOutDate": "2023-09-20",
                "dueDate": "2023-10-04"
            }

  II) RETURN BOOK :
    PUT : http://localhost:8082/api/loans/return/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..

  III) GET ACTIVE LOANS BY STUDENT :
    GET : http://localhost:8082/api/loans/active/student/1
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..

  IV) GET MY ACTIVE LOANS :
    GET : http://localhost:8082/api/loans/active/me
    HEADERS : Content-Type / application/json
              Authorization / Bearer Token..