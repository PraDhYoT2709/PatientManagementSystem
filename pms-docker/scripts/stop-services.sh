#!/bin/bash

# Stop Patient Management System services
echo "Stopping Patient Management System..."

# Stop services
docker-compose down

echo "✅ All services stopped successfully!"