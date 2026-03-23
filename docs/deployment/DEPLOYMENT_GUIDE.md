# Deployment Guide - Microservices Task Management Platform

## Deployment Environments

This guide covers deployment to:
1. **Local Machine** (Docker Compose)
2. **Kubernetes Cluster** (Production)
3. **Cloud Platforms** (AWS ECS, GCP, Azure AKS, DigitalOcean)

---

## Pre-Deployment Checklist

- [ ] Java 21 installed (for local builds)
- [ ] Maven 3.9+ installed
- [ ] Docker & Docker Compose installed
- [ ] Git repository cloned locally
- [ ] Docker Hub account (for image registry)
- [ ] `.env.local` file created with secrets
- [ ] All tests passing (`mvn clean test`)
- [ ] JWT_SECRET environment variable set
- [ ] Database passwords configured

---

## Part 1: Local Development Deployment

### Step 1.1: Clone the Repository

```bash
git clone https://github.com/your-username/cloud_native_task_manager.git
cd cloud_native_task_manager
```

### Step 1.2: Create Environment File

```bash
cp .env.example .env.local

# Edit .env.local with your values
nano .env.local
```

**.env.local sample:**
```env
SPRING_DATASOURCE_USERNAME=taskuser
SPRING_DATASOURCE_PASSWORD=yoursecurepassword123
JWT_SECRET=ZGV2LW9ubHktcGxhY2Vib2xkZXIta2V5LWNoYW5nZS1tZS1pbi1wcm9kdWN0aW9uIQ==
JWT_EXPIRATION=86400000
DOCKER_REGISTRY=docker.io
DOCKER_USERNAME=your-dockerhub-username
```

### Step 1.3: Start All Services

```bash
# Build and start all containers
docker compose up --build

# Or in detached mode (background)
docker compose up -d --build
```

**Expected output:**
```
[+] Running 11/11
 ✔ service-discovery Pulled
 ✔ auth-db Pulled
 ✔ task-db Pulled
 ✔ user-db Pulled
 ✔ auth-service Built
 ✔ task-service Built
 ✔ user-service Built
 ✔ api-gateway Built
 ✔ frontend Built

[+] Running 11/11
 ✔ Container taskmanager_eureka          Created
 ✔ Container taskmanager_auth_db         Created
 ✔ Container taskmanager_task_db         Created
 ✔ Container taskmanager_user_db         Created
 ✔ Container taskmanager_auth_service    Started
 ✔ Container taskmanager_task_service    Started
 ✔ Container taskmanager_user_service    Started
 ✔ Container taskmanager_api_gateway     Started
 ✔ Container taskmanager_frontend        Started
```

### Step 1.4: Verify Services Are Running

**Check service health:**
```bash
# Wait 30 seconds for services to start
sleep 30

# Check Eureka dashboard
curl http://localhost:8761/eureka/apps
# Should list all services

# Check API Gateway
curl http://localhost:8080/api/auth/register -X OPTIONS -v
# Should return 200 OK

# Check Frontend
curl http://localhost:3000/
# Should return HTML
```

**Check logs:**
```bash
docker compose logs -f auth-service
docker compose logs -f task-service
docker compose logs -f api-gateway
```

### Step 1.5: Access Services

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:3000 | Main UI |
| API Gateway | http://localhost:8080/api | API entry point |
| Auth Service | http://localhost:8081/api | Direct access (debug) |
| Task Service | http://localhost:8082/api | Direct access (debug) |
| User Service | http://localhost:8083/api | Direct access (debug) |
| Eureka Dashboard | http://localhost:8761 | Service registry |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | API documentation |

### Step 1.6: Test the Application

**Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Sample response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER"
}
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Create a task (use token from response above):**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement API docs",
    "description": "Add Swagger documentation",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2026-04-15"
  }'
```

### Step 1.7: Stop Services

```bash
# Stop and remove containers
docker compose down

