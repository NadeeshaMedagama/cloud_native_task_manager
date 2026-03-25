# Kubernetes Setup for Minikube (Local Images)

This directory contains Kubernetes manifests for running the task management platform on a local Minikube cluster.

## What is included

- `namespace.yaml`: Creates namespace `task-manager`
- `configmap.yaml`: Non-sensitive app settings
- `secret.yaml`: Sensitive app settings (replace defaults before use)
- `postgres.yaml`: PostgreSQL deployment, PVC, and service
- `backend.yaml`: Spring Boot backend deployment and service
- `frontend.yaml`: Next.js frontend deployment and service
- `ingress.yaml`: Host-based ingress (`taskmanager.local`)
- `kustomization.yaml`: Applies all manifests together

## 1) Start Minikube and enable ingress

```bash
minikube start
minikube addons enable ingress
```

## 2) Build Docker images inside Minikube Docker daemon

```bash
eval "$(minikube docker-env)"
docker build -t taskmanager/backend:local ./backend
docker build -t taskmanager/frontend:local ./frontend
```

## 3) Update secrets

Edit `k8s/secret.yaml` and set:

- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET` (base64-encoded 256-bit secret, e.g. `openssl rand -base64 32`)

## 4) Deploy everything

```bash
kubectl apply -k k8s
```

## 5) Wait for pods

```bash
kubectl get pods -n task-manager -w
```

## 6) Configure local hostname for ingress

Get Minikube IP:

```bash
minikube ip
```

Add an entry in `/etc/hosts` (replace `<MINIKUBE_IP>`):

```text
<MINIKUBE_IP> taskmanager.local
```

Then open:

- Frontend: `http://taskmanager.local`
- Backend API: `http://taskmanager.local/api`

## Useful troubleshooting

```bash
kubectl get all -n task-manager
kubectl describe pod -n task-manager <pod-name>
kubectl logs -n task-manager deploy/backend
kubectl logs -n task-manager deploy/frontend
kubectl logs -n task-manager deploy/postgres
```

## Notes

- Deployments use `imagePullPolicy: Never` so Kubernetes uses local Minikube images.
- PostgreSQL data is persisted with a PVC (`postgres-pvc`).
- If your backend does not expose `/actuator/health`, adjust probes in `backend.yaml`.

