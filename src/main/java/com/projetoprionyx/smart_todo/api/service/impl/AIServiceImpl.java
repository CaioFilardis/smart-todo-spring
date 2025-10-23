package com.projetoprionyx.smart_todo.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    private final ObjectMapper objectMapper;
    private final String promptTemplateString;

    public AIServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.promptTemplateString = """
                Sua tarefa é analisar o título e a descrição de uma tarefa e retornar uma sugestão de prioridade e data de conclusão.
                A data de hoje é: {currentDate}.
                
                Título da Tarefa: "{title}"
                Descrição da Tarefa: "{description}"
                
                Responda APENAS com um objeto JSON válido, sem nenhum texto ou formatação adicional.
                O JSON deve conter exatamente as seguintes chaves:
                1. "priority": use um dos valores "HIGH", "MEDIUM", ou "LOW".
                2. "dueDate": use o formato "YYYY-MM-DD".
                
                Analise o texto em busca de urgência (ex: "para hoje", "urgente", "crítico") e datas explícitas.
                Se houver urgência detectada, use "HIGH".
                Se nenhuma urgência for detectada, use "LOW".
                
                NÃO USE markdown (```) na resposta. Retorne APENAS o JSON puro.
                """;
    }

    @Override
    public AIPredictionDto getTaskPrediction(String title, String description) {
        try {
            logger.debug("Iniciando previsão de IA para tarefa: {}", title);

            PromptTemplate promptTemplate = new PromptTemplate(this.promptTemplateString);

            Map<String, Object> promptValues = Map.of(
                    "currentDate", LocalDate.now().toString(),
                    "title", title,
                    "description", (description != null ? description : "Nenhuma")
            );

            String responseContent = this.chatClient
                    .prompt(promptTemplate.create(promptValues))
                    .call()
                    .content();

            logger.debug("Resposta recebida da IA (bruta): {}", responseContent);

            String cleanedResponse = responseContent
                    .replaceAll("(?is)```json\\s*(.*?)\\s*```", "$1") // Extrai conteúdo do bloco JSON
                    .replaceAll("(?i)^\\s*json\\s*", "")             // Remove o prefixo "json\n" ou "json "
                    .replaceAll("[`]+", "")                           // Remove backticks
                    .trim();

            logger.debug("Resposta limpa: {}", cleanedResponse);

            try {
                AIPredictionDto prediction = this.objectMapper.readValue(cleanedResponse, AIPredictionDto.class);
                logger.debug("Previsão obtida: priority={}, dueDate={}", prediction.getPriority(), prediction.getDueDate());
                return prediction;
            } catch (Exception e) {
                logger.error("Erro ao converter resposta JSON da IA: {}", cleanedResponse, e);
                return new AIPredictionDto(null, LocalDate.now().plusDays(1));
            }

        } catch (Exception e) {
            logger.error("Erro ao chamar a API de IA", e);
            return new AIPredictionDto(null, LocalDate.now().plusDays(1));
        }
    }
}
