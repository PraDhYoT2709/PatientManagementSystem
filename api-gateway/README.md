# API Gateway

A Spring Cloud Gateway-based API gateway for the Patient Management System (PMS) microservices architecture. This service provides centralized routing, JWT authentication, load balancing, and cross-cutting concerns for all microservices.

## 🚀 Features

- **Centralized Routing**: Single entry point for all microservices
- **JWT Authentication**: Token-based authentication and authorization
- **Load Balancing**: Automatic load balancing across service instances
- **CORS Support**: Cross-origin resource sharing configuration
- **Service Discovery**: Integration with Eureka for dynamic routing
- **Health Monitoring**: Actuator endpoints for monitoring
- **Request/Response Filtering**: Custom filters for authentication and logging

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka Client
- **Security**: JWT token validation
- **Testing**: JUnit 5 with WebFlux test support

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- Eureka Discovery Server running on port 8761
- All microservices registered with Eureka

## 🛠️ Setup

### 1. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8080 and register with Eureka.

### 2. Access the Gateway

The gateway will be available at: `http://localhost:8080`

## 📡 API Routes

### Service Routing

| Service | Route | Description |
|---------|-------|-------------|
| Auth Service | `/api/auth/**` | Authentication and authorization |
| Patient Service | `/api/patients/**` | Patient management |
| Doctor Service | `/api/doctors/**` | Doctor management |
| Appointment Service | `/api/appointments/**` | Appointment management |
| Chatbot Service | `/api/chat/**` | Chatbot functionality |
| OAuth2 | `/oauth2/**`, `/login/oauth2/**` | OAuth2 authentication |
| Actuator | `/actuator/**` | Health and metrics |

### Authentication

- **Auth endpoints**: No JWT required
- **All other endpoints**: JWT token required in Authorization header
- **Format**: `Authorization: Bearer <jwt-token>`

## 📝 API Examples

### Login (No JWT Required)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Access Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/patients \
  -H "Authorization: Bearer your-jwt-token"
```

### Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Get Patients

```bash
curl -X GET http://localhost:8080/api/patients \
  -H "Authorization: Bearer your-jwt-token"
```

### Get Doctors

```bash
curl -X GET http://localhost:8080/api/doctors \
  -H "Authorization: Bearer your-jwt-token"
```

### Create Appointment

```bash
curl -X POST http://localhost:8080/api/appointments \
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

### Chat with Bot

```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "message": "Hello, I need help with my appointment"
  }'
```

## 🔐 Security Configuration

### JWT Authentication

- **Secret**: Configurable via `jwt.secret` property
- **Validation**: Automatic token validation for protected routes
- **User Context**: Username extracted from token and passed to downstream services

### Protected Routes

All routes except the following require JWT authentication:
- `/api/auth/**` - Authentication endpoints
- `/oauth2/**` - OAuth2 endpoints
- `/login/oauth2/**` - OAuth2 callbacks
- `/actuator/**` - Health and metrics endpoints

### CORS Configuration

- **Allowed Origins**: `*` (configurable)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: `*`
- **Credentials**: Allowed

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes comprehensive tests:
- **Application Context Tests**: Ensures Spring context loads correctly
- **JWT Filter Tests**: Tests JWT authentication filter
- **Route Tests**: Validates routing configuration
- **Integration Tests**: End-to-end testing with WebFlux

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Gateway Routes

```bash
curl http://localhost:8080/actuator/gateway/routes
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Gateway port |
| `jwt.secret` | - | JWT signing secret |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |

### Environment Variables

- `JWT_SECRET`: JWT signing secret
- `EUREKA_SERVER_URL`: Eureka server URL

## 🐳 Docker

### Build Image

```bash
docker build -t api-gateway .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e JWT_SECRET=your-jwt-secret \
  api-gateway
```

## 🔄 Request Flow

1. **Client Request**: Client sends request to gateway
2. **Route Matching**: Gateway matches request to appropriate service
3. **JWT Validation**: For protected routes, JWT token is validated
4. **Service Discovery**: Gateway looks up service instance from Eureka
5. **Load Balancing**: Request is forwarded to available service instance
6. **Response**: Service response is returned to client

## 🚨 Error Handling

The gateway handles various error scenarios:

- **Invalid JWT Token**: Returns 401 Unauthorized
- **Missing JWT Token**: Returns 401 Unauthorized
- **Service Unavailable**: Returns 503 Service Unavailable
- **Route Not Found**: Returns 404 Not Found

## 🔧 Custom Filters

### JWT Authentication Filter

- Validates JWT tokens for protected routes
- Extracts username from token
- Adds user context to downstream services
- Handles authentication errors

### CORS Filter

- Handles cross-origin requests
- Configurable CORS policies
- Preflight request support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.