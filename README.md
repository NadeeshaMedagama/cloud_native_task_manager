# Cloud_Native_Task_Manager - Microservices Architecture (v2.0.0)

## 🎯 Overview

A **production-ready cloud-native microservices task management platform** built with Spring Boot 3.2, Spring Cloud, PostgreSQL, and Next.js 14.

**Project:** Cloud_Native_Task_Manager  
**Version:** 2.0.0 (Microservices)  
**Status:** Production-Ready  

### Key Technologies
- **Backend**: Spring Boot 3.2, Spring Cloud, Java 21, 6 independent microservices
- **Microservices**: Auth, Task, User, API Gateway, Service Discovery (Eureka), Common Library
- **Databases**: 3 separate PostgreSQL instances (auth_db, task_db, user_db)
- **Frontend**: Next.js 14 (App Router), TypeScript, Tailwind CSS
- **DevOps**: Docker, Docker Compose, Kubernetes, Kustomize
- **CI/CD**: GitHub Actions (automated testing, building, security scanning)

---

## 📋 Quick Start (3 Minutes)

```bash
# Clone repository
git clone https://github.com/your-username/Cloud_Native_Task_Manager.git
cd Cloud_Native_Task_Manager

# Start all microservices
docker compose up --build

# Access services
# Frontend:       http://localhost:3000
# API Gateway:    http://localhost:8080/api
# Eureka:         http://localhost:8761
# Swagger UI:     http://localhost:8080/api/swagger-ui.html
```

**Default Credentials:**
- Username: `admin`
- Password: `admin123`

### Local Run Script (Recommended for Daily Development)

Use the helper script `run-local.sh` from the project root:

```bash
# Show available modes
./run-local.sh help

# Start backend stack (Docker Compose)
./run-local.sh backend

# Start frontend dev server only
./run-local.sh frontend

# Start backend (detached) + frontend (foreground)
./run-local.sh all

# Follow backend logs
./run-local.sh logs

# Stop backend stack
./run-local.sh stop
```

For frontend-only development, `npm run dev` in `frontend/` still works as usual.

---

## 📁 Project Structure

```
Cloud_Native_Task_Manager/
├── 🏗️ Microservices (6 independent services)
│   ├── service-discovery/          # Eureka Server (port 8761)
│   ├── api-gateway/                # Spring Cloud Gateway (port 8080)
│   ├── auth-service/               # Authentication service (port 8081)
│   ├── task-service/               # Task management service (port 8082)
│   ├── user-service/               # User admin service (port 8083)
│   └── common/                     # Shared DTOs & exceptions
│
├── 🖼️ Frontend
│   └── frontend/                   # Next.js 14 application (port 3000)
│
├── 🐳 Containerization
│   ├── docker-compose.yml          # Multi-service orchestration
│   ├── pom.xml                     # Parent POM (multi-module Maven)
│   └── .env.example                # Environment variables template
│
├── ☸️ Kubernetes
│   ├── k8s/namespace.yaml
│   ├── k8s/secret.yaml
│   ├── k8s/service-discovery.yaml  # Eureka deployment
│   ├── k8s/postgres.yaml           # 3x PostgreSQL databases
│   ├── k8s/backend.yaml            # All microservices
│   ├── k8s/frontend.yaml
│   ├── k8s/ingress.yaml
│   └── k8s/kustomization.yaml
│
├── 📚 Documentation
│   ├── docs/README.md              # Documentation index
│   ├── docs/architecture/          # System design & patterns
│   ├── docs/deployment/            # Setup & deployment guides
│   ├── docs/guides/                # Quick start & reference
│   ├── docs/migration/             # v1.0.0 → v2.0.0 migration
│   ├── docs/cleanup/               # Legacy file cleanup
│   ├── docs/api/                   # API documentation
│   └── docs/reference/             # Configuration & schema
│
├── 🔄 CI/CD
│   └── .github/workflows/          # GitHub Actions pipelines
│
└── 📖 Main Documentation
    ├── README.md                   # This file
    ├── MICROSERVICES_README.md     # Complete microservices guide
    ├── ARCHITECTURE.md             # System architecture & design
    ├── DEPLOYMENT_GUIDE.md         # Deployment instructions
    ├── MIGRATION_SUMMARY.md        # v1→v2 migration info
    ├── QUICK_REFERENCE.md          # Quick command reference
    ├── CHANGES.md                  # Detailed change list
    └── CLEANUP_GUIDE.md            # Legacy cleanup procedures
```

---

## ✨ Key Features

