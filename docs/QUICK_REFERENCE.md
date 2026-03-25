# Quick Reference - Task Management Microservices

## 🚀 Start Development Environment

```bash
# Build everything
mvn clean install -DskipTests

# Start all services
docker compose up --build

# Stop all services
docker compose down

# View logs
docker compose logs -f auth-service
docker compose logs -f task-service
docker compose logs -f api-gateway
```

## 📋 Service Ports

| Service | Port | URL |
|---------|------|-----|
| Frontend | 3000 | http://localhost:3000 |
| API Gateway | 8080 | http://localhost:8080/api |
| Auth Service | 8081 | http://localhost:8081/api |
| Task Service | 8082 | http://localhost:8082/api |
| User Service | 8083 | http://localhost:8083/api |
| Eureka | 8761 | http://localhost:8761 |
| Swagger UI | 8080 | http://localhost:8080/api/swagger-ui.html |
| Auth DB | 5432 | localhost:5432 |
| Task DB | 5433 | localhost:5433 |
| User DB | 5434 | localhost:5434 |

## 🔑 Default Credentials

| Role | Username | Password | Email |
|------|----------|----------|-------|
| Admin | admin | admin123 | admin@taskmanager.com |
| Test User | (register new) | (your choice) | (your email) |

## 📡 API Endpoints

### Authentication (No Auth Required)

```bash
# Register
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}

# Login
POST /api/auth/login
{
  "username": "john",
  "password": "password123"
}
```

### Tasks (Requires: `Authorization: Bearer <token>`)

```bash
# List tasks
GET /api/tasks?page=0&size=10&status=TODO&priority=HIGH

# Create task
POST /api/tasks
{
  "title": "Task title",
  "description": "Task description",
  "status": "TODO",
  "priority": "MEDIUM",
  "dueDate": "2026-04-15"
}

# Get task
GET /api/tasks/{id}

# Update task
PUT /api/tasks/{id}
{ ... same as create ... }

# Update status only
PATCH /api/tasks/{id}/status?status=DONE

# Delete task
DELETE /api/tasks/{id}
```

### Users (Admin Only, Requires: `Authorization: Bearer <admin-token>`)

```bash
# List users
GET /api/users?page=0&size=10

# Get user
GET /api/users/{id}
```

## 🗄️ Databases

### Auth DB (`auth_db`, port 5432)
```sql
SELECT * FROM users;
-- Shows: id, username, email, password_hash, role, created_at
```

### Task DB (`task_db`, port 5433)
```sql
SELECT * FROM tasks;
-- Shows: id, title, description, status, priority, due_date, owner_id, created_at, updated_at
```

### User DB (`user_db`, port 5434)
```sql
SELECT * FROM users;
-- Shows: id, username, email, role, created_at
```

## 🔧 Build Individual Services

```bash
# Auth Service
mvn clean package -pl auth-service -DskipTests

# Task Service
mvn clean package -pl task-service -DskipTests

# User Service
mvn clean package -pl user-service -DskipTests

# API Gateway
mvn clean package -pl api-gateway -DskipTests

# Service Discovery
mvn clean package -pl service-discovery -DskipTests

# Common library
mvn clean package -pl common -DskipTests
```

## 🐳 Docker Commands

```bash
# Build specific image
docker build -f auth-service/Dockerfile -t task-management-platform/auth-service:latest .

# Run specific container
docker run -p 8081:8081 task-management-platform/auth-service:latest

# Push to registry
docker tag task-management-platform/auth-service:latest your-registry/auth-service:latest
docker push your-registry/auth-service:latest

# Remove unused images
docker image prune -a

# View container resource usage
docker stats
```

## ☸️ Kubernetes Commands

```bash
# Deploy
kubectl apply -k k8s/

# View all resources
kubectl get all -n task-management

# View pods
kubectl get pods -n task-management

# View services
kubectl get svc -n task-management

# View logs
kubectl logs -f deployment/auth-service -n task-management

# Port forward
kubectl port-forward -n task-management svc/api-gateway 8080:8080

# Scale service
kubectl scale deployment auth-service --replicas=3 -n task-management

# Delete all
kubectl delete -k k8s/

# Execute command in pod
kubectl exec -it pod-name -n task-management -- /bin/bash
```

## 🧪 Testing

```bash
# Run all tests
mvn clean test

# Run tests for specific service
mvn test -pl auth-service

# Run integration tests
mvn clean verify

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Skip tests during build
mvn clean package -DskipTests
```

## 🔍 Debugging

### View Eureka Dashboard
```
http://localhost:8761
```

