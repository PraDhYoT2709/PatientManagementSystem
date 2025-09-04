-- Initialize databases for Patient Management System

-- Create databases
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS patient_db;
CREATE DATABASE IF NOT EXISTS doctor_db;
CREATE DATABASE IF NOT EXISTS appointment_db;

-- Grant permissions
GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON patient_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON doctor_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON appointment_db.* TO 'root'@'%';

-- Flush privileges
FLUSH PRIVILEGES;