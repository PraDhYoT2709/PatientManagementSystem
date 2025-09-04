# Auth Service

A microservice for authentication and authorization in the Patient Management System (PMS). This service provides JWT-based authentication, OAuth2 integration with Google, user registration, and role-based access control.

## 🚀 Features

- **JWT Authentication**: Stateless session handling using JSON Web Tokens
- **OAuth2 Integration**: Google login support with automatic user registration
- **User Registration**: Local user registration with email/password
- **Role-Based Access Control**: Support for ADMIN, DOCTOR, and PATIENT roles
- **Password Encryption**: BCrypt hashing for secure password storage
- **Service Discovery**: Integrated with Eureka for service registration
- **Health Checks**: Actuator endpoints for monitoring

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Security**: Spring Security 6.x
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **JWT**: JJWT library for token generation and validation
- **OAuth2**: Spring Security OAuth2 Client
- **Service Discovery**: Netflix Eureka Client
- **Testing**: JUnit 5 with Mockito and TestContainers

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- MySQL 8.0
- Eureka Discovery Server running on port 8761
- Google OAuth2 credentials (for OAuth2 login)

## 🛠️ Setup

### 1. Database Setup

```sql
CREATE DATABASE auth_db;
```

### 2. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add authorized redirect URI: `http://localhost:8084/login/oauth2/code/google`

### 3. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db
    username: your_username
    password: your_password
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret

jwt:
  secret: your-jwt-secret-key
  expirationMs: 86400000
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8084 and register with Eureka.

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login with email/password |
| POST | `/api/auth/register` | Register new user |
| GET | `/oauth2/authorization/google` | Initiate Google OAuth2 login |
| GET | `/login/oauth2/code/google` | Google OAuth2 callback |

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/user/{email}` | Get user by email |
| GET | `/api/auth/user/username/{username}` | Get user by username |
| GET | `/api/auth/exists/email/{email}` | Check if email exists |
| GET | `/api/auth/exists/username/{username}` | Check if username exists |

## 📝 API Examples

### Register User

```bash
curl -X POST http://localhost:8084/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["PATIENT"]
}
```

### Get User by Email

```bash
curl "http://localhost:8084/api/auth/user/john@example.com"
```

### Check Email Exists

```bash
curl "http://localhost:8084/api/auth/exists/email/john@example.com"
```

### Google OAuth2 Login

1. Redirect user to: `http://localhost:8084/oauth2/authorization/google`
2. User completes Google authentication
3. User is redirected to: `http://localhost:3000/dashboard` (configurable)

## 🔐 Security Configuration

### JWT Token

- **Secret**: Configurable via `jwt.secret` property
- **Expiration**: Configurable via `jwt.expirationMs` property (default: 24 hours)
- **Algorithm**: HS512

### Roles

The system supports three roles:
- **ADMIN**: Full system access
- **DOCTOR**: Doctor-specific functionality
- **PATIENT**: Patient-specific functionality

### Password Requirements

- Minimum 6 characters
- Stored using BCrypt hashing

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes comprehensive tests:
- **Controller Tests**: MockMvc tests for all endpoints
- **Service Tests**: Unit tests with Mockito for business logic
- **Security Tests**: Authentication and authorization tests
- **Integration Tests**: TestContainers for database testing

### Test Database

Tests use TestContainers with MySQL for integration testing.

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8084/actuator/health
```

### Metrics

```bash
curl http://localhost:8084/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8084 | Service port |
| `spring.datasource.url` | - | Database URL |
| `jwt.secret` | - | JWT signing secret |
| `jwt.expirationMs` | 86400000 | JWT expiration time (ms) |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |

### Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret
- `GOOGLE_CLIENT_ID`: Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth2 client secret

## 🐳 Docker

### Build Image

```bash
docker build -t auth-service .
```

### Run Container

```bash
docker run -p 8084:8084 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/auth_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e JWT_SECRET=your-jwt-secret \
  auth-service
```

## 🚨 Error Handling

The service handles various error scenarios:

- **Invalid Credentials**: Returns 401 Unauthorized
- **Email Already Exists**: Returns 400 Bad Request
- **Username Already Taken**: Returns 400 Bad Request
- **User Not Found**: Returns 404 Not Found
- **Invalid JWT Token**: Returns 401 Unauthorized
- **OAuth2 Errors**: Handles Google authentication failures

## 🔄 OAuth2 Flow

1. User clicks "Login with Google"
2. Redirected to Google OAuth2 consent screen
3. User grants permissions
4. Google redirects back with authorization code
5. Service exchanges code for user info
6. User is created/updated in database
7. JWT token is generated and returned
8. User is redirected to dashboard

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.