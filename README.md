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

  
