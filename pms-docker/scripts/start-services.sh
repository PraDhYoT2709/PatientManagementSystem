#!/bin/bash

# Start Patient Management System services
echo "Starting Patient Management System..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install docker-compose first."
    exit 1
fi

# Start services
echo "Starting services with docker-compose..."
docker-compose up -d

# Wait for services to be healthy
echo "Waiting for services to be healthy..."
sleep 30

# Check service health
check_service_health() {
    local service_name=$1
    local port=$2
    
    echo "Checking $service_name health..."
    for i in {1..30}; do
        if curl -f "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo "✅ $service_name is healthy"
            return 0
        fi
        echo "⏳ Waiting for $service_name... ($i/30)"
        sleep 10
    done
    
    echo "❌ $service_name failed to become healthy"
    return 1
}

# Check all services
check_service_health "discovery-service" "8761"
check_service_health "auth-service" "8084"
check_service_health "patient-service" "8081"
check_service_health "doctor-service" "8082"
check_service_health "appointment-service" "8083"
check_service_health "chatbot-service" "8085"
check_service_health "api-gateway" "8080"

echo ""
echo "🎉 Patient Management System is running!"
echo ""
echo "📋 Service URLs:"
echo "  • API Gateway: http://localhost:8080"
echo "  • Discovery Service: http://localhost:8761"
echo "  • Auth Service: http://localhost:8084"
echo "  • Patient Service: http://localhost:8081"
echo "  • Doctor Service: http://localhost:8082"
echo "  • Appointment Service: http://localhost:8083"
echo "  • Chatbot Service: http://localhost:8085"
echo "  • Chat Widget: http://localhost:3000"
echo ""
echo "🔧 Management Commands:"
echo "  • View logs: docker-compose logs -f [service-name]"
echo "  • Stop services: docker-compose down"
echo "  • Restart service: docker-compose restart [service-name]"
echo "  • View service status: docker-compose ps"