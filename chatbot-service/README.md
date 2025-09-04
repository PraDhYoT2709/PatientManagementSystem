# Chatbot Service

A microservice for intelligent chatbot functionality in the Patient Management System (PMS). This service provides Natural Language Understanding (NLU), JWT-secured endpoints, and integration with other microservices via Feign clients.

## 🚀 Features

- **Natural Language Understanding**: Rule-based NLU with intent detection and entity extraction
- **JWT Security**: Secure endpoints with JWT token validation
- **Service Integration**: Feign clients for patient, doctor, and appointment services
- **Intent Recognition**: Support for multiple intents (greeting, appointment booking, etc.)
- **Entity Extraction**: Date, time, specialty, doctor name, and patient ID extraction
- **Action Support**: Action-based responses for appointment booking and cancellation
- **Service Discovery**: Integration with Eureka for service registration

## 🏗️ Architecture

- **Framework**: Spring Boot 3.5.3
- **Security**: Spring Security with JWT
- **Service Communication**: OpenFeign for HTTP clients
- **Service Discovery**: Netflix Eureka Client
- **Testing**: JUnit 5 with Mockito

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- Eureka Discovery Server running on port 8761
- Patient Service running and registered with Eureka
- Doctor Service running and registered with Eureka
- Appointment Service running and registered with Eureka

## 🛠️ Setup

### 1. Run the Application

```bash
mvn spring-boot:run
```

The service will start on port 8085 and register with Eureka.

## 📡 API Endpoints

### Chat Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat/message` | Send a message to the chatbot |
| GET | `/api/chat/intents` | Get available intents |

### Authentication

All endpoints require JWT authentication:
- **Header**: `Authorization: Bearer <jwt-token>`

## 📝 API Examples

### Send Message

```bash
curl -X POST http://localhost:8085/api/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "message": "Hello, I need help with my appointment",
    "sessionId": "user-session-123"
  }'
```

**Response:**
```json
{
  "message": "Hello! I'm your Patient Management System assistant. How can I help you today?",
  "intent": "greeting",
  "entities": {},
  "timestamp": "2024-01-15T10:00:00",
  "sessionId": "user-session-123",
  "requiresAction": false,
  "actionType": null,
  "actionData": {}
}
```

### Book Appointment

```bash
curl -X POST http://localhost:8085/api/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "message": "I want to book an appointment for tomorrow at 2 PM with a cardiology doctor",
    "sessionId": "user-session-123"
  }'
```

**Response:**
```json
{
  "message": "I can help you book an appointment. Let me check available doctors for you.",
  "intent": "appointment_booking",
  "entities": {
    "date": "2024-01-16T00:00:00",
    "time": "2:00 PM",
    "specialty": "cardiology"
  },
  "timestamp": "2024-01-15T10:00:00",
  "sessionId": "user-session-123",
  "requiresAction": true,
  "actionType": "BOOK_APPOINTMENT",
  "actionData": {
    "date": "2024-01-16T00:00:00",
    "time": "2:00 PM",
    "specialty": "cardiology"
  }
}
```

### Get Available Intents

```bash
curl -X GET http://localhost:8085/api/chat/intents \
  -H "Authorization: Bearer your-jwt-token"
```

**Response:**
```json
[
  "greeting",
  "appointment_booking",
  "appointment_cancellation",
  "appointment_inquiry",
  "doctor_inquiry",
  "patient_info",
  "help",
  "goodbye"
]
```

## 🧠 Natural Language Understanding

### Supported Intents

| Intent | Description | Example Phrases |
|--------|-------------|-----------------|
| `greeting` | Greeting messages | "Hello", "Hi", "Good morning" |
| `appointment_booking` | Book appointments | "Book appointment", "Schedule meeting" |
| `appointment_cancellation` | Cancel appointments | "Cancel appointment", "Reschedule" |
| `appointment_inquiry` | Check appointments | "My appointments", "When is my visit" |
| `doctor_inquiry` | Find doctors | "Cardiology doctor", "Find specialist" |
| `patient_info` | Patient information | "My info", "Patient details" |
| `help` | Help requests | "Help", "Support", "How to" |
| `goodbye` | Farewell messages | "Bye", "Thank you", "Goodbye" |

### Entity Extraction

| Entity Type | Description | Examples |
|-------------|-------------|----------|
| `date` | Date references | "tomorrow", "12/25/2024", "next week" |
| `time` | Time references | "2:30 PM", "morning", "afternoon" |
| `specialty` | Medical specialties | "cardiology", "dermatology", "neurology" |
| `doctor_name` | Doctor names | "Dr. Smith", "Dr. Johnson" |
| `patient_id` | Patient IDs | "patient 123", "ID 456" |

## 🔧 Service Integration

### Feign Clients

The service integrates with other microservices:

- **PatientServiceClient**: Retrieves patient information
- **DoctorServiceClient**: Gets doctor details and specialties
- **AppointmentServiceClient**: Manages appointment data

### JWT Token Forwarding

All Feign client calls forward the JWT token to maintain security context.

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

The service includes comprehensive tests:
- **NLU Tests**: Intent detection and entity extraction
- **Service Tests**: Business logic and service integration
- **Controller Tests**: REST endpoint testing
- **Security Tests**: JWT authentication validation

### NLU Test Examples

```java
@Test
void processMessage_WithGreeting_ShouldReturnGreetingIntent() {
    IntentResult result = nluService.processMessage("Hello, how are you?");
    assertEquals("greeting", result.getIntent());
    assertTrue(result.getConfidence() > 0);
}

@Test
void processMessage_WithDateEntity_ShouldExtractDate() {
    IntentResult result = nluService.processMessage("Book appointment for 12/25/2024");
    assertTrue(result.getEntities().containsKey("date"));
    assertNotNull(result.getEntities().get("date"));
}
```

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8085/actuator/health
```

### Metrics

```bash
curl http://localhost:8085/actuator/metrics
```

## 🔧 Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8085 | Service port |
| `jwt.secret` | - | JWT signing secret |
| `eureka.client.service-url.defaultZone` | http://localhost:8761/eureka/ | Eureka server URL |
| `feign.client.config.default.connect-timeout` | 5000 | Feign connection timeout |
| `feign.client.config.default.read-timeout` | 5000 | Feign read timeout |

### Environment Variables

- `JWT_SECRET`: JWT signing secret
- `EUREKA_SERVER_URL`: Eureka server URL

## 🐳 Docker

### Build Image

```bash
docker build -t chatbot-service .
```

### Run Container

```bash
docker run -p 8085:8085 \
  -e JWT_SECRET=your-jwt-secret \
  chatbot-service
```

## 🚨 Error Handling

The service handles various error scenarios:

- **Invalid JWT Token**: Returns 401 Unauthorized
- **Service Unavailable**: Returns error message with retry suggestion
- **Invalid Input**: Returns 400 Bad Request
- **Unknown Intent**: Returns helpful default response

## 🔄 Message Processing Flow

1. **Message Reception**: Client sends message with JWT token
2. **JWT Validation**: Token is validated for authentication
3. **NLU Processing**: Intent and entities are extracted
4. **Service Integration**: Relevant services are called if needed
5. **Response Generation**: Appropriate response is generated
6. **Action Determination**: Action requirements are identified
7. **Response Return**: Structured response is returned to client

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.