# Remove volumes (delete all data)
docker compose down -v

# View logs after stopping
docker compose logs --tail=100
```

---

## Part 2: Kubernetes Deployment (Production)

### Prerequisites

- `kubectl` installed and configured
- Access to a Kubernetes cluster (minikube, EKS, GKE, AKS, etc.)
- `helm` (optional, for advanced configurations)
- Docker images pushed to a registry (Docker Hub, ECR, GCR, etc.)

### Step 2.1: Build and Push Docker Images

```bash
# Build images
docker compose build

# Tag images
docker tag task-management-platform/service-discovery:latest your-registry/service-discovery:latest
docker tag task-management-platform/auth-service:latest your-registry/auth-service:latest
docker tag task-management-platform/task-service:latest your-registry/task-service:latest
docker tag task-management-platform/user-service:latest your-registry/user-service:latest
docker tag task-management-platform/api-gateway:latest your-registry/api-gateway:latest
docker tag task-management-platform/frontend:latest your-registry/frontend:latest

# Push to registry
docker push your-registry/service-discovery:latest
docker push your-registry/auth-service:latest
docker push your-registry/task-service:latest
docker push your-registry/user-service:latest
docker push your-registry/api-gateway:latest
docker push your-registry/frontend:latest
```

### Step 2.2: Update Kubernetes Manifests

Edit `k8s/backend.yaml` to use your image registry:

```bash
sed -i 's|task-management-platform/|your-registry/|g' k8s/backend.yaml
sed -i 's|IfNotPresent|Always|g' k8s/backend.yaml  # Always pull latest
```

Or manually:
```yaml
# In k8s/backend.yaml, change:
image: task-management-platform/auth-service:latest
# To:
image: your-registry/auth-service:latest
imagePullPolicy: Always
```

### Step 2.3: Create Kubernetes Namespace and Secrets

```bash
# Create namespace
kubectl create namespace task-management

# Create secrets
kubectl create secret generic task-manager-secrets \
  --from-literal=db-password=yoursecurepassword123 \
  --from-literal=jwt-secret=ZGV2LW9ubHktcGxhY2Vib2xkZXIta2V5LWNoYW5nZS1tZS1pbi1wcm9kdWN0aW9uIQ== \
  -n task-management

# Verify
kubectl get secrets -n task-management
```

### Step 2.4: Deploy with Kustomize

```bash
# Deploy all resources
kubectl apply -k k8s/

# Verify deployment
kubectl get all -n task-management

# Watch rollout status
kubectl rollout status deployment/api-gateway -n task-management
kubectl rollout status deployment/auth-service -n task-management
kubectl rollout status deployment/task-service -n task-management
```

### Step 2.5: Verify Services

```bash
# Check pod status
kubectl get pods -n task-management

# Check services
kubectl get svc -n task-management

# Check ingress
kubectl get ingress -n task-management

# View logs
kubectl logs -f deployment/auth-service -n task-management
kubectl logs -f deployment/api-gateway -n task-management
```

### Step 2.6: Access Services via Port-Forwarding

```bash
# API Gateway
kubectl port-forward -n task-management svc/api-gateway 8080:8080

# Frontend
kubectl port-forward -n task-management svc/frontend 3000:3000

# Eureka (in another terminal)
kubectl port-forward -n task-management svc/service-discovery 8761:8761
```

Then access:
- Frontend: http://localhost:3000
- API: http://localhost:8080/api
- Eureka: http://localhost:8761

### Step 2.7: Configure Ingress (Optional)

If your cluster has an Ingress Controller (NGINX, Traefik, etc.):

```bash
# Update k8s/ingress.yaml with your domain
kubectl apply -f k8s/ingress.yaml

# Add to /etc/hosts (local testing)
echo "127.0.0.1 taskmanager.local" | sudo tee -a /etc/hosts

