package com.pms.chatbot.service;

import com.pms.chatbot.dto.IntentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NLUService {

    private static final Map<String, List<String>> INTENT_PATTERNS = Map.of(
        "greeting", Arrays.asList("hello", "hi", "hey", "good morning", "good afternoon", "good evening"),
        "appointment_booking", Arrays.asList("book", "schedule", "appointment", "meeting", "visit"),
        "appointment_cancellation", Arrays.asList("cancel", "reschedule", "postpone", "change"),
        "appointment_inquiry", Arrays.asList("appointment", "schedule", "when", "time", "date"),
        "doctor_inquiry", Arrays.asList("doctor", "physician", "specialist", "specialty"),
        "patient_info", Arrays.asList("patient", "my info", "profile", "details"),
        "help", Arrays.asList("help", "support", "assistance", "how to"),
        "goodbye", Arrays.asList("bye", "goodbye", "see you", "thanks", "thank you")
    );

    private static final Map<String, Pattern> ENTITY_PATTERNS = Map.of(
        "date", Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|tomorrow|today|next week|next month)\\b", Pattern.CASE_INSENSITIVE),
        "time", Pattern.compile("\\b(\\d{1,2}:\\d{2}\\s*(am|pm)?|morning|afternoon|evening)\\b", Pattern.CASE_INSENSITIVE),
        "specialty", Pattern.compile("\\b(cardiology|dermatology|neurology|orthopedics|pediatrics|psychiatry|radiology|surgery)\\b", Pattern.CASE_INSENSITIVE),
        "doctor_name", Pattern.compile("\\bdr\\.?\\s+([a-zA-Z]+)\\b", Pattern.CASE_INSENSITIVE),
        "patient_id", Pattern.compile("\\b(patient\\s+)?(id\\s+)?(\\d+)\\b", Pattern.CASE_INSENSITIVE)
    );

    public IntentResult processMessage(String message) {
        String normalizedMessage = message.toLowerCase().trim();
        
        // Find best matching intent
        String bestIntent = findBestIntent(normalizedMessage);
        double confidence = calculateConfidence(normalizedMessage, bestIntent);
        
        // Extract entities
        Map<String, Object> entities = extractEntities(message);
        
        return new IntentResult(bestIntent, confidence, entities);
    }

    private String findBestIntent(String message) {
        String bestIntent = "unknown";
        int maxMatches = 0;
        
        for (Map.Entry<String, List<String>> entry : INTENT_PATTERNS.entrySet()) {
            String intent = entry.getKey();
            List<String> patterns = entry.getValue();
            
            int matches = 0;
            for (String pattern : patterns) {
                if (message.contains(pattern)) {
                    matches++;
                }
            }
            
            if (matches > maxMatches) {
                maxMatches = matches;
                bestIntent = intent;
            }
        }
        
        return bestIntent;
    }

    private double calculateConfidence(String message, String intent) {
        List<String> patterns = INTENT_PATTERNS.get(intent);
        if (patterns == null) return 0.0;
        
        int matches = 0;
        for (String pattern : patterns) {
            if (message.contains(pattern)) {
                matches++;
            }
        }
        
        return Math.min(1.0, (double) matches / patterns.size());
    }

    private Map<String, Object> extractEntities(String message) {
        Map<String, Object> entities = new HashMap<>();
        
        for (Map.Entry<String, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            String entityType = entry.getKey();
            Pattern pattern = entry.getValue();
            Matcher matcher = pattern.matcher(message);
            
            if (matcher.find()) {
                String value = matcher.group();
                
                // Process specific entity types
                switch (entityType) {
                    case "date":
                        entities.put("date", parseDate(value));
                        break;
                    case "time":
                        entities.put("time", parseTime(value));
                        break;
                    case "specialty":
                        entities.put("specialty", value.toLowerCase());
                        break;
                    case "doctor_name":
                        entities.put("doctor_name", matcher.group(1));
                        break;
                    case "patient_id":
                        entities.put("patient_id", Long.parseLong(matcher.group(3)));
                        break;
                    default:
                        entities.put(entityType, value);
                }
            }
        }
        
        return entities;
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            if (dateStr.equalsIgnoreCase("today")) {
                return LocalDate.now().atStartOfDay();
            } else if (dateStr.equalsIgnoreCase("tomorrow")) {
                return LocalDate.now().plusDays(1).atStartOfDay();
            } else {
                // Try different date formats
                String[] formats = {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "MM-dd-yyyy"};
                for (String format : formats) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                        return LocalDate.parse(dateStr, formatter).atStartOfDay();
                    } catch (DateTimeParseException ignored) {
                        // Try next format
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", dateStr);
        }
        return null;
    }

    private String parseTime(String timeStr) {
        try {
            if (timeStr.equalsIgnoreCase("morning")) {
                return "09:00";
            } else if (timeStr.equalsIgnoreCase("afternoon")) {
                return "14:00";
            } else if (timeStr.equalsIgnoreCase("evening")) {
                return "18:00";
            } else {
                // Return as-is if it looks like a time
                if (timeStr.matches("\\d{1,2}:\\d{2}")) {
                    return timeStr;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse time: {}", timeStr);
        }
        return null;
    }

    public List<String> getAvailableIntents() {
        return new ArrayList<>(INTENT_PATTERNS.keySet());
    }
}