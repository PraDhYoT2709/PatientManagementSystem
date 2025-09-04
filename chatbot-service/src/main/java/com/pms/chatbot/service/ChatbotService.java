package com.pms.chatbot.service;

import com.pms.chatbot.dto.*;
import com.pms.chatbot.feign.AppointmentServiceClient;
import com.pms.chatbot.feign.DoctorServiceClient;
import com.pms.chatbot.feign.PatientServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final NLUService nluService;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final AppointmentServiceClient appointmentServiceClient;

    public ChatResponse processMessage(ChatMessage chatMessage, String authorization) {
        String message = chatMessage.getMessage();
        String sessionId = chatMessage.getSessionId() != null ? chatMessage.getSessionId() : generateSessionId();
        
        log.info("Processing message: {} for session: {}", message, sessionId);
        
        // Process NLU
        IntentResult intentResult = nluService.processMessage(message);
        String intent = intentResult.getIntent();
        Map<String, Object> entities = intentResult.getEntities();
        
        log.info("Detected intent: {} with confidence: {}", intent, intentResult.getConfidence());
        
        // Generate response based on intent
        String response = generateResponse(intent, entities, authorization);
        
        // Determine if action is required
        boolean requiresAction = requiresAction(intent);
        String actionType = getActionType(intent);
        Map<String, Object> actionData = getActionData(intent, entities);
        
        return new ChatResponse(
            response,
            intent,
            entities,
            LocalDateTime.now(),
            sessionId,
            requiresAction,
            actionType,
            actionData
        );
    }

    private String generateResponse(String intent, Map<String, Object> entities, String authorization) {
        switch (intent) {
            case "greeting":
                return "Hello! I'm your Patient Management System assistant. How can I help you today?";
                
            case "appointment_booking":
                return "I can help you book an appointment. Let me check available doctors for you.";
                
            case "appointment_cancellation":
                return "I can help you cancel or reschedule your appointment. Please provide your patient ID or appointment details.";
                
            case "appointment_inquiry":
                return getAppointmentInfo(entities, authorization);
                
            case "doctor_inquiry":
                return getDoctorInfo(entities, authorization);
                
            case "patient_info":
                return getPatientInfo(entities, authorization);
                
            case "help":
                return "I can help you with:\n" +
                       "• Booking appointments\n" +
                       "• Checking appointment details\n" +
                       "• Finding doctors by specialty\n" +
                       "• Viewing patient information\n" +
                       "• Canceling appointments\n\n" +
                       "What would you like to do?";
                       
            case "goodbye":
                return "Thank you for using our Patient Management System. Have a great day!";
                
            default:
                return "I'm not sure I understand. Could you please rephrase your question? I can help you with appointments, doctors, or patient information.";
        }
    }

    private String getAppointmentInfo(Map<String, Object> entities, String authorization) {
        try {
            Long patientId = (Long) entities.get("patient_id");
            if (patientId != null) {
                ResponseEntity<List<AppointmentDto>> response = appointmentServiceClient
                    .getAppointmentsByPatientId(patientId, authorization);
                
                if (response.getBody() != null && !response.getBody().isEmpty()) {
                    List<AppointmentDto> appointments = response.getBody();
                    StringBuilder sb = new StringBuilder("Here are your appointments:\n");
                    
                    for (AppointmentDto appointment : appointments) {
                        sb.append(String.format("• %s - %s (%s)\n", 
                            appointment.getDateTime(), 
                            appointment.getReason(), 
                            appointment.getStatus()));
                    }
                    return sb.toString();
                } else {
                    return "No appointments found for patient ID " + patientId;
                }
            } else {
                return "Please provide your patient ID to check your appointments.";
            }
        } catch (Exception e) {
            log.error("Error fetching appointment info: {}", e.getMessage());
            return "Sorry, I couldn't retrieve your appointment information. Please try again later.";
        }
    }

    private String getDoctorInfo(Map<String, Object> entities, String authorization) {
        try {
            String specialty = (String) entities.get("specialty");
            if (specialty != null) {
                ResponseEntity<List<DoctorDto>> response = doctorServiceClient
                    .getDoctorsBySpecialty(specialty, authorization);
                
                if (response.getBody() != null && !response.getBody().isEmpty()) {
                    List<DoctorDto> doctors = response.getBody();
                    StringBuilder sb = new StringBuilder("Here are the " + specialty + " doctors:\n");
                    
                    for (DoctorDto doctor : doctors) {
                        sb.append(String.format("• Dr. %s - %s (%s)\n", 
                            doctor.getName(), 
                            doctor.getQualification(), 
                            doctor.isAvailable() ? "Available" : "Not Available"));
                    }
                    return sb.toString();
                } else {
                    return "No " + specialty + " doctors found.";
                }
            } else {
                // Get all available doctors
                ResponseEntity<List<DoctorDto>> response = doctorServiceClient
                    .getAvailableDoctors(authorization);
                
                if (response.getBody() != null && !response.getBody().isEmpty()) {
                    List<DoctorDto> doctors = response.getBody();
                    StringBuilder sb = new StringBuilder("Here are the available doctors:\n");
                    
                    for (DoctorDto doctor : doctors) {
                        sb.append(String.format("• Dr. %s - %s (%s)\n", 
                            doctor.getName(), 
                            doctor.getSpecialty(), 
                            doctor.getQualification()));
                    }
                    return sb.toString();
                } else {
                    return "No available doctors found.";
                }
            }
        } catch (Exception e) {
            log.error("Error fetching doctor info: {}", e.getMessage());
            return "Sorry, I couldn't retrieve doctor information. Please try again later.";
        }
    }

    private String getPatientInfo(Map<String, Object> entities, String authorization) {
        try {
            Long patientId = (Long) entities.get("patient_id");
            if (patientId != null) {
                ResponseEntity<PatientDto> response = patientServiceClient
                    .getPatientById(patientId, authorization);
                
                if (response.getBody() != null) {
                    PatientDto patient = response.getBody();
                    return String.format("Patient Information:\n" +
                                       "• Name: %s\n" +
                                       "• Age: %d\n" +
                                       "• Gender: %s\n" +
                                       "• Disease: %s\n" +
                                       "• Status: %s\n" +
                                       "• Email: %s\n" +
                                       "• Phone: %s",
                                       patient.getName(),
                                       patient.getAge(),
                                       patient.getGender(),
                                       patient.getDisease(),
                                       patient.isAdmitted() ? "Admitted" : "Not Admitted",
                                       patient.getEmail(),
                                       patient.getPhone());
                } else {
                    return "Patient not found with ID " + patientId;
                }
            } else {
                return "Please provide your patient ID to view your information.";
            }
        } catch (Exception e) {
            log.error("Error fetching patient info: {}", e.getMessage());
            return "Sorry, I couldn't retrieve your patient information. Please try again later.";
        }
    }

    private boolean requiresAction(String intent) {
        return Arrays.asList("appointment_booking", "appointment_cancellation").contains(intent);
    }

    private String getActionType(String intent) {
        switch (intent) {
            case "appointment_booking":
                return "BOOK_APPOINTMENT";
            case "appointment_cancellation":
                return "CANCEL_APPOINTMENT";
            default:
                return null;
        }
    }

    private Map<String, Object> getActionData(String intent, Map<String, Object> entities) {
        Map<String, Object> actionData = new HashMap<>();
        
        switch (intent) {
            case "appointment_booking":
                actionData.put("patient_id", entities.get("patient_id"));
                actionData.put("date", entities.get("date"));
                actionData.put("time", entities.get("time"));
                actionData.put("specialty", entities.get("specialty"));
                break;
            case "appointment_cancellation":
                actionData.put("patient_id", entities.get("patient_id"));
                actionData.put("appointment_id", entities.get("appointment_id"));
                break;
        }
        
        return actionData;
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public List<String> getAvailableIntents() {
        return nluService.getAvailableIntents();
    }
}