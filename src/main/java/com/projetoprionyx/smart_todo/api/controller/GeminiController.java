package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.service.SimpleChatService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-gemini")
public class GeminiController {

    private final SimpleChatService simpleChatService;

    public GeminiController(SimpleChatService simpleChatService) {
        this.simpleChatService = simpleChatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody String message) {
        return this.simpleChatService.chatWithResponse(message);
    }

    @GetMapping("/chat")
    public ChatResponse chatGet(String message) {
        return this.simpleChatService.chatWithResponse(message);
    }
}
