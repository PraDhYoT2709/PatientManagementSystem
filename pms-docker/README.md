# PMS Docker

Docker configuration and orchestration for the Patient Management System (PMS) microservices architecture. This repository contains Docker Compose configuration, Dockerfiles, and management scripts for running the entire PMS system in containers.

## 🚀 Features

- **Complete Microservices Stack**: All 8 microservices containerized
- **Service Orchestration**: Docker Compose for easy deployment
- **Health Checks**: Automatic health monitoring for all services
- **Service Discovery**: Eureka server for service registration
- **Database Integration**: MySQL with automatic database initialization
- **Network Isolation**: Custom Docker network for service communication
- **Management Scripts**: Automated build, start, stop, and cleanup scripts

## 🏗️ Architecture

### Services

| Service | Port | Description |
|---------|------|-------------|
| MySQL | 3306 | Database server |
| Discovery Service | 8761 | Eureka service discovery |
| Auth Service | 8084 | Authentication and authorization |
| Patient Service | 8081 | Patient management |
| Doctor Service | 8082 | Doctor management |
| Appointment Service | 8083 | Appointment management |
| Chatbot Service | 8085 | AI chatbot with NLU |
| API Gateway | 8080 | Central API gateway |
| Chat Widget | 3000 | React chat widget |

### Network

- **Custom Network**: `pms-network` for service communication
- **Service Discovery**: Services register with Eureka
- **Load Balancing**: API Gateway handles routing

## 📋 Prerequisites

- Docker 20.10+
- Docker Compose 2.0+
- 8GB+ RAM recommended
- 10GB+ disk space

## 🛠️ Quick Start

### 1. Clone and Build

```bash
# Clone all repositories (you'll need to do this for each service)
git clone <patient-service-repo>
git clone <doctor-service-repo>
git clone <appointment-service-repo>
git clone <auth-service-repo>
git clone <discovery-service-repo>
git clone <api-gateway-repo>
git clone <chatbot-service-repo>
git clone <pms-chat-widget-repo>

# Build all services
cd pms-docker
./scripts/build-all.sh
```

### 2. Start Services

```bash
# Start all services
./scripts/start-services.sh

# Or manually
docker-compose up -d
```

### 3. Verify Deployment

```bash
# Test API endpoints
./scripts/test-api.sh

# Check service status
docker-compose ps
```

## 📡 Service URLs

Once running, access the services at:

- **API Gateway**: http://localhost:8080
- **Discovery Service**: http://localhost:8761
- **Auth Service**: http://localhost:8084
- **Patient Service**: http://localhost:8081
- **Doctor Service**: http://localhost:8082
- **Appointment Service**: http://localhost:8083
- **Chatbot Service**: http://localhost:8085
- **Chat Widget**: http://localhost:3000

## 🔧 Management Scripts

### Build All Services

```bash
./scripts/build-all.sh
```

Builds all microservices and prepares them for containerization.

### Start Services

```bash
./scripts/start-services.sh
```

Starts all services with health checks and provides status information.

### Stop Services

```bash
./scripts/stop-services.sh
```

Gracefully stops all services.

### Test API

```bash
./scripts/test-api.sh
```

Tests all API endpoints to verify system functionality.

### Cleanup

```bash
./scripts/cleanup.sh
```

Removes all containers, images, volumes, and networks.

## 🐳 Docker Commands

### Basic Operations

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f [service-name]

# Restart service
docker-compose restart [service-name]

# Scale service
docker-compose up -d --scale patient-service=2
```

### Health Checks

```bash
# Check service health
docker-compose ps

# View health check logs
docker-compose logs discovery-service
```

### Database Operations

```bash
# Access MySQL
docker-compose exec mysql mysql -u root -p

# Backup database
docker-compose exec mysql mysqldump -u root -p --all-databases > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -p < backup.sql
```

## 🔧 Configuration

### Environment Variables

Key environment variables for each service:

```yaml
# Database
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/[database_name]
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: prakhaar12

# Service Discovery
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://discovery-service:8761/eureka/

# JWT
JWT_SECRET: b2D3dM7qA5N8sXzK1JvB4YtR6LcT9QpFgUwErZnHxVbMsD7PfKgVhWnXcYaTrMzB
```

### Custom Configuration

To customize the deployment:

1. **Modify docker-compose.yml** for port changes, environment variables
2. **Update Dockerfiles** for custom base images or configurations
3. **Edit init-databases.sql** for custom database setup

## 📊 Monitoring

### Service Health

```bash
# Check all services
curl http://localhost:8080/actuator/health
curl http://localhost:8761/actuator/health
curl http://localhost:8081/actuator/health
# ... etc for each service
```

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f api-gateway
docker-compose logs -f mysql
```

### Metrics

```bash
# Service metrics
curl http://localhost:8080/actuator/metrics
curl http://localhost:8761/actuator/metrics
```

## 🚨 Troubleshooting

### Common Issues

1. **Services not starting**
   ```bash
   # Check logs
   docker-compose logs [service-name]
   
   # Check health
   docker-compose ps
   ```

2. **Database connection issues**
   ```bash
   # Check MySQL logs
   docker-compose logs mysql
   
   # Test connection
   docker-compose exec mysql mysql -u root -p
   ```

3. **Service discovery issues**
   ```bash
   # Check Eureka dashboard
   open http://localhost:8761
   
   # Check service registration
   curl http://localhost:8761/eureka/apps
   ```

4. **Port conflicts**
   ```bash
   # Check port usage
   netstat -tulpn | grep :8080
   
   # Change ports in docker-compose.yml
   ```

### Performance Issues

1. **Memory issues**
   ```bash
   # Check memory usage
   docker stats
   
   # Increase Docker memory limit
   ```

2. **Slow startup**
   ```bash
   # Check startup logs
   docker-compose logs -f
   
   # Increase health check intervals
   ```

## 🔒 Security

### Production Considerations

1. **Change default passwords**
2. **Use secrets management**
3. **Enable HTTPS**
4. **Configure firewall rules**
5. **Regular security updates**

### Secrets Management

```bash
# Create secrets
echo "your-secure-password" | docker secret create mysql_password -
echo "your-jwt-secret" | docker secret create jwt_secret -

# Use in docker-compose.yml
secrets:
  - mysql_password
  - jwt_secret
```

## 📈 Scaling

### Horizontal Scaling

```bash
# Scale specific services
docker-compose up -d --scale patient-service=3
docker-compose up -d --scale doctor-service=2
```

### Load Balancing

The API Gateway automatically load balances requests across multiple instances.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with the provided scripts
5. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.

## 🆘 Support

For support and questions:

1. Check the troubleshooting section
2. Review service logs
3. Verify all prerequisites are met
4. Check Docker and Docker Compose versions
5. Ensure sufficient system resources