### 🏗️ Microservices Architecture
- ✅ **6 Independent Microservices** — Auth, Task, User, API Gateway, Service Discovery, Common Library
- ✅ **Service Discovery** — Eureka server for dynamic service registration & health monitoring
- ✅ **API Gateway** — Spring Cloud Gateway routes all requests transparently
- ✅ **Independent Scaling** — Scale each service independently based on load
- ✅ **Loose Coupling** — Services communicate via REST APIs & JWT tokens, not direct calls
- ✅ **Data Isolation** — 3 separate PostgreSQL databases prevent cross-service coupling

### 🔐 Authentication & Security
- ✅ **JWT Authentication** — Register, login, token-based access
- ✅ **BCrypt Password Hashing** — Secure password storage
- ✅ **Role-Based Access Control** — ADMIN sees all tasks; USER manages own tasks
- ✅ **Local Token Validation** — Each service validates JWT independently
- ✅ **Claim-Based Authorization** — Extract userId, role from token claims

### 📝 Task Management
- ✅ **Full CRUD Operations** — Create, read, update, delete tasks with ownership validation
- ✅ **Quick Status Toggle** — Move tasks through TODO → IN_PROGRESS → DONE in one click
- ✅ **Filtering** — Filter by status and priority
- ✅ **Pagination** — Server-side pagination with configurable page size
- ✅ **Sorting** — Sort by created date, due date, or priority (asc/desc)
- ✅ **Admin Visibility** — Admins see all tasks; users see only their own

### 🚀 Deployment & DevOps
- ✅ **Docker Compose** — One-command start for local development (10 containers)
- ✅ **Kubernetes Ready** — Complete K8s manifests with Kustomize
- ✅ **Multi-Environment** — Configuration for local, staging, production
- ✅ **Health Checks** — Built-in endpoint monitoring & automatic service discovery
- ✅ **Zero Downtime** — Rolling updates & graceful shutdown support

### 📚 Documentation
- ✅ **4,500+ Lines** — Comprehensive documentation across 7 organized folders
- ✅ **Role-Based Guides** — Separate paths for developers, DevOps, architects, QA
- ✅ **API Documentation** — Complete endpoints, examples, error codes
- ✅ **Architecture Guide** — Design decisions, patterns, trade-offs
- ✅ **Deployment Guide** — Docker Compose, Kubernetes, cloud platforms

### 📊 Monitoring & Observability
- ✅ **Eureka Dashboard** — Service registry visualization & health status
- ✅ **Actuator Endpoints** — `/actuator/health`, `/actuator/metrics` on all services
- ✅ **Request Logging** — Built-in request/response logging
- ✅ **Error Handling** — Consistent JSON error responses across all services
- ✅ **Swagger UI** — Interactive API documentation

### ♾️ CI/CD & GitHub Actions
- ✅ **Automated Build & Test** — On every push & pull request
- ✅ **Security Scanning** — CodeQL analysis for Java & JavaScript
- ✅ **Docker Image Push** — Automatic push to Docker Hub on release
- ✅ **Dependency Management** — Automated dependency updates via Dependabot
- ✅ **Release Automation** — GitHub Releases with artifacts & documentation

### 🔄 100% Backward Compatible
- ✅ **Zero Breaking Changes** — Same API endpoints as v1.0.0
- ✅ **Frontend Compatible** — Frontend requires zero code changes
- ✅ **Request Format** — Unchanged request/response structure
- ✅ **Easy Migration** — Transparent upgrade path from monolith

---

## 🎯 Service Architecture

### Microservices Overview

```
┌──────────────────────��───────────────────────────────────┐
│                   Frontend (Next.js)                     │
│                  http://localhost:3000                   │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP/REST
                         ▼
┌──────────────────────────────────────────────────────────┐
│              API Gateway (Spring Cloud)                  │
│                  http://localhost:8080                   │
│  Routes: /api/auth/* → :8081, /api/tasks/* → :8082     │
└────┬─────────────┬──────────────────┬────────────────────┘
     │ Discovery   │ Discovery        │ Discovery
     ▼             ▼                  ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Auth Service │ │ Task Service │ │ User Service │
│  :8081       │ │  :8082       │ │  :8083       │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │ 
       ▼                ▼                ▼
    ┌────┐           ┌────┐           ┌────┐
    │auth│           │task│           │user│
    │ db │           │ db │           │ db │
    │:543│           │:543│           │:543│
    │ 2 │           │ 3  │           │ 4  │
    └────┘           └────┘           └────┘

           SERVICE DISCOVERY (Eureka)
         ┌─────────────────────────────┐
         │   localhost:8761            │
         │ Service Registry + Health   │
         └─────────────────────────────┘
```

