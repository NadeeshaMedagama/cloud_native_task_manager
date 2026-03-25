# 🗂️ Reference Documentation

This folder contains reference documentation including database schemas, configuration options, and project structure.

## 📄 Files in This Folder

### [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md) - Database Schema Reference (NEW)
**Complete database schema for all microservices**

Covers:
- Auth database (auth_db) schema
- Task database (task_db) schema
- User database (user_db) schema
- Table structures
- Column definitions
- Data types
- Constraints
- Indexes
- Relationships

**Best for:** Developers, DBAs, anyone working with data

**Read time:** 15-20 minutes

---

### [CONFIGURATION_REFERENCE.md](./CONFIGURATION_REFERENCE.md) - Configuration Reference (NEW)
**Complete configuration and environment variables reference**

Covers:
- All environment variables
- Configuration by service
- Default values
- Required vs optional
- Production settings
- Development settings
- Docker Compose variables
- Kubernetes ConfigMaps

**Best for:** DevOps, system administrators, configuration management

**Read time:** 20-25 minutes

---

### [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) - Project Structure Reference (NEW)
**Complete project directory structure and organization**

Covers:
- Root directory structure
- Microservices directory structure
- Kubernetes manifests structure
- Documentation structure
- Frontend structure
- Build artifacts location
- Configuration files location

**Best for:** New team members, architecture review

**Read time:** 10-15 minutes

---

### [TECHNOLOGY_STACK.md](./TECHNOLOGY_STACK.md) - Technology Stack Reference (NEW)
**Complete technology stack and dependencies**

Covers:
- Java/Spring Boot stack
- Frontend stack
- Database stack
- DevOps stack
- Versions
- Why chosen
- Alternatives
- Future considerations

**Best for:** Architects, tech leads, dependency management

**Read time:** 15-20 minutes

---

## 🎯 Quick Navigation

### Database Structure
→ See [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md)

### Environment Variables
→ See [CONFIGURATION_REFERENCE.md](./CONFIGURATION_REFERENCE.md)

### Directory Structure
→ See [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md)

### Technology Stack
→ See [TECHNOLOGY_STACK.md](./TECHNOLOGY_STACK.md)

---

## 📊 Quick Reference Tables

### Databases
| Database | Port | Purpose |
|----------|------|---------|
| auth_db | 5432 | User credentials & authentication |
| task_db | 5433 | Task data & management |
| user_db | 5434 | User profiles & admin data |

### Microservices
| Service | Port | Purpose |
|---------|------|---------|
| Auth Service | 8081 | Authentication |
| Task Service | 8082 | Task management |
| User Service | 8083 | User admin |
| API Gateway | 8080 | Request routing |
| Service Discovery | 8761 | Eureka registry |

### Technologies
| Layer | Technology | Version |
|-------|-----------|---------|
| **Java** | Spring Boot | 3.2.3 |
| **Java** | Spring Cloud | 2023.0.0 |
| **Database** | PostgreSQL | 16 |
| **Container** | Docker | Latest |
| **Orchestration** | Kubernetes | 1.24+ |
| **Frontend** | Next.js | 14 |
| **Frontend** | React | 18 |

---

## 🔗 Related Documentation

**For API details:**
→ See [../api/API_REFERENCE.md](../api/API_REFERENCE.md)

**For deployment instructions:**
→ See [../deployment/DEPLOYMENT_GUIDE.md](../deployment/DEPLOYMENT_GUIDE.md)

**For architecture understanding:**
→ See [../architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md)

---

## 📋 Configuration by Environment

### Local Development
```
DATABASE: PostgreSQL (3 containers)
SERVICES: Docker Compose (6 services)
CONFIG: .env.local
LOGGING: Console output
```

### Kubernetes (Production)
```
DATABASE: PostgreSQL (managed)
SERVICES: K8s Deployments (independent scaling)
CONFIG: ConfigMaps + Secrets
LOGGING: Aggregated (ELK stack)
```

### Cloud (AWS/GCP/DigitalOcean)
```
DATABASE: Managed database service
SERVICES: Container orchestration
CONFIG: Environment variables
LOGGING: Cloud provider logs
```

---

## 📖 Reference Purposes

| Document | Use Case |
|----------|----------|
| DATABASE_SCHEMA.md | Design queries, migrations, indexes |
| CONFIGURATION_REFERENCE.md | Setup environment, production config |
| PROJECT_STRUCTURE.md | Navigate codebase, understand organization |
| TECHNOLOGY_STACK.md | Understand choices, evaluate alternatives |

---

## 👥 Audience

- ✅ **Developers** - Database schema, configuration
- ✅ **DevOps** - Configuration, project structure
- ✅ **DBAs** - Database schema, optimization
- ✅ **Architects** - Technology stack, structure
- ✅ **New Team Members** - Project structure, overview

---

## 📊 Quick Stats

| Metric | Value |
|--------|-------|
| **Java Services** | 6 microservices |
| **Databases** | 3 PostgreSQL instances |
| **Frontend** | Next.js 14 with React 18 |
| **Configuration Files** | 10+ YAML files |
| **Database Tables** | 3 (users per service) + tasks |
| **API Endpoints** | 10+ endpoints |
| **Docker Containers** | 10 total (production) |
| **K8s Objects** | 50+ (deployments, services, etc.) |

---

**Last Updated:** March 19, 2026  
**Version:** 2.0.0 (Microservices)  
**Status:** Complete Reference Documentation

