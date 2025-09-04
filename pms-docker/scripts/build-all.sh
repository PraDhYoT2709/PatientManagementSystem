#!/bin/bash

# Build all microservices
echo "Building all Patient Management System microservices..."

# Function to build a service
build_service() {
    local service_name=$1
    local service_path=$2
    
    echo "Building $service_name..."
    cd "$service_path"
    
    if [ -f "pom.xml" ]; then
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            echo "✅ $service_name built successfully"
        else
            echo "❌ Failed to build $service_name"
            exit 1
        fi
    elif [ -f "package.json" ]; then
        npm ci
        npm run build
        if [ $? -eq 0 ]; then
            echo "✅ $service_name built successfully"
        else
            echo "❌ Failed to build $service_name"
            exit 1
        fi
    else
        echo "❌ No build file found for $service_name"
        exit 1
    fi
    
    cd - > /dev/null
}

# Build all services
build_service "discovery-service" "../discovery-service"
build_service "auth-service" "../auth-service"
build_service "patient-service" "../patient-service"
build_service "doctor-service" "../doctor-service"
build_service "appointment-service" "../appointment-service"
build_service "chatbot-service" "../chatbot-service"
build_service "api-gateway" "../api-gateway"
build_service "chat-widget" "../pms-chat-widget"

echo "🎉 All services built successfully!"
echo "You can now run: docker-compose up -d"