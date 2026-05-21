# Event-Driven Notification System

An event-driven notification platform built with 3 Spring Boot microservices.

- Ingest events via REST
- Stream with Kafka
- Persist to PostgreSQL
- Deliver with RabbitMQ + retries + DLQ
- Push real-time user notifications over WebSocket

## Architecture

```
[REST Client / Postman]
        |
        | POST /api/events
        v
+-------------------+
| event-producer    |  Spring Boot - Port 8081
+-------------------+
        |
        | Kafka Topic: notification.events
        v
+-------------------+     writes      +------------+
| notification-     | ------------->  | PostgreSQL |
| processor         |                 | (5432)     |
| (Port 8082)       |                 +------------+
+-------------------+
        |
        | RabbitMQ Exchange: notifications.exchange
        | Queue: notifications.queue
        | DLQ: notifications.dlq
        v
+-------------------+
| notification-     |  Spring Boot - Port 8083
| gateway           |
+-------------------+
        |
        | ws://localhost:8083/ws
        | /topic/notifications/{userId}
        v
[Browser / WebSocket client]
```

## Tech Stack

![Java 17](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6.x-black)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12.x-ff6600)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

## Services

| Service | Port | Responsibility |
|---|---:|---|
| event-producer | 8081 | Accept REST events and publish to Kafka |
| notification-processor | 8082 | Consume Kafka, store in PostgreSQL, publish to RabbitMQ |
| notification-gateway | 8083 | Consume RabbitMQ, deliver WebSocket, expose history API |
| PostgreSQL | 5432 | Notification persistence |
| RabbitMQ | 5672 / 15672 | Broker, retry, DLQ |
| Kafka | 9092 | Event stream backbone |
| Zookeeper | 2181 | Kafka coordination |

## API

- `POST /api/events` -> publish event
- `GET /api/notifications/{userId}` -> fetch stored notification history
- `GET /api/notifications/health` -> gateway health

## Local Run

```bash
docker compose up --build
```

## Evidence-First Validation

### Claim 1: Event is accepted and published to Kafka

Paste:
- Postman request screenshot for `POST /api/events`
- Success response body
- `event-producer` log line showing publish

Placeholder:

```md
![POST event request success](docs/proof/01-post-event-success.png)
```

### Claim 2: Processor consumes event, saves DB row, publishes to RabbitMQ

Paste:
- `notification-processor` logs with:
  - `Received event from Kafka`
  - `Notification saved to PostgreSQL`
  - `Publishing notification to RabbitMQ`

Placeholder:

```md
![Processor Kafka->DB->Rabbit flow](docs/proof/02-processor-flow-logs.png)
```

### Claim 3: Gateway pushes real-time notification over WebSocket

Paste:
- WebSocket/STOMP client connected to `/ws`
- subscription to `/topic/notifications/user-1`
- live message visible after POST event

Placeholder:

```md
![Realtime websocket delivery](docs/proof/03-websocket-live-delivery.png)
```

### Claim 4: Notification history endpoint returns persisted records

Paste:
- GET `http://localhost:8083/api/notifications/user-1`
- JSON array with recent records

Placeholder:

```md
![Notification history API response](docs/proof/04-history-endpoint.png)
```

### Claim 5: Retry and DLQ behavior is configured and observable

Paste:
- Gateway config screenshot showing retry settings
- DLQ log screenshot from `handleDeadLetter(...)`

Placeholder:

```md
![Retry and DLQ proof](docs/proof/05-retry-dlq-proof.png)
```

### Claim 6: Throughput benchmark

Measured locally:
- `100 events processed in 1052ms`

Paste:
- Terminal screenshot that includes the exact timing output

Placeholder:

```md
![100-event timing output](docs/proof/06-benchmark-100-events.png)
```

## Proof Folder Structure (Create These)

Create this under repo root and paste your artifacts there:

```text
docs/
  proof/
    01-post-event-success.png
    02-processor-flow-logs.png
    03-websocket-live-delivery.png
    04-history-endpoint.png
    05-retry-dlq-proof.png
    06-benchmark-100-events.png
```

## Design Decisions

- Kafka + RabbitMQ split:
Kafka handles event stream ingestion and decoupling. RabbitMQ handles delivery policy, retries, and DLQ lifecycle.

- DLQ and retries:
`notifications.queue` routes failed messages to DLX, with retry backoff (`2s -> 4s -> 8s`, max attempts `3`).

- Contract-safe JSON flow:
Cross-service type headers were disabled to avoid class-coupled deserialization (`EventRequest` class mismatch across services).

