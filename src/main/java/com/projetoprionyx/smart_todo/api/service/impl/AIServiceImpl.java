package com.projetoprionyx.smart_todo.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoprionyx.smart_todo.api.dto.ai.AIPredictionDto;
import com.projetoprionyx.smart_todo.api.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper; // Ferramenta para converter JSON
    private final String promptTemplateString;

    // Injetamos o ChatClient.Builder e o ObjectMapper via construtor
    public AIServiceImpl(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper; // Armazenamos o ObjectMapper

        this.promptTemplateString = """
            Analise a seguinte tarefa e forneça uma sugestão de prioridade e data de conclusão.
            A data de hoje é: {currentDate}.

            Título da Tarefa: {title}
            Descrição da Tarefa: {description}

            Responda APENAS com um objeto JSON válido, sem nenhum texto adicional antes ou depois.
            O JSON deve ter duas chaves:
            1. "priority": com um dos seguintes valores em maiúsculas: "HIGH", "MEDIUM", "LOW".
            2. "dueDate": com a data sugerida no formato "YYYY-MM-DD".

            Se a tarefa não tiver urgência ou data implícita, use a prioridade "LOW" e a data de hoje.
            """;
    }

    @Override
    public AIPredictionDto getTaskPrediction(String title, String description) {
        PromptTemplate promptTemplate = new PromptTemplate(this.promptTemplateString);
        Map<String, Object> promptValues = Map.of(
                "currentDate", LocalDate.now().toString(),
                "title", title,
                "description", (description != null ? description : "N/A")
        );

        // 1. Faz a chamada e obtém a resposta como uma String de texto.
        String jsonResponse = this.chatClient.prompt()
                .messages(promptTemplate.createMessage(promptValues))
                .call()
                .content(); // <-- Usamos .content() para obter a String

        // 2. Converte (parse) a String JSON para nosso objeto DTO.
        try {
            return objectMapper.readValue(jsonResponse, AIPredictionDto.class);
        } catch (Exception e) {
            logger.error("Falha ao converter a resposta JSON da IA: {}", jsonResponse, e);
            // Retorna um valor padrão ou nulo em caso de erro de conversão.
            return null;
        }
    }
}