### Service Details

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| **Service Discovery** | 8761 | - | Eureka server, dynamic service registry |
| **API Gateway** | 8080 | - | Routes requests to microservices |
| **Auth Service** | 8081 | auth_db:5432 | User authentication & JWT generation |
| **Task Service** | 8082 | task_db:5433 | Task CRUD & management |
| **User Service** | 8083 | user_db:5434 | User admin operations (RBAC) |
| **Frontend** | 3000 | - | Next.js React application |

---

## 📡 API Endpoints

All endpoints are accessed through the **API Gateway** at `http://localhost:8080/api`

### Authentication (No Auth Required)
```
POST   /api/auth/register    - Register new user
POST   /api/auth/login       - Login & get JWT token
```

### Tasks (Requires Authorization)
```
GET    /api/tasks                    - List user's tasks
POST   /api/tasks                    - Create new task
GET    /api/tasks/{id}               - Get task by ID
PUT    /api/tasks/{id}               - Update task
PATCH  /api/tasks/{id}/status        - Update task status only
DELETE /api/tasks/{id}               - Delete task
```

### Users (Admin Only)
```
GET    /api/users                    - List all users
GET    /api/users/{id}               - Get user details
```

**Full API Documentation:** [docs/api/API_REFERENCE.md](docs/api/API_REFERENCE.md)  
**Interactive Swagger:** http://localhost:8080/api/swagger-ui.html

---

## 🗂️ Database Design

### Three Separate Databases (Microservices Pattern)

#### auth_db (Port 5432) - Auth Service
```sql
users (id, username, email, password_hash, role, created_at)
```

#### task_db (Port 5433) - Task Service  
```sql
tasks (id, title, description, status, priority, due_date, owner_id, created_at, updated_at)
```

#### user_db (Port 5434) - User Service
```sql
users (id, username, email, role, created_at)
```

**Benefits:**
- Independent scaling per service
- Each service owns its data model
- Eliminates cross-service database dependencies
- Easier backup & recovery strategies

**Full Schema Reference:** [docs/reference/DATABASE_SCHEMA.md](docs/reference/DATABASE_SCHEMA.md)

---

## 🚀 Deployment

### Option 1: Docker Compose (Recommended - 3 minutes)

```bash
# Navigate to project
cd cloud_native_task_manager

# Start all services
docker compose up --build

# Services available at:
#   Frontend:       http://localhost:3000
#   API Gateway:    http://localhost:8080/api
#   Eureka:         http://localhost:8761
#   Swagger UI:     http://localhost:8080/api/swagger-ui.html

# Stop services
docker compose down

# Remove all data
docker compose down -v
```

### Option 2: Kubernetes (Production - 10+ minutes)

```bash
# Build Docker images
docker compose build

# Push to Docker Hub (optional)
docker tag task-management-platform/auth-service:latest your-registry/auth-service:latest
docker push your-registry/auth-service:latest
# ... repeat for other services

# Deploy to Kubernetes
kubectl apply -k k8s/

# Verify deployment
kubectl get pods -n task-management
kubectl get svc -n task-management

# Access services via port-forward
kubectl port-forward -n task-management svc/api-gateway 8080:8080
kubectl port-forward -n task-management svc/frontend 3000:3000
```

### Option 3: Cloud Deployment (AWS, GCP, DigitalOcean)

See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for:
- AWS ECS / Fargate
- Google Cloud Run
- DigitalOcean Kubernetes
- Azure AKS
- Other cloud platforms

---

## 🔧 Configuration

### Environment Variables

Create `.env.local` from `.env.example`:

```bash
# Copy template
cp .env.example .env.local

# Edit with your values
nano .env.local
```

