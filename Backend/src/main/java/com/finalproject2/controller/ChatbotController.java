package com.finalproject2.controller;

import com.finalproject2.model.request.ChatbotRequest;
import com.finalproject2.model.response.ChatbotResponse;
import com.finalproject2.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(originPatterns = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatbotResponse> ask(@RequestBody ChatbotRequest request) {
        String reply = chatbotService.getChatbotReply(request.getMessage());
        return ResponseEntity.ok(new ChatbotResponse(reply));
    }
}
