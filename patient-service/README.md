# Patient Service

A microservice for managing patient information in the Patient Management System (PMS). This service provides CRUD operations for patient data and integrates with the Eureka service discovery.

## 🚀 Features

- **Patient CRUD Operations**: Create, read, update, and delete patient records
- **Search & Filter**: Search patients by disease with case-insensitive queries
- **Doctor Association**: Link patients to doctors via doctor ID
- **Admission Status**: Track patient admission and discharge status
- **Validation**: Comprehensive input validation with Jakarta Bean Validation
- **Service Discovery**: Integrated with Eureka for service registration
- **Health Checks**: Actuator endpoints for monitoring

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Service Discovery**: Netflix Eureka Client
- **Testing**: JUnit 5 with Mockito and TestContainers

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- MySQL 8.0
- Eureka Discovery Server running on port 8761

## 🛠️ Setup

### 1. Database Setup

```sql
CREATE DATABASE patient_db;
```

### 2. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/patient_db
    username: your_username
    password: your_password
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8081 and register with Eureka.

## 📡 API Endpoints

### Patient Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/patients` | Create a new patient |
| GET | `/api/patients` | Get all patients (paginated) |
| GET | `/api/patients/{id}` | Get patient by ID |
| GET | `/api/patients/email/{email}` | Get patient by email |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Delete patient |

### Search & Filter

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients/search?disease={disease}` | Search patients by disease |
| GET | `/api/patients/doctor/{doctorId}` | Get patients by doctor ID |
| GET | `/api/patients/admitted` | Get admitted patients |
| GET | `/api/patients/discharged` | Get discharged patients |
| GET | `/api/patients/exists/{id}` | Check if patient exists |

### Query Parameters

- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: id)
- `sortDir`: Sort direction (asc/desc, default: asc)

## 📝 API Examples

### Create Patient

```bash
curl -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "age": 30,
    "gender": "Male",
    "disease": "Fever",
    "email": "john@example.com",
    "phone": "9876543210",
    "admitted": true,
    "address": "123 Main St"
  }'
```

### Get All Patients (Paginated)

```bash
curl "http://localhost:8081/api/patients?page=0&size=10&sortBy=name&sortDir=asc"
```

### Search Patients by Disease

```bash
curl "http://localhost:8081/api/patients/search?disease=Fever&page=0&size=5"
```

### Update Patient

```bash
curl -X PUT http://localhost:8081/api/patients/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "age": 31,
    "gender": "Male",
    "disease": "Recovered",
    "email": "john@example.com",
    "phone": "9876543210",
    "admitted": false,
    "address": "123 Main St"
  }'
```

### Get Patients by Doctor

```bash
curl "http://localhost:8081/api/patients/doctor/1"
```

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes comprehensive tests:
- **Controller Tests**: MockMvc tests for all endpoints
- **Service Tests**: Unit tests with Mockito
- **Integration Tests**: TestContainers for database testing

### Test Database

Tests use TestContainers with MySQL for integration testing.

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

### Metrics

```bash
curl http://localhost:8081/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8081 | Service port |
| `spring.datasource.url` | - | Database URL |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |

### Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## 🐳 Docker

### Build Image

```bash
docker build -t patient-service .
```

### Run Container

```bash
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/patient_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  patient-service
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.