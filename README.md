# ✈️ Flight Booking Platform

> A production-inspired distributed Flight Booking Platform built with **Spring Boot Microservices**, **Kafka**, **gRPC**, **PostgreSQL**, **Docker**, and **Kubernetes**.

This project demonstrates how modern distributed systems handle consistency, reliability, fault tolerance, and inter-service communication using **Event-Driven Architecture**, **Saga Pattern**, **Transactional Outbox Pattern**, and **gRPC**.

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
- [Monitoring](#-monitoring)
- [Tested Scenarios](#-tested-scenarios)
- [Roadmap](#-roadmap)

---

## 🏗️ Architecture

```
          +------------------+
          |   Auth Service   |
          +--------+---------+
                   |
                   | JWT Validation
                   ▼
+------------------+    Kafka     +-------------------+    Kafka     +------------------+
|  Booking Service | -----------> | Inventory Service | -----------> | Payment Service  |
+------------------+              +-------------------+              +------------------+
         ▲                                                                    |
         |                                                                    |
         +--------------------------------------------------------------------+
                              Event-Driven Saga (Compensation)
```

**Communication Patterns:**
- `BookingService → InventoryService` — **gRPC** (fast synchronous seat reservation)
- `BookingService → InventoryService → PaymentService` — **Kafka** (async Saga choreography)
- All services expose **REST APIs**

---

## 🧩 Services

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

---

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3 |
| **Security** | Spring Security, JWT, BCrypt |
| **Persistence** | Spring Data JPA, PostgreSQL |
| **Messaging** | Apache Kafka, Zookeeper |
| **Communication** | REST APIs, gRPC, Protocol Buffers |
| **Reliability** | Resilience4j (Retry, Circuit Breaker, Timeout) |
| **Distributed Systems** | Saga Choreography, Transactional Outbox, Idempotency |
| **DevOps** | Docker, Docker Compose, Kubernetes *(In Progress)* |
| **Monitoring** | Prometheus, Grafana |

---

## 📁 Project Structure

```
FlightBooking/
├── AuthService/              # JWT-based authentication
├── BookingService/           # Saga orchestrator + Outbox publisher
├── InventoryService/         # Seat management + gRPC server
├── PaymentService/           # Payment processing + compensation
├── SharedEvents/             # Shared Kafka event DTOs (common library)
├── ProtoContracts/           # Protobuf definitions for gRPC
├── docker-compose.yml        # Full local environment
├── init-db.sql               # Database initialization
└── k8s/                      # Kubernetes manifests (In Progress)
```

---

## 🔄 Booking Workflow

### ✅ Successful Booking Flow

```
User Request
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
                              ◀───────Kafka────────
                                      │
                              Booking Service
                                      │
                                      ▼
                              BOOKING_CONFIRMED ✅
```

### ❌ Failed Payment Flow (Compensation)

```
... (same as above until SEAT_LOCKED) ...
                                      │
                               [PAYMENT_FAILED]
                                      │
                              ──────Kafka──────▶  Inventory Service
                                                       │
                                                 [SEAT_RELEASED]
                                                       │
                                              ◀────────Kafka──────
                                                       │
                                              Booking Service
                                                       │
                                                       ▼
                                              BOOKING_CANCELLED ❌
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
- gRPC Java
- Spring Boot gRPC integration

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
git clone https://github.com/your-username/FlightBooking.git
cd FlightBooking

docker compose up --build
```

### Services & Ports

| Service | Port |
|---|---|
| Auth Service | `8081` |
| Booking Service | `8082` |
| Inventory Service | `8083` (REST) / `9090` (gRPC) |
| Payment Service | `8084` |
| PostgreSQL | `5432` |
| PgAdmin | `5050` |
| Kafka | `9092` |
| Kafka UI | `8090` |
| Prometheus | `9091` |
| Grafana | `3000` |

### Quick Test (Happy Path)

```bash
# 1. Register a user
POST http://localhost:8081/api/auth/register

# 2. Login and get JWT
POST http://localhost:8081/api/auth/login

# 3. Create seats (admin)
POST http://localhost:8083/api/inventory/seats

# 4. Create a booking
POST http://localhost:8082/api/bookings
Header: Authorization: Bearer <token>
Header: Idempotency-Key: <uuid>

# 5. Watch booking status change
GET http://localhost:8082/api/bookings/{bookingId}
```

---

## 📊 Monitoring

### Prometheus
Collects metrics exposed by Spring Actuator:
- JVM metrics (heap, GC, threads)
- HTTP request metrics (latency, error rates)
- Custom application metrics

**URL:** `http://localhost:9091`

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

---

## 🗺️ Roadmap

- [ ] **Kubernetes Deployment** — Deployments, Services, ConfigMaps, Ingress
- [ ] **Jenkins CI/CD Pipeline** — Automated build, test, and deploy
- [ ] **Distributed Tracing** — Zipkin / OpenTelemetry across all services
- [ ] **API Gateway** — Centralized routing, rate limiting, auth filter
- [ ] **Redis Caching** — Cache seat availability, reduce DB load
- [ ] **Centralized Configuration** — Spring Cloud Config Server
- [ ] **Service Mesh** — Istio exploration for observability and traffic control

---

## 🧠 Concepts Demonstrated

| Concept | Implementation |
|---|---|
| Microservices Architecture | 4 independent Spring Boot services |
| Event-Driven Architecture | Kafka-based async communication |
| Saga Pattern (Choreography) | Booking → Inventory → Payment chain |
| Distributed Transactions | Compensation on failure |
| Transactional Outbox | Reliable event publishing |
| gRPC | High-performance sync RPC |
| Idempotency | Duplicate request handling |
| Resilience Patterns | Retry, Circuit Breaker, Timeout |
| Containerization | Docker + Docker Compose |
| Observability | Prometheus + Grafana |

---

> Built as a distributed systems learning project to explore modern backend architecture patterns: Microservices, Event-Driven Systems, Distributed Transactions, Reliability Engineering, and Cloud-Native Development.
