# Microservices Architecture Overview

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT (Frontend)                             │
│                      (Next.js 14, Port 3000)                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │ HTTP/REST
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY                                  │
│                  (Spring Cloud Gateway, Port 8080)                  │
│                                                                     │
│  Routes:                                                            │
│  /api/auth/* ────► Auth Service (8081)                             │
│  /api/tasks/* ───► Task Service (8082)                             │
│  /api/users/* ───► User Service (8083)                             │
│  /eureka/* ──────► Service Discovery (8761)                        │
└─────┬──────────────┬──────────────────┬──────────────────────────────┘
      │ Discovery    │ Discovery        │ Discovery
      ▼ (Eureka)     ▼ (Eureka)         ▼ (Eureka)
┌─────────────┐  ┌─────────────┐  ┌──────────────┐
│ Auth Svc    │  │ Task Svc    │  │ User Svc     │
│ (8081)      │  │ (8082)      │  │ (8083)       │
│             │  │             │  │              │
│ - Register  │  │ - CRUD      │  │ - List Users │
│ - Login     │  │ - Filter    │  │ - Get User   │
│ - JWT Gen   │  │ - Paginate  │  │ - Admin Only │
│ - Auth Chk  │  │ - Sort      │  │              │
└─────┬───────┘  └─────┬───────┘  └──────┬───────┘
      │                │                  │
      └────────────────┼──────────────────┘
                       │ Database Access
      ┌────────────────┼─���────────────────┐
      ▼                ▼                   ▼
  ┌────────┐      ┌────────┐          ┌────────┐
  │Auth DB │      │Task DB │          │User DB │
  │(5432) │      │(5433) │          │(5434) │
  │        │      │        │          │        │
  │- Users │      │- Tasks │          │- Users │
  │        │      │        │          │        │
  └────────┘      └────────┘          └────────┘

                SERVICE DISCOVERY
              ┌────────────────────┐
              │  Eureka Server     │
              │  (Port 8761)       │
              │                    │
              │ Service Registry   │
              │ Health Monitoring  │
              │ Load Balancing     │
              └─────────────���──────┘
```

---

## Bounded Contexts & Service Responsibilities

### 1. Auth Service (`com.taskmanager.authservice`)
**Purpose:** User lifecycle management and authentication

**Responsibilities:**
- User registration with validation
- Password hashing (BCrypt)
- JWT token generation & validation
- Login authentication
- Default admin user seeding

**Database:** `auth_db` (PostgreSQL)
- `users` table (username, email, password_hash, role, created_at)

**API Endpoints:**
- `POST /auth/register` — Create new user account
- `POST /auth/login` — Authenticate user, return JWT token

**Outbound Dependencies:** None (stateless, no external service calls)

**Key Classes:**
- `User` — Entity
- `UserRepository` — JPA Repository
- `AuthService` — Business logic
- `AuthController` — REST endpoint handler
- `JwtTokenProvider` — JWT generation & validation
- `SecurityConfig` — Password encoder configuration

---

### 2. Task Service (`com.taskmanager.taskservice`)
**Purpose:** Task CRUD and management

**Responsibilities:**
- Task creation with ownership
- Task retrieval (with access control)
- Task updates (status, priority, etc.)
- Task deletion
- Filtering (status, priority)
- Pagination and sorting
- Admin access to all tasks

**Database:** `task_db` (PostgreSQL)
- `tasks` table (id, title, description, status, priority, due_date, owner_id, created_at, updated_at)

**API Endpoints:**
- `GET /tasks` — List user's tasks (or all if ADMIN)
- `POST /tasks` — Create new task
- `GET /tasks/{id}` — Get task by ID
- `PUT /tasks/{id}` — Update task
- `PATCH /tasks/{id}/status` — Update task status only
- `DELETE /tasks/{id}` — Delete task

**Inbound Dependencies:**
- Validates JWT token via `JwtAuthenticationFilter`
- Extracts user ID and role from JWT claims

**Outbound Dependencies:** None (no direct service-to-service calls; user validation via JWT)

**Key Classes:**
- `Task` — Entity
- `TaskRepository` — JPA Repository with custom queries
- `TaskService` — Business logic with ownership validation
- `TaskController` — REST endpoint handler
- `TaskMapper` — Entity ↔ DTO conversion
- `JwtAuthenticationFilter` — JWT extraction and validation
- `SecurityConfig` — Spring Security configuration

---

### 3. User Service (`com.taskmanager.userservice`)
**Purpose:** User management (ADMIN operations only)

**Responsibilities:**
- List all users (ADMIN only)
- Get user details (ADMIN only)
- RBAC enforcement via JWT role claim

**Database:** `user_db` (PostgreSQL)
- `users` table (id, username, email, role, created_at)

**API Endpoints:**
- `GET /users` — List all users (paginated)
- `GET /users/{id}` — Get specific user details

**Inbound Dependencies:**
- Validates JWT token with role check
- Enforces ADMIN-only access via `JwtAuthenticationFilter`

**Outbound Dependencies:** None (read-only operations)

**Key Classes:**
- `User` — Entity (minimal; no password field)
- `UserRepository` — JPA Repository
- `UserService` — Business logic
- `UserController` — REST endpoint handler
- `UserMapper` — Entity ↔ DTO conversion
- `JwtAuthenticationFilter` — JWT extraction + ADMIN role enforcement
- `SecurityConfig` — Spring Security configuration

---

### 4. API Gateway (`com.taskmanager.apigateway`)
**Purpose:** Single entry point for all client requests

**Responsibilities:**
- Request routing to microservices
- Service discovery integration (Eureka)
- Load balancing across service instances
- Circuit breaker support (Resilience4j)
- Request/response transformation

**Configuration:**
- `/api/auth/**` → `Auth Service` (port 8081)
- `/api/tasks/**` → `Task Service` (port 8082)
- `/api/users/**` → `User Service` (port 8083)
- `/eureka/**` → `Eureka Server` (port 8761)

**Key Features:**
- Discovery client enabled (registers with Eureka)
- Dynamic routing via service name (load balancer prefix: `lb://`)
- Automatic retry on service unavailability
- Health check endpoints

**Key Classes:**
- `ApiGatewayApplication` — Main application class
- `application.yml` — Route definitions and Eureka config

---

### 5. Service Discovery (`com.taskmanager.servicediscovery`)
**Purpose:** Dynamic service registration and discovery

**Responsibilities:**
- Accept service registrations
- Maintain service registry
- Health check monitoring
- Service lookup for clients

**Configuration:**
- Eureka Server (port 8761)
- Dashboard: `http://localhost:8761/eureka`
- Self-registration disabled (is itself the server)

**Key Classes:**
- `ServiceDiscoveryApplication` — Main application with `@EnableEurekaServer`
- `application.yml` — Eureka configuration

---

## Inter-Service Communication Patterns

### 1. **Synchronous REST (Current Implementation)**

**Used for:**
- All client → microservice calls (via API Gateway)
- No direct microservice → microservice calls

**Example:**
```
Client → API Gateway → Task Service → Database
```

**Advantages:**
- Simple, well-understood
- Synchronous responses (immediate feedback)
- Easy debugging and testing

**Disadvantages:**
- Services tightly coupled via REST
- Cascading failures if one service is down
- Performance impact of nested calls

**Mitigation:**
- Each service validates JWT independently (no cascading auth calls)
- Circuit breakers (Resilience4j) prevent cascading failures
- Timeouts and retry logic

---

### 2. **Asynchronous Messaging (Future Enhancement)**

**Could be used for:**
- Event-driven updates (e.g., user.created, task.assigned)
- Eventual consistency patterns
- Decoupled service notifications

**Example technology:** RabbitMQ, Apache Kafka

```
Task Service → Kafka Topic "task.created"
User Service → Subscribes to "task.created" event
```

---

### 3. **Service-to-Service Calls (Avoided)**

**Why we avoid direct microservice calls:**
- Violates service independence
- Increases complexity and failure points
- Makes scaling difficult

**Example of anti-pattern (NOT used here):**
```
Task Service → (HTTP call) → Auth Service
```

**Our solution:**
- Task Service validates JWT locally (no Auth Service call)
- User ID and role extracted from JWT claims directly

---

## Data Isolation & Consistency

### Database Isolation Strategy

**Each microservice has its own database:**

| Service | Database | Tables | Owner |
|---------|----------|--------|-------|
| Auth Service | `auth_db` | users | auth_service |
| Task Service | `task_db` | tasks | task_service |
| User Service | `user_db` | users | user_service |

**Advantages:**
- ✅ Independent scaling (each DB sized for its workload)
- ✅ Technology choice freedom (could use different DB types)
- ✅ Isolation from other services' failures
- ✅ Clear data ownership

**Challenges:**
- ❌ Distributed transactions required for cross-service operations
- ❌ Data consistency is eventual (not ACID)
- ❌ Duplicate data across services (users table in auth_db and user_db)

### Handling Data Consistency

#### Problem 1: User Duplication
- `auth_db.users` — credentials, auth info (username, email, password, role)
- `user_db.users` — profile info (username, email, role)

**Solution:** Treat `auth_db` as source-of-truth; sync to `user_db` on registration.

```
Registration Flow:
1. Auth Service creates user in auth_db
2. Auth Service publishes "user.created" event
3. User Service subscribes and replicates to user_db
```

**Current state:** Manual setup required (should be implemented with event bus)

#### Problem 2: Task Ownership Validation
- Task Service has `tasks.owner_id` (FK to non-existent Auth Service users)
- Cannot use database-level foreign key

**Solution:** Application-level validation

```
Task Service validates:
- GET /tasks/{id}: Check JWT token owner_id matches tasks.owner_id
- DELETE /tasks/{id}: Same validation
```

#### Problem 3: Distributed Transactions
- Creating a task for a newly registered user spans Auth + Task services
- No ACID guarantee

**Solution:** Orchestration pattern (current) or Saga pattern (future)

```
Current (Happy Path Only):
1. Auth Service creates user ✓
2. Client calls Task Service with JWT ✓
3. Task Service creates task ✓

What if step 2 fails?
- User created but no tasks created
- Not transactional, but acceptable for this use case
```

---

## Deployment Patterns

### Docker Compose (Development)

```
docker-compose up
```

Starts:
1. `service-discovery` (Eureka Server)
2. `auth-db`, `task-db`, `user-db` (PostgreSQL)
3. `auth-service`, `task-service`, `user-service` (Microservices)
4. `api-gateway` (Request router)
5. `frontend` (Next.js)

**Network:** All containers on `task-net` bridge network

---

### Kubernetes (Production)

```
kubectl apply -k k8s/
```

**Namespace:** `task-management`

**Components:**
- Deployments: service-discovery, auth-service, task-service, user-service, api-gateway, frontend
- Services: ClusterIP (internal), LoadBalancer (api-gateway, frontend)
- StatefulSets: PostgreSQL (if using)
- PersistentVolumeClaims: Database storage
- Ingress: HTTP routing (api-gateway → /api, frontend → /)
- ConfigMap: Shared configuration
- Secrets: Passwords, JWT secret

**Replica Strategy:**
- `service-discovery`: 1 (single Eureka instance)
- `auth-service`: 2 (horizontally scalable)
- `task-service`: 2 (horizontally scalable)
- `user-service`: 1 (low traffic, ADMIN only)
- `api-gateway`: 2 (load balanced)
- `frontend`: 2 (CDN-like, static content)

---

## Failure Scenarios & Resilience

### Scenario 1: Auth Service Down
**Impact:** Cannot authenticate (cannot get JWT token)

**Mitigation:**
- API Gateway detects via Eureka health check
- Other services unaffected (they validate locally)
- Clients get 503 Service Unavailable on auth requests

**Recovery:**
- Restart Auth Service
- Re-register with Eureka (automatic)

### Scenario 2: Task Service Down
**Impact:** Cannot manage tasks

**Mitigation:**
- API Gateway detects failure
- Auth & User services still available
- Eureka shows unhealthy instance

**Recovery:**
- Restart Task Service
- Clients with valid JWT can retry

### Scenario 3: Task Database Down
**Impact:** Task CRUD fails

**Mitigation:**
- Connection pool detects offline DB
- Service returns 500 error
- Eureka marks service as unhealthy

**Recovery:**
- Restart database
- Service automatically reconnects

### Scenario 4: Eureka Server Down
**Impact:** New service instances cannot register; client discovery stops

**Mitigation:**
- Services have cached service registry (local)
- Existing connections continue to work
- No new instances can be discovered

**Recovery:**
- Restart Eureka
- Services re-register
- New instances discovered

---

## Security Considerations

### Authentication Flow

```
1. Client → POST /api/auth/login
2. Auth Service → Validate credentials, generate JWT
3. Auth Service ← Return token to client
4. Client → GET /api/tasks (with "Authorization: Bearer <token>")
5. API Gateway → Route to Task Service
6. Task Service → Validate JWT locally (using JwtAuthenticationFilter)
7. Task Service → Extract userId, role from JWT claims
8. Task Service → Perform operation
```

### JWT Token Structure

```
Header:
{
  "alg": "HS512",
  "typ": "JWT"
}

Payload:
{
  "sub": "username",          // Subject (username)
  "userId": 1,                // Custom claim: user ID
  "role": "USER",             // Custom claim: role (USER or ADMIN)
  "iat": 1234567890,          // Issued at (timestamp)
  "exp": 1234567890 + 86400   // Expiration (24 hours)
}

Signature:
HMACSHA512(
  base64(header) + "." + base64(payload),
  JWT_SECRET
)
```

### JWT Secret Management

**Current Implementation:**
- Stored in environment variable `JWT_SECRET`
- Shared across all services
- Base64-encoded 32-byte secret (256 bits)

**Recommended Improvements:**
- Use HashiCorp Vault or AWS Secrets Manager
- Rotate secrets periodically
- Use asymmetric keys (RS256 instead of HS512)
- Implement token revocation list (blacklist)

### Service-to-Service Security (Future)

**Could implement:**
- mTLS (mutual TLS) for microservice communication
- Service mesh (Istio, Linkerd) for authentication & authorization
- API key authentication between services

---

## Observability & Monitoring

### Health Checks

All services expose `/actuator/health`:

```bash
curl http://localhost:8081/actuator/health
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "livenessState": { "status": "UP" },
    "readinessState": { "status": "UP" }
  }
}
```

### Metrics

Services expose `/actuator/metrics`:

```bash
curl http://localhost:8082/actuator/metrics
{
  "names": [
    "http.server.requests",
    "jvm.memory.used",
    "jvm.threads.live",
    "process.uptime",
    ...
  ]
}
```

### Logging

Each service logs to stdout (captured by Docker/Kubernetes):

```
[service-discovery] 2026-03-19 10:00:00 - INFO - EurekaServerInitializerConfiguration : Setting the eureka configuration..
[auth-service] 2026-03-19 10:00:15 - INFO - s.d.s.w.PropertySourceBootstrapConfiguration : Located property source: ConfigServer
[task-service] 2026-03-19 10:00:30 - INFO - o.s.j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory
```

### Eureka Dashboard

```
http://localhost:8761
```

Shows:
- Registered instances
- Health status
- Last heartbeat time
- Instance metadata

---

## Testing Strategy

### Unit Tests
- Individual service classes (Service, Controller, Repository)
- Mock external dependencies
- Run: `mvn test`

### Integration Tests
- Service + database (via TestContainers)
- Real database interactions
- Run: `mvn verify`

### Contract Tests
- Verify API contracts between services
- Spring Cloud Contract
- Prevent breaking changes

### End-to-End Tests
- Full stack (all services + databases)
- Docker Compose environment
- API client integration tests

---

## Cost Considerations

### Development (Docker Compose)
- Single machine, all services
- Minimal resource overhead

### Production (Kubernetes)
- Multiple machines / cloud nodes
- Database replicas (3 separate instances)
- Load balancer (cloud provider)
- Persistent storage (EBS, PersistentVolumes)
- Monitoring (Prometheus, Grafana)

**Optimization:**
- Scale down non-critical services (user-service)
- Use database connection pooling
- Implement caching (Redis)
- Consider read replicas for reporting

---

## Future Enhancements

1. **Event-Driven Architecture**
   - Kafka or RabbitMQ for async communication
   - Saga pattern for distributed transactions
   - Event sourcing for audit trails

2. **API Versioning**
   - Support multiple API versions concurrently
   - Backward compatibility layer

3. **Rate Limiting & Quotas**
   - Token bucket algorithm
   - Per-user or per-service rate limits

4. **Advanced Caching**
   - Redis for session caching
   - CDN for static assets
   - Query result caching

5. **Service Mesh**
   - Istio or Linkerd for observability
   - Automatic mTLS
   - Advanced traffic management

6. **GraphQL**
   - Alternative to REST
   - Flexible query language
   - Reduced over-fetching

7. **Search Capabilities**
   - Elasticsearch for full-text task search
   - Advanced filtering

8. **Notifications**
   - Email alerts (task due)
   - Slack integration
   - Push notifications

---

## References

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Cloud Eureka](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Microservices Patterns](https://microservices.io/)
- [Building Microservices (Sam Newman)](https://samnewman.io/books/building_microservices/)

