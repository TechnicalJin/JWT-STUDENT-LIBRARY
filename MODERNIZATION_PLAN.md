# 🚀 JWT-STUDENT-LIBRARY Modernization & Enhancement Plan

## 📋 Table of Contents
1. [Current State Analysis](#current-state-analysis)
2. [Gap Analysis](#gap-analysis)
3. [Architecture Overview](#architecture-overview)
4. [Detailed Implementation Roadmap](#detailed-implementation-roadmap)
5. [Technology Stack Matrix](#technology-stack-matrix)

---

## 🔍 Current State Analysis

### ✅ What's Already Implemented

#### **Service Architecture (3 Microservices)**
1. **Authentication Service** (Port 8080)
   - JWT token generation & validation
   - Refresh token mechanism
   - User registration & login
   - Role-based access control (ADMIN, USER, STUDENT, LIBRARIAN)
   - Spring Security configured
   - MySQL database: `stu_jwt`

2. **Student Management Service** (Port 8081)
   - Student CRUD operations
   - Student authentication
   - JWT validation
   - Spring Retry configured
   - MySQL database: `student_management`

3. **Library Management Service** (Port 8082)
   - Book management
   - Loan management (checkout/return)
   - Reservation system
   - Inter-service communication via RestTemplate
   - Student verification through Management Service
   - MySQL database: `library`

#### **Current Technologies**
- ✅ Java 17
- ✅ Spring Boot 3.4.4
- ✅ Spring Security
- ✅ JWT Authentication (io.jsonwebtoken:jjwt 0.11.5)
- ✅ MySQL (3 separate databases)
- ✅ Spring Data JPA
- ✅ Lombok
- ✅ MapStruct (for DTOs)
- ✅ Spring AOP
- ✅ Spring Retry
- ✅ Swagger Annotations (management service)
- ✅ Validation API

#### **Inter-Service Communication**
- ✅ RestTemplate-based synchronous calls
- ✅ JWT token propagation between services
- ✅ Custom `InterServiceClient` for auth header injection

#### **Business Logic Implemented**
- ✅ Book checkout/return flow
- ✅ Reservation system (PENDING → APPROVED → COMPLETED/CANCELLED)
- ✅ Loan limit validation (max 5 active loans per student)
- ✅ Overdue loan detection (scheduled task)
- ✅ Book availability tracking
- ✅ Security: Students can only create reservations for themselves

---

## ❌ Gap Analysis - What's Missing

### **1. Service Discovery & API Gateway**
- ❌ No Eureka Server
- ❌ No Spring Cloud Gateway
- ❌ Hardcoded service URLs in properties
- ❌ No load balancing

### **2. Centralized Configuration**
- ❌ No Spring Cloud Config Server
- ❌ Duplicate JWT secrets across services
- ❌ Configuration scattered across 3 services

### **3. Event-Driven Architecture**
- ❌ No Kafka setup
- ❌ No domain events
- ❌ No async communication
- ❌ No event sourcing

### **4. Payment Integration**
- ❌ No Razorpay integration
- ❌ No fine calculation system
- ❌ No payment tracking

### **5. Caching & Session Management**
- ❌ No Redis
- ❌ No caching strategy
- ❌ No rate limiting
- ❌ No session management

### **6. Search Capability**
- ❌ No Elasticsearch
- ❌ Basic SQL queries only

### **7. Resilience**
- ❌ No Resilience4j
- ❌ No circuit breakers
- ❌ No rate limiters
- ❌ No bulkhead patterns
- ⚠️ Basic Spring Retry exists (only in management service)

### **8. Observability**
- ❌ No Micrometer
- ❌ No Prometheus
- ❌ No Grafana
- ❌ No distributed tracing
- ❌ Only basic logging

### **9. DevOps**
- ❌ No Docker/Docker Compose
- ❌ No CI/CD pipelines
- ❌ No GitHub Actions

### **10. Testing**
- ❌ No unit tests (only template test files)
- ❌ No integration tests
- ❌ No Testcontainers
- ❌ No k6 load tests
- ❌ No REST Assured

### **11. API Documentation**
- ❌ No Springdoc OpenAPI integration
- ❌ No Swagger UI
- ⚠️ Only basic Swagger annotations in management service

### **12. Notifications**
- ❌ No WebSocket implementation
- ❌ No notification service
- ⚠️ Commented out notification code exists in LoanService

### **13. OAuth2**
- ❌ No OAuth2 integration
- ❌ Only basic JWT

---

## 🏗️ Architecture Overview

### **Current Architecture**
```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Authentication │     │    Management    │     │     Library     │
│    Service      │     │     Service      │     │    Service      │
│   (Port 8080)   │     │   (Port 8081)    │     │   (Port 8082)   │
└────────┬────────┘     └─────────┬────────┘     └────────┬────────┘
         │                        │                       │
         │                        │      RestTemplate     │
         │                        │◄──────────────────────┤
         │                        │                       │
         ▼                        ▼                       ▼
    ┌─────────┐            ┌─────────┐             ┌─────────┐
    │  MySQL  │            │  MySQL  │             │  MySQL  │
    │ stu_jwt │            │ student │             │ library │
    └─────────┘            └─────────┘             └─────────┘
```

### **Target Architecture**
```
                        ┌──────────────────────┐
                        │   Spring Cloud       │
                        │      Gateway         │
                        │    (Port 8080)       │
                        └──────────┬───────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
         ┌──────────▼────┐  ┌──────▼──────┐  ┌───▼──────────┐
         │  Auth Service │  │   Student   │  │   Library    │
         │  (Port 8081)  │  │  Service    │  │   Service    │
         │               │  │ (Port 8082) │  │  (Port 8083) │
         └───────┬───────┘  └──────┬──────┘  └──────┬───────┘
                 │                 │                 │
                 │    ┌────────────▼─────────────┐   │
                 │    │   Service Discovery      │   │
                 └────►   (Eureka Server)        ◄───┘
                      │     (Port 8761)          │
                      └──────────────────────────┘
                                   │
        ┌──────────────────────────┼────────────────────────┐
        │                          │                        │
   ┌────▼─────┐             ┌──────▼──────┐         ┌──────▼──────┐
   │  Config  │             │    Kafka    │         │   Redis     │
   │  Server  │             │  Messaging  │         │   Cache     │
   │(Pt 8888) │             │  (Pt 9092)  │         │  (Pt 6379)  │
   └──────────┘             └──────┬──────┘         └─────────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
              ┌─────▼────┐   ┌─────▼────┐   ┌────▼─────┐
              │  MySQL   │   │Elastic-  │   │Prometheus│
              │ Database │   │ search   │   │ Grafana  │
              └──────────┘   └──────────┘   └──────────┘
```

---

## 🗺️ Detailed Implementation Roadmap

### **Phase 1: Infrastructure Foundation (Week 1-2)**

#### **1.1 Service Discovery & Gateway**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨🔨 Medium

**Steps:**
1. Create Eureka Server module
   ```
   /eureka-server
   ├── pom.xml
   ├── src/main/java/com/discovery/EurekaServerApplication.java
   └── src/main/resources/application.yml
   ```

2. Create API Gateway module
   ```
   /api-gateway
   ├── pom.xml
   ├── src/main/java/com/gateway/GatewayApplication.java
   ├── src/main/java/com/gateway/config/
   │   ├── GatewayConfig.java
   │   ├── SecurityConfig.java
   │   └── RateLimitConfig.java
   └── src/main/resources/application.yml
   ```

3. Update existing services to register with Eureka
   - Add `spring-cloud-starter-netflix-eureka-client` dependency
   - Configure `@EnableDiscoveryClient`
   - Update port numbers (Auth: 8081, Management: 8082, Library: 8083)

4. Configure Gateway routes
   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: auth-service
             uri: lb://AUTH-SERVICE
             predicates:
               - Path=/api/auth/**
           - id: student-service
             uri: lb://MANAGEMENT-SERVICE
             predicates:
               - Path=/api/students/**
           - id: library-service
             uri: lb://LIBRARY-SERVICE
             predicates:
               - Path=/api/books/**, /api/loans/**, /api/reservations/**
   ```

**Dependencies to add:**
```xml
<!-- All services -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

**Benefits:**
- Dynamic service discovery
- Load balancing
- No hardcoded URLs
- Centralized routing
- Easy scaling

---

#### **1.2 Centralized Configuration**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨 Low-Medium

**Steps:**
1. Create Config Server module
   ```
   /config-server
   ├── pom.xml
   ├── src/main/java/com/config/ConfigServerApplication.java
   └── src/main/resources/application.yml
   ```

2. Create Git repository for configurations
   ```
   /config-repo
   ├── authentication-service.yml
   ├── management-service.yml
   ├── library-service.yml
   ├── gateway-service.yml
   └── application.yml (common configs)
   ```

3. Centralize common properties:
   - JWT secret (currently duplicated)
   - Database credentials
   - Service URLs
   - CORS settings

4. Update all services to fetch config from Config Server

**Dependencies:**
```xml
<!-- Config Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

<!-- Config Clients (all services) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

**Benefits:**
- Single source of truth
- Environment-specific configs
- Dynamic refresh without restart
- Better security (externalized secrets)

---

### **Phase 2: Event-Driven Architecture with Kafka (Week 3-4)**

#### **2.1 Kafka Setup**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨🔨🔨 High

**Steps:**

1. **Create Docker Compose for Kafka**
   ```yaml
   # docker-compose.yml
   version: '3.8'
   services:
     zookeeper:
       image: confluentinc/cp-zookeeper:latest
       environment:
         ZOOKEEPER_CLIENT_PORT: 2181
       ports:
         - "2181:2181"

     kafka:
       image: confluentinc/cp-kafka:latest
       depends_on:
         - zookeeper
       ports:
         - "9092:9092"
       environment:
         KAFKA_BROKER_ID: 1
         KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
         KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
         KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
   ```

2. **Add Kafka dependencies to all services**
   ```xml
   <dependency>
       <groupId>org.springframework.kafka</groupId>
       <artifactId>spring-kafka</artifactId>
   </dependency>
   ```

3. **Create Event Models (in each service)**
   ```
   /common-events (new module)
   └── src/main/java/com/library/events/
       ├── StudentRegisteredEvent.java
       ├── BookIssuedEvent.java
       ├── BookReturnedEvent.java
       ├── BookOverdueEvent.java
       ├── FineAppliedEvent.java
       ├── FinePaymentEvent.java
       ├── BookReservationEvent.java
       └── StockReplenishedEvent.java
   ```

4. **Implement Event Publishers**
   ```java
   // In Library Service
   @Service
   public class LoanEventPublisher {
       @Autowired
       private KafkaTemplate<String, Object> kafkaTemplate;
       
       public void publishBookIssuedEvent(Loan loan) {
           BookIssuedEvent event = BookIssuedEvent.builder()
               .loanId(loan.getId())
               .bookId(loan.getBookId())
               .studentId(loan.getStudentId())
               .checkoutDate(loan.getCheckOutDate())
               .dueDate(loan.getDueDate())
               .timestamp(LocalDateTime.now())
               .build();
           
           kafkaTemplate.send("book-issued-events", event);
       }
   }
   ```

5. **Implement Event Consumers**
   ```java
   // In Management Service
   @Service
   public class StudentEventConsumer {
       @KafkaListener(topics = "student-registered-events", groupId = "management-group")
       public void handleStudentRegistered(StudentRegisteredEvent event) {
           // Update student stats, send welcome notification
           log.info("Student registered: {}", event.getStudentId());
       }
   }
   ```

**Domain Events to Implement:**

| Event | Producer Service | Consumer Services | Use Case |
|-------|-----------------|-------------------|----------|
| **StudentRegisteredEvent** | Management | Library, Notification | Initialize library card, welcome email |
| **BookIssuedEvent** | Library | Management, Notification | Update student record, notify student |
| **BookReturnedEvent** | Library | Management, Notification | Update stats, notify if late |
| **BookOverdueEvent** | Library (scheduler) | Management, Fine, Notification | Apply fine, send reminder |
| **FineAppliedEvent** | Library | Management, Notification | Update student balance |
| **FinePaymentEvent** | Payment | Library, Management | Clear fine, update records |
| **BookReservationEvent** | Library | Notification | Notify librarian for approval |
| **StockReplenishedEvent** | Library | Notification | Notify students with pending reservations |

**Kafka Topics:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    topics:
      student-events: student-registered-events
      loan-events: book-issued-events,book-returned-events
      overdue-events: book-overdue-events
      fine-events: fine-applied-events,fine-payment-events
      reservation-events: book-reservation-events
      stock-events: stock-replenished-events
```

**Benefits:**
- Loose coupling between services
- Async communication (better performance)
- Event sourcing for audit trails
- Real-time notifications
- Scalable architecture

---

### **Phase 3: Payment Integration - Razorpay (Week 5)**

#### **3.1 Create Payment Service**
**Priority:** 🟡 MEDIUM | **Effort:** 🔨🔨🔨 Medium

**Steps:**

1. **Create new Payment Service module**
   ```
   /payment-service
   ├── pom.xml
   ├── src/main/java/com/payment/
   │   ├── PaymentApplication.java
   │   ├── controller/
   │   │   ├── PaymentController.java
   │   │   └── WebhookController.java
   │   ├── service/
   │   │   ├── PaymentService.java
   │   │   ├── FineCalculationService.java
   │   │   └── RazorpayService.java
   │   ├── model/
   │   │   ├── Payment.java
   │   │   ├── Fine.java
   │   │   └── PaymentStatus.java
   │   └── dto/
   │       ├── PaymentRequest.java
   │       └── PaymentResponse.java
   ```

2. **Add Razorpay dependency**
   ```xml
   <dependency>
       <groupId>com.razorpay</groupId>
       <artifactId>razorpay-java</artifactId>
       <version>1.4.6</version>
   </dependency>
   ```

3. **Implement Fine Calculation**
   ```java
   @Service
   public class FineCalculationService {
       private static final double FINE_PER_DAY = 10.0; // Rs 10 per day
       
       public Fine calculateOverdueFine(Loan loan) {
           long daysOverdue = ChronoUnit.DAYS.between(
               loan.getDueDate(), 
               LocalDateTime.now()
           );
           
           double amount = daysOverdue * FINE_PER_DAY;
           
           return Fine.builder()
               .loanId(loan.getId())
               .studentId(loan.getStudentId())
               .daysOverdue(daysOverdue)
               .amount(amount)
               .status(FineStatus.PENDING)
               .build();
       }
   }
   ```

4. **Integrate Razorpay**
   ```java
   @Service
   public class RazorpayService {
       @Value("${razorpay.key.id}")
       private String keyId;
       
       @Value("${razorpay.key.secret}")
       private String keySecret;
       
       private RazorpayClient razorpayClient;
       
       @PostConstruct
       public void init() throws RazorpayException {
           this.razorpayClient = new RazorpayClient(keyId, keySecret);
       }
       
       public Order createOrder(PaymentRequest request) throws RazorpayException {
           JSONObject orderRequest = new JSONObject();
           orderRequest.put("amount", request.getAmount() * 100); // paise
           orderRequest.put("currency", "INR");
           orderRequest.put("receipt", "fine_" + request.getFineId());
           
           Order order = razorpayClient.orders.create(orderRequest);
           return order;
       }
       
       public boolean verifyPayment(String orderId, String paymentId, String signature) {
           String generatedSignature = HmacUtils.hmacSha256Hex(
               keySecret, 
               orderId + "|" + paymentId
           );
           return generatedSignature.equals(signature);
       }
   }
   ```

5. **Add Payment Webhook Handler**
   ```java
   @RestController
   @RequestMapping("/api/webhooks/razorpay")
   public class WebhookController {
       @Autowired
       private PaymentService paymentService;
       
       @PostMapping
       public ResponseEntity<Void> handleWebhook(
           @RequestBody String payload,
           @RequestHeader("X-Razorpay-Signature") String signature
       ) {
           if (paymentService.verifyWebhookSignature(payload, signature)) {
               paymentService.processPaymentUpdate(payload);
               return ResponseEntity.ok().build();
           }
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }
   }
   ```

**Use Cases to Implement:**
1. ✅ Fine payment for overdue books
2. ✅ Library membership fees
3. ✅ Lost book charges
4. ✅ Advance book reservations (premium)

**Payment Flow:**
```
Student → Payment Controller → Razorpay API → Create Order
    ↓
Razorpay Payment Page
    ↓
Webhook → Verify Signature → Update Payment Status → Publish FinePaymentEvent
    ↓
Kafka → Library Service → Clear Fine → Update Loan Status
```

**Database Schema:**
```sql
CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fine_id BIGINT,
    student_id BIGINT,
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    amount DECIMAL(10,2),
    status VARCHAR(50),
    payment_type VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE fine (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT,
    student_id BIGINT,
    amount DECIMAL(10,2),
    days_overdue INT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    paid_at TIMESTAMP
);
```

---

### **Phase 4: Caching & Session Management with Redis (Week 6)**

#### **4.1 Redis Integration**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨 Low-Medium

**Steps:**

1. **Add Redis to Docker Compose**
   ```yaml
   redis:
     image: redis:7-alpine
     ports:
       - "6379:6379"
     volumes:
       - redis-data:/data
     command: redis-server --appendonly yes
   ```

2. **Add Redis dependencies**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.session</groupId>
       <artifactId>spring-session-data-redis</artifactId>
   </dependency>
   ```

3. **Configure Redis**
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: # if set
       timeout: 2000
     cache:
       type: redis
       redis:
         time-to-live: 3600000 # 1 hour
   ```

4. **Implement Caching Strategy**

   **a) Book Cache (Library Service)**
   ```java
   @Service
   public class BookService {
       @Cacheable(value = "books", key = "#id")
       public BookDTO getBookById(Long id) {
           // Fetch from DB
       }
       
       @CachePut(value = "books", key = "#book.id")
       public BookDTO updateBook(Book book) {
           // Update DB
       }
       
       @CacheEvict(value = "books", key = "#id")
       public void deleteBook(Long id) {
           // Delete from DB
       }
       
       @Cacheable(value = "available-books")
       public List<BookDTO> getAvailableBooks() {
           // Frequently accessed
       }
   }
   ```

   **b) Student Cache (Management Service)**
   ```java
   @Cacheable(value = "students", key = "#studentId")
   public StudentDTO getStudentById(Long studentId) {
       // Cache student profiles
   }
   ```

   **c) JWT Token Blacklist (Auth Service)**
   ```java
   @Service
   public class TokenBlacklistService {
       @Autowired
       private RedisTemplate<String, String> redisTemplate;
       
       public void blacklistToken(String token, long expirationTime) {
           redisTemplate.opsForValue().set(
               "blacklist:" + token,
               "true",
               expirationTime,
               TimeUnit.MILLISECONDS
           );
       }
       
       public boolean isTokenBlacklisted(String token) {
           return Boolean.TRUE.equals(
               redisTemplate.hasKey("blacklist:" + token)
           );
       }
   }
   ```

5. **Rate Limiting (API Gateway)**
   ```java
   @Configuration
   public class RateLimitConfig {
       @Bean
       KeyResolver userKeyResolver() {
           return exchange -> Mono.just(
               exchange.getRequest()
                   .getHeaders()
                   .getFirst("X-User-Id")
           );
       }
   }
   ```

   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: library-service
             uri: lb://LIBRARY-SERVICE
             predicates:
               - Path=/api/books/**
             filters:
               - name: RequestRateLimiter
                 args:
                   redis-rate-limiter.replenishRate: 10  # requests/sec
                   redis-rate-limiter.burstCapacity: 20
   ```

**Cache Strategy:**

| Data | Cache | TTL | Eviction Strategy |
|------|-------|-----|-------------------|
| Book details | Yes | 1 hour | Update/Delete triggers eviction |
| Available books list | Yes | 5 mins | Scheduled eviction |
| Student profile | Yes | 30 mins | Update triggers eviction |
| Active loans | No | - | Real-time data |
| JWT tokens (blacklist) | Yes | Token expiry | TTL-based |

**Benefits:**
- 🚀 Faster response times (cache hits)
- 📉 Reduced database load
- 🔒 JWT token blacklisting
- 🛡️ Rate limiting per user/IP
- 💾 Session management

---

### **Phase 5: Search with Elasticsearch (Week 7)**

#### **5.1 Elasticsearch Integration**
**Priority:** 🟡 MEDIUM | **Effort:** 🔨🔨🔨 Medium

**Steps:**

1. **Add Elasticsearch to Docker Compose**
   ```yaml
   elasticsearch:
     image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
     environment:
       - discovery.type=single-node
       - xpack.security.enabled=false
     ports:
       - "9200:9200"
     volumes:
       - es-data:/usr/share/elasticsearch/data
   
   kibana:
     image: docker.elastic.co/kibana/kibana:8.11.0
     ports:
       - "5601:5601"
     depends_on:
       - elasticsearch
   ```

2. **Add Elasticsearch dependency**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
   </dependency>
   ```

3. **Create Searchable Book Entity**
   ```java
   @Document(indexName = "books")
   @Data
   public class BookSearchDocument {
       @Id
       private Long id;
       
       @Field(type = FieldType.Text, analyzer = "standard")
       private String title;
       
       @Field(type = FieldType.Text, analyzer = "standard")
       private String author;
       
       @Field(type = FieldType.Keyword)
       private String isbn;
       
       @Field(type = FieldType.Text)
       private String genre;
       
       @Field(type = FieldType.Integer)
       private int availableQuantity;
       
       @Field(type = FieldType.Keyword)
       private BookStatus status;
   }
   ```

4. **Implement Search Repository**
   ```java
   public interface BookSearchRepository extends ElasticsearchRepository<BookSearchDocument, Long> {
       List<BookSearchDocument> findByTitleContainingOrAuthorContaining(
           String title, String author
       );
       
       List<BookSearchDocument> findByGenre(String genre);
       
       List<BookSearchDocument> findByStatusAndAvailableQuantityGreaterThan(
           BookStatus status, int quantity
       );
   }
   ```

5. **Sync with MySQL (Event-driven)**
   ```java
   @Service
   public class BookSyncService {
       @Autowired
       private BookSearchRepository searchRepository;
       
       @KafkaListener(topics = "book-events", groupId = "search-sync")
       public void syncBookToElasticsearch(BookEvent event) {
           BookSearchDocument doc = mapToSearchDocument(event.getBook());
           searchRepository.save(doc);
       }
   }
   ```

6. **Advanced Search Controller**
   ```java
   @RestController
   @RequestMapping("/api/books/search")
   public class BookSearchController {
       @GetMapping
       public List<BookDTO> search(
           @RequestParam(required = false) String query,
           @RequestParam(required = false) String genre,
           @RequestParam(required = false) String author,
           @RequestParam(defaultValue = "true") boolean onlyAvailable
       ) {
           // Full-text search implementation
       }
   }
   ```

**Search Features:**
- ✅ Full-text search across title, author, genre
- ✅ Fuzzy matching (typo tolerance)
- ✅ Faceted search (filter by genre, availability)
- ✅ Autocomplete suggestions
- ✅ Pagination & sorting

---

### **Phase 6: Resilience with Resilience4j (Week 8)**

#### **6.1 Resilience4j Integration**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨 Low-Medium

**Steps:**

1. **Add Resilience4j dependencies**
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
   </dependency>
   <dependency>
       <groupId>io.github.resilience4j</groupId>
       <artifactId>resilience4j-spring-boot3</artifactId>
   </dependency>
   ```

2. **Configure Circuit Breaker**
   ```yaml
   resilience4j:
     circuitbreaker:
       instances:
         studentService:
           registerHealthIndicator: true
           slidingWindowSize: 10
           minimumNumberOfCalls: 5
           permittedNumberOfCallsInHalfOpenState: 3
           automaticTransitionFromOpenToHalfOpenEnabled: true
           waitDurationInOpenState: 5s
           failureRateThreshold: 50
           eventConsumerBufferSize: 10
           
     retry:
       instances:
         studentService:
           maxAttempts: 3
           waitDuration: 1s
           enableExponentialBackoff: true
           exponentialBackoffMultiplier: 2
           
     ratelimiter:
       instances:
         bookCheckout:
           limitForPeriod: 10
           limitRefreshPeriod: 1s
           timeoutDuration: 500ms
   ```

3. **Apply to Inter-Service Calls**
   ```java
   @Service
   public class StudentServiceClient {
       @Autowired
       private CircuitBreakerFactory circuitBreakerFactory;
       
       @CircuitBreaker(name = "studentService", fallbackMethod = "getStudentFallback")
       @Retry(name = "studentService")
       public StudentDTO getStudentById(Long studentId) {
           // Call student service
       }
       
       private StudentDTO getStudentFallback(Long studentId, Exception ex) {
           log.warn("Fallback for student {}: {}", studentId, ex.getMessage());
           // Return cached data or default response
           return StudentDTO.builder()
               .id(studentId)
               .firstname("Unknown")
               .build();
       }
   }
   ```

4. **Rate Limiting for Loan Checkout**
   ```java
   @RateLimiter(name = "bookCheckout", fallbackMethod = "checkoutRateLimitFallback")
   public LoanDTO checkOutBook(LoanRequestDTO requestDTO, String username) {
       // Checkout logic
   }
   
   private LoanDTO checkoutRateLimitFallback(LoanRequestDTO requestDTO, 
                                             String username, 
                                             RequestNotPermitted ex) {
       throw new RateLimitExceededException(
           "Too many checkout requests. Please try again later."
       );
   }
   ```

5. **Bulkhead Pattern (Thread Pool Isolation)**
   ```yaml
   resilience4j:
     bulkhead:
       instances:
         externalApi:
           maxConcurrentCalls: 10
           maxWaitDuration: 500ms
   ```

**Patterns to Implement:**

| Pattern | Service | Use Case |
|---------|---------|----------|
| **Circuit Breaker** | Library → Management | Student verification calls |
| **Retry** | Payment → Razorpay | Transient payment failures |
| **Rate Limiter** | Library | Book checkout operations |
| **Bulkhead** | All | Isolate thread pools |
| **Time Limiter** | Search | Elasticsearch queries |

---

### **Phase 7: Observability (Week 9-10)**

#### **7.1 Monitoring Stack**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨🔨🔨 High

**Steps:**

1. **Add Micrometer & Prometheus**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   <dependency>
       <groupId>io.micrometer</groupId>
       <artifactId>micrometer-registry-prometheus</artifactId>
   </dependency>
   ```

2. **Add Prometheus to Docker Compose**
   ```yaml
   prometheus:
     image: prom/prometheus:latest
     ports:
       - "9090:9090"
     volumes:
       - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
       - prometheus-data:/prometheus
     command:
       - '--config.file=/etc/prometheus/prometheus.yml'
   
   grafana:
     image: grafana/grafana:latest
     ports:
       - "3000:3000"
     environment:
       - GF_SECURITY_ADMIN_PASSWORD=admin
     volumes:
       - grafana-data:/var/lib/grafana
       - ./grafana/dashboards:/etc/grafana/provisioning/dashboards
     depends_on:
       - prometheus
   ```

3. **Configure Prometheus (prometheus.yml)**
   ```yaml
   global:
     scrape_interval: 15s

   scrape_configs:
     - job_name: 'spring-services'
       metrics_path: '/actuator/prometheus'
       static_configs:
         - targets:
           - 'host.docker.internal:8081'  # Auth Service
           - 'host.docker.internal:8082'  # Management Service
           - 'host.docker.internal:8083'  # Library Service
   ```

4. **Expose Actuator Endpoints**
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: health,info,metrics,prometheus
     metrics:
       export:
         prometheus:
           enabled: true
       distribution:
         percentiles-histogram:
           http.server.requests: true
   ```

5. **Custom Metrics**
   ```java
   @Service
   public class LoanService {
       private final MeterRegistry meterRegistry;
       private final Counter checkoutCounter;
       private final Timer checkoutTimer;
       
       public LoanService(MeterRegistry meterRegistry) {
           this.meterRegistry = meterRegistry;
           this.checkoutCounter = Counter.builder("library.book.checkout")
               .description("Number of book checkouts")
               .tag("type", "loan")
               .register(meterRegistry);
           
           this.checkoutTimer = Timer.builder("library.checkout.duration")
               .description("Time taken to checkout a book")
               .register(meterRegistry);
       }
       
       public LoanDTO checkOutBook(LoanRequestDTO requestDTO) {
           return checkoutTimer.record(() -> {
               checkoutCounter.increment();
               // Checkout logic
           });
       }
   }
   ```

6. **Distributed Tracing (Optional - Zipkin)**
   ```xml
   <dependency>
       <groupId>io.micrometer</groupId>
       <artifactId>micrometer-tracing-bridge-brave</artifactId>
   </dependency>
   <dependency>
       <groupId>io.zipkin.reporter2</groupId>
       <artifactId>zipkin-reporter-brave</artifactId>
   </dependency>
   ```

**Grafana Dashboards to Create:**
- Service health & uptime
- Request rate & latency
- Error rates
- Active loans
- Overdue books
- Payment success rate
- Cache hit ratio
- Database query performance

---

### **Phase 8: DevOps - Docker & CI/CD (Week 11)**

#### **8.1 Dockerization**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨🔨 Medium

**Steps:**

1. **Create Dockerfile for each service**
   ```dockerfile
   # authentication/Dockerfile
   FROM eclipse-temurin:17-jdk-alpine AS build
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN ./mvnw clean package -DskipTests

   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   COPY --from=build /app/target/*.jar app.jar
   EXPOSE 8081
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Master Docker Compose**
   ```yaml
   version: '3.8'

   services:
     # Infrastructure
     mysql:
       image: mysql:8.0
       environment:
         MYSQL_ROOT_PASSWORD: rootpass
       volumes:
         - mysql-data:/var/lib/mysql
         - ./init-scripts:/docker-entrypoint-initdb.d
       ports:
         - "3306:3306"

     redis:
       image: redis:7-alpine
       ports:
         - "6379:6379"

     zookeeper:
       image: confluentinc/cp-zookeeper:latest
       environment:
         ZOOKEEPER_CLIENT_PORT: 2181

     kafka:
       image: confluentinc/cp-kafka:latest
       depends_on:
         - zookeeper
       ports:
         - "9092:9092"

     elasticsearch:
       image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
       environment:
         - discovery.type=single-node
       ports:
         - "9200:9200"

     # Spring Cloud Services
     eureka-server:
       build: ./eureka-server
       ports:
         - "8761:8761"

     config-server:
       build: ./config-server
       ports:
         - "8888:8888"
       depends_on:
         - eureka-server

     api-gateway:
       build: ./api-gateway
       ports:
         - "8080:8080"
       depends_on:
         - eureka-server
         - config-server

     # Business Services
     auth-service:
       build: ./authentication
       ports:
         - "8081:8081"
       depends_on:
         - mysql
         - redis
         - eureka-server
       environment:
         SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/stu_jwt
         SPRING_REDIS_HOST: redis
         EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka

     management-service:
       build: ./management
       ports:
         - "8082:8082"
       depends_on:
         - mysql
         - kafka
         - eureka-server

     library-service:
       build: ./library
       ports:
         - "8083:8083"
       depends_on:
         - mysql
         - kafka
         - redis
         - elasticsearch
         - eureka-server

     payment-service:
       build: ./payment-service
       ports:
         - "8084:8084"
       depends_on:
         - mysql
         - kafka
         - eureka-server

     # Monitoring
     prometheus:
       image: prom/prometheus:latest
       ports:
         - "9090:9090"
       volumes:
         - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml

     grafana:
       image: grafana/grafana:latest
       ports:
         - "3000:3000"

   volumes:
     mysql-data:
     redis-data:
     es-data:
     prometheus-data:
     grafana-data:
   ```

3. **Multi-stage builds for optimization**
4. **Environment-specific compose files** (dev, prod)

---

#### **8.2 CI/CD with GitHub Actions**
**Priority:** 🟡 MEDIUM | **Effort:** 🔨🔨🔨 Medium

**Create `.github/workflows/ci-cd.yml`**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ master, develop ]
  pull_request:
    branches: [ master ]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [authentication, management, library, payment-service]
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Tests
        working-directory: ./${{ matrix.service }}
        run: ./mvnw test
      
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./${{ matrix.service }}/target/site/jacoco/jacoco.xml

  build:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker Images
        run: docker-compose build
      
      - name: Push to Registry
        if: github.ref == 'refs/heads/master'
        run: |
          echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
          docker-compose push

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/master'
    
    steps:
      - name: Deploy to Production
        run: |
          # SSH to server and docker-compose up
```

---

### **Phase 9: Testing (Week 12)**

#### **9.1 Comprehensive Testing Strategy**
**Priority:** 🔴 HIGH | **Effort:** 🔨🔨🔨🔨 High

**Steps:**

1. **Unit Tests with JUnit 5 & Mockito**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
   </dependency>
   ```

   ```java
   @ExtendWith(MockitoExtension.class)
   class LoanServiceTest {
       @Mock
       private LoanRepository loanRepository;
       
       @Mock
       private BookService bookService;
       
       @InjectMocks
       private LoanService loanService;
       
       @Test
       void testCheckOutBook_Success() {
           // Given
           LoanRequestDTO request = new LoanRequestDTO(1L, 1L);
           Book book = Book.builder()
               .id(1L)
               .availableQuantity(5)
               .build();
           
           when(bookService.getBookEntity(1L)).thenReturn(book);
           when(loanRepository.countByStudentIdAndStatus(1L, LoanStatus.ACTIVE))
               .thenReturn(2L);
           
           // When
           LoanDTO result = loanService.checkOutBook(request, "librarian");
           
           // Then
           assertNotNull(result);
           verify(loanRepository).save(any(Loan.class));
       }
   }
   ```

2. **Integration Tests with Testcontainers**
   ```xml
   <dependency>
       <groupId>org.testcontainers</groupId>
       <artifactId>testcontainers</artifactId>
       <version>1.19.3</version>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.testcontainers</groupId>
       <artifactId>mysql</artifactId>
       <version>1.19.3</version>
       <scope>test</scope>
   </dependency>
   ```

   ```java
   @SpringBootTest
   @Testcontainers
   class LibraryServiceIntegrationTest {
       @Container
       static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
           .withDatabaseName("test_library")
           .withUsername("test")
           .withPassword("test");
       
       @DynamicPropertySource
       static void properties(DynamicPropertyRegistry registry) {
           registry.add("spring.datasource.url", mysql::getJdbcUrl);
           registry.add("spring.datasource.username", mysql::getUsername);
           registry.add("spring.datasource.password", mysql::getPassword);
       }
       
       @Test
       void testFullCheckoutFlow() {
           // Test with real DB
       }
   }
   ```

3. **API Tests with REST Assured**
   ```xml
   <dependency>
       <groupId>io.rest-assured</groupId>
       <artifactId>rest-assured</artifactId>
       <version>5.3.2</version>
       <scope>test</scope>
   </dependency>
   ```

   ```java
   @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
   class BookControllerTest {
       @LocalServerPort
       private int port;
       
       @Test
       void testGetAllBooks() {
           given()
               .port(port)
               .header("Authorization", "Bearer " + getToken())
           .when()
               .get("/api/books")
           .then()
               .statusCode(200)
               .body("size()", greaterThan(0));
       }
   }
   ```

4. **Load Testing with k6**
   ```javascript
   // scripts/load-test.js
   import http from 'k6/http';
   import { check, sleep } from 'k6';

   export let options = {
       stages: [
           { duration: '2m', target: 100 }, // Ramp up
           { duration: '5m', target: 100 }, // Stay at 100 users
           { duration: '2m', target: 0 },   // Ramp down
       ],
   };

   export default function () {
       let res = http.get('http://localhost:8080/api/books');
       check(res, {
           'status is 200': (r) => r.status === 200,
           'response time < 500ms': (r) => r.timings.duration < 500,
       });
       sleep(1);
   }
   ```

   Run: `k6 run scripts/load-test.js`

**Test Coverage Goals:**
- Unit tests: 80%+
- Integration tests: Critical flows
- API tests: All endpoints
- Load tests: 1000+ concurrent users

---

### **Phase 10: Documentation & WebSocket Notifications (Week 13)**

#### **10.1 API Documentation with Springdoc OpenAPI**
**Priority:** 🟡 MEDIUM | **Effort:** 🔨 Low

**Steps:**

1. **Add Springdoc dependency**
   ```xml
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
       <version>2.3.0</version>
   </dependency>
   ```

2. **Configure OpenAPI**
   ```java
   @Configuration
   public class OpenAPIConfig {
       @Bean
       public OpenAPI customOpenAPI() {
           return new OpenAPI()
               .info(new Info()
                   .title("Library Management API")
                   .version("1.0")
                   .description("API for managing library books, loans, and reservations")
                   .contact(new Contact()
                       .name("Your Name")
                       .email("your.email@example.com")))
               .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
               .components(new Components()
                   .addSecuritySchemes("bearer-jwt",
                       new SecurityScheme()
                           .type(SecurityScheme.Type.HTTP)
                           .scheme("bearer")
                           .bearerFormat("JWT")));
       }
   }
   ```

3. **Document Controllers**
   ```java
   @RestController
   @RequestMapping("/api/books")
   @Tag(name = "Book Management", description = "APIs for managing library books")
   public class BookController {
       
       @Operation(summary = "Get all books", description = "Retrieve all books in the library")
       @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Success"),
           @ApiResponse(responseCode = "401", description = "Unauthorized")
       })
       @GetMapping
       public List<BookDTO> getAllBooks() {
           // ...
       }
   }
   ```

Access Swagger UI: `http://localhost:8080/swagger-ui.html`

---

#### **10.2 WebSocket Notifications**
**Priority:** 🟡 MEDIUM | **Effort:** 🔨🔨🔨 Medium

**Steps:**

1. **Add WebSocket dependency**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-websocket</artifactId>
   </dependency>
   ```

2. **Configure WebSocket**
   ```java
   @Configuration
   @EnableWebSocketMessageBroker
   public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
       @Override
       public void configureMessageBroker(MessageBrokerRegistry config) {
           config.enableSimpleBroker("/topic", "/queue");
           config.setApplicationDestinationPrefixes("/app");
       }
       
       @Override
       public void registerStompEndpoints(StompEndpointRegistry registry) {
           registry.addEndpoint("/ws")
               .setAllowedOrigins("*")
               .withSockJS();
       }
   }
   ```

3. **Create Notification Service**
   ```java
   @Service
   public class NotificationService {
       @Autowired
       private SimpMessagingTemplate messagingTemplate;
       
       @KafkaListener(topics = "book-overdue-events", groupId = "notification")
       public void handleOverdueNotification(BookOverdueEvent event) {
           Notification notification = Notification.builder()
               .studentId(event.getStudentId())
               .message("Your book is overdue: " + event.getBookTitle())
               .type(NotificationType.OVERDUE)
               .timestamp(LocalDateTime.now())
               .build();
           
           messagingTemplate.convertAndSend(
               "/topic/notifications/" + event.getStudentId(),
               notification
           );
       }
   }
   ```

4. **Frontend (JavaScript example)**
   ```javascript
   const socket = new SockJS('http://localhost:8083/ws');
   const stompClient = Stomp.over(socket);
   
   stompClient.connect({}, function(frame) {
       stompClient.subscribe('/topic/notifications/' + studentId, function(notification) {
           const data = JSON.parse(notification.body);
           showNotification(data.message);
       });
   });
   ```

**Notification Types:**
- Book overdue reminder
- Reservation approved
- Book available (after return)
- Fine payment reminder
- New books added

---

## 📊 Technology Stack Matrix

| Category | Technology | Version | Purpose | Status |
|----------|-----------|---------|---------|--------|
| **Core Framework** | Spring Boot | 3.4.4 | Base framework | ✅ Implemented |
| **Language** | Java | 17 | Programming language | ✅ Implemented |
| **Service Discovery** | Eureka Server | Latest | Service registry | ❌ To Implement |
| **API Gateway** | Spring Cloud Gateway | Latest | Routing, filtering | ❌ To Implement |
| **Configuration** | Spring Cloud Config | Latest | Centralized config | ❌ To Implement |
| **Messaging** | Apache Kafka | Latest (Docker) | Event streaming | ❌ To Implement |
| **Databases** | MySQL | 8.0 | Primary database | ✅ Implemented |
| **Cache** | Redis | 7-alpine | Caching, sessions | ❌ To Implement |
| **Search** | Elasticsearch | 8.11.0 | Full-text search | ❌ To Implement |
| **Payment** | Razorpay SDK | 1.4.6 | Payment gateway | ❌ To Implement |
| **Security** | Spring Security | 6.x | Authentication | ✅ Implemented |
| **Security** | JWT | 0.11.5 | Token-based auth | ✅ Implemented |
| **Security** | OAuth2 | Latest | Social login | ❌ To Implement |
| **Resilience** | Resilience4j | Latest | Circuit breaker, retry | ❌ To Implement |
| **Monitoring** | Micrometer | Latest | Metrics collection | ❌ To Implement |
| **Monitoring** | Prometheus | Latest | Metrics storage | ❌ To Implement |
| **Visualization** | Grafana | Latest | Dashboards | ❌ To Implement |
| **Tracing** | Zipkin | Latest (Optional) | Distributed tracing | ❌ Optional |
| **Container** | Docker | Latest | Containerization | ❌ To Implement |
| **Orchestration** | Docker Compose | Latest | Multi-container | ❌ To Implement |
| **CI/CD** | GitHub Actions | - | Automation | ❌ To Implement |
| **Testing** | JUnit 5 | 5.10.x | Unit testing | ⚠️ Basic setup |
| **Testing** | Mockito | Latest | Mocking | ⚠️ Basic setup |
| **Testing** | Testcontainers | 1.19.3 | Integration tests | ❌ To Implement |
| **Testing** | k6 | Latest | Load testing | ❌ To Implement |
| **Testing** | REST Assured | 5.3.2 | API testing | ❌ To Implement |
| **Documentation** | Springdoc OpenAPI | 2.3.0 | API docs | ⚠️ Partial (management) |
| **Documentation** | Swagger UI | Bundled | Interactive API docs | ❌ To Implement |
| **WebSocket** | Spring WebSocket | Latest | Real-time notifications | ❌ To Implement |
| **Mapping** | MapStruct | 1.5.x | DTO mapping | ✅ Implemented |
| **Utilities** | Lombok | Latest | Boilerplate reduction | ✅ Implemented |

---

## 📅 Implementation Timeline (13 Weeks)

| Week | Phase | Priority | Key Deliverables |
|------|-------|----------|------------------|
| 1-2 | Infrastructure Foundation | 🔴 HIGH | Eureka, Gateway, Config Server |
| 3-4 | Event-Driven Architecture | 🔴 HIGH | Kafka, Domain Events |
| 5 | Payment Integration | 🟡 MEDIUM | Razorpay, Fines |
| 6 | Caching | 🔴 HIGH | Redis, Rate Limiting |
| 7 | Search | 🟡 MEDIUM | Elasticsearch |
| 8 | Resilience | 🔴 HIGH | Resilience4j patterns |
| 9-10 | Observability | 🔴 HIGH | Prometheus, Grafana |
| 11 | DevOps | 🔴 HIGH | Docker, CI/CD |
| 12 | Testing | 🔴 HIGH | All test types |
| 13 | Documentation & Notifications | 🟡 MEDIUM | OpenAPI, WebSocket |

---

## 🚦 Getting Started - Quick Start Guide

### **Step 1: Install Prerequisites**
```bash
# Install Docker Desktop
# Download from: https://www.docker.com/products/docker-desktop/

# Verify installations
docker --version
docker-compose --version
java -version  # Should be 17
mvn --version
```

### **Step 2: Start Infrastructure**
```bash
# Navigate to project root
cd JWT-STUDENT-LIBRARY

# Start infrastructure services
docker-compose up -d mysql redis kafka zookeeper elasticsearch

# Verify services
docker ps
```

### **Step 3: Create Spring Cloud Services**
```bash
# Create Eureka Server
# Create Config Server
# Create API Gateway
# (Follow Phase 1 steps)
```

### **Step 4: Update Existing Services**
```bash
# Add Eureka client to all services
# Update port numbers
# Configure service discovery
```

### **Step 5: Run Services**
```bash
# Start in order:
1. Config Server (8888)
2. Eureka Server (8761)
3. Auth Service (8081)
4. Management Service (8082)
5. Library Service (8083)
6. API Gateway (8080)
```

---

## 📈 Expected Benefits

### **Performance**
- ⚡ 70% faster response times (Redis caching)
- 🚀 Handle 10x more concurrent users
- 📉 90% reduction in database load

### **Reliability**
- 🛡️ 99.9% uptime (circuit breakers)
- 🔄 Graceful degradation
- 🔁 Automatic retries

### **Scalability**
- 📊 Horizontal scaling with load balancing
- 🔀 Service instances can scale independently
- 💪 Handle traffic spikes

### **Developer Experience**
- 🎯 Centralized configuration
- 📝 Auto-generated API docs
- 🧪 Comprehensive test coverage
- 🐳 Easy local development with Docker

### **Business Value**
- 💰 Payment integration (revenue stream)
- 📊 Real-time analytics
- 🔔 Instant notifications
- 🔍 Better search experience

---

## 🎯 Success Criteria

- [ ] All services registered with Eureka
- [ ] API Gateway routing all traffic
- [ ] Kafka events flowing between services
- [ ] Redis cache hit ratio > 70%
- [ ] Payment integration working end-to-end
- [ ] Circuit breakers tested and working
- [ ] Prometheus collecting metrics
- [ ] Grafana dashboards created
- [ ] Docker Compose running all services
- [ ] CI/CD pipeline deploying successfully
- [ ] Test coverage > 80%
- [ ] Swagger UI accessible for all services
- [ ] WebSocket notifications working

---

## 📚 Learning Resources

### **Spring Cloud**
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)

### **Kafka**
- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Kafka Quickstart](https://kafka.apache.org/quickstart)

### **Resilience4j**
- [Resilience4j Docs](https://resilience4j.readme.io/)

### **Observability**
- [Micrometer](https://micrometer.io/)
- [Prometheus Getting Started](https://prometheus.io/docs/introduction/overview/)

---

## 🤝 Contributing Guidelines

1. Follow existing code structure
2. Write tests for new features
3. Update documentation
4. Create feature branches
5. Submit pull requests

---

## 📞 Next Steps

1. ✅ Review this plan
2. ✅ Set up development environment
3. ✅ Start with Phase 1 (Infrastructure Foundation)
4. ✅ Create project branches
5. ✅ Begin implementation

---

## 💻 CODE GENERATION STEPS

### **PHASE 1: INFRASTRUCTURE FOUNDATION - CODE GENERATION**

---

#### **Step 1.1: Create Eureka Server**

**1. Create Module Structure**
```bash
mkdir -p eureka-server/src/main/java/com/discovery
mkdir -p eureka-server/src/main/resources
```

**2. Create `eureka-server/pom.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
    
    <groupId>com.library</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0.0</version>
    <name>Eureka Server</name>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**3. Create `eureka-server/src/main/java/com/discovery/EurekaServerApplication.java`**
```java
package com.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

**4. Create `eureka-server/src/main/resources/application.yml`**
```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 4000

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

**5. Run Eureka Server**
```bash
cd eureka-server
mvn clean install
mvn spring-boot:run
```

**Verify:** Open `http://localhost:8761` - You should see Eureka dashboard

---

#### **Step 1.2: Create Config Server**

**1. Create Module Structure**
```bash
mkdir -p config-server/src/main/java/com/config
mkdir -p config-server/src/main/resources
```

**2. Create `config-server/pom.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
    
    <groupId>com.library</groupId>
    <artifactId>config-server</artifactId>
    <version>1.0.0</version>
    <name>Config Server</name>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**3. Create `config-server/src/main/java/com/config/ConfigServerApplication.java`**
```java
package com.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

**4. Create `config-server/src/main/resources/application.yml`**
```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/YOUR_USERNAME/library-config-repo
          # OR use native (file system) for local development
        native:
          search-locations: classpath:/config
  profiles:
    active: native

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh,busrefresh
```

**5. Create Config Repository Structure**
```bash
mkdir -p config-server/src/main/resources/config
cd config-server/src/main/resources/config
```

**6. Create `config-server/src/main/resources/config/application.yml` (Common Config)**
```yaml
# Common configuration for all services
jwt:
  secret: 5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**7. Create `config-server/src/main/resources/config/authentication-service.yml`**
```yaml
server:
  port: 8081

spring:
  application:
    name: authentication-service
  datasource:
    url: jdbc:mysql://localhost:3306/stu_jwt
    username: root
    password: yourpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

**8. Create `config-server/src/main/resources/config/management-service.yml`**
```yaml
server:
  port: 8082

spring:
  application:
    name: management-service
  datasource:
    url: jdbc:mysql://localhost:3306/student_management
    username: root
    password: yourpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: management-group
      auto-offset-reset: earliest

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

service:
  auth:
    url: lb://authentication-service
```

**9. Create `config-server/src/main/resources/config/library-service.yml`**
```yaml
server:
  port: 8083

spring:
  application:
    name: library-service
  datasource:
    url: jdbc:mysql://localhost:3306/library
    username: root
    password: yourpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  elasticsearch:
    uris: http://localhost:9200

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

service:
  management:
    url: lb://management-service

resilience4j:
  circuitbreaker:
    instances:
      studentService:
        register-health-indicator: true
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 5s
  retry:
    instances:
      studentService:
        max-attempts: 3
        wait-duration: 1s
```

---

#### **Step 1.3: Create API Gateway**

**1. Create Module Structure**
```bash
mkdir -p api-gateway/src/main/java/com/gateway/config
mkdir -p api-gateway/src/main/java/com/gateway/filter
mkdir -p api-gateway/src/main/resources
```

**2. Create `api-gateway/pom.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
    
    <groupId>com.library</groupId>
    <artifactId>api-gateway</artifactId>
    <version>1.0.0</version>
    <name>API Gateway</name>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**3. Create `api-gateway/src/main/java/com/gateway/GatewayApplication.java`**
```java
package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

**4. Create `api-gateway/src/main/java/com/gateway/config/GatewayConfig.java`**
```java
package com.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Authentication Service Routes
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://authentication-service"))
                
                // Student Management Service Routes
                .route("management-service", r -> r
                        .path("/api/students/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://management-service"))
                
                // Library Service Routes - Books
                .route("library-books", r -> r
                        .path("/api/books/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://library-service"))
                
                // Library Service Routes - Loans
                .route("library-loans", r -> r
                        .path("/api/loans/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://library-service"))
                
                // Library Service Routes - Reservations
                .route("library-reservations", r -> r
                        .path("/api/reservations/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://library-service"))
                
                .build();
    }
}
```

**5. Create `api-gateway/src/main/java/com/gateway/filter/AuthenticationFilter.java`**
```java
package com.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Paths that don't require authentication
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/eureka"
    );

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip authentication for open endpoints
            if (isOpenEndpoint(request.getURI().getPath())) {
                return chain.filter(exchange);
            }

            // Check if Authorization header exists
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validate JWT token
                Claims claims = validateToken(token);
                
                // Add user information to request headers
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Roles", claims.get("roles", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                
            } catch (Exception e) {
                return onError(exchange, "Invalid or expired token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isOpenEndpoint(String path) {
        return OPEN_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String errorResponse = String.format("{\"error\": \"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8))));
    }

    public static class Config {
        // Configuration properties if needed
    }
}
```

**6. Create `api-gateway/src/main/resources/application.yml`**
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "*"
            allowed-methods:
              - GET
              - POST
              - PUT
              - DELETE
              - PATCH
              - OPTIONS
            allowed-headers: "*"
            allow-credentials: false
      default-filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter:
              replenishRate: 100
              burstCapacity: 200
              requestedTokens: 1
  redis:
    host: localhost
    port: 6379

jwt:
  secret: 5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway
  endpoint:
    gateway:
      enabled: true

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty: DEBUG
```

---

#### **Step 1.4: Update Existing Services**

**For EACH service (authentication, management, library), perform these steps:**

**1. Update POM file - Add Dependencies**
```xml
<!-- Add to existing pom.xml -->
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Config Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    
    <!-- Spring Boot Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>

<!-- Add dependency management -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**2. Update Main Application Class**
```java
// Example for AuthenticationApplication.java
package com.jwt.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // Add this annotation
public class AuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
```

**3. Create `bootstrap.yml` in `src/main/resources` (BEFORE application.properties)**
```yaml
# authentication/src/main/resources/bootstrap.yml
spring:
  application:
    name: authentication-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: false
```

**4. Update `application.properties` to `application.yml`**
```yaml
# Keep service-specific properties only
# Remove common configs (they're now in Config Server)
server:
  port: 8081

# Minimal config - rest comes from Config Server
```

**5. Test Service Registration**
```bash
# Start services in order:
# 1. Config Server
cd config-server
mvn spring-boot:run

# 2. Eureka Server  
cd eureka-server
mvn spring-boot:run

# 3. Your service (e.g., authentication)
cd authentication
mvn spring-boot:run

# Check Eureka dashboard: http://localhost:8761
# You should see your service registered
```

---

### **PHASE 2: EVENT-DRIVEN ARCHITECTURE - CODE GENERATION**

#### **Step 2.1: Setup Kafka with Docker**

**1. Create `docker-compose.yml` in project root**
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: library-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - library-network

  kafka:
    image: confluentinc/cp-kafka:latest
    container_name: library-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
    networks:
      - library-network

  mysql:
    image: mysql:8.0
    container_name: library-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpass
      MYSQL_DATABASE: library
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - library-network

  redis:
    image: redis:7-alpine
    container_name: library-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - library-network

networks:
  library-network:
    driver: bridge

volumes:
  mysql-data:
  redis-data:
```

**2. Start Infrastructure**
```bash
docker-compose up -d
```

---

#### **Step 2.2: Create Common Events Module**

**1. Create Module Structure**
```bash
mkdir -p common-events/src/main/java/com/library/events
mkdir -p common-events/src/main/java/com/library/events/model
```

**2. Create `common-events/pom.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
    
    <groupId>com.library</groupId>
    <artifactId>common-events</artifactId>
    <version>1.0.0</version>
    <name>Common Events</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
    </dependencies>
</project>
```

**3. Create Event Models**

**File: `common-events/src/main/java/com/library/events/BookIssuedEvent.java`**
```java
package com.library.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssuedEvent {
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private Long studentId;
    private String studentName;
    private LocalDateTime checkoutDate;
    private LocalDateTime dueDate;
    private LocalDateTime timestamp;
    private String issuedBy;
}
```

**File: `common-events/src/main/java/com/library/events/BookReturnedEvent.java`**
```java
package com.library.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReturnedEvent {
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private Long studentId;
    private LocalDateTime returnDate;
    private LocalDateTime dueDate;
    private boolean wasOverdue;
    private Long daysOverdue;
    private LocalDateTime timestamp;
}
```

**File: `common-events/src/main/java/com/library/events/BookOverdueEvent.java`**
```java
package com.library.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookOverdueEvent {
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private Long studentId;
    private String studentEmail;
    private LocalDateTime dueDate;
    private Long daysOverdue;
    private Double fineAmount;
    private LocalDateTime timestamp;
}
```

**File: `common-events/src/main/java/com/library/events/StudentRegisteredEvent.java`**
```java
package com.library.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisteredEvent {
    private Long studentId;
    private String firstname;
    private String lastname;
    private String email;
    private String enrollmentNumber;
    private LocalDateTime registrationDate;
    private LocalDateTime timestamp;
}
```

**File: `common-events/src/main/java/com/library/events/FineAppliedEvent.java`**
```java
package com.library.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineAppliedEvent {
    private Long fineId;
    private Long loanId;
    private Long studentId;
    private Double amount;
    private Long daysOverdue;
    private String reason;
    private LocalDateTime appliedDate;
    private LocalDateTime timestamp;
}
```

**4. Install Common Events Module**
```bash
cd common-events
mvn clean install
```

---

#### **Step 2.3: Integrate Kafka in Library Service**

**1. Add Dependencies to `library/pom.xml`**
```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- Common Events -->
    <dependency>
        <groupId>com.library</groupId>
        <artifactId>common-events</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**2. Create Kafka Configuration**

**File: `library/src/main/java/com/management/library/config/KafkaProducerConfig.java`**
```java
package com.management.library.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

**3. Create Event Publisher Service**

**File: `library/src/main/java/com/management/library/service/LoanEventPublisher.java`**
```java
package com.management.library.service;

import com.library.events.BookIssuedEvent;
import com.library.events.BookOverdueEvent;
import com.library.events.BookReturnedEvent;
import com.management.library.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String BOOK_ISSUED_TOPIC = "book-issued-events";
    private static final String BOOK_RETURNED_TOPIC = "book-returned-events";
    private static final String BOOK_OVERDUE_TOPIC = "book-overdue-events";

    public void publishBookIssuedEvent(Loan loan, String bookTitle, String studentName, String issuedBy) {
        BookIssuedEvent event = BookIssuedEvent.builder()
                .loanId(loan.getId())
                .bookId(loan.getBookId())
                .bookTitle(bookTitle)
                .studentId(loan.getStudentId())
                .studentName(studentName)
                .checkoutDate(loan.getCheckOutDate())
                .dueDate(loan.getDueDate())
                .issuedBy(issuedBy)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BOOK_ISSUED_TOPIC, event);
        log.info("Published BookIssuedEvent for loan: {}", loan.getId());
    }

    public void publishBookReturnedEvent(Loan loan, String bookTitle) {
        long daysOverdue = 0;
        boolean wasOverdue = false;
        
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
            wasOverdue = true;
        }

        BookReturnedEvent event = BookReturnedEvent.builder()
                .loanId(loan.getId())
                .bookId(loan.getBookId())
                .bookTitle(bookTitle)
                .studentId(loan.getStudentId())
                .returnDate(loan.getReturnDate())
                .dueDate(loan.getDueDate())
                .wasOverdue(wasOverdue)
                .daysOverdue(daysOverdue)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BOOK_RETURNED_TOPIC, event);
        log.info("Published BookReturnedEvent for loan: {}", loan.getId());
    }

    public void publishBookOverdueEvent(Loan loan, String bookTitle, String studentEmail, Double fineAmount) {
        long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());

        BookOverdueEvent event = BookOverdueEvent.builder()
                .loanId(loan.getId())
                .bookId(loan.getBookId())
                .bookTitle(bookTitle)
                .studentId(loan.getStudentId())
                .studentEmail(studentEmail)
                .dueDate(loan.getDueDate())
                .daysOverdue(daysOverdue)
                .fineAmount(fineAmount)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BOOK_OVERDUE_TOPIC, event);
        log.info("Published BookOverdueEvent for loan: {}", loan.getId());
    }
}
```

**4. Update LoanService to Publish Events**

**Modify `library/src/main/java/com/management/library/service/LoanService.java`**
```java
// Add to existing LoanService class
@Autowired
private LoanEventPublisher loanEventPublisher;

// In checkOutBook method, after saving loan:
public LoanDTO checkOutBook(LoanRequestDTO requestDTO, String username) {
    // ... existing validation code ...
    
    Loan savedLoan = loanRepository.save(loan);
    
    // PUBLISH EVENT
    loanEventPublisher.publishBookIssuedEvent(
        savedLoan,
        book.getTitle(),
        student.getFirstname() + " " + student.getLastname(),
        username
    );
    
    return loanMapper.toDto(savedLoan);
}

// In returnBook method, after updating loan:
public LoanDTO returnBook(Long id) {
    // ... existing code ...
    
    loan.setReturnDate(LocalDateTime.now());
    Loan updatedLoan = loanRepository.save(loan);
    
    // PUBLISH EVENT
    loanEventPublisher.publishBookReturnedEvent(updatedLoan, book.getTitle());
    
    return loanMapper.toDto(updatedLoan);
}
```

---

#### **Step 2.4: Create Kafka Consumer in Management Service**

**1. Add Dependencies to `management/pom.xml`**
```xml
<dependencies>
    <!-- Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- Common Events -->
    <dependency>
        <groupId>com.library</groupId>
        <artifactId>common-events</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**2. Create Kafka Consumer Configuration**

**File: `management/src/main/java/com/student/management/config/KafkaConsumerConfig.java`**
```java
package com.student.management.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "management-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.library.events.BookIssuedEvent");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

**3. Create Event Consumer**

**File: `management/src/main/java/com/student/management/service/LoanEventConsumer.java`**
```java
package com.student.management.service;

import com.library.events.BookIssuedEvent;
import com.library.events.BookOverdueEvent;
import com.library.events.BookReturnedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanEventConsumer {

    @KafkaListener(topics = "book-issued-events", groupId = "management-group")
    public void handleBookIssued(BookIssuedEvent event) {
        log.info("Received BookIssuedEvent: Student {} checked out book {}", 
                event.getStudentId(), event.getBookTitle());
        
        // TODO: Update student statistics
        // TODO: Send notification to student
        // TODO: Log activity
    }

    @KafkaListener(topics = "book-returned-events", groupId = "management-group")
    public void handleBookReturned(BookReturnedEvent event) {
        log.info("Received BookReturnedEvent: Student {} returned book {}", 
                event.getStudentId(), event.getBookTitle());
        
        if (event.isWasOverdue()) {
            log.warn("Book was returned {} days late", event.getDaysOverdue());
            // TODO: Send overdue notification
        }
        
        // TODO: Update student statistics
    }

    @KafkaListener(topics = "book-overdue-events", groupId = "management-group")
    public void handleBookOverdue(BookOverdueEvent event) {
        log.warn("Received BookOverdueEvent: Student {} has overdue book {}", 
                event.getStudentId(), event.getBookTitle());
        
        // TODO: Send reminder email
        // TODO: Update student status (flag as having overdue books)
        // TODO: Calculate and apply fine
    }
}
```

---

**Testing Kafka Integration:**
```bash
# 1. Start all services
docker-compose up -d
mvn spring-boot:run (in each service)

# 2. Checkout a book via API
curl -X POST http://localhost:8080/api/loans \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"bookId": 1, "studentId": 1}'

# 3. Check logs - you should see:
# Library Service: "Published BookIssuedEvent for loan: X"
# Management Service: "Received BookIssuedEvent: Student X checked out book Y"
```

---

This is a comprehensive start! Would you like me to continue with:
- **Phase 3: Payment Service (Razorpay)**
- **Phase 4: Redis Caching**
- **Phase 5: Elasticsearch Integration**
- **Phase 6: Resilience4j**
- **Or any other specific phase?**