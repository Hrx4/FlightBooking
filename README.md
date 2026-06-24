# ✈️ Flight Booking Platform

> A production-inspired distributed Flight Booking Platform built with **Spring Boot Microservices**, **Kafka**, **gRPC**, **PostgreSQL**, **Docker**, **Kubernetes**, and **Jenkins CI/CD**.

This project demonstrates how modern distributed systems handle consistency, reliability, fault tolerance, and inter-service communication using **Event-Driven Architecture**, **Saga Pattern**, **Transactional Outbox Pattern**, **gRPC**, and **Cloud-Native Deployment**.

---

## 📑 Table of Contents

- [Architecture](#-architecture)
- [Services](#-services)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Booking Workflow](#-booking-workflow)
- [Transactional Outbox Pattern](#-transactional-outbox-pattern)
- [gRPC Communication](#-grpc-communication)
- [Reliability Features](#-reliability-features)
- [Getting Started](#-getting-started)
- [Kubernetes Deployment](#-kubernetes-deployment)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Monitoring](#-monitoring)
- [Tested Scenarios](#-tested-scenarios)

---

## 🏗️ Architecture

```
                         +------------------+
                         |   API Gateway    |  ← JWT Validation, Rate Limiting, Routing
                         +--------+---------+
                                  |
              ┌───────────────────┼────────────────────┐
              ▼                   ▼                    ▼
   +------------------+  +------------------+  +------------------+
   |   Auth Service   |  | Booking Service  |  | Payment Service  |
   +------------------+  +--------+---------+  +------------------+
                                  │  gRPC                |
                                  ▼                      |
                         +-------------------+           |
                         | Inventory Service |           |
                         +-------------------+           |
                                  │                      │
                                  └────────┬─────────────┘
                                           │ Kafka (Saga Choreography)
                                           ▼
                                +----------------------+
                                | Notification Service |
                                +----------------------+
```

**Communication Patterns:**
- `Client → All Services` — **API Gateway** (centralized routing, JWT auth, rate limiting)
- `BookingService → InventoryService` — **gRPC** (fast synchronous seat reservation)
- `BookingService → InventoryService → PaymentService` — **Kafka** (async Saga choreography)
- `PaymentService → NotificationService` — **Kafka** (async booking confirmation/cancellation emails)
- All services expose **REST APIs**

---

## 🧩 Services

### 🌐 API Gateway
| Feature | Details |
|---|---|
| Centralized Routing | Single entry point for all client requests |
| JWT Authentication Filter | Validates tokens before forwarding requests |
| Rate Limiting | Protects downstream services from traffic spikes |
| Path-based Routing | Routes `/api/auth/**`, `/api/bookings/**`, etc. |
| Spring Cloud Gateway | Reactive, non-blocking gateway implementation |

### 🔐 Auth Service
| Feature | Details |
|---|---|
| User Registration | Create accounts with role assignment |
| User Login | Returns signed JWT token |
| JWT Authentication | Stateless token-based auth |
| Password Encryption | BCrypt hashing |
| Role-Based Access | Foundation for RBAC |

### 📋 Booking Service
| Feature | Details |
|---|---|
| Flight Booking Creation | Creates bookings with idempotency key |
| Booking Status Tracking | Tracks `PENDING → CONFIRMED / CANCELLED` |
| Idempotent Requests | Prevents duplicate bookings |
| Saga Orchestration | Choreographs multi-service transaction |
| Transactional Outbox | Guarantees reliable Kafka event publishing |

### 🪑 Inventory Service
| Feature | Details |
|---|---|
| Seat Creation | Admin API to create flight seats |
| Seat Reservation | Locks seat during booking flow |
| Seat Release | Releases seat on payment failure |
| Availability Management | Tracks seat states |
| gRPC APIs | High-performance sync API for BookingService |

### 💳 Payment Service
| Feature | Details |
|---|---|
| Payment Processing | Processes payment for confirmed bookings |
| Payment Success Flow | Triggers `BOOKING_CONFIRMED` |
| Payment Failure Simulation | Triggers compensation saga |
| Compensation Triggering | Publishes `PAYMENT_FAILED` to release seat |

### 🔔 Notification Service
| Feature | Details |
|---|---|
| Booking Confirmation Email | Sends confirmation on `BOOKING_CONFIRMED` event |
| Booking Cancellation Email | Sends cancellation on `BOOKING_CANCELLED` event |
| Kafka Consumer | Subscribes to payment outcome events |
| Async Processing | Non-blocking; does not affect core booking flow |
| Extensible | Easily extend to SMS, push notifications |

---

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3 |
| **API Gateway** | Spring Cloud Gateway |
| **Security** | Spring Security, JWT, BCrypt |
| **Persistence** | Spring Data JPA, PostgreSQL |
| **Messaging** | Apache Kafka |
| **Communication** | REST APIs, gRPC, Protocol Buffers |
| **Reliability** | Resilience4j (Retry, Circuit Breaker, Timeout) |
| **Distributed Systems** | Saga Choreography, Transactional Outbox, Idempotency |
| **DevOps** | Docker, Docker Compose, Kubernetes, Jenkins |
| **Monitoring** | Prometheus, Grafana |

---

## 📁 Project Structure

```
FlightBooking/
├── ApiGateway/               # Spring Cloud Gateway — routing, auth filter, rate limiting
├── AuthService/              # JWT-based authentication
├── BookingService/           # Saga orchestrator + Outbox publisher
├── InventoryService/         # Seat management + gRPC server
├── PaymentService/           # Payment processing + compensation
├── NotificationService/      # Email notifications via Kafka events
├── SharedEvents/             # Shared Kafka event DTOs (common library)
├── ProtoContracts/           # Protobuf definitions for gRPC
├── docker-compose.infra.yml  
├── Jenkinsfile               # CI/CD pipeline definition
├── init-db.sql               # Database initialization
└── k8s/        # Kubernetes manifests (Kustomize)
    
```

---

## 🔄 Booking Workflow

### ✅ Successful Booking Flow

```
Client Request
    │
    ▼
API Gateway  ──JWT check──▶  (forward if valid)
    │
    ▼
Booking Service  ──gRPC──▶  Inventory Service
    │                            │
    │   [BOOKING_CREATED]        │  [SEAT_LOCKED]
    │ ◀──────Kafka───────────────┘
    │
    │   [SEAT_LOCKED]
    │ ──────Kafka──────────────▶  Payment Service
                                      │
                               [PAYMENT_SUCCESS]
                                      │
                      ┌───────────────┴──────────────────┐
                      ▼                                  ▼
              Booking Service                  Notification Service
              (BOOKING_CONFIRMED ✅)           (sends confirmation email 📧)
```

### ❌ Failed Payment Flow (Compensation)

```
... (same as above until SEAT_LOCKED) ...
                                      │
                               [PAYMENT_FAILED]
                                      │
                ┌─────────────────────┴──────────────────┐
                ▼                                        ▼
     Inventory Service                        Notification Service
     (releases seat)                          (sends cancellation email 📧)
          │
    [SEAT_RELEASED]
          │
    Booking Service
    (BOOKING_CANCELLED ❌)
```

---

## 📬 Transactional Outbox Pattern

> Guarantees that Kafka events are **never lost**, even during crashes or Kafka downtime.

```
Booking Request
      │
      ▼
 ┌─────────────────────────────────┐
 │  Single DB Transaction          │
 │  1. Save Booking (PENDING)      │
 │  2. Save Outbox Event           │
 └───────────────┬─────────────────┘
                 │
                 ▼
      Scheduler polls Outbox table
                 │
                 ▼
        Publish to Kafka
                 │
                 ▼
        Mark Event as PROCESSED
```

**Benefits:**
- ✅ No lost events on application crash
- ✅ Handles Kafka outages gracefully
- ✅ Guarantees at-least-once delivery

---

## ⚡ gRPC Communication

The **Inventory Service** exposes gRPC APIs consumed by the **Booking Service** for fast, synchronous seat reservation before publishing to Kafka.

```
BookingService  ──── gRPC Call ────▶  InventoryService
                   (reserve seat)       (returns OK / UNAVAILABLE)
```

**Implementation:**
- Protocol Buffers (`.proto` files in `ProtoContracts/`)
- gRPC Java (`net.devh` on InventoryService, `org.springframework.grpc` on BookingService)
- Wire-compatible across libraries; gRPC on port `9091`, HTTP on `8083`

---

## 🛡️ Reliability Features

### Retry (Resilience4j)
```yaml
max-attempts: 3
wait-duration: 500ms
retry-on: [InventoryUnavailableException, PaymentException]
```

### Circuit Breaker
Protects downstream calls to:
- **Inventory Service** (gRPC)
- **Payment Service** (Kafka publish)

Prevents cascading failures when a service is down.

### Timeout Handling
Configures deadlines on gRPC calls and Kafka publish operations to prevent thread starvation.

### Idempotency
Every booking request carries an **Idempotency Key** — duplicate requests within a time window return the original response without re-processing.

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21
- Maven


### Run the entire platform

```bash
git clone https://github.com/hrx4/FlightBooking.git
cd FlightBooking
docker compose -f docker-compose.infra.yml up -d

```

### Services & Ports

| Service | Port |
|---|---|
| API Gateway | `8080` |
| Auth Service | `8081` |
| Booking Service | `8082` |
| Inventory Service | `8083` (REST) / `9091` (gRPC) |
| Payment Service | `8084` |
| Notification Service | `8085` |
| PostgreSQL | `5432` |
| PgAdmin | `5050` |
| Kafka | `9092` |
| Kafka UI | `8090` |
| Prometheus | `9090` |
| Grafana | `3000` |

### Quick Test (Happy Path)

All requests go through the **API Gateway** on port `8080`:

```bash
# 1. Register a user
POST http://localhost:8080/api/auth/register

# 2. Login and get JWT
POST http://localhost:8080/api/auth/login

# 3. Create seats (admin)
POST http://localhost:8080/api/inventory/seats

# 4. Create a booking
POST http://localhost:8080/api/bookings
Header: Authorization: Bearer <token>
Header: Idempotency-Key: <uuid>

# 5. Watch booking status change (PENDING → CONFIRMED)
GET http://localhost:8080/api/bookings/{bookingId}
```

---

## ☸️ Kubernetes Deployment

The platform deploys to Kubernetes using **Kustomize** (base + overlays pattern).

### Namespace

```bash
kubectl get pods -n flightbooking
```

### Apply manifests

```bash
# Dev overlay
kubectl apply -k k8s/<service>
```

### Access services (local cluster)

```bash
# Port-forward any service
kubectl port-forward svc/booking-service 8082:8082 -n flightbooking
kubectl port-forward svc/api-gateway 8080:8080 -n flightbooking
```

### Key design decisions
- All services use `ClusterIP` — external access via `kubectl port-forward` for local dev
- `imagePullPolicy: IfNotPresent` with `:latest` tag for local development
- Spring Boot Actuator probes on `/actuator/health/readiness` and `/actuator/health/liveness`

---

## 🔧 CI/CD Pipeline (Jenkins)

The Jenkins pipeline runs in Docker and automates the full build → push → deploy lifecycle.

```
┌─────────────────────────────────────────────────────────┐
│                    Jenkinsfile                          │
│                                                         │
│  Checkout → Build (Maven) → Docker Build → Docker Push  │
│                  → kubectl apply (K8s deploy)           │
└─────────────────────────────────────────────────────────┘
```

### Pipeline stages

| Stage | Description |
|---|---|
| **Checkout** | Pulls latest code from SCM |
| **Build SharedEvents** | Installs shared Kafka event library to local `.m2` |
| **Build Services** | `mvn clean package -DskipTests` for each service |
| **Docker Build & Push** | Builds images tagged `hrx4/<service>:latest`, pushes to DockerHub |
| **Deploy to K8s** | `kubectl apply -k k8s/<service>` via `KUBECONFIG` credential |

### Jenkins setup

Jenkins runs as a custom Docker image with:
- Docker CLI (socket-mounted from host, `DOCKER_GID` passed as build arg)
- `kubectl` for Kubernetes deployments
- Maven + Java 21

```bash
# Start Jenkins
cd jenkins
docker compose up -d
```

### Required Jenkins credentials

| Credential ID | Type | Description |
|---|---|---|
| `docker-hub-credentials` | Username/Password | DockerHub login |
| `kubeconfig` | Secret file | Kubernetes cluster config |

---

## 📊 Monitoring

### Prometheus
Collects metrics exposed by Spring Actuator:
- JVM metrics (heap, GC, threads)
- HTTP request metrics (latency, error rates)
- Custom application metrics

**URL:** `http://localhost:9090`

### Grafana
Pre-built dashboards to visualize:
- Service health
- Kafka consumer lag
- Booking success/failure rates

**URL:** `http://localhost:3000` (default: `admin/admin`)

---

## ✅ Tested Scenarios

| Scenario | Status |
|---|---|
| Successful Booking Flow | ✅ |
| Failed Payment + Compensation Flow | ✅ |
| Kafka Outage Recovery (Outbox) | ✅ |
| Duplicate Booking Prevention (Idempotency) | ✅ |
| Seat Reservation via gRPC | ✅ |
| Circuit Breaker Triggering | ✅ |
| Retry on Transient Failures | ✅ |
| Dockerized Full Stack Deployment | ✅ |
| Booking Confirmation Notification | ✅ |
| Cancellation Notification on Payment Failure | ✅ |
| API Gateway JWT Filtering | ✅ |
| API Gateway Rate Limiting | ✅ |
| Kubernetes Deployment (Docker Desktop) | ✅ |
| Jenkins CI/CD Pipeline (Build → Push → Deploy) | ✅ |

---

## 🧠 Concepts Demonstrated

| Concept | Implementation |
|---|---|
| Microservices Architecture | 6 independent Spring Boot services |
| API Gateway Pattern | Spring Cloud Gateway with JWT filter and rate limiting |
| Event-Driven Architecture | Kafka-based async communication |
| Saga Pattern (Choreography) | Booking → Inventory → Payment → Notification chain |
| Distributed Transactions | Compensation on failure |
| Transactional Outbox | Reliable event publishing |
| gRPC | High-performance sync RPC |
| Idempotency | Duplicate request handling |
| Resilience Patterns | Retry, Circuit Breaker, Timeout |
| Containerization | Docker + Docker Compose |
| Container Orchestration | Kubernetes with Kustomize (base + overlays) |
| CI/CD | Jenkins pipeline (build, push, deploy) |
| Observability | Prometheus + Grafana |

---

> Built as a distributed systems portfolio project to demonstrate production-grade backend architecture: Microservices, Event-Driven Systems, Distributed Transactions, Reliability Engineering, Cloud-Native Deployment, and DevOps automation.