# ClinicalFlow — Healthcare Event-Driven Microservices Platform

A distributed microservices system for clinical care coordination, built with Spring Boot, Apache Kafka, PostgreSQL, and Docker. Implements real-world hospital workflows including patient Registration, Admission, Transfer, and Discharge (RATD) with event-driven communication across services.

> ⚠️ **Disclaimer:** All patient data shown in this project is entirely fictitious. No real patient health information (PHI) is used. This project is for demonstration purposes only and is not intended for clinical use.

## Architecture

```
┌─────────────────┐    ┌──────────────┐    ┌──────────────────────┐
│ Patient Service  │───▶│    Kafka      │───▶│ Notification Service │
│   (port 8081)    │    │              │    │     (port 8083)      │
└─────────────────┘    │  Topics:     │    └──────────────────────┘
                       │  • patient   │
┌─────────────────┐    │    .events   │
│   Lab Service    │───▶│  • lab      │
│   (port 8082)    │    │    .results │
└─────────────────┘    └──────────────┘
        │                      │
        ▼                      ▼
   [PostgreSQL]          [PostgreSQL]
   lab_db :5433          patient_db :5432
```

## Patient Lifecycle State Machine

The Patient Service enforces a clinical state machine that mirrors real hospital workflows. Each transition publishes a domain event to Kafka's `patient.events` topic.

```
REGISTERED ──▶ ADMITTED ──▶ TRANSFERRED ──▶ DISCHARGED
```

## Domain Model

**Patient Events:** registration, admission, transfer, discharge, medication-order

**Lab Events:** lab-ordered, result-available, critical-result

**Notifications:** alert clinicians on critical lab values, discharge summaries

## Tech Stack

| Component         | Technology                      |
| ----------------- | ------------------------------- |
| Backend Services  | Spring Boot 3.3 (Java 21)       |
| Persistence       | Spring Data JPA / Hibernate 6.5 |
| Messaging         | Apache Kafka 3.7.1 (KRaft mode) |
| Database          | PostgreSQL 16                   |
| Containerization  | Docker + Docker Compose         |
| API Documentation | SpringDoc OpenAPI (Swagger UI)  |
| Build Tool        | Maven                           |

## API Endpoints — Patient Service

| Method | Endpoint                       | Description                                  |
| ------ | ------------------------------ | -------------------------------------------- |
| POST   | `/api/patients`                | Register a new patient                       |
| GET    | `/api/patients`                | List all patients (with status/name filters) |
| GET    | `/api/patients/{id}`           | Get patient by UUID                          |
| GET    | `/api/patients/mrn/{mrn}`      | Lookup patient by Medical Record Number      |
| PATCH  | `/api/patients/{id}`           | Update patient details                       |
| POST   | `/api/patients/{id}/admit`     | Admit a registered patient                   |
| POST   | `/api/patients/{id}/transfer`  | Transfer an admitted patient                 |
| POST   | `/api/patients/{id}/discharge` | Discharge a patient                          |

## Screenshots

### Swagger UI — API Documentation

![Swagger Overview](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/swagger-overview.png)

### Create Patient (POST) — Returns 201 with UUID and REGISTERED status

![Create Patient](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-create.png)

### Admit Patient — Status: REGISTERED → ADMITTED

![Admit Patient](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-admit.png)

### Transfer Patient — Status: ADMITTED → TRANSFERRED

![Transfer Patient](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-transfer.png)

### Discharge Patient — Status: TRANSFERRED → DISCHARGED

![Discharge Patient](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-discharge.png)

### List All Patients (GET) — Filtered results with multiple statuses

![Patient List](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-list.png)

### Lookup by Medical Record Number (MRN)

![MRN Lookup](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/patient-mrn-lookup.png)

### Docker Infrastructure — PostgreSQL + Kafka running

