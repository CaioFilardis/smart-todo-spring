package com.projetoprionyx.smart_todo.api.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SimpleChatService {

    private final ChatClient chatClient;

    public SimpleChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String chat(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public ChatResponse chatWithResponse(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();
    }
}