**Key Variables:**
```env
# Database
SPRING_DATASOURCE_USERNAME=taskuser
SPRING_DATASOURCE_PASSWORD=taskpassword

# JWT
JWT_SECRET=<base64-encoded-secret>
JWT_EXPIRATION=86400000

# Admin Account
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

**Complete Reference:** [docs/reference/CONFIGURATION_REFERENCE.md](docs/reference/CONFIGURATION_REFERENCE.md)

---

## 📚 Documentation

Start with the appropriate guide for your role:

### 👨‍💻 For Developers
1. **[MICROSERVICES_README.md](MICROSERVICES_README.md)** - Complete guide (655 lines)
2. **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Quick commands & API examples
3. **[docs/guides/GETTING_STARTED.md](docs/guides/GETTING_STARTED.md)** - First steps

### 🏗️ For Architects
1. **[ARCHITECTURE.md](ARCHITECTURE.md)** - System design & patterns (500+ lines)
2. **[docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md)** - Deep design dive
3. **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Deployment options

### 🔧 For DevOps
1. **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Deployment instructions (600+ lines)
2. **[docs/deployment/MONITORING_LOGGING.md](docs/deployment/MONITORING_LOGGING.md)** - Monitoring setup
3. **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Docker & K8s commands

### 📖 Main Documentation Index
- **[docs/README.md](docs/README.md)** - Complete documentation directory
- **[DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)** - All doc files & navigation

---

## 🔄 Migration from v1.0.0

The project has been **fully migrated from a monolith (v1.0.0) to microservices (v2.0.0)** with:
- ✅ **Zero breaking changes** - Same API endpoints
- ✅ **100% backward compatible** - Frontend requires no code changes
- ✅ **6 independent services** - Decomposed from single monolith
- ✅ **3 separate databases** - Data isolation per service
- ✅ **Complete documentation** - 4,500+ lines of guides

**Migration Details:** [MIGRATION_SUMMARY.md](MIGRATION_SUMMARY.md)

---

## 🎓 Technology Stack

### Backend Services
| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.3 | Application framework |
| Spring Cloud | 2023.0.0 | Microservices patterns |
| Spring Security | 6.1 | Authentication & authorization |
| Spring Data JPA | 3.2 | Database access |
| Eureka | Spring Cloud Netflix | Service discovery |
| Spring Cloud Gateway | - | API routing & gateway |
| PostgreSQL | 16 | Relational databases |
| JJWT | 0.11.5 | JWT token handling |
| Maven | 3.9+ | Build tool (multi-module) |

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| Next.js | 14 | React framework (App Router) |
| React | 18 | UI library |
| TypeScript | Latest | Type safety |
| Tailwind CSS | 3.x | Utility-first styling |
| Axios | Latest | HTTP client |

### DevOps & Deployment
| Technology | Version | Purpose |
|-----------|---------|---------|
| Docker | Latest | Containerization |
| Docker Compose | Latest | Multi-service orchestration |
| Kubernetes | 1.24+ | Container orchestration |
| Kustomize | Built-in | K8s template management |
| GitHub Actions | - | CI/CD pipelines |

---

## 🔒 Security Features

### Authentication
- JWT tokens with HMAC-SHA512 signing
- 24-hour token expiration
- Refresh token support
- Per-service token validation

### Authorization
- Role-based access control (ADMIN/USER)
- Claim-based authorization
- Ownership validation for resources
- Admin-only endpoints

### Data Protection
- BCrypt password hashing
- Environment-based secrets management
- Separate databases per service
- Connection pooling with HikariCP

**Security Details:** [docs/api/AUTHENTICATION.md](docs/api/AUTHENTICATION.md)

---

## 💡 Getting Help

### Quick Questions
👉 **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Common commands & troubleshooting

### Understanding the System
👉 **[ARCHITECTURE.md](ARCHITECTURE.md)** - Design & architecture decisions

### Deployment Issues
👉 **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Setup & troubleshooting

### API Usage
👉 **[docs/api/API_REFERENCE.md](docs/api/API_REFERENCE.md)** - Complete API documentation

### All Documentation
👉 **[docs/README.md](docs/README.md)** - Complete documentation index

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Commit: `git commit -m "feat: add my feature"`
5. Push: `git push origin feature/my-feature`
6. Open a Pull Request

**CI/CD Pipeline** runs automatically on all PRs:
- ✅ Build & test backend (Maven)
- ✅ Build & lint frontend (Node.js)
- ✅ Security scanning (CodeQL)
- ✅ Docker image builds
- ✅ Code review (GitHub Copilot)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 📞 Support & Resources

| Need | Resource |
|------|----------|
| Quick start | [QUICK_REFERENCE.md](QUICK_REFERENCE.md) |
| Full guide | [MICROSERVICES_README.md](MICROSERVICES_README.md) |
| Architecture | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Deployment | [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) |
| API docs | [docs/api/API_REFERENCE.md](docs/api/API_REFERENCE.md) |
| All docs | [docs/README.md](docs/README.md) |

---

## 📊 Project Stats

| Metric | Value |
|--------|-------|
| **Microservices** | 6 services |
| **Databases** | 3 PostgreSQL instances |
| **Java Code** | 50+ classes, 3,000+ LOC |
| **Documentation** | 4,500+ lines across 8 guides |
| **API Endpoints** | 10+ endpoints |
| **Docker Containers** | 10 (local development) |
| **Kubernetes Objects** | 50+ manifests |
| **CI/CD Workflows** | GitHub Actions |

---

**Version:** 2.0.0 (Microservices)  
**Last Updated:** March 19, 2026  
**Status:** ✅ Production-Ready