![Docker and Kafka Infrastructure](https://raw.githubusercontent.com/aliminagar/clinicalflow-microservices/main/docs/images/docker-kafka-infrastructure.png)

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker Desktop

## Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/aliminagar/clinicalflow-microservices.git
cd clinicalflow-microservices/clinicalflow

# 2. Start infrastructure (PostgreSQL + Kafka)
docker compose up -d

# 3. Start patient-service
cd patient-service && mvn spring-boot:run

# 4. Start lab-service (new terminal)
cd lab-service && mvn spring-boot:run

# 5. Start notification-service (new terminal)
cd notification-service && mvn spring-boot:run
```

### Access Swagger UI

```
http://localhost:8081/swagger-ui/index.html
```

### Test the Patient Workflow

```bash
# Register a patient
curl -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Sarah","lastName":"Johnson","dateOfBirth":"1985-03-22","email":"sarah.johnson@example.com","mrn":"MRN-001"}'

# Admit the patient (use the id from the response above)
curl -X POST http://localhost:8081/api/patients/{id}/admit

# Transfer the patient
curl -X POST http://localhost:8081/api/patients/{id}/transfer

# Discharge the patient
curl -X POST http://localhost:8081/api/patients/{id}/discharge

# List all patients
curl http://localhost:8081/api/patients

# Lookup by MRN
curl http://localhost:8081/api/patients/mrn/MRN-001
```

### Test Critical Lab Value Alert

```bash
# Submit a critical lab result — triggers Kafka event to notification service
curl -X POST http://localhost:8082/api/lab-results \
  -H "Content-Type: application/json" \
  -d '{"patientId":"<uuid-from-above>","testName":"Potassium","value":6.2,"unit":"mEq/L","referenceMin":3.5,"referenceMax":5.0}'
```

A critical potassium value (6.2 > 5.0) triggers a Kafka event that the notification service consumes and logs as a clinical alert.

clinicalflow/
├── docker-compose.yml # Kafka + PostgreSQL infrastructure
├── patient-service/ # Patient registration & lifecycle events
│ ├── Dockerfile
│ ├── pom.xml
│ └── src/main/java/com/clinicalflow/patient/
│ ├── PatientServiceApplication.java
│ ├── config/
│ │ └── KafkaConfig.java
│ ├── controller/
│ │ └── PatientController.java
│ ├── model/
│ │ ├── Patient.java
│ │ └── PatientStatus.java
│ ├── repository/
│ │ └── PatientRepository.java
│ └── service/
│ └── PatientService.java
├── lab-service/ # Lab orders, results & critical value detection
│ ├── Dockerfile
│ ├── pom.xml
│ └── src/main/java/com/clinicalflow/lab/
│ ├── LabServiceApplication.java
│ ├── config/
│ │ └── KafkaConfig.java
│ ├── controller/
│ │ └── LabResultController.java
│ ├── event/
│ │ ├── LabResultEvent.java
│ │ └── LabResultEventPublisher.java
│ ├── model/
│ │ ├── LabResult.java
│ │ ├── ResultFlag.java
│ │ └── ResultStatus.java
│ ├── repository/
│ │ └── LabResultRepository.java
│ └── service/
│ └── LabResultService.java
├── notification-service/ # Clinical alert consumer
├── docs/
│ └── images/ # Screenshots for documentation
└── .gitignore

## Design Decisions

**Database-per-service:** Each microservice owns its data store — no shared database, enabling independent scaling and deployment.

**Event-driven communication:** Services communicate via Kafka topics, not direct HTTP calls, ensuring loose coupling and resilience.

**KRaft mode Kafka:** No ZooKeeper dependency — uses modern Kafka's built-in consensus protocol.

**Clinical state machine:** The RATD workflow enforces valid state transitions, preventing invalid operations like discharging a patient who hasn't been admitted.

**Domain events:** Events carry clinical context (not just IDs) to reduce cross-service queries.

**UUID primary keys:** Supports distributed systems and prevents enumeration attacks on patient records.

**Medical Record Number (MRN):** A unique business identifier separate from the technical UUID, reflecting real-world hospital EHR systems.

**Critical value detection:** Lab service applies clinical reference ranges before publishing events, mimicking real laboratory information systems.

## Author

Alireza Minagar, MD, MBA, MS (Bioinformatics), MSIT

Software Engineer | Neurologist & Neuro-immunologist

Building healthcare technology informed by 20+ years of clinical experience.

[GitHub](https://github.com/aliminagar) | [LinkedIn](https://www.linkedin.com/in/alireza-minagar-b450aa173/)

## License

This project is for portfolio and demonstration purposes.
