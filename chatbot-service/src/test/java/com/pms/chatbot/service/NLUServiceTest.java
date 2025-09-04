package com.pms.chatbot.service;

import com.pms.chatbot.dto.IntentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NLUServiceTest {

    @InjectMocks
    private NLUService nluService;

    @Test
    void processMessage_WithGreeting_ShouldReturnGreetingIntent() {
        // Given
        String message = "Hello, how are you?";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("greeting", result.getIntent());
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void processMessage_WithAppointmentBooking_ShouldReturnAppointmentBookingIntent() {
        // Given
        String message = "I want to book an appointment for tomorrow";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("appointment_booking", result.getIntent());
        assertTrue(result.getConfidence() > 0);
        assertTrue(result.getEntities().containsKey("date"));
    }

    @Test
    void processMessage_WithAppointmentCancellation_ShouldReturnCancellationIntent() {
        // Given
        String message = "I need to cancel my appointment";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("appointment_cancellation", result.getIntent());
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void processMessage_WithDoctorInquiry_ShouldReturnDoctorIntent() {
        // Given
        String message = "I need a cardiology doctor";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("doctor_inquiry", result.getIntent());
        assertTrue(result.getConfidence() > 0);
        assertTrue(result.getEntities().containsKey("specialty"));
        assertEquals("cardiology", result.getEntities().get("specialty"));
    }

    @Test
    void processMessage_WithPatientInfo_ShouldReturnPatientIntent() {
        // Given
        String message = "Show me patient ID 123 information";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("patient_info", result.getIntent());
        assertTrue(result.getConfidence() > 0);
        assertTrue(result.getEntities().containsKey("patient_id"));
        assertEquals(123L, result.getEntities().get("patient_id"));
    }

    @Test
    void processMessage_WithHelp_ShouldReturnHelpIntent() {
        // Given
        String message = "I need help with the system";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("help", result.getIntent());
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void processMessage_WithGoodbye_ShouldReturnGoodbyeIntent() {
        // Given
        String message = "Thank you, goodbye";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("goodbye", result.getIntent());
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void processMessage_WithUnknownMessage_ShouldReturnUnknownIntent() {
        // Given
        String message = "xyz abc def random text";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertEquals("unknown", result.getIntent());
        assertEquals(0.0, result.getConfidence());
    }

    @Test
    void processMessage_WithDateEntity_ShouldExtractDate() {
        // Given
        String message = "Book appointment for 12/25/2024";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertTrue(result.getEntities().containsKey("date"));
        assertNotNull(result.getEntities().get("date"));
    }

    @Test
    void processMessage_WithTimeEntity_ShouldExtractTime() {
        // Given
        String message = "Schedule appointment at 2:30 PM";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertTrue(result.getEntities().containsKey("time"));
        assertEquals("2:30 PM", result.getEntities().get("time"));
    }

    @Test
    void processMessage_WithDoctorName_ShouldExtractDoctorName() {
        // Given
        String message = "I want to see Dr. Smith";

        // When
        IntentResult result = nluService.processMessage(message);

        // Then
        assertTrue(result.getEntities().containsKey("doctor_name"));
        assertEquals("Smith", result.getEntities().get("doctor_name"));
    }

    @Test
    void getAvailableIntents_ShouldReturnAllIntents() {
        // When
        List<String> intents = nluService.getAvailableIntents();

        // Then
        assertNotNull(intents);
        assertTrue(intents.contains("greeting"));
        assertTrue(intents.contains("appointment_booking"));
        assertTrue(intents.contains("appointment_cancellation"));
        assertTrue(intents.contains("doctor_inquiry"));
        assertTrue(intents.contains("patient_info"));
        assertTrue(intents.contains("help"));
        assertTrue(intents.contains("goodbye"));
    }
}