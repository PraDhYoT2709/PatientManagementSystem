package com.pms.chatbot.service;

import com.pms.chatbot.dto.*;
import com.pms.chatbot.feign.AppointmentServiceClient;
import com.pms.chatbot.feign.DoctorServiceClient;
import com.pms.chatbot.feign.PatientServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private NLUService nluService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private DoctorServiceClient doctorServiceClient;

    @Mock
    private AppointmentServiceClient appointmentServiceClient;

    @InjectMocks
    private ChatbotService chatbotService;

    private ChatMessage testMessage;
    private String authorization = "Bearer token";

    @BeforeEach
    void setUp() {
        testMessage = new ChatMessage();
        testMessage.setMessage("Hello");
        testMessage.setSessionId("test-session");
    }

    @Test
    void processMessage_WithGreeting_ShouldReturnGreetingResponse() {
        // Given
        IntentResult intentResult = new IntentResult("greeting", 0.9, new HashMap<>());
        when(nluService.processMessage("Hello")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("greeting", response.getIntent());
        assertTrue(response.getMessage().contains("Hello"));
        assertFalse(response.isRequiresAction());
        assertNull(response.getActionType());
    }

    @Test
    void processMessage_WithAppointmentBooking_ShouldReturnBookingResponse() {
        // Given
        testMessage.setMessage("I want to book an appointment");
        IntentResult intentResult = new IntentResult("appointment_booking", 0.9, new HashMap<>());
        when(nluService.processMessage("I want to book an appointment")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("appointment_booking", response.getIntent());
        assertTrue(response.getMessage().contains("book an appointment"));
        assertTrue(response.isRequiresAction());
        assertEquals("BOOK_APPOINTMENT", response.getActionType());
    }

    @Test
    void processMessage_WithAppointmentCancellation_ShouldReturnCancellationResponse() {
        // Given
        testMessage.setMessage("I need to cancel my appointment");
        IntentResult intentResult = new IntentResult("appointment_cancellation", 0.9, new HashMap<>());
        when(nluService.processMessage("I need to cancel my appointment")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("appointment_cancellation", response.getIntent());
        assertTrue(response.getMessage().contains("cancel"));
        assertTrue(response.isRequiresAction());
        assertEquals("CANCEL_APPOINTMENT", response.getActionType());
    }

    @Test
    void processMessage_WithDoctorInquiry_ShouldReturnDoctorInfo() {
        // Given
        testMessage.setMessage("I need a cardiology doctor");
        Map<String, Object> entities = new HashMap<>();
        entities.put("specialty", "cardiology");
        IntentResult intentResult = new IntentResult("doctor_inquiry", 0.9, entities);
        
        when(nluService.processMessage("I need a cardiology doctor")).thenReturn(intentResult);
        
        DoctorDto doctor = new DoctorDto();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        doctor.setSpecialty("cardiology");
        doctor.setQualification("MD");
        doctor.setAvailable(true);
        
        when(doctorServiceClient.getDoctorsBySpecialty("cardiology", authorization))
                .thenReturn(ResponseEntity.ok(Arrays.asList(doctor)));

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("doctor_inquiry", response.getIntent());
        assertTrue(response.getMessage().contains("cardiology doctors"));
        assertTrue(response.getMessage().contains("Dr. Smith"));
    }

    @Test
    void processMessage_WithPatientInfo_ShouldReturnPatientInfo() {
        // Given
        testMessage.setMessage("Show me patient ID 123 information");
        Map<String, Object> entities = new HashMap<>();
        entities.put("patient_id", 123L);
        IntentResult intentResult = new IntentResult("patient_info", 0.9, entities);
        
        when(nluService.processMessage("Show me patient ID 123 information")).thenReturn(intentResult);
        
        PatientDto patient = new PatientDto();
        patient.setId(123L);
        patient.setName("John Doe");
        patient.setAge(30);
        patient.setGender("Male");
        patient.setDisease("Fever");
        patient.setEmail("john@example.com");
        patient.setPhone("1234567890");
        patient.setAdmitted(false);
        
        when(patientServiceClient.getPatientById(123L, authorization))
                .thenReturn(ResponseEntity.ok(patient));

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("patient_info", response.getIntent());
        assertTrue(response.getMessage().contains("Patient Information"));
        assertTrue(response.getMessage().contains("John Doe"));
        assertTrue(response.getMessage().contains("30"));
    }

    @Test
    void processMessage_WithAppointmentInquiry_ShouldReturnAppointmentInfo() {
        // Given
        testMessage.setMessage("Show me appointments for patient ID 123");
        Map<String, Object> entities = new HashMap<>();
        entities.put("patient_id", 123L);
        IntentResult intentResult = new IntentResult("appointment_inquiry", 0.9, entities);
        
        when(nluService.processMessage("Show me appointments for patient ID 123")).thenReturn(intentResult);
        
        AppointmentDto appointment = new AppointmentDto();
        appointment.setId(1L);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));
        appointment.setReason("Checkup");
        appointment.setStatus("SCHEDULED");
        
        when(appointmentServiceClient.getAppointmentsByPatientId(123L, authorization))
                .thenReturn(ResponseEntity.ok(Arrays.asList(appointment)));

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("appointment_inquiry", response.getIntent());
        assertTrue(response.getMessage().contains("appointments"));
        assertTrue(response.getMessage().contains("Checkup"));
    }

    @Test
    void processMessage_WithHelp_ShouldReturnHelpResponse() {
        // Given
        testMessage.setMessage("I need help");
        IntentResult intentResult = new IntentResult("help", 0.9, new HashMap<>());
        when(nluService.processMessage("I need help")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("help", response.getIntent());
        assertTrue(response.getMessage().contains("help you with"));
        assertTrue(response.getMessage().contains("appointments"));
        assertTrue(response.getMessage().contains("doctors"));
    }

    @Test
    void processMessage_WithGoodbye_ShouldReturnGoodbyeResponse() {
        // Given
        testMessage.setMessage("Thank you, goodbye");
        IntentResult intentResult = new IntentResult("goodbye", 0.9, new HashMap<>());
        when(nluService.processMessage("Thank you, goodbye")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("goodbye", response.getIntent());
        assertTrue(response.getMessage().contains("Thank you"));
        assertTrue(response.getMessage().contains("great day"));
    }

    @Test
    void processMessage_WithUnknownIntent_ShouldReturnDefaultResponse() {
        // Given
        testMessage.setMessage("xyz random text");
        IntentResult intentResult = new IntentResult("unknown", 0.0, new HashMap<>());
        when(nluService.processMessage("xyz random text")).thenReturn(intentResult);

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("unknown", response.getIntent());
        assertTrue(response.getMessage().contains("not sure I understand"));
    }

    @Test
    void processMessage_WithServiceError_ShouldReturnErrorResponse() {
        // Given
        testMessage.setMessage("Show me patient ID 123 information");
        Map<String, Object> entities = new HashMap<>();
        entities.put("patient_id", 123L);
        IntentResult intentResult = new IntentResult("patient_info", 0.9, entities);
        
        when(nluService.processMessage("Show me patient ID 123 information")).thenReturn(intentResult);
        when(patientServiceClient.getPatientById(123L, authorization))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When
        ChatResponse response = chatbotService.processMessage(testMessage, authorization);

        // Then
        assertNotNull(response);
        assertEquals("patient_info", response.getIntent());
        assertTrue(response.getMessage().contains("Sorry"));
        assertTrue(response.getMessage().contains("try again later"));
    }

    @Test
    void getAvailableIntents_ShouldReturnIntents() {
        // Given
        List<String> expectedIntents = Arrays.asList("greeting", "appointment_booking", "help");
        when(nluService.getAvailableIntents()).thenReturn(expectedIntents);

        // When
        List<String> intents = chatbotService.getAvailableIntents();

        // Then
        assertNotNull(intents);
        assertEquals(expectedIntents, intents);
    }
}