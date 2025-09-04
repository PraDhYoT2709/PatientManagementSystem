package com.pms.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    @NotBlank(message = "Message is required")
    private String message;
    
    private String intent;
    private String response;
    private LocalDateTime timestamp;
    private String userId;
    private String sessionId;
}