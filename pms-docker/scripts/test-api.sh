#!/bin/bash

# Test Patient Management System API endpoints
echo "Testing Patient Management System API..."

# Base URL
BASE_URL="http://localhost:8080"

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    
    echo "Testing $method $endpoint..."
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL$endpoint")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    fi
    
    if [ "$response" = "$expected_status" ]; then
        echo "✅ $method $endpoint - Status: $response"
    else
        echo "❌ $method $endpoint - Expected: $expected_status, Got: $response"
    fi
}

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 10

# Test API Gateway health
test_endpoint "GET" "/actuator/health" "" "200"

# Test auth endpoints
test_endpoint "POST" "/api/auth/register" '{"username":"testuser","email":"test@example.com","password":"password123"}' "201"
test_endpoint "POST" "/api/auth/login" '{"email":"test@example.com","password":"password123"}' "200"

# Test discovery service
test_endpoint "GET" "http://localhost:8761/actuator/health" "" "200"

echo ""
echo "🎉 API testing completed!"
echo ""
echo "📋 Test Results:"
echo "  • Check the output above for test results"
echo "  • All endpoints should return expected status codes"
echo "  • If any tests fail, check service logs with: docker-compose logs [service-name]"