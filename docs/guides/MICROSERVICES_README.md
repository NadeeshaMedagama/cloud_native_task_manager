# Microservices Task Management Platform

## Overview

This is the **microservices version (v2.0.0)** of the Task Management System. The monolithic backend has been decomposed into three independent Spring Boot microservices with a centralized API Gateway and service discovery.

**Architecture:**
- **Auth Service** (port 8081) - Authentication, JWT, user registration/login
- **Task Service** (port 8082) - Task CRUD, filtering, pagination
- **User Service** (port 8083) - User management (Admin only)
- **API Gateway** (port 8080) - Request routing, load balancing
- **Service Discovery (Eureka)** (port 8761) - Service registration & discovery
- **Frontend** (Next.js 14, port 3000) - React UI
- **PostgreSQL** (3 separate databases) - Auth, Task, User data separation

---

## Project Structure

```
cloud_native_task_manager/
├── pom.xml                           # Parent POM (multi-module Maven)
├── common/                           # Shared DTOs, exceptions, utilities
├── service-discovery/                # Eureka Server (Spring Cloud)
├── api-gateway/                      # Spring Cloud Gateway (routing)
├── auth-service/                     # Auth microservice
│   ├── src/main/java/com/taskmanager/authservice/
│   │   ├── domain/                   # User entity, repository
│   │   ├── application/              # AuthService business logic
│   │   ├── infrastructure/           # JWT, security config
│   │   └── web/                      # AuthController
│   └── pom.xml
├── task-service/                     # Task microservice
│   ├── src/main/java/com/taskmanager/taskservice/
│   │   ├── domain/                   # Task entity, repository
│   │   ├── application/              # TaskService business logic
│   │   ├── infrastructure/           # JWT filter, security
│   │   └── web/                      # TaskController
│   └── pom.xml
├── user-service/                     # User microservice (Admin only)
│   ├── src/main/java/com/taskmanager/userservice/
│   │   ├── domain/                   # User entity, repository
│   │   ├── application/              # UserService business logic
│   │   ├── infrastructure/           # JWT filter, RBAC
│   │   └── web/                      # UserController
│   └── pom.xml
├── frontend/                         # Next.js 14 frontend
├── docker-compose.yml                # Multi-container orchestration
└── k8s/                              # Kubernetes manifests
    ├── namespace.yaml                # K8s namespace
    ├── secret.yaml                   # Secrets & ConfigMap
    ├── service-discovery.yaml        # Eureka deployment
    ├── postgres.yaml                 # 3x PostgreSQL databases
    ├── backend.yaml                  # Auth, Task, User, Gateway services
    ├── frontend.yaml                 # Frontend deployment
    ├── ingress.yaml                  # Ingress routing
    └── kustomization.yaml            # Kustomize orchestration
```

---

## Key Features

### Microservices Architecture
- ✅ **Service Independence** — Each service has its own database (auth_db, task_db, user_db)
- ✅ **Service Discovery** — Eureka server for dynamic service registration
- ✅ **API Gateway** — Spring Cloud Gateway routes requests to services
- ✅ **JWT Authentication** — Shared JWT token validation across services
- ✅ **Distributed Resilience** — Built-in health checks & service fallbacks

### Auth Service
- User registration with email validation
- BCrypt password hashing
- JWT token generation (24hr default expiration)
- Default admin account seeding

### Task Service
- Full CRUD operations with ownership validation
- Filtering by status and priority
- Pagination with configurable page size
- Sorting by creation date, due date, or priority
- Admin visibility of all tasks

### User Service
- User listing (admin only)
- User profile retrieval
- RBAC enforcement via JWT roles

### API Gateway
- Transparent routing: `/api/auth/*` → Auth Service, `/api/tasks/*` → Task Service, `/api/users/*` → User Service
- No breaking changes to frontend (still uses `/api/...` routes)
- Request/response logging and monitoring

---

## Quick Start

### Prerequisites
- **Docker & Docker Compose** (recommended) OR
- **Java 21**, **Maven 3.9+**, **Node.js 20+**, **PostgreSQL 16**

### Option 1: Docker Compose (Recommended)

```bash
# Navigate to project root
cd cloud_native_task_manager

# Start all services (Eureka, 3x databases, 3x microservices, API Gateway, Frontend)
docker compose up --build

# Services available at:
#   Frontend:        http://localhost:3000
#   API Gateway:     http://localhost:8080/api
#   Auth Service:    http://localhost:8081/api (direct access)
#   Task Service:    http://localhost:8082/api (direct access)
#   User Service:    http://localhost:8083/api (direct access)
#   Eureka Dashboard: http://localhost:8761
```

To stop:
```bash
docker compose down
# Remove volumes (clean databases):
docker compose down -v
```

### Option 2: Kubernetes (Helm / Kustomize)

#### Prerequisites
- `kubectl` configured with a running cluster
- Images built and pushed to Docker Hub or local registry

#### Deploy with Kustomize
```bash
# Apply all Kubernetes manifests
kubectl apply -k k8s/

# Verify deployments
kubectl get deployments -n task-management
kubectl get services -n task-management

# Watch pod status
kubectl get pods -n task-management -w

# Port-forward to access services
kubectl port-forward -n task-management svc/api-gateway 8080:8080
kubectl port-forward -n task-management svc/frontend 3000:3000
```

#### Cleanup
```bash
kubectl delete -k k8s/
```

---

## Database Schema

### Auth Database (`auth_db`)
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,  -- BCrypt hashed
  role VARCHAR(20) NOT NULL,        -- USER or ADMIN
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Task Database (`task_db`)
```sql
CREATE TABLE tasks (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  status VARCHAR(20) NOT NULL,     -- TODO, IN_PROGRESS, DONE
  priority VARCHAR(10) NOT NULL,   -- LOW, MEDIUM, HIGH
  due_date DATE,
  owner_id BIGINT NOT NULL,        -- User ID from auth service
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

### User Database (`user_db`)
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  role VARCHAR(20) NOT NULL,        -- USER or ADMIN
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

## API Documentation

### Shared Response Format

All services follow the same error response format:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "timestamp": "2026-03-19T10:00:00",
  "path": "/api/endpoint"
}
```

### Authentication Endpoints (via API Gateway)

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}

Response 201:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response 200: (Same as register)
```

### Task Endpoints (via API Gateway)

All require `Authorization: Bearer <token>` header.

#### List Tasks
```http
GET /api/tasks?status=TODO&priority=HIGH&page=0&size=10&sortBy=createdAt&direction=desc
Authorization: Bearer <token>

Response 200:
{
  "content": [
    {
      "id": 1,
      "title": "Fix login bug",
      "description": "Login fails on mobile Safari",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2026-04-01",
      "ownerId": 1,
      "createdAt": "2026-03-19T10:00:00",
      "updatedAt": "2026-03-19T10:00:00"
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1
}
```

#### Create Task
```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Implement feature X",
  "description": "Add user preferences",
  "status": "TODO",
  "priority": "MEDIUM",
  "dueDate": "2026-04-15"
}

Response 201: (Task DTO)
```

#### Get Task
```http
GET /api/tasks/{id}
Authorization: Bearer <token>

Response 200: (Task DTO)
```

#### Update Task
```http
PUT /api/tasks/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2026-04-20"
}

Response 200: (Task DTO)
```

#### Update Task Status (Quick Toggle)
```http
PATCH /api/tasks/{id}/status?status=DONE
Authorization: Bearer <token>

Response 200: (Task DTO with new status)
```

#### Delete Task
```http
DELETE /api/tasks/{id}
Authorization: Bearer <token>

Response 204 No Content
```

### User Endpoints (Admin Only, via API Gateway)

#### List All Users
```http
GET /api/users?page=0&size=10
Authorization: Bearer <admin_token>

Response 200: (Page of UserDTOs)
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <admin_token>

Response 200: (UserDTO)
```

---

## Environment Variables

### Common (all services)
| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_USERNAME` | `taskuser` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `taskpassword` | Database password |
| `JWT_SECRET` | (see .env.example) | Base64-encoded HMAC secret |
| `JWT_EXPIRATION` | `86400000` | Token expiry (ms) |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka/` | Eureka server URL |

### Auth Service
| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/auth_db` | PostgreSQL JDBC URL |
| `ADMIN_USERNAME` | `admin` | Default admin username |
| `ADMIN_EMAIL` | `admin@taskmanager.com` | Default admin email |
| `ADMIN_PASSWORD` | `admin123` | Default admin password |

### Task Service
| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/task_db` | PostgreSQL JDBC URL |

### User Service
| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/user_db` | PostgreSQL JDBC URL |

### API Gateway
| Variable | Default | Description |
|----------|---------|-------------|
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka/` | Service discovery URL |

---

## Service Mesh & Resilience

### Service-to-Service Communication
- **Auth Service** → Task Service: Via JWT token validation (stateless)
- **Task Service** → Auth Service: Via JWT claims (no direct call)
- **User Service** → Isolated (ADMIN operations only)
- **API Gateway** → All Services: Via Eureka discovery + load balancing

### Health Checks
Each service exposes `/actuator/health` and is registered in Eureka with health monitoring enabled.

### Circuit Breaker (Future Enhancement)
Spring Cloud Resilience4j can be added to detect and gracefully handle service failures:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      task-service:
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

---

## Monitoring & Logging

### Actuator Endpoints (all services)
```bash
GET /api/actuator/health          # Service health
GET /api/actuator/metrics         # JVM metrics
GET /api/actuator/env             # Environment info
```

### Eureka Dashboard
```
http://localhost:8761
```

Shows all registered services, their instances, and health status.

### Docker Compose Logs
```bash
docker compose logs -f auth-service    # Follow auth service logs
docker compose logs -f task-service    # Follow task service logs
docker compose logs -f api-gateway     # Follow gateway logs
```

### Kubernetes Logs
```bash
kubectl logs -f deployment/auth-service -n task-management
kubectl logs -f deployment/task-service -n task-management
kubectl logs -f deployment/api-gateway -n task-management
```

---

## Building and Deploying

### Build All Microservices Locally
```bash
mvn clean install -DskipTests
```

### Build Docker Images
```bash
# Build via docker-compose
docker compose build

