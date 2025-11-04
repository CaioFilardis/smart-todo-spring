package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.dto.ai.ChatRequestDto;
import com.projetoprionyx.smart_todo.api.service.SimpleChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/google-gemini")
public class GeminiController {

    private final SimpleChatService simpleChatService;

    public GeminiController(SimpleChatService simpleChatService) {
        this.simpleChatService = simpleChatService;
    }

    @PostMapping("/chat")
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        String reply = this.simpleChatService.chat(request.getMessage());
        return new ChatResponseDto(reply);
    }
}