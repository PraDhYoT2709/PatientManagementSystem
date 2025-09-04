#!/bin/bash

# Cleanup Patient Management System
echo "Cleaning up Patient Management System..."

# Stop and remove containers
echo "Stopping and removing containers..."
docker-compose down -v

# Remove images
echo "Removing images..."
docker-compose down --rmi all

# Remove volumes
echo "Removing volumes..."
docker volume prune -f

# Remove networks
echo "Removing networks..."
docker network prune -f

echo "✅ Cleanup completed successfully!"