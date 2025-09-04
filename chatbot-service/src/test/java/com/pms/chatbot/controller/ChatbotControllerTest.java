package com.pms.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.chatbot.dto.ChatMessage;
import com.pms.chatbot.dto.ChatResponse;
import com.pms.chatbot.service.ChatbotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatbotController.class)
class ChatbotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatbotService chatbotService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void sendMessage_WithValidMessage_ShouldReturnResponse() throws Exception {
        // Given
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage("Hello");
        chatMessage.setSessionId("test-session");

        ChatResponse chatResponse = new ChatResponse(
            "Hello! How can I help you?",
            "greeting",
            new HashMap<>(),
            LocalDateTime.now(),
            "test-session",
            false,
            null,
            new HashMap<>()
        );

        when(chatbotService.processMessage(any(ChatMessage.class), anyString()))
                .thenReturn(chatResponse);

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .with(csrf())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatMessage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello! How can I help you?"))
                .andExpect(jsonPath("$.intent").value("greeting"))
                .andExpect(jsonPath("$.sessionId").value("test-session"))
                .andExpect(jsonPath("$.requiresAction").value(false));
    }

    @Test
    @WithMockUser
    void sendMessage_WithAppointmentBooking_ShouldReturnActionResponse() throws Exception {
        // Given
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage("I want to book an appointment");
        chatMessage.setSessionId("test-session");

        ChatResponse chatResponse = new ChatResponse(
            "I can help you book an appointment.",
            "appointment_booking",
            new HashMap<>(),
            LocalDateTime.now(),
            "test-session",
            true,
            "BOOK_APPOINTMENT",
            new HashMap<>()
        );

        when(chatbotService.processMessage(any(ChatMessage.class), anyString()))
                .thenReturn(chatResponse);

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .with(csrf())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatMessage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("I can help you book an appointment."))
                .andExpect(jsonPath("$.intent").value("appointment_booking"))
                .andExpect(jsonPath("$.requiresAction").value(true))
                .andExpect(jsonPath("$.actionType").value("BOOK_APPOINTMENT"));
    }

    @Test
    @WithMockUser
    void sendMessage_WithInvalidMessage_ShouldReturnBadRequest() throws Exception {
        // Given
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(""); // Empty message should fail validation
        chatMessage.setSessionId("test-session");

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .with(csrf())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatMessage)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void sendMessage_WithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
        // Given
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage("Hello");
        chatMessage.setSessionId("test-session");

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatMessage)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAvailableIntents_ShouldReturnIntents() throws Exception {
        // Given
        List<String> intents = Arrays.asList("greeting", "appointment_booking", "help");
        when(chatbotService.getAvailableIntents()).thenReturn(intents);

        // When & Then
        mockMvc.perform(get("/api/chat/intents")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("greeting"))
                .andExpect(jsonPath("$[1]").value("appointment_booking"))
                .andExpect(jsonPath("$[2]").value("help"));
    }

    @Test
    @WithMockUser
    void getAvailableIntents_WithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/chat/intents"))
                .andExpect(status().isUnauthorized());
    }
}