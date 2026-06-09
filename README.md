# Distributed Task Processing Platform

A distributed task processing platform built using Java, Spring Boot, and PostgreSQL that supports asynchronous task execution, retry scheduling, worker coordination, task claiming, stuck task recovery, and operational metrics.

This project was built to understand the internals of modern task processing systems such as AWS SQS, RabbitMQ workers, Sidekiq, Celery, and distributed job execution platforms.

---

## Features

- Asynchronous task execution
- Task status tracking
- Worker-based task processing
- Retry scheduling with configurable retry policies
- Delayed retries
- Worker identity management
- Task claiming mechanism
- Duplicate processing prevention
- Stuck task recovery
- Operational metrics API
- PostgreSQL persistence

---

# Architecture

```text
                    ┌─────────────────┐
                    │     Client      │
                    └────────┬────────┘
                             │
                             ▼
                  ┌─────────────────────┐
                  │      Task API       │
                  └────────┬────────────┘
                           │
                           ▼
                  ┌─────────────────────┐
                  │    Task Service     │
                  └────────┬────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ PostgreSQL  │
                    └──────┬──────┘
                           │
     ┌─────────────────────┼─────────────────────┐
     │                     │                     │
     ▼                     ▼                     ▼

┌─────────────┐   ┌────────────────┐   ┌────────────────────┐
│   Worker    │   │ Retry Scheduler│   │ Recovery Scheduler │
│ Scheduler   │   │                │   │                    │
└──────┬──────┘   └───────┬────────┘   └─────────┬──────────┘
       │                  │                      │
       ▼                  ▼                      ▼

┌─────────────┐   ┌────────────────┐   ┌────────────────────┐
│WorkerService│   │ Retry Service  │   │ Stuck Task Service │
└──────┬──────┘   └───────┬────────┘   └─────────┬──────────┘
       │
       ▼

┌─────────────┐
│TaskExecutor │
└─────────────┘
```

---

# Task Lifecycle

## Successful Task Flow

```text
PENDING
    ↓
CLAIMED
    ↓
RUNNING
    ↓
SUCCESS
```

---

## Failed Task With Retry

```text
PENDING
    ↓
CLAIMED
    ↓
RUNNING
    ↓
RETRY_SCHEDULED
    ↓
PENDING
    ↓
CLAIMED
    ↓
RUNNING
```

---

## Failed Task Without Remaining Retries

```text
PENDING
    ↓
CLAIMED
    ↓
RUNNING
    ↓
FAILED
```

---

# Core Components

## Task Service

Responsible for:

- Task creation
- Task retrieval
- Task lifecycle management

### APIs

```http
POST /api/tasks
GET /api/tasks
GET /api/tasks/{id}
```

---

## Worker Scheduler

Runs periodically and initiates task processing.

Responsibilities:

- Claim pending tasks
- Assign tasks to workers
- Start task execution

---

## Worker Service

Responsible for:

- Claiming tasks
- Transitioning task state
- Executing tasks
- Handling success and failure scenarios

---

## Task Executor

Executes task-specific business logic.

Supported task types:

```text
SEND_EMAIL
GENERATE_REPORT
PROCESS_FILE
```

---

## Retry Service

Handles task failures and retry scheduling.

Responsibilities:

- Create retry records
- Calculate retry schedules
- Determine retry eligibility
- Move tasks to RETRY_SCHEDULED state

---

## Retry Scheduler

Promotes retryable tasks back into the execution queue.

Flow:

```text
RETRY_SCHEDULED
       ↓
nextRetryAt <= now
       ↓
PENDING
```

---

## Stuck Task Recovery

Handles abandoned tasks caused by worker failures.

Recovery logic:

```text
CLAIMED / RUNNING
        ↓
Task exceeds timeout threshold
        ↓
Reset to PENDING
        ↓
Available for processing
```

---

# Worker Claiming Strategy

To prevent multiple workers from processing the same task simultaneously, task claiming uses database locking.

### Claiming Flow

```text
Worker A claims Task 1
Worker B skips Task 1
Worker B claims Task 2
Worker C claims Task 3
```

This guarantees:

```text
One Task
One Worker
One Execution
```

### PostgreSQL Locking

```sql
FOR UPDATE SKIP LOCKED
```

---

# Retry Mechanism

Retry policies are configured per task type.

## Retry Configuration

| Task Type | Max Retries |
|------------|------------|
| SEND_EMAIL | 3 |
| GENERATE_REPORT | 2 |
| PROCESS_FILE | 1 |

---

## Retry Delays

| Attempt | Delay |
|----------|---------|
| 1 | 30 Seconds |
| 2 | 60 Seconds |
| 3 | 300 Seconds |

---

## Retry Flow

```text
Task Failure
      ↓
Retry Service
      ↓
Create Retry Record
      ↓
Task Status → RETRY_SCHEDULED
      ↓
Wait Until nextRetryAt
      ↓
Retry Scheduler
      ↓
Task Status → PENDING
```

---

# Metrics API

The platform exposes operational metrics for monitoring task execution.

### Endpoint

```http
GET /api/metrics
```

### Response

```json
{
  "totalTasks": 150,
  "pendingTasks": 5,
  "claimedTasks": 2,
  "runningTasks": 1,
  "successfulTasks": 130,
  "failedTasks": 4,
  "retryScheduledTasks": 8,
  "averageExecutionTimeMillis": 245.5
}
```

---

# Database Model

## Tasks

Stores task metadata and execution state.

Key fields:

```text
id
taskType
payload
taskStatus
retryCount
maxRetries
nextRetryAt
lockedBy
lockedAt
createdAt
updatedAt
startedAt
completedAt
errorMessage
```

---

## Task Retries

Stores retry history.

Key fields:

```text
id
taskId
attemptNumber
status
errorMessage
nextRetryAt
createdAt
updatedAt
```

---

# Technology Stack

## Backend

```text
Java 21
Spring Boot
Spring Data JPA
Spring Scheduler
```

## Database

```text
PostgreSQL
```

## Build Tool

```text
Maven
```

---