# Or manually (from root directory)
docker build -f service-discovery/Dockerfile -t task-management-platform/service-discovery:latest .
docker build -f auth-service/Dockerfile -t task-management-platform/auth-service:latest .
docker build -f task-service/Dockerfile -t task-management-platform/task-service:latest .
docker build -f user-service/Dockerfile -t task-management-platform/user-service:latest .
docker build -f api-gateway/Dockerfile -t task-management-platform/api-gateway:latest .
docker build -f frontend/Dockerfile -t task-management-platform/frontend:latest .
```

### Push to Docker Hub
```bash
docker tag task-management-platform/auth-service:latest <your-docker-hub>/auth-service:latest
docker push <your-docker-hub>/auth-service:latest
# Repeat for other services
```

### Deploy to Kubernetes (GKE, EKS, AKS, K3s, etc.)
```bash
# Update image registry in k8s/backend.yaml if needed

# Deploy with Kustomize
kubectl apply -k k8s/

# Or with plain kubectl
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/service-discovery.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

---

## Migration from Monolith (v1.0.0) to Microservices (v2.0.0)

### Breaking Changes
**None for frontend users!** The API remains at `/api/...` routes.

### Data Migration
1. **Export data from old backend:**
   ```bash
   pg_dump taskmanager > backup.sql
   ```

2. **Import into separate databases:**
   ```bash
   psql auth_db < auth_schema.sql
   psql task_db < task_schema.sql
   psql user_db < user_schema.sql
   ```

3. **Run microservices** — Hibernate `ddl-auto: update` will auto-migrate schema.

### Backward Compatibility
- JWT token format is unchanged
- API endpoints remain the same (`/api/auth/`, `/api/tasks/`, `/api/users/`)
- Frontend requires **zero** code changes

---

## Technology Stack

### Backend
- **Spring Boot 3.2** — Application framework
- **Spring Cloud 2023.0** — Service discovery, Gateway, Config
- **Spring Data JPA / Hibernate** — ORM
- **PostgreSQL 16** — Relational database (3 instances)
- **JJWT 0.11.5** — JWT authentication
- **SpringDoc OpenAPI 2.3** — Swagger/OpenAPI docs
- **Bean Validation** — Input validation

### Frontend
- **Next.js 14** (App Router) — React framework
- **TypeScript** — Type safety
- **Tailwind CSS** — Styling
- **Axios** — HTTP client
- **React Hot Toast** — Notifications

### DevOps
- **Docker & Docker Compose** — Containerization
- **Kubernetes (K8s)** — Orchestration
- **Kustomize** — K8s template management
- **GitHub Actions** — CI/CD pipelines
- **Eureka** — Service discovery
- **Spring Cloud Gateway** — API routing

---

## Troubleshooting

### Service Registration Issues
**Problem:** Services not appearing in Eureka dashboard.
```bash
# Check Eureka logs
docker logs taskmanager_eureka

# Verify Eureka connectivity
curl http://localhost:8761/eureka/apps
```

### Database Connection Errors
**Problem:** "Connection refused" errors.
```bash
# Ensure databases are running
docker ps | grep db

# Check connection strings
docker logs taskmanager_auth_db
docker logs taskmanager_task_db
docker logs taskmanager_user_db

# Verify credentials in docker-compose.yml
```

### Gateway Routing Issues
**Problem:** 404 errors from API Gateway.
```bash
# Check gateway logs
docker logs taskmanager_api_gateway

# Verify service discovery
curl http://localhost:8761/eureka/apps

# Test direct service access
curl http://localhost:8081/api/auth/  # Auth service
curl http://localhost:8082/api/tasks/ # Task service
```

### Token Validation Issues
**Problem:** "Unauthorized" errors after login.
```bash
# Verify JWT_SECRET matches across services
echo $JWT_SECRET

# Check token format (should be: "Bearer eyJhbGc...")
# Ensure Authorization header is set
```

---

## Performance Considerations

### Service Scaling
Each service can be independently scaled:
```bash
# Docker Compose example
docker compose up -d --scale task-service=3
```

```yaml
# Kubernetes replica adjustment
kubectl scale deployment task-service --replicas=5 -n task-management
```

### Database Optimization
- Separate databases reduce contention
- Add indexes for frequently queried columns (e.g., `owner_id`, `status`)
- Consider read replicas for reporting queries

### API Gateway Caching
Add caching headers to reduce downstream load:
```java
// In api-gateway filters
response.setHeader("Cache-Control", "max-age=300");
```

---

## Contributing

1. Create a feature branch: `git checkout -b feature/my-feature`
2. Commit changes: `git commit -m "feat: add feature"`
3. Push to branch: `git push origin feature/my-feature`
4. Open a Pull Request

---

## License

MIT License - See LICENSE file for details

---

## Support

- **Documentation:** `docs/` folder
- **API Reference:** Swagger UI at `http://localhost:8080/api/swagger-ui.html`
- **Issues:** GitHub Issues
- **Email:** support@taskmanager.local

