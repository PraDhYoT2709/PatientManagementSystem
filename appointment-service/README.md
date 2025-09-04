# Appointment Service

A microservice for managing appointments in the Patient Management System (PMS). This service provides CRUD operations for appointment data, integrates with patient and doctor services via Feign clients, and includes comprehensive scheduling conflict detection.

## 🚀 Features

- **Appointment CRUD Operations**: Create, read, update, and delete appointment records
- **Service Integration**: Uses Feign clients to communicate with patient and doctor services
- **Scheduling Conflict Detection**: Prevents double-booking of doctors
- **Date Range Queries**: Filter appointments by date ranges
- **Status Management**: Track appointment status (SCHEDULED, CANCELLED, COMPLETED)
- **Validation**: Comprehensive input validation with Jakarta Bean Validation
- **Service Discovery**: Integrated with Eureka for service registration
- **Health Checks**: Actuator endpoints for monitoring

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Service Discovery**: Netflix Eureka Client
- **Service Communication**: OpenFeign for HTTP clients
- **Testing**: JUnit 5 with Mockito and TestContainers

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- MySQL 8.0
- Eureka Discovery Server running on port 8761
- Patient Service running and registered with Eureka
- Doctor Service running and registered with Eureka

## 🛠️ Setup

### 1. Database Setup

```sql
CREATE DATABASE appointment_db;
```

### 2. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/appointment_db
    username: your_username
    password: your_password
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8083 and register with Eureka.

## 📡 API Endpoints

### Appointment Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Create a new appointment |
| GET | `/api/appointments` | Get all appointments (paginated) |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| PUT | `/api/appointments/{id}` | Update appointment |
| DELETE | `/api/appointments/{id}` | Delete appointment |

### Search & Filter

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointments/patient/{patientId}` | Get appointments by patient |
| GET | `/api/appointments/doctor/{doctorId}` | Get appointments by doctor |
| GET | `/api/appointments/status/{status}` | Get appointments by status |
| GET | `/api/appointments/date-range` | Get appointments by date range |
| GET | `/api/appointments/patient/{patientId}/date-range` | Get patient appointments by date range |
| GET | `/api/appointments/doctor/{doctorId}/date-range` | Get doctor appointments by date range |

### Related Data

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointments/{id}/patient` | Get patient details for appointment |
| GET | `/api/appointments/{id}/doctor` | Get doctor details for appointment |
| GET | `/api/appointments/exists/{id}` | Check if appointment exists |

### Query Parameters

- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: id)
- `sortDir`: Sort direction (asc/desc, default: asc)
- `startDate`: Start date for date range queries (ISO format)
- `endDate`: End date for date range queries (ISO format)

## 📝 API Examples

### Create Appointment

```bash
curl -X POST http://localhost:8083/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "dateTime": "2024-01-15T10:00:00",
    "reason": "Regular checkup",
    "status": "SCHEDULED",
    "patientId": 1,
    "doctorId": 1
  }'
```

### Get All Appointments (Paginated)

```bash
curl "http://localhost:8083/api/appointments?page=0&size=10&sortBy=dateTime&sortDir=asc"
```

### Get Appointments by Patient

```bash
curl "http://localhost:8083/api/appointments/patient/1"
```

### Get Appointments by Doctor

```bash
curl "http://localhost:8083/api/appointments/doctor/1"
```

### Get Appointments by Date Range

```bash
curl "http://localhost:8083/api/appointments/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59"
```

### Get Patient Details for Appointment

```bash
curl "http://localhost:8083/api/appointments/1/patient" \
  -H "Authorization: Bearer your-jwt-token"
```

### Get Doctor Details for Appointment

```bash
curl "http://localhost:8083/api/appointments/1/doctor" \
  -H "Authorization: Bearer your-jwt-token"
```

### Update Appointment

```bash
curl -X PUT http://localhost:8083/api/appointments/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "dateTime": "2024-01-15T11:00:00",
    "reason": "Follow-up appointment",
    "status": "SCHEDULED",
    "patientId": 1,
    "doctorId": 1
  }'
```

### Cancel Appointment

```bash
curl -X PUT http://localhost:8083/api/appointments/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "dateTime": "2024-01-15T10:00:00",
    "reason": "Regular checkup",
    "status": "CANCELLED",
    "patientId": 1,
    "doctorId": 1
  }'
```

## 🔧 Service Integration

### Feign Clients

The service uses OpenFeign to communicate with other microservices:

- **PatientServiceClient**: Validates patient existence and retrieves patient details
- **DoctorServiceClient**: Validates doctor existence and retrieves doctor details

### JWT Token Forwarding

All Feign client calls forward the JWT token from the incoming request to maintain security context across services.

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes comprehensive tests:
- **Controller Tests**: MockMvc tests for all endpoints
- **Service Tests**: Unit tests with Mockito for business logic
- **Integration Tests**: TestContainers for database testing
- **Feign Client Tests**: Mocked external service calls

### Test Database

Tests use TestContainers with MySQL for integration testing.

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8083/actuator/health
```

### Metrics

```bash
curl http://localhost:8083/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8083 | Service port |
| `spring.datasource.url` | - | Database URL |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |
| `feign.client.config.default.connect-timeout` | 5000 | Feign connection timeout |
| `feign.client.config.default.read-timeout` | 5000 | Feign read timeout |

### Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## 🐳 Docker

### Build Image

```bash
docker build -t appointment-service .
```

### Run Container

```bash
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/appointment_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  appointment-service
```

## 🚨 Error Handling

The service handles various error scenarios:

- **Patient Not Found**: Returns 400 Bad Request when patient doesn't exist
- **Doctor Not Found**: Returns 400 Bad Request when doctor doesn't exist
- **Scheduling Conflicts**: Returns 400 Bad Request when doctor is already booked
- **Appointment Not Found**: Returns 404 Not Found for non-existent appointments
- **Validation Errors**: Returns 400 Bad Request for invalid input data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.