### Check Service Health
```bash
curl http://localhost:8081/api/actuator/health
curl http://localhost:8082/api/actuator/health
curl http://localhost:8083/api/actuator/health
```

### Check Metrics
```bash
curl http://localhost:8081/api/actuator/metrics
curl http://localhost:8082/api/actuator/metrics
```

### View Docker Logs
```bash
docker compose logs auth-service
docker compose logs task-service
docker compose logs api-gateway
docker compose logs frontend
```

### Test Database Connection
```bash
# From container
docker exec -it taskmanager_auth_db psql -U taskuser -d auth_db -c "SELECT * FROM users;"

# From host (if psql installed)
psql -h localhost -U taskuser -d auth_db -c "SELECT * FROM users;"
```

## 🌐 Environment Variables

See `.env.example` for all variables. Key ones:

```bash
# Database
SPRING_DATASOURCE_USERNAME=taskuser
SPRING_DATASOURCE_PASSWORD=taskpassword

# JWT
JWT_SECRET=<base64-secret>
JWT_EXPIRATION=86400000

# API
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# Service Discovery
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

## 📚 Documentation

| File | Purpose |
|------|---------|
| `MICROSERVICES_README.md` | Complete guide with API docs |
| `ARCHITECTURE.md` | System design & patterns |
| `DEPLOYMENT_GUIDE.md` | Deployment instructions |
| `MIGRATION_SUMMARY.md` | What changed from v1 to v2 |
| `README.md` | Legacy monolithic version docs |

## 💡 Common Tasks

### Add New Endpoint to Task Service

1. **Create DTO** in `task-service/src/main/java/.../application/dto/`
2. **Create Controller method** in `task-service/src/main/java/.../web/controller/TaskController.java`
3. **Add Service logic** in `task-service/src/main/java/.../application/service/TaskService.java`
4. **Test locally** via Swagger UI or cURL
5. **Build & deploy** `mvn clean package -pl task-service`

### Add New Database Table

1. **Create JPA Entity** in `task-service/src/main/java/.../domain/model/`
2. **Create Repository** in `task-service/src/main/java/.../domain/repository/`
3. **Hibernate will auto-create** table (set `spring.jpa.hibernate.ddl-auto=update`)
4. **Add Service logic** to handle the entity
5. **Add Controller endpoint** if needed

### Deploy to Kubernetes

```bash
# 1. Build images
docker compose build

# 2. Push images
docker push your-registry/auth-service:latest
# ... repeat for other services

# 3. Update image in k8s/backend.yaml
sed -i 's|IfNotPresent|Always|g' k8s/backend.yaml

# 4. Deploy
kubectl apply -k k8s/

# 5. Verify
kubectl get pods -n task-management -w
```

### Monitor Performance

```bash
# Docker resource usage
docker stats

# Kubernetes resource usage
kubectl top pods -n task-management
kubectl top nodes

# Database query performance
docker exec -it taskmanager_auth_db psql -U taskuser -d auth_db \
  -c "SELECT query, calls, mean_time FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;"
```

## 🚨 Troubleshooting

### Service won't start
```bash
# Check logs
docker logs taskmanager_auth-service

# Common causes:
# - Database connection refused: Wait 30s for DB to start
# - Port already in use: docker ps to see what's running
# - JWT_SECRET not set: Check .env.local
```

### Can't connect to database
```bash
# Verify DB is running
docker ps | grep db

# Check DB logs
docker logs taskmanager_auth_db

# Test connection
docker exec taskmanager_auth_db psql -U taskuser -d auth_db -c "SELECT version();"
```

### API Gateway returns 404
```bash
# Check if service is registered
curl http://localhost:8761/eureka/apps

# Check gateway logs
docker logs taskmanager_api_gateway

# Test service directly
curl http://localhost:8081/api/auth/  # Should work
curl http://localhost:8080/api/auth/  # May not work if not registered
```

### JWT validation fails
```bash
# Verify secret is set
echo $JWT_SECRET

# Check token format in response
curl -X POST http://localhost:8080/api/auth/login ... | jq .token

# Use token in request
TOKEN="eyJ..."
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/tasks/
```

## 📞 Support

1. **Check MICROSERVICES_README.md** - Comprehensive guide
2. **Check ARCHITECTURE.md** - Design decisions
3. **Check DEPLOYMENT_GUIDE.md** - Deployment help
4. **Review logs** - Most issues show in logs
5. **Check health endpoints** - `/actuator/health`
6. **Check Eureka dashboard** - Service registry status

---

**Happy coding! 🚀**