# Access via ingress
curl http://taskmanager.local/api/auth/register
curl http://taskmanager.local/
```

### Step 2.8: Scaling Services

```bash
# Scale auth-service to 3 replicas
kubectl scale deployment auth-service --replicas=3 -n task-management

# Scale task-service to 5 replicas
kubectl scale deployment task-service --replicas=5 -n task-management

# View autoscaling status
kubectl get hpa -n task-management
```

### Step 2.9: Update Configuration

```bash
# Edit ConfigMap
kubectl edit configmap task-manager-config -n task-management

# Or update via command-line
kubectl set env configmap/task-manager-config \
  JWT_EXPIRATION=172800000 \
  -n task-management

# Restart deployments to apply config changes
kubectl rollout restart deployment/auth-service -n task-management
```

### Step 2.10: Cleanup

```bash
# Delete all resources
kubectl delete -k k8s/

# Or manually
kubectl delete namespace task-management
```

---

## Part 3: Cloud Platform Deployments

### AWS ECS (Elastic Container Service)

```bash
# 1. Push images to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com

docker tag task-management-platform/auth-service:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/auth-service:latest
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/auth-service:latest
# Repeat for other images

# 2. Create ECS cluster
aws ecs create-cluster --cluster-name task-management

# 3. Deploy via CloudFormation or ECS CLI
ecs-cli compose -f docker-compose.yml up --cluster-name task-management --region us-east-1

# 4. View service status
ecs-cli ps --cluster-name task-management
```

### Google Cloud Run (Serverless)

```bash
# 1. Enable Cloud Run API
gcloud services enable run.googleapis.com

# 2. Build and push images to GCR
gcloud builds submit --tag gcr.io/PROJECT_ID/auth-service

# 3. Deploy services
gcloud run deploy auth-service \
  --image gcr.io/PROJECT_ID/auth-service \
  --platform managed \
  --region us-central1 \
  --set-env-vars SPRING_DATASOURCE_URL=cloudsql://PROJECT:INSTANCE/auth_db

# 4. Access service
curl https://auth-service-xxxxx.a.run.app/health
```

### DigitalOcean Kubernetes (DOKS)

```bash
# 1. Create DOKS cluster
doctl kubernetes cluster create task-management

# 2. Get kubeconfig
doctl kubernetes cluster kubeconfig save task-management

# 3. Deploy with kubectl
kubectl apply -k k8s/

# 4. View services
doctl kubernetes cluster get task-management

# 5. Access via LoadBalancer
kubectl get svc -n task-management
# Get EXTERNAL-IP and access frontend/api
```

---

## Monitoring & Observability

### Prometheus & Grafana (Kubernetes)

```bash
# Install Prometheus via Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/prometheus -n task-management

# Install Grafana
helm repo add grafana https://grafana.github.io/helm-charts
helm install grafana grafana/grafana -n task-management

# Port-forward to Grafana
kubectl port-forward -n task-management svc/grafana 3001:80

# Access: http://localhost:3001 (default admin/admin)
```

### ELK Stack (Logging)

```bash
# Install via Helm
helm repo add elastic https://Helm.elastic.co
helm install elasticsearch elastic/elasticsearch -n task-management
helm install logstash elastic/logstash -n task-management
helm install kibana elastic/kibana -n task-management

# Access Kibana
kubectl port-forward -n task-management svc/kibana 5601:5601
# Visit: http://localhost:5601
```

---

## Troubleshooting Deployment Issues

### Issue: Pods Stuck in Pending

```bash
# Check node status
kubectl get nodes

# Check pod events
kubectl describe pod pod-name -n task-management

# Common causes: Insufficient resources, image pull errors
```

### Issue: CrashLoopBackOff

```bash
# Check logs
kubectl logs pod-name -n task-management

# Common causes: Database connection failed, missing environment variables
```

### Issue: Services Not Communicating

```bash
# Test DNS resolution
kubectl exec -it pod-name -n task-management -- nslookup auth-service

# Test network connectivity
kubectl exec -it pod-name -n task-management -- curl http://auth-service:8081/actuator/health

