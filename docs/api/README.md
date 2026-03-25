# 📡 API Documentation

This folder contains complete API documentation and reference.

## 📄 Files in This Folder

### [API_REFERENCE.md](./API_REFERENCE.md) - Complete API Reference (NEW)
**Full API documentation with examples**

Covers:
- Authentication endpoints (register, login)
- Task endpoints (CRUD, filtering, pagination)
- User endpoints (list, get - admin only)
- Request/response formats
- Error codes & messages
- Authentication & authorization
- Rate limiting
- API versioning
- Swagger/OpenAPI documentation

**Best for:** Developers, QA, API consumers

**Read time:** 20-30 minutes

---

### [AUTHENTICATION.md](./AUTHENTICATION.md) - Authentication Guide (NEW)
**Complete authentication & authorization reference**

Covers:
- JWT token structure
- Registration flow
- Login flow
- Token validation
- Token refresh
- Permission model
- Role-based access control (RBAC)
- OAuth (if applicable)
- API key management

**Best for:** Developers implementing auth, security review

**Read time:** 15-20 minutes

---

### [ERROR_CODES.md](./ERROR_CODES.md) - Error Codes Reference (NEW)
**Complete list of API error codes and messages**

Covers:
- HTTP status codes (400, 401, 403, 404, 500, etc.)
- Error response format
- Common error scenarios
- Error handling in client
- Retry strategies
- Rate limit errors

**Best for:** Developers, API consumers, QA

**Read time:** 10-15 minutes

---

## 🎯 Quick Navigation

### I Want to Use the API
→ Start with [API_REFERENCE.md](./API_REFERENCE.md)

### I Need to Implement Authentication
→ Read [AUTHENTICATION.md](./AUTHENTICATION.md)

### What Error Codes Can I Get?
→ See [ERROR_CODES.md](./ERROR_CODES.md)

### Example API Calls
→ Check [API_REFERENCE.md](./API_REFERENCE.md) (examples section)

---

## 📡 API Endpoints Overview

### Authentication Service (Port 8081 / 8080/api/auth)
```
POST /auth/register    - Register new user
POST /auth/login       - Login & get JWT token
```

### Task Service (Port 8082 / 8080/api/tasks)
```
GET    /tasks                    - List tasks
POST   /tasks                    - Create task
GET    /tasks/{id}               - Get task
PUT    /tasks/{id}               - Update task
PATCH  /tasks/{id}/status        - Update status only
DELETE /tasks/{id}               - Delete task
```

### User Service (Port 8083 / 8080/api/users) - Admin Only
```
GET    /users                    - List all users
GET    /users/{id}               - Get user details
```

---

## 🔑 Quick Reference

### Default Credentials
```
Username: admin
Password: admin123
```

### API Gateway URL (Development)
```
http://localhost:8080/api
```

### Service Direct URLs
```
Auth:  http://localhost:8081/api
Tasks: http://localhost:8082/api
Users: http://localhost:8083/api
```

### Authorization Header
```
Authorization: Bearer <jwt_token>
```

---

## 📋 API Categories

| Endpoint | Method | Auth | Role | Purpose |
|----------|--------|------|------|---------|
| /auth/register | POST | ❌ | - | Create account |
| /auth/login | POST | ❌ | - | Get token |
| /tasks | GET | ✅ | USER | List tasks |
| /tasks | POST | ✅ | USER | Create task |
| /tasks/{id} | GET | ✅ | USER | Get task |
| /tasks/{id} | PUT | ✅ | USER | Update task |
| /tasks/{id}/status | PATCH | ✅ | USER | Update status |
| /tasks/{id} | DELETE | ✅ | USER | Delete task |
| /users | GET | ✅ | ADMIN | List users |
| /users/{id} | GET | ✅ | ADMIN | Get user |

---

## 🔗 Related Documentation

**For deployment details:**
→ See [../deployment/README.md](../deployment/README.md)

**For quick commands:**
→ See [../guides/QUICK_REFERENCE.md](../guides/QUICK_REFERENCE.md)

**For architecture:**
→ See [../architecture/README.md](../architecture/README.md)

---

## 🧪 Testing the API

### Using cURL
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123"}'

# List tasks (replace TOKEN with actual token)
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/api/tasks
```

### Using Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

### Using Postman
1. Import API collection
2. Set Authorization header
3. Run requests

---

## 👥 Audience

- ✅ **Frontend Developers** - Using the API
- ✅ **Mobile Developers** - API consumers
- ✅ **Backend Developers** - Implementing API
- ✅ **QA Engineers** - Testing endpoints
- ✅ **API Consumers** - Third-party integrations

---

## 📖 Learning Path

### Beginner (30 minutes)
1. Read: API_REFERENCE.md (overview section) - 10 min
2. Try: Example API calls with cURL - 10 min
3. Explore: Swagger UI - 10 min

### Intermediate (45 minutes)
1. Read: API_REFERENCE.md (complete) - 20 min
2. Read: AUTHENTICATION.md - 15 min
3. Practice: Build client code - 10 min

### Advanced (60+ minutes)
1. Read: All API docs - 30 min
2. Read: [../architecture/README.md](../architecture/README.md) - 45 min
3. Implement: Complete client - varies

---

**Last Updated:** March 19, 2026  
**Version:** 2.0.0 (Microservices)  
**Status:** Complete & Comprehensive

