package com.projetoprionyx.smart_todo.api.service;

import com.projetoprionyx.smart_todo.api.dto.ai.AIPredictionDto;
import org.springframework.stereotype.Service;

public interface AIService {

    AIPredictionDto getTaskPrediction(String title, String description);
}