# Check network policies
kubectl get networkpolicy -n task-management
```

### Issue: Database Connection Timeout

```bash
# Verify database is running
kubectl get pods -n task-management | grep db

# Check database logs
kubectl logs deployment/auth-db -n task-management

# Verify connection string in ConfigMap
kubectl get configmap task-manager-config -n task-management -o yaml
```

---

## Security Checklist for Production

- [ ] Enable HTTPS/TLS on API Gateway (cert-manager)
- [ ] Rotate JWT_SECRET regularly
- [ ] Use strong database passwords (min 20 chars, alphanumeric + special)
- [ ] Enable Pod Security Policies (PSP) or Pod Security Standards (PSS)
- [ ] Use NetworkPolicies to restrict inter-pod communication
- [ ] Enable RBAC (Role-Based Access Control)
- [ ] Scan images for vulnerabilities (Trivy, Snyk)
- [ ] Set resource limits and requests for all pods
- [ ] Enable pod autoscaling (HPA) for high-traffic services
- [ ] Implement rate limiting on API Gateway
- [ ] Enable audit logging in Kubernetes
- [ ] Use sealed secrets for sensitive data
- [ ] Implement backup strategy for databases
- [ ] Enable monitoring and alerting

---

## Performance Tuning

### Database Connection Pooling

In `application.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
```

### JVM Heap Size

In Dockerfile or K8s deployment:
```
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC"
```

### API Gateway Rate Limiting

In `api-gateway/application.yml`:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 10
                  burstCapacity: 20
```

---

## Backup & Disaster Recovery

### Database Backups

```bash
# Manual backup
docker exec taskmanager_auth_db pg_dump -U taskuser auth_db > auth_db_backup.sql

# Automated backup (cron)
0 2 * * * docker exec taskmanager_auth_db pg_dump -U taskuser auth_db | gzip > /backups/auth_db_$(date +\%Y\%m\%d_\%H\%M\%S).sql.gz

# Restore from backup
docker exec -i taskmanager_auth_db psql -U taskuser auth_db < auth_db_backup.sql
```

### Kubernetes PVC Backups

```bash
# Create backup
kubectl get pvc -n task-management
# Use cloud provider snapshots or Velero for full cluster backups
```

---

## Rolling Updates & Zero-Downtime Deployment

### Kubernetes Rolling Update

```bash
# Update image
kubectl set image deployment/auth-service \
  auth-service=your-registry/auth-service:v2.0.1 \
  -n task-management

# Monitor rollout
kubectl rollout status deployment/auth-service -n task-management

# Rollback if needed
kubectl rollout undo deployment/auth-service -n task-management
```

### Blue-Green Deployment

```bash
# Deploy new version (green)
kubectl apply -f k8s/backend-v2.yaml -n task-management

# Switch ingress to green
kubectl patch ingress task-management-ingress -p '{"spec":{"rules":[{"http":{"paths":[{"backend":{"serviceName":"api-gateway-v2"}}]}}]}}' -n task-management

# Remove blue if stable
kubectl delete deployment api-gateway -n task-management
```

---

## Cost Optimization

1. **Reduce Replica Counts**
   ```bash
   kubectl scale deployment task-service --replicas=1 -n task-management
   ```

2. **Right-size Resource Requests**
   - Monitor actual usage: `kubectl top pods -n task-management`
   - Adjust resource limits downward if overprovisioned

3. **Use Spot Instances** (AWS, GCP)
   - 70% cost reduction for non-critical workloads

4. **Implement Auto-Shutdown**
   - Scale down to 0 during off-hours

5. **Database Optimization**
   - Remove unused indexes
   - Archive old task records
   - Use read replicas for reporting

---

## Support & Troubleshooting

For issues, consult:
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Docker Documentation](https://docs.docker.com/)
- Project Issues: [GitHub Issues](https://github.com/your-repo/issues)

