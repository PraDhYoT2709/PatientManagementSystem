package com.pms.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private String intent;
    private Map<String, Object> entities;
    private LocalDateTime timestamp;
    private String sessionId;
    private boolean requiresAction;
    private String actionType;
    private Map<String, Object> actionData;
}