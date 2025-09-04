package com.pms.chatbot.controller;

import com.pms.chatbot.dto.ChatMessage;
import com.pms.chatbot.dto.ChatResponse;
import com.pms.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody ChatMessage chatMessage,
            @RequestHeader("Authorization") String authorization) {
        
        ChatResponse response = chatbotService.processMessage(chatMessage, authorization);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/intents")
    public ResponseEntity<List<String>> getAvailableIntents() {
        List<String> intents = chatbotService.getAvailableIntents();
        return ResponseEntity.ok(intents);
    }
}