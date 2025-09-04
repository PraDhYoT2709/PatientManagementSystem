package com.pms.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class IntentResult {
    private String intent;
    private double confidence;
    private Map<String, Object> entities;
}