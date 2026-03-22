#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

print_help() {
  cat <<'EOF'
Cloud_Native_Task_Manager local runner

Usage:
  ./run-local.sh <mode>

Modes:
  all         Start backend stack (docker compose) + frontend dev server
  backend     Start backend stack with docker compose
  frontend    Start frontend dev server only
  stop        Stop backend docker compose services
  logs        Follow backend docker compose logs
  help        Show this help message

Examples:
  ./run-local.sh backend
  ./run-local.sh frontend
  ./run-local.sh all
EOF
}

ensure_cmd() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Error: required command not found: $cmd" >&2
    exit 1
  fi
}

start_backend() {
  ensure_cmd docker
  (cd "$ROOT_DIR" && docker compose up --build)
}

start_frontend() {
  ensure_cmd npm
  if [[ ! -d "$ROOT_DIR/frontend" ]]; then
    echo "Error: frontend directory not found at $ROOT_DIR/frontend" >&2
    exit 1
  fi

  cd "$ROOT_DIR/frontend"

  # Install dependencies if node_modules is missing.
  if [[ ! -d node_modules ]]; then
    echo "node_modules not found. Installing dependencies..."
    npm install
  fi

  npm run dev
}

start_all() {
  ensure_cmd docker
  ensure_cmd npm

  echo "Starting backend stack in background..."
  (cd "$ROOT_DIR" && docker compose up --build -d)

  cat <<'EOF'
Backend stack started in detached mode.
- API Gateway: http://localhost:8080/api
- Eureka: http://localhost:8761

Starting frontend dev server in foreground...
EOF

  start_frontend
}

stop_backend() {
  ensure_cmd docker
  (cd "$ROOT_DIR" && docker compose down)
}

show_logs() {
  ensure_cmd docker
  (cd "$ROOT_DIR" && docker compose logs -f)
}

MODE="${1:-help}"

case "$MODE" in
  all)
    start_all
    ;;
  backend)
    start_backend
    ;;
  frontend)
    start_frontend
    ;;
  stop)
    stop_backend
    ;;
  logs)
    show_logs
    ;;
  help|-h|--help)
    print_help
    ;;
  *)
    echo "Unknown mode: $MODE" >&2
    echo
    print_help
    exit 1
    ;;
esac

