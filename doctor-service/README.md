# Doctor Service

A microservice for managing doctor information in the Patient Management System (PMS). This service provides CRUD operations for doctor data and integrates with the Eureka service discovery.

## 🚀 Features

- **Doctor CRUD Operations**: Create, read, update, and delete doctor records
- **Search & Filter**: Search doctors by name, specialty, and department
- **Availability Management**: Track doctor availability status
- **Specialty Management**: Organize doctors by medical specialties
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
CREATE DATABASE doctor_db;
```

### 2. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/doctor_db
    username: your_username
    password: your_password
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8082 and register with Eureka.

## 📡 API Endpoints

### Doctor Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/doctors` | Create a new doctor |
| GET | `/api/doctors` | Get all doctors (paginated) |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/email/{email}` | Get doctor by email |
| PUT | `/api/doctors/{id}` | Update doctor |
| DELETE | `/api/doctors/{id}` | Delete doctor |

### Search & Filter

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors/search/name?name={name}` | Search doctors by name |
| GET | `/api/doctors/search/specialty?specialty={specialty}` | Search doctors by specialty |
| GET | `/api/doctors/specialty/{specialty}` | Get doctors by exact specialty |
| GET | `/api/doctors/department/{department}` | Get doctors by department |
| GET | `/api/doctors/available` | Get available doctors |
| GET | `/api/doctors/exists/{id}` | Check if doctor exists |

### Query Parameters

- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: id)
- `sortDir`: Sort direction (asc/desc, default: asc)

## 📝 API Examples

### Create Doctor

```bash
curl -X POST http://localhost:8082/api/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. John Smith",
    "specialty": "Cardiology",
    "email": "dr.john@example.com",
    "phone": "9876543210",
    "qualification": "MD",
    "experience": 10,
    "department": "Cardiology",
    "consultationFee": "500",
    "available": true
  }'
```

### Get All Doctors (Paginated)

```bash
curl "http://localhost:8082/api/doctors?page=0&size=10&sortBy=name&sortDir=asc"
```

### Search Doctors by Name

```bash
curl "http://localhost:8082/api/doctors/search/name?name=John&page=0&size=5"
```

### Search Doctors by Specialty

```bash
curl "http://localhost:8082/api/doctors/search/specialty?specialty=Cardiology&page=0&size=5"
```

### Get Available Doctors

```bash
curl "http://localhost:8082/api/doctors/available"
```

### Update Doctor

```bash
curl -X PUT http://localhost:8082/api/doctors/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. John Smith",
    "specialty": "Cardiology",
    "email": "dr.john@example.com",
    "phone": "9876543210",
    "qualification": "MD",
    "experience": 12,
    "department": "Cardiology",
    "consultationFee": "600",
    "available": true
  }'
```

### Get Doctors by Specialty

```bash
curl "http://localhost:8082/api/doctors/specialty/Cardiology"
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
curl http://localhost:8082/actuator/health
```

### Metrics

```bash
curl http://localhost:8082/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8082 | Service port |
| `spring.datasource.url` | - | Database URL |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |

### Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## 🐳 Docker

### Build Image

```bash
docker build -t doctor-service .
```

### Run Container

```bash
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/doctor_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  doctor-service
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