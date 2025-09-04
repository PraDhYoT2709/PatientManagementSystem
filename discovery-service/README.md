# Discovery Service (Eureka Server)

A Netflix Eureka server for service discovery in the Patient Management System (PMS) microservices architecture. This service enables automatic service registration and discovery for all microservices in the system.

## 🚀 Features

- **Service Registration**: Automatic registration of microservices
- **Service Discovery**: Dynamic service discovery and load balancing
- **Health Monitoring**: Service health checks and status monitoring
- **High Availability**: Support for multiple Eureka server instances
- **Web Dashboard**: Built-in web interface for service monitoring
- **REST API**: RESTful API for service management

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Service Discovery**: Netflix Eureka Server
- **Monitoring**: Spring Boot Actuator
- **Testing**: JUnit 5

## 📋 Prerequisites

- Java 21
- Maven 3.6+

## 🛠️ Setup

### 1. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8761.

### 2. Access the Dashboard

Open your browser and navigate to: `http://localhost:8761`

## 📡 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8761 | Eureka server port |
| `eureka.instance.hostname` | localhost | Eureka server hostname |
| `eureka.client.register-with-eureka` | false | Whether to register with other Eureka servers |
| `eureka.client.fetch-registry` | false | Whether to fetch registry from other Eureka servers |
| `eureka.server.enable-self-preservation` | false | Enable self-preservation mode |

### Environment Variables

- `EUREKA_SERVER_PORT`: Eureka server port (default: 8761)
- `EUREKA_HOSTNAME`: Eureka server hostname (default: localhost)

## 🔧 Service Registration

### Microservices Configuration

Each microservice should be configured to register with this Eureka server:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Registered Services

The following services will register with this Eureka server:

- **patient-service** (port 8081)
- **doctor-service** (port 8082)
- **appointment-service** (port 8083)
- **auth-service** (port 8084)
- **api-gateway** (port 8080)
- **chatbot-service** (port 8085)

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8761/actuator/health
```

### Metrics

```bash
curl http://localhost:8761/actuator/metrics
```

### Service Registry

```bash
curl http://localhost:8761/eureka/apps
```

## 🌐 Web Dashboard

The Eureka dashboard provides:

- **Instances**: List of all registered service instances
- **Status**: Health status of each service
- **Metadata**: Service metadata and configuration
- **Actions**: Service management actions

### Dashboard Features

- **Service Overview**: View all registered services
- **Instance Details**: Detailed information about each service instance
- **Health Status**: Real-time health monitoring
- **Service Actions**: Start, stop, and restart services

## 🔄 Service Discovery Flow

1. **Service Startup**: Microservice starts and registers with Eureka
2. **Heartbeat**: Service sends periodic heartbeats to maintain registration
3. **Service Discovery**: Other services query Eureka for service locations
4. **Load Balancing**: Eureka provides multiple instances for load balancing
5. **Health Monitoring**: Eureka monitors service health and removes unhealthy instances

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes:
- **Application Context Tests**: Ensures Spring context loads correctly
- **Configuration Tests**: Validates Eureka server configuration

## 🐳 Docker

### Build Image

```bash
docker build -t discovery-service .
```

### Run Container

```bash
docker run -p 8761:8761 discovery-service
```

## 🔧 Production Configuration

### High Availability Setup

For production environments, configure multiple Eureka servers:

```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
```

### Security Configuration

Add security for production:

```yaml
spring:
  security:
    user:
      name: admin
      password: secure-password
```

## 🚨 Troubleshooting

### Common Issues

1. **Service Not Registering**: Check Eureka server URL in service configuration
2. **Connection Refused**: Ensure Eureka server is running on correct port
3. **Self-Preservation Mode**: Disable for development, enable for production

### Logs

Check application logs for detailed information:

```bash
tail -f logs/application